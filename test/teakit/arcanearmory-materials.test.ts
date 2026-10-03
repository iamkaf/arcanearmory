import { Capability, Readiness, describe, pos, test } from "@teakit/test";
import type { BlockPos, ItemId, ScreenMenuSlotSnapshot, TeaKitTestContext } from "@teakit/test";

describe.configure({
  timeout: "8m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.ClientScreens,
    Capability.ClientScreenshot,
    Capability.PlayerInteractions,
    Capability.PlayerInventory,
    Capability.PlayerReset,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldBlock,
    Capability.WorldLoot,
    Capability.WorldRecipes,
  ],
});

const TEMPLATE = "arcanearmory:voidium_upgrade_smithing_template";
const TABLE: BlockPos = pos(2, 72, 0);

describe("Arcane trim materials", () => {
  test("Arcane gems and ingots trim armor at the smithing table", { target: { minecraft: ">=1.20" } }, async (ctx) => {
    await prepare(ctx);

    const trims: [ItemId, ItemId][] = [
      ["minecraft:diamond_chestplate", "arcanearmory:ruby"],
      ["arcanearmory:ruby_helmet", "arcanearmory:voidium_ingot"],
      ["minecraft:iron_boots", "arcanearmory:shadow_crystal"],
    ];
    for (const [armor, material] of trims) {
      await ctx.recipes.assertSmithingTransform(armor, armor, {
        templateItemId: "minecraft:coast_armor_trim_smithing_template",
        additionItemId: material,
      });
    }
  });

  test("Arcane trims show on worn armor and on Arcane armor icons", { target: { minecraft: ">=1.20" } }, async (ctx) => {
    await prepare(ctx);
    const modern = atLeast(await minecraftVersion(ctx), "1.20.5");
    const trim = (material: string) => {
      const data = `material:"arcanearmory:${material}",pattern:"minecraft:sentry"`;
      return modern ? `[minecraft:trim={${data}}]` : `{Trim:{${data}}}`;
    };

    await ctx.commands.run("/kill @e[type=minecraft:armor_stand]");
    await ctx.commands.assert("/summon minecraft:armor_stand 0.5 72 2.5 {Rotation:[180f,0f]}");
    const worn = [["head", "helmet", "aquamarine"], ["chest", "chestplate", "ruby"], ["legs", "leggings", "amber"], ["feet", "boots", "voidium"]];
    for (const [slot, piece, material] of worn) {
      await ctx.commands.assert(
        `/item replace entity @e[type=minecraft:armor_stand,limit=1,sort=nearest] armor.${slot} with arcanearmory:black_diamond_${piece}${trim(material)}`,
      );
    }
    for (const [index, material] of ["ruby", "sapphire", "solarflare_gem", "voidium"].entries()) {
      await ctx.commands.assert(`/item replace entity @s hotbar.${index} with arcanearmory:titanium_chestplate${trim(material)}`);
    }
    await ctx.commands.assert(`/item replace entity @s hotbar.4 with minecraft:diamond_chestplate${trim("ruby")}`);
    await ctx.player.teleport({ x: 0.5, y: 72, z: 0.5 });
    await ctx.player.lookAt({ x: 0.5, y: 73, z: 2.5 });
    // A test that runs first can still be behind the terrain loading screen.
    for (let attempt = 0; attempt < 40 && (await ctx.client.screen()).open; attempt++) {
      await ctx.runtime.wait(250);
    }
    await ctx.client.waitForFrames(10);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-arcane-trims-worn"));

    await ctx.commands.run("/gamemode survival");
    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-arcane-trims-icons"));
    await ctx.client.closeMenus();
    await ctx.commands.run("/kill @e[type=minecraft:armor_stand]");
    await assertNoClientResourceErrors(ctx, "Arcane trim rendering");
  });
});

