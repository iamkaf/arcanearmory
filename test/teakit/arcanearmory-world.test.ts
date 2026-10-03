import { Capability, Readiness, describe, pos, test } from "@teakit/test";
import type { BlockPos, TeaKitTestContext } from "@teakit/test";

describe.configure({
  timeout: "8m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.ClientScreens,
    Capability.PlayerDriver,
    Capability.PlayerInteractions,
    Capability.PlayerInventory,
    Capability.PlayerReset,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldBlock,
    Capability.WorldEntities,
    Capability.WorldExplosions,
    Capability.WorldLoot,
  ],
});

const MINED = pos(5, 73, 0);
const GLFW_KEY_ESCAPE = 256;

// Ores that generate throughout the Overworld.
const OVERWORLD_ORES = [
  "aetheric_crystal",
  "black_diamond",
  "chrysoberyl",
  "coolpper",
  "ruby",
  "sapphire",
  "titanium",
  "topaz",
] as const;

// Ores that generate only in some biomes; the materials tests check where.
const BIOME_ORES = ["aquamarine", "frost_diamond", "solarflare_gem"] as const;

// The Overworld from bedrock to sea level, in fill-sized layers.
const OVERWORLD_LAYERS = [[-64, -33], [-32, -1], [0, 31], [32, 63]];

// Gear families vanilla chests never hold, so Arcane loot leaves them out too.
const NON_LOOT_GEAR = ["_hammer", "_bow", "_shield"];

describe("Arcane Armory blocks", () => {
  test("ores need the right pickaxe tier and drop their gem, raw metal, or themselves with Silk Touch", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);

    await expectDrops(ctx, "minecraft:stone_pickaxe", "arcanearmory:ruby_ore", []);
    await expectDrops(ctx, "minecraft:iron_pickaxe", "arcanearmory:ruby_ore", ["arcanearmory:ruby"]);
    await expectDrops(ctx, "minecraft:iron_pickaxe", "arcanearmory:deepslate_titanium_ore", ["arcanearmory:raw_titanium"]);
    await expectDrops(ctx, silkTouch(version, "minecraft:iron_pickaxe"), "arcanearmory:ruby_ore", ["arcanearmory:ruby_ore"]);
  });

  test("amber ore drops raw amber, which smelts into amber and alloys into amber ingots", async (ctx) => {
    await prepare(ctx);

    await expectDrops(ctx, "minecraft:iron_pickaxe", "arcanearmory:amber_ore", ["arcanearmory:raw_amber"]);
    await ctx.recipes.assertCooking("minecraft:smelting", "arcanearmory:raw_amber", "arcanearmory:amber");
    await ctx.recipes.assertCrafting(2, 1, ["minecraft:iron_ingot", "arcanearmory:raw_amber"], "arcanearmory:amber_ingot", {
      resultCount: 2,
    });
  });

  test("void obsidian ore needs a netherite-tier pickaxe", async (ctx) => {
    await prepare(ctx);

    await expectDrops(ctx, "minecraft:diamond_pickaxe", "arcanearmory:void_obsidian_fragment_ore", []);
    await expectDrops(ctx, "minecraft:netherite_pickaxe", "arcanearmory:void_obsidian_fragment_ore", [
      "arcanearmory:void_obsidian_fragment",
    ]);
    await expectDrops(ctx, "arcanearmory:voidium_pickaxe", "arcanearmory:deepslate_void_obsidian_fragment_ore", [
      "arcanearmory:void_obsidian_fragment",
    ]);
  });

  test("storage blocks, raw blocks, and Doomflare drop themselves when mined with a pickaxe", async (ctx) => {
    await prepare(ctx);

    for (const block of ["arcanearmory:ruby_block", "arcanearmory:raw_titanium_block", "arcanearmory:doomflare_block"]) {
      await expectDrops(ctx, "minecraft:iron_pickaxe", block, [block]);
    }
  });

  test("Arcanthe grows on grass, breaks by hand, and goes in a flower pot", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.assert(`/setblock ${MINED.x} ${MINED.y - 1} ${MINED.z} minecraft:grass_block`);
    await expectDrops(ctx, "minecraft:air", "arcanearmory:arcanthe", ["arcanearmory:arcanthe"]);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.assert(`/setblock ${MINED.x} ${MINED.y} ${MINED.z} minecraft:flower_pot`);
    await ctx.commands.assert("/item replace entity @s weapon.mainhand with arcanearmory:arcanthe");
    await ctx.player.inventory().waitForItem("arcanearmory:arcanthe", { selected: true, timeout: "2s" });
    await ctx.player.teleport({ x: MINED.x - 1 + 0.5, y: MINED.y - 1, z: MINED.z + 0.5 });
    await ctx.player.lookAt({ x: MINED.x + 0.5, y: MINED.y + 0.3, z: MINED.z + 0.5 });
    await ctx.player.useBlockServer(MINED);
    const potted = await ctx.world.block(MINED);
    if (potted.id !== "arcanearmory:potted_arcanthe") {
      throw new Error(`Using Arcanthe on a flower pot left ${potted.id}`);
    }

    await expectDrops(ctx, "minecraft:air", "arcanearmory:potted_arcanthe", ["arcanearmory:arcanthe", "minecraft:flower_pot"], {
      placed: true,
    });
  });

  test("Doomflare caught in an explosion detonates with ten times the force of TNT", async (ctx) => {
    await prepare(ctx);

    // A TNT-sized blast one block from the core cannot reach a marker twelve blocks away; Doomflare's can.
    const marker = await blastPastCore(ctx, "minecraft:iron_block");
    if (marker !== "minecraft:stone") {
      throw new Error(`A TNT-sized blast alone should not reach the marker, found ${marker}`);
    }
    const doomflareMarker = await blastPastCore(ctx, "arcanearmory:doomflare_block");
    if (doomflareMarker !== "minecraft:air") {
      throw new Error(`Doomflare should have detonated and destroyed the marker, found ${doomflareMarker}`);
    }
  });
});