describe("Voidium upgrade", () => {
  test("a smithing table upgrades Black Diamond gear to Voidium and keeps its enchantments", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);
    const templated = atLeast(version, "1.20");

    await ctx.commands.assert(`/setblock ${TABLE.x} ${TABLE.y} ${TABLE.z} minecraft:smithing_table`);
    await ctx.commands.run("/gamemode survival");
    await ctx.commands.assert(`/item replace entity @s hotbar.0 with ${sharpness(version, "arcanearmory:black_diamond_sword")}`);
    await ctx.commands.assert("/item replace entity @s hotbar.1 with arcanearmory:voidium_ingot");
    if (templated) {
      await ctx.commands.assert(`/item replace entity @s hotbar.2 with ${TEMPLATE}`);
    }
    await ctx.player.lookAt({ x: TABLE.x + 0.5, y: TABLE.y + 0.5, z: TABLE.z + 0.5 });
    await ctx.player.openBlock(TABLE);
    await ctx.client.waitForFrames(5);

    // Shift-clicking moves each input into the smithing slot that accepts it, as a player would.
    for (const item of [TEMPLATE, "arcanearmory:black_diamond_sword", "arcanearmory:voidium_ingot"]) {
      if (item === TEMPLATE && !templated) {
        continue;
      }
      const slot = (await ctx.client.screen()).menu().slots().find((candidate) => candidate.slot > 3 && slotItemId(candidate) === item);
      if (!slot) {
        throw new Error(`${item} is not in the smithing table's inventory slots`);
      }
      await (await ctx.client.screen()).menu().slot(slot.slot).click({ clickType: "QUICK_MOVE" });
      await ctx.client.waitForFrames(3);
    }
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-voidium-upgrade"));
    // Legacy smithing has no template slot, so its result sits one slot earlier.
    const resultSlot = templated ? 3 : 2;
    const result = (await ctx.client.screen()).menu().slots().find((slot) => slot.slot === resultSlot);
    if (slotItemId(result) !== "arcanearmory:voidium_sword") {
      throw new Error(`The smithing table offers ${slotItemId(result) || "nothing"}, expected a Voidium Sword`);
    }
    await (await ctx.client.screen()).menu().slot(resultSlot).click({ clickType: "QUICK_MOVE" });
    await ctx.client.waitForFrames(3);
    await ctx.client.closeMenus();

    const enchanted = atLeast(version, "1.21.5")
      ? '{id:"arcanearmory:voidium_sword",components:{"minecraft:enchantments":{"minecraft:sharpness":3}}}'
      : atLeast(version, "1.20.5")
        ? '{id:"arcanearmory:voidium_sword",components:{"minecraft:enchantments":{levels:{"minecraft:sharpness":3}}}}'
        : '{id:"arcanearmory:voidium_sword",tag:{Enchantments:[{id:"minecraft:sharpness",lvl:3s}]}}';
    await ctx.commands.assert(`/execute if entity @s[nbt={Inventory:[${enchanted}]}]`);
    await ctx.commands.run(`/setblock ${TABLE.x} ${TABLE.y} ${TABLE.z} minecraft:air`);
  });

  test("Voidium tools and armor no longer craft from ingots; the shield still does", async (ctx) => {
    await prepare(ctx);

    let crafted = true;
    try {
      await ctx.recipes.assertCrafting(1, 3, ["arcanearmory:voidium_ingot", "arcanearmory:voidium_ingot", "minecraft:stick"],
        "arcanearmory:voidium_sword");
    } catch {
      crafted = false;
    }
    if (crafted) {
      throw new Error("Voidium Swords should come only from the smithing table");
    }
    await ctx.recipes.assertCrafting(3, 3, [
      "minecraft:oak_planks", "arcanearmory:voidium_ingot", "minecraft:oak_planks",
      "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks",
      "minecraft:air", "minecraft:oak_planks", "minecraft:air",
    ], "arcanearmory:voidium_shield");
  });

  test("End city chests hold the template, and void obsidian duplicates it", { target: { minecraft: ">=1.20" } }, async (ctx) => {
    await prepare(ctx);

    // About one End city chest in seven holds a template, so sixty chests all but always show one.
    const items = await lootFrom(ctx, "minecraft:chests/end_city_treasure", 60);
    if (!items.includes(TEMPLATE)) {
      throw new Error(`Expected a Voidium Upgrade Smithing Template among ${items.join(", ") || "nothing"}`);
    }
    const fragment = "arcanearmory:void_obsidian_fragment";
    await ctx.recipes.assertCrafting(3, 3, [
      fragment, TEMPLATE, fragment,
      fragment, "minecraft:end_stone", fragment,
      fragment, fragment, fragment,
    ], TEMPLATE, { resultCount: 2 });
  });
});

describe("Ores with a place", () => {
  test("Solarflare Gem generates in deserts and Frost Diamond in snowy biomes, never the other way round", async (ctx) => {
    await prepare(ctx);

    await expectOre(ctx, "solarflare_gem", "minecraft:desert", true);
    await expectOre(ctx, "frost_diamond", "minecraft:snowy_plains", true);
    await expectOre(ctx, "frost_diamond", "minecraft:desert", false);
    await expectOre(ctx, "solarflare_gem", "minecraft:snowy_plains", false);
  });
});

// Scans freshly generated chunks around a biome for an ore. Small veins can miss one area, so a
// wanted ore gets three; an unwanted one must be absent from the one area scanned.
async function expectOre(ctx: TeaKitTestContext, ore: string, biome: string, wanted: boolean) {
  for (let area = 0; area < (wanted ? 3 : 1); area++) {
    const at = await freshBiomeArea(ctx, biome);
    // Found ore is replaced, since nothing reuses these chunks.
    const found = await scanFreshChunks(ctx, at.x, at.z, [[-64, -33], [-32, -1], [0, 31]], async (fill) =>
      (await ctx.commands.run(`${fill} minecraft:stone replace arcanearmory:${ore}_ore`, { requireSuccess: false })).success === true
      || (await ctx.commands.run(`${fill} minecraft:deepslate replace arcanearmory:deepslate_${ore}_ore`, { requireSuccess: false })).success === true);
    if (found && !wanted) {
      throw new Error(`${ore} ore generated near the ${biome} at ${at.x + 32}, ${at.z + 32}`);
    }
    if (found) {
      return;
    }
  }
  if (wanted) {
    throw new Error(`No ${ore} ore generated near three fresh ${biome} areas`);
  }
}