describe("Arcane Armory world generation", () => {
  test("Overworld ores and the amber geode generate in freshly generated chunks", async (ctx) => {
    await prepare(ctx);

    // Some areas lack a rare ore or a geode with amber in it, so a miss gets more fresh areas.
    for (const material of [...OVERWORLD_ORES, "amber"]) {
      let found = false;
      for (let area = 0; area < 6 && !found; area++) {
        found = await scanFreshChunks(ctx, "minecraft:overworld", freshX(), 4096, OVERWORLD_LAYERS, async (fill) =>
          (await ctx.commands.run(`${fill} minecraft:stone replace arcanearmory:${material}_ore`, { requireSuccess: false })).success === true
          || (await ctx.commands.run(`${fill} minecraft:stone replace arcanearmory:deepslate_${material}_ore`, { requireSuccess: false })).success === true);
      }
      if (!found) {
        throw new Error(`No ${material} ore generated in six fresh Overworld areas`);
      }
    }
  });

  test("Black Diamond generates only deep down, and Star Corundum not underground at all", async (ctx) => {
    await prepare(ctx);

    // Veins reach a little past the Y -32 they start below.
    let deep = false;
    for (let area = 0; area < 3 && !deep; area++) {
      const x = freshX();
      deep = await scanFreshChunks(ctx, "minecraft:overworld", x, 4096, [[-64, -33]], async (fill) =>
        (await ctx.commands.run(`${fill} minecraft:deepslate replace arcanearmory:deepslate_black_diamond_ore`, { requireSuccess: false })).success === true);
      const shallow = await scanFreshChunks(ctx, "minecraft:overworld", x, 4096, [[-24, 7], [8, 39]], async (fill) =>
        (await ctx.commands.run(`${fill} minecraft:stone replace arcanearmory:black_diamond_ore`, { requireSuccess: false })).success === true
        || (await ctx.commands.run(`${fill} minecraft:deepslate replace arcanearmory:deepslate_black_diamond_ore`, { requireSuccess: false })).success === true);
      if (shallow) {
        throw new Error(`Black Diamond ore generated above Y -24 at ${x}, 4096`);
      }
    }
    if (!deep) {
      throw new Error("No Black Diamond ore generated below Y -32 in three fresh Overworld areas");
    }

    for (let area = 0; area < 2; area++) {
      const corundum = await scanFreshChunks(ctx, "minecraft:overworld", freshX(), 4096, OVERWORLD_LAYERS, async (fill) =>
        (await ctx.commands.run(`${fill} minecraft:stone replace arcanearmory:star_corundum_ore`, { requireSuccess: false })).success === true
        || (await ctx.commands.run(`${fill} minecraft:deepslate replace arcanearmory:deepslate_star_corundum_ore`, { requireSuccess: false })).success === true);
      if (corundum) {
        throw new Error("Star Corundum ore generated underground");
      }
    }
  });

  // `/place` arrives in 1.19.
  test("every Overworld ore feature and the amber geode place their blocks", { target: { minecraft: ">=1.19" } }, async (ctx) => {
    await prepare(ctx);

    for (const material of [...OVERWORLD_ORES, ...BIOME_ORES]) {
      await ctx.commands.run("/fill 20 140 20 28 148 28 minecraft:stone");
      await placeFeature(ctx, `arcanearmory:${material}_ore`, "24 144 24");
      await ctx.commands.assert(`/fill 20 140 20 28 148 28 minecraft:air replace arcanearmory:${material}_ore`);
      await ctx.commands.run("/fill 20 140 20 28 148 28 minecraft:air");
    }

    await ctx.commands.run("/fill 20 140 20 28 148 28 minecraft:deepslate");
    await placeFeature(ctx, "arcanearmory:ruby_ore", "24 144 24");
    await ctx.commands.assert("/fill 20 140 20 28 148 28 minecraft:air replace arcanearmory:deepslate_ruby_ore");
    await ctx.commands.run("/fill 20 140 20 28 148 28 minecraft:air");

    // Geodes refuse to form in open air, like vanilla's. Amber ore is the geode's alternate inner
    // layer, so a single geode can lack it; grow a few.
    let amber = false;
    for (let attempt = 0; attempt < 6 && !amber; attempt++) {
      await ctx.commands.run("/fill 12 158 12 36 182 36 minecraft:stone");
      await placeFeature(ctx, "arcanearmory:amber_geode", "24 170 24");
      const found = await ctx.commands.run("/fill 12 158 12 36 182 36 minecraft:air replace arcanearmory:amber_ore", {
        requireSuccess: false,
      });
      amber = found.success === true;
    }
    await ctx.commands.run("/fill 12 158 12 36 182 36 minecraft:air");
    if (!amber) {
      throw new Error("Six amber geodes formed without amber ore");
    }
  });

  test("Nether and End ores generate in freshly generated chunks", async (ctx) => {
    await prepare(ctx);

    // The test world persists, so scan Nether chunks no earlier run has generated. Some biomes
    // offer little base stone, so a material missing from one area gets a second fresh area.
    const nether = ["bloodfire_garnet", "doom_fragment", "shadow_crystal"];
    for (const material of nether) {
      let found = false;
      for (let area = 0; area < 3 && !found; area++) {
        found = await generatedOreFound(ctx, "minecraft:the_nether", freshX(), 0, material);
      }
      if (!found) {
        throw new Error(`No ${material} ore generated in three fresh Nether areas`);
      }
    }
    await expectGeneratedOres(ctx, "minecraft:the_end", 32, -32, ["void_obsidian_fragment"]);
  });
});

describe("Arcane Armory loot and trades", () => {
  test("treasure chests hold Arcane tools and armor but never hammers, bows, or shields", async (ctx) => {
    await prepare(ctx);

    const items = await lootFrom(ctx, "minecraft:chests/end_city_treasure", 6);
    const gear = items.filter((id) => /_(sword|pickaxe|axe|shovel|hoe|helmet|chestplate|leggings|boots)$/.test(id));
    if (gear.length === 0) {
      throw new Error(`Expected Arcane gear in End city chests, found ${items.join(", ") || "nothing"}`);
    }
    assertNoNonLootGear(items);
  });

  test("ordinary chests hold Overworld materials only", async (ctx) => {
    await prepare(ctx);

    const items = await lootFrom(ctx, "minecraft:chests/simple_dungeon", 30);
    const overworld = new Set<string>(OVERWORLD_ORES.map((material) => material === "coolpper" || material === "titanium"
      ? `arcanearmory:${material}_ingot`
      : `arcanearmory:${material}`));
    if (!items.some((id) => overworld.has(id))) {
      throw new Error(`Expected Overworld materials in dungeon chests, found ${items.join(", ") || "nothing"}`);
    }
    const forbidden = items.filter((id) => [
      "arcanearmory:doom_fragment",
      "arcanearmory:void_obsidian_fragment",
      "arcanearmory:bloodfire_garnet",
      "arcanearmory:shadow_crystal",
      "arcanearmory:amber",
      "arcanearmory:arcanthium_ingot",
      "arcanearmory:voidium_ingot",
    ].includes(id));
    if (forbidden.length > 0) {
      throw new Error(`Only Overworld materials belong in chests, found ${forbidden.join(", ")}`);
    }
    assertNoNonLootGear(items);
  });

  test("chest gear is sometimes enchanted", async (ctx) => {
    await prepare(ctx);
    const enchantments = atLeast(await minecraftVersion(ctx), "1.20.5")
      ? 'components:{"minecraft:enchantments":{}}'
      : "tag:{Enchantments:[{}]}";

    const items = await lootFrom(ctx, "minecraft:chests/abandoned_mineshaft", 120, { keep: true });
    const gear = [...new Set(items.filter((id) => id.startsWith("arcanearmory:") && /_(sword|pickaxe|axe|shovel|hoe|helmet|chestplate|leggings|boots)$/.test(id)))];
    let enchanted = 0;
    for (const id of gear) {
      const result = await ctx.commands.run(
        `/execute if entity @e[type=minecraft:item,nbt={Item:{id:"${id}",${enchantments}}}]`,
        { requireSuccess: false },
      );
      if (result.success) {
        enchanted++;
      }
    }
    await ctx.commands.run("/kill @e[type=minecraft:item]");
    if (enchanted === 0) {
      throw new Error(`Expected enchanted Arcane gear among ${gear.join(", ") || "no gear"}`);
    }
  });

  test("novice weaponsmiths can sell a Coolpper Axe for two emeralds", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/kill @e[type=minecraft:villager]");
    await ctx.commands.run("/fill 9 71 3 23 71 7 minecraft:stone");
    await ctx.commands.run("/fill 9 72 3 23 75 7 minecraft:air");
    // A novice weaponsmith offers two of its level-one trades, so check a dozen of them.
    for (let index = 0; index < 12; index++) {
      await ctx.commands.assert(
        `/summon minecraft:villager ${10.5 + index} 72 6.5 {NoAI:1b,Silent:1b,VillagerData:{profession:"minecraft:weaponsmith",level:1,type:"minecraft:plains"}}`,
      );
    }
    // Villagers pick their offers the first time someone in reach tries to trade.
    const villagers = await ctx.entities.query({ origin: pos(16, 72, 6), radius: 12, type: "minecraft:villager" }).list();
    for (const villager of villagers) {
      const at = (await villager.inspect()).position;
      if (!at) {
        throw new Error(`Villager ${villager.id} has no position`);
      }
      await ctx.player.teleport({ x: at.x, y: 72, z: at.z - 2 });
      await ctx.player.lookAt({ x: at.x, y: at.y + 1.5, z: at.z });
      await ctx.player.useItemOnEntity(villager);
      await ctx.client.waitForFrames(5);
      await ctx.client.key(GLFW_KEY_ESCAPE);
      await ctx.client.waitForFrames(5);
    }
    const emeralds = atLeast(await minecraftVersion(ctx), "1.20.5") ? "count:2" : "Count:2b";
    await ctx.commands.assert(
      `/execute if entity @e[type=minecraft:villager,nbt={Offers:{Recipes:[{buy:{id:"minecraft:emerald",${emeralds}},sell:{id:"arcanearmory:coolpper_axe"}}]}}]`,
    );
    await ctx.commands.run("/kill @e[type=minecraft:villager]");
  });
});