// Finds a biome far from the test area and from earlier runs, where the persistent world has no chunks yet,
// and returns the corner of a 64-block square centered on it.
async function freshBiomeArea(ctx: TeaKitTestContext, biome: string): Promise<{ x: number; z: number }> {
  const locate = atLeast(await minecraftVersion(ctx), "1.19") ? `locate biome ${biome}` : `locatebiome ${biome}`;
  const from = 8192 + 64 * Math.floor(Math.random() * 4096);
  const result = await ctx.commands.assert(`/execute positioned ${from} 64 ${-from} run ${locate}`, { captureOutput: true });
  const match = result.output.join(" ").match(/\[(-?\d+), *(?:~|-?\d+), *(-?\d+)\]/);
  if (!match) {
    throw new Error(`Could not read where ${biome} is from: ${result.output.join(" ")}`);
  }
  return { x: Number(match[1]) - 32, z: Number(match[2]) - 32 };
}

// Generates the 64-by-64 Overworld area at x, z and runs `probe` on each 32-block cube of the given layers.
async function scanFreshChunks(
  ctx: TeaKitTestContext,
  x: number,
  z: number,
  layers: number[][],
  probe: (fill: string) => Promise<boolean>,
): Promise<boolean> {
  await ctx.commands.assert(`/forceload add ${x} ${z} ${x + 63} ${z + 63}`);
  try {
    // `execute if loaded` arrives in 1.19.4. Block checks fail on unloaded chunks on every line.
    const corner = `${x + 63} 0 ${z + 63}`;
    let loaded = false;
    for (let attempt = 0; attempt < 60 && !loaded; attempt++) {
      loaded = (await ctx.commands.run(`/execute if block ${corner} minecraft:air`, { requireSuccess: false })).success === true
        || (await ctx.commands.run(`/execute unless block ${corner} minecraft:air`, { requireSuccess: false })).success === true;
      if (!loaded) {
        await ctx.runtime.wait(500);
      }
    }
    if (!loaded) {
      throw new Error(`Chunks at ${x}, ${z} did not load`);
    }

    let found = false;
    for (const dx of [0, 32]) {
      for (const dz of [0, 32]) {
        for (const [low, high] of layers) {
          if (await probe(`/fill ${x + dx} ${low} ${z + dz} ${x + dx + 31} ${high} ${z + dz + 31}`)) {
            found = true;
          }
        }
      }
    }
    return found;
  } finally {
    await ctx.commands.run(`/forceload remove ${x} ${z} ${x + 63} ${z + 63}`);
  }
}

async function lootFrom(ctx: TeaKitTestContext, table: string, rolls: number): Promise<string[]> {
  const at: BlockPos = pos(20, 72, 20);
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/fill 14 71 14 26 71 26 minecraft:stone");
  for (let roll = 0; roll < rolls; roll++) {
    await ctx.commands.run(`/loot spawn ${at.x} ${at.y} ${at.z} loot ${table}`);
  }
  await ctx.runtime.wait(500);
  const items = (await ctx.loot.near(at, { radius: 12 }).list()).map((entity) => entity.item ?? entity.itemId ?? "");
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  return items;
}

function sharpness(version: string, item: string): string {
  if (atLeast(version, "1.21.5")) {
    return `${item}[minecraft:enchantments={"minecraft:sharpness":3}]`;
  }
  if (atLeast(version, "1.20.5")) {
    return `${item}[minecraft:enchantments={levels:{"minecraft:sharpness":3}}]`;
  }
  return `${item}{Enchantments:[{id:"minecraft:sharpness",lvl:3s}]}`;
}

function slotItemId(slot: ScreenMenuSlotSnapshot | undefined): string {
  const item = slot?.item as (Record<string, unknown> & { id?: string }) | null | undefined;
  const id = item?.["itemId"] ?? item?.id;
  return typeof id === "string" ? id : "";
}

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
  await ctx.commands.run("/time set noon");
  await ctx.commands.run("/weather clear");
  await ctx.commands.run("/tp @s 0.5 72 0.5");
  await loadTestArea(ctx);
  await ctx.commands.run("/fill -3 71 -3 8 71 3 minecraft:stone replace");
  await ctx.commands.run("/fill -3 72 -3 8 76 3 minecraft:air replace");
}

async function assertNoClientResourceErrors(ctx: TeaKitTestContext, phase: string) {
  const text = await ctx.logs.text({ limit: 12000 });
  const failures = text
    .split(/\r?\n/)
    .filter((line) => /missing item model|missing model|missing texture|unable to load model|could not load model|failed to load model|filenotfound|palette/i.test(line));
  if (failures.length > 0) {
    throw new Error(`Client resource errors after ${phase}:\n${failures.join("\n")}`);
  }
}

async function minecraftVersion(ctx: TeaKitTestContext): Promise<string> {
  return (await ctx.runtime.health()).minecraftVersion ?? "";
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