async function prepare(ctx: TeaKitTestContext) {
  await ctx.player.reset({
    gameMode: "creative",
    health: 20,
    food: 20,
    saturation: 20,
    effects: "clear",
    inventory: "clear",
  });
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/tp @s 0.5 72 0.5");
  await loadTestArea(ctx);
  await ctx.commands.run("/fill -2 71 -2 8 71 2 minecraft:stone replace");
  await ctx.commands.run("/fill -2 72 -2 8 76 2 minecraft:air replace");
  // Clearing the area can cut nearby tree trunks, and the leaves left behind decay into stray drops.
  await ctx.commands.run("/fill -12 66 -12 20 90 12 minecraft:air replace #minecraft:leaves");
}

async function minecraftVersion(ctx: TeaKitTestContext): Promise<string> {
  return (await ctx.runtime.health()).minecraftVersion ?? "";
}

async function expectDrops(
  ctx: TeaKitTestContext,
  tool: string,
  block: string,
  expected: string[],
  options: { placed?: boolean } = {},
) {
  // Arrows left by the bow tests count as pickups too.
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/kill @e[type=minecraft:arrow]");
  await ctx.commands.run("/clear @s");
  await ctx.commands.run("/gamemode survival");
  await ctx.commands.assert(`/item replace entity @s weapon.mainhand with ${tool}`);
  if (!options.placed) {
    await ctx.commands.assert(`/setblock ${MINED.x} ${MINED.y} ${MINED.z} ${block}`);
  }
  await ctx.player.teleport({ x: MINED.x - 1 + 0.5, y: MINED.y - 1, z: MINED.z + 0.5 });
  await ctx.player.lookAt({ x: MINED.x + 0.5, y: MINED.y + 0.5, z: MINED.z + 0.5 });
  await ctx.player.mine(MINED, { timeout: "20s" });
  await ctx.runtime.wait(500);

  const dropped = [...new Set(await collectedItems(ctx, MINED, tool))].sort();
  const wanted = [...expected].sort();
  if (JSON.stringify(dropped) !== JSON.stringify(wanted)) {
    throw new Error(`Mining ${block} with ${tool} dropped [${dropped.join(", ")}], expected [${wanted.join(", ")}]`);
  }
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/clear @s");
}

// The player picks drops up almost at once, so count both the ground and the inventory, minus the tool.
async function collectedItems(ctx: TeaKitTestContext, at: BlockPos, tool: string): Promise<string[]> {
  const ground = (await ctx.loot.near(at, { radius: 4 }).list()).map(itemId);
  const inventory = await ctx.player.inventory();
  const toolId = tool.replace(/[[{].*$/, "");
  const held = inventory.items
    .filter((stack) => !(stack.slot === inventory.selectedSlot && stackId(stack) === toolId))
    .map(stackId);
  return [...ground, ...held].filter((id) => id !== "");
}

function stackId(stack: { id?: string; [key: string]: unknown }): string {
  const id = stack["itemId"] ?? stack.id;
  return typeof id === "string" ? id : "";
}

function silkTouch(version: string, tool: string): string {
  if (atLeast(version, "1.21.5")) {
    return `${tool}[minecraft:enchantments={"minecraft:silk_touch":1}]`;
  }
  if (atLeast(version, "1.20.5")) {
    return `${tool}[minecraft:enchantments={levels:{"minecraft:silk_touch":1}}]`;
  }
  return `${tool}{Enchantments:[{id:"minecraft:silk_touch",lvl:1s}]}`;
}

// Detonates a TNT-sized blast beside a core block, high in the air, and reports what is left of a 3x3
// stone wall twelve blocks away. A wall rather than one block, since explosion rays spread apart.
async function blastPastCore(ctx: TeaKitTestContext, core: string): Promise<string> {
  const origin = { x: 20, y: 200, z: 0 };
  const clear = `/fill ${origin.x - 2} ${origin.y - 2} ${origin.z - 2} ${origin.x + 16} ${origin.y + 2} ${origin.z + 2} minecraft:air`;
  await ctx.commands.run(clear);
  await ctx.commands.assert(`/setblock ${origin.x + 1} ${origin.y} ${origin.z} ${core}`);
  await ctx.commands.assert(`/fill ${origin.x + 13} ${origin.y - 1} ${origin.z - 1} ${origin.x + 13} ${origin.y + 1} ${origin.z + 1} minecraft:stone`);
  // Fuse was renamed fuse in 1.20.3; set both so the TNT detonates on its first tick.
  await ctx.commands.assert(`/summon minecraft:tnt ${origin.x} ${origin.y} ${origin.z} {Fuse:0s,fuse:0s,NoGravity:1b}`);
  await ctx.runtime.wait(1000);
  // Refilling the wall's air succeeds only if the blast broke some of it.
  const broken = await ctx.commands.run(
    `/fill ${origin.x + 13} ${origin.y - 1} ${origin.z - 1} ${origin.x + 13} ${origin.y + 1} ${origin.z + 1} minecraft:stone replace minecraft:air`,
    { requireSuccess: false },
  );
  await ctx.commands.run(clear);
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  return broken.success ? "minecraft:air" : "minecraft:stone";
}

async function placeFeature(ctx: TeaKitTestContext, feature: string, at: string) {
  // Size-3 ore veins (solarflare, frost diamond) usually place nothing, so allow many attempts.
  for (let attempt = 0; attempt < 40; attempt++) {
    const result = await ctx.commands.run(`/place feature ${feature} ${at}`, { requireSuccess: false });
    if (result.success) {
      return;
    }
  }
  throw new Error(`Could not place ${feature}`);
}

async function expectGeneratedOres(ctx: TeaKitTestContext, dimension: string, x: number, z: number, materials: string[]) {
  for (const material of materials) {
    if (!(await generatedOreFound(ctx, dimension, x, z, material))) {
      throw new Error(`No ${material} ore generated in ${dimension}`);
    }
  }
}

// Scans a 64x64 column of generated chunks. Ore is counted by swapping it for its deepslate form
// and back, so the world keeps its ores for the next run.
async function generatedOreFound(
  ctx: TeaKitTestContext,
  dimension: string,
  x: number,
  z: number,
  material: string,
): Promise<boolean> {
  // Swap the ore for its deepslate form and back, so the fresh chunks keep what generated.
  return scanFreshChunks(ctx, dimension, x, z, [[0, 31], [32, 63]], async (fill) => {
    const swapped = await ctx.commands.run(
      `${fill} arcanearmory:deepslate_${material}_ore replace arcanearmory:${material}_ore`,
      { requireSuccess: false },
    );
    if (swapped.success) {
      await ctx.commands.assert(`${fill} arcanearmory:${material}_ore replace arcanearmory:deepslate_${material}_ore`);
    }
    return swapped.success === true;
  });
}

// Generates the 64-by-64 area at x, z and runs `probe` on each 32-block cube of the given layers.
async function scanFreshChunks(
  ctx: TeaKitTestContext,
  dimension: string,
  x: number,
  z: number,
  layers: number[][],
  probe: (fill: string) => Promise<boolean>,
): Promise<boolean> {
  const inDimension = `/execute in ${dimension} run`;
  await ctx.commands.assert(`${inDimension} forceload add ${x} ${z} ${x + 63} ${z + 63}`);
  try {
    // `execute if loaded` arrives in 1.19.4. Block checks fail on unloaded chunks on every line.
    const corner = `${x + 63} 0 ${z + 63}`;
    let loaded = false;
    for (let attempt = 0; attempt < 60 && !loaded; attempt++) {
      loaded = (await ctx.commands.run(`/execute in ${dimension} if block ${corner} minecraft:air`, { requireSuccess: false })).success === true
        || (await ctx.commands.run(`/execute in ${dimension} unless block ${corner} minecraft:air`, { requireSuccess: false })).success === true;
      if (!loaded) {
        await ctx.runtime.wait(500);
      }
    }
    if (!loaded) {
      throw new Error(`Chunks in ${dimension} did not load`);
    }

    let found = false;
    for (const dx of [0, 32]) {
      for (const dz of [0, 32]) {
        for (const [low, high] of layers) {
          if (await probe(`${inDimension} fill ${x + dx} ${low} ${z + dz} ${x + dx + 31} ${high} ${z + dz + 31}`)) {
            found = true;
          }
        }
      }
    }
    return found;
  } finally {
    await ctx.commands.run(`${inDimension} forceload remove ${x} ${z} ${x + 63} ${z + 63}`);
  }
}

// The test world persists, so each scan picks chunks no earlier run has generated.
function freshX(): number {
  // Spread across most of the world so a run practically never reuses chunks generated by older code.
  return 4096 + 64 * Math.floor(Math.random() * 400000);
}

async function lootFrom(
  ctx: TeaKitTestContext,
  table: string,
  rolls: number,
  options: { keep?: boolean } = {},
): Promise<string[]> {
  const at: BlockPos = pos(20, 72, 20);
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/fill 14 71 14 26 71 26 minecraft:stone");
  for (let roll = 0; roll < rolls; roll++) {
    await ctx.commands.run(`/loot spawn ${at.x} ${at.y} ${at.z} loot ${table}`);
  }
  await ctx.runtime.wait(500);
  const items = (await ctx.loot.near(at, { radius: 12 }).list()).map(itemId).filter((id) => id.startsWith("arcanearmory:"));
  if (!options.keep) {
    await ctx.commands.run("/kill @e[type=minecraft:item]");
  }
  return items;
}

function assertNoNonLootGear(items: string[]) {
  const unexpected = items.filter((id) => NON_LOOT_GEAR.some((suffix) => id.endsWith(suffix)));
  if (unexpected.length > 0) {
    throw new Error(`Hammers, bows, and shields never appear in chests, found ${unexpected.join(", ")}`);
  }
}

function itemId(entity: { item?: string; itemId?: string }): string {
  return entity.item ?? entity.itemId ?? "";
}

function atLeast(version: string, minimum: string): boolean {
  const left = version.split(".").map((part) => Number.parseInt(part, 10));
  const right = minimum.split(".").map((part) => Number.parseInt(part, 10));
  const length = Math.max(left.length, right.length);

  for (let index = 0; index < length; index++) {
    const difference = (left[index] ?? 0) - (right[index] ?? 0);
    if (difference !== 0) return difference > 0;
  }

  return true;
}

// A normal world spawns the player away from the test area, so keep its chunks loaded.
async function loadTestArea(ctx: TeaKitTestContext) {
  await ctx.commands.run("/forceload add -16 -16 47 15");
  // A normal world spawns hostile mobs; a creeper blast once wrecked a mining test. 1.21.11 renamed the rule.
  await ctx.commands.run("/gamerule doMobSpawning false", { requireSuccess: false });
  await ctx.commands.run("/gamerule spawn_mobs false", { requireSuccess: false });
  await ctx.commands.run("/kill @e[type=!minecraft:player,distance=..96]", { requireSuccess: false });
  await ctx.commands.run("/kill @e[type=minecraft:item,distance=..96]", { requireSuccess: false });
  const corners = ["-16 0 -16", "47 0 -16", "-16 0 15", "47 0 15"];
  for (let attempt = 0; attempt < 60; attempt++) {
    let loaded = true;
    for (const corner of corners) {
      // Block checks fail on unloaded chunks, so one of these succeeds only once the chunk loads.
      loaded &&= (await ctx.commands.run(`/execute if block ${corner} minecraft:air`, { requireSuccess: false })).success === true
        || (await ctx.commands.run(`/execute unless block ${corner} minecraft:air`, { requireSuccess: false })).success === true;
    }
    if (loaded) {
      return;
    }
    await ctx.runtime.wait(250);
  }
  throw new Error("The test area did not load");
}
