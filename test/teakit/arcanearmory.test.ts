import { Capability, Readiness, describe, pos, test } from "@teakit/test";
import type { BlockPos, TeaKitTestContext } from "@teakit/test";

const materials = [
  "ruby",
  "sapphire",
  "frost_diamond",
  "black_diamond",
  "topaz",
  "chrysoberyl",
  "aquamarine",
  "star_corundum",
  "doom_fragment",
  "void_obsidian_fragment",
  "solarflare_gem",
  "bloodfire_garnet",
  "aetheric_crystal",
  "shadow_crystal",
  "coolpper",
  "titanium",
  "amber",
  "arcanthium",
  "voidium",
] as const;

const ingots = new Set(["coolpper", "titanium", "arcanthium", "voidium"]);
const tools = new Set([
  "ruby",
  "sapphire",
  "black_diamond",
  "topaz",
  "chrysoberyl",
  "aquamarine",
  "star_corundum",
  "bloodfire_garnet",
  "aetheric_crystal",
  "coolpper",
  "titanium",
  "amber",
  "arcanthium",
  "voidium",
]);
const shields = new Set(["ruby", "coolpper", "titanium", "arcanthium", "voidium"]);

describe.configure({
  timeout: "8m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.PlayerInteractions,
    Capability.ClientScreen,
    Capability.PlayerDriver,
    Capability.PlayerInventory,
    Capability.PlayerReset,
    Capability.PlayerUseItem,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldBlock,
    Capability.WorldEntities,
    Capability.WorldFill,
    Capability.WorldLoot,
    Capability.WorldRecipes,
  ],
});

describe("Arcane Armory registry parity", () => {
  test("materials, blocks, and owned item families resolve", async (ctx) => {
    await prepare(ctx);

    for (const material of materials) {
      const materialItem = ingots.has(material) ? `${material}_ingot` : material;
      await ctx.commands.assert(`/give @s arcanearmory:${materialItem} 1`);
      await ctx.commands.assert(`/setblock 0 72 0 arcanearmory:${material}_block`);
      await ctx.commands.assert(`/execute if block 0 72 0 arcanearmory:${material}_block`);
      await ctx.commands.run("/setblock 0 72 0 minecraft:air");
    }

    for (const material of tools) {
      await ctx.commands.assert(`/give @s arcanearmory:${material}_sword 1`);
      await ctx.commands.assert(`/give @s arcanearmory:${material}_hammer 1`);
      await ctx.commands.assert(`/give @s arcanearmory:${material}_bow 1`);
      await ctx.commands.assert(`/item replace entity @s armor.head with arcanearmory:${material}_helmet`);
    }

    for (const material of shields) {
      await ctx.commands.assert(`/give @s arcanearmory:${material}_shield 1`);
    }

    await ctx.commands.assert("/give @s arcanearmory:doomflare_block 1");
    await ctx.commands.assert("/give @s arcanearmory:arcanthe 1");
  });

  test("alloy, compression, smelting, and ice recipes resolve through the recipe manager", async (ctx) => {
    await prepare(ctx);

    await ctx.recipes.assertCrafting(2, 1, ["minecraft:iron_ingot", "arcanearmory:raw_amber"], "arcanearmory:amber_ingot", {
      resultCount: 2,
    });
    // Wither roses count as Arcanthe, the only survival source for arcanthium.
    await ctx.recipes.assertCrafting(
      2,
      2,
      ["arcanearmory:titanium_ingot", "arcanearmory:aetheric_crystal", "minecraft:wither_rose", "minecraft:pink_dye"],
      "arcanearmory:arcanthium_ingot",
      { resultCount: 2 },
    );
    await ctx.recipes.assertCrafting(
      3,
      2,
      [
        "arcanearmory:doom_fragment",
        "arcanearmory:solarflare_gem",
        "arcanearmory:shadow_crystal",
        "arcanearmory:aetheric_crystal",
        "minecraft:obsidian",
        "minecraft:air",
      ],
      "arcanearmory:doomflare_block",
    );
    await ctx.recipes.assertCrafting(
      3,
      1,
      ["arcanearmory:void_obsidian_fragment", "arcanearmory:bloodfire_garnet", "minecraft:netherite_ingot"],
      "arcanearmory:voidium_ingot",
      { resultCount: 2 },
    );
    await ctx.recipes.assertCrafting(1, 1, ["arcanearmory:coolpper_ore"], "arcanearmory:coolpper_ingot", { resultCount: 2 });
    await ctx.recipes.assertCrafting(1, 1, ["arcanearmory:frost_diamond"], "minecraft:ice", { resultCount: 16 });
    await ctx.recipes.assertCrafting(3, 3, Array(9).fill("arcanearmory:ruby"), "arcanearmory:ruby_block");
    await ctx.recipes.assertCrafting(1, 1, ["arcanearmory:ruby_block"], "arcanearmory:ruby", { resultCount: 9 });
    await ctx.recipes.assertCrafting(3, 3, Array(9).fill("arcanearmory:raw_titanium"), "arcanearmory:raw_titanium_block");
    await ctx.recipes.assertCooking("minecraft:smelting", "arcanearmory:raw_titanium", "arcanearmory:titanium_ingot");
    await ctx.recipes.assertCooking("minecraft:smelting", "arcanearmory:deepslate_ruby_ore", "arcanearmory:ruby");
    // TeaKit asserts only smelting, so run a real blast furnace for the 100-tick blasting recipe.
    await ctx.commands.run("/setblock 14 73 0 minecraft:blast_furnace[facing=north]");
    await ctx.commands.assert("/item replace block 14 73 0 container.0 with arcanearmory:deepslate_ruby_ore 1");
    await ctx.commands.assert("/item replace block 14 73 0 container.1 with minecraft:coal 1");
    await ctx.runtime.wait(6000);
    await ctx.commands.assert('/execute if data block 14 73 0 Items[{Slot:2b,id:"arcanearmory:ruby"}]');
    await ctx.commands.run("/setblock 14 73 0 minecraft:air");
    await ctx.recipes.assertCrafting(
      3,
      3,
      [
        "minecraft:oak_planks", "arcanearmory:ruby", "minecraft:oak_planks",
        "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks",
        "minecraft:air", "minecraft:oak_planks", "minecraft:air",
      ],
      "arcanearmory:ruby_shield",
    );
    await ctx.recipes.assertCrafting(
      3,
      3,
      [
        "arcanearmory:topaz", "arcanearmory:topaz", "arcanearmory:topaz",
        "arcanearmory:topaz", "arcanearmory:topaz", "arcanearmory:topaz",
        "minecraft:air", "minecraft:stick", "minecraft:air",
      ],
      "arcanearmory:topaz_hammer",
    );
  });

  test("hammers mine a 3x3 plane around the target block", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.world.fill({ x: 4, y: 72, z: -1 }, { x: 4, y: 74, z: 1 }, "minecraft:stone");
    // Dirt is not hammer material, so the hammer leaves it standing.
    await ctx.world.setBlock({ x: 4, y: 74, z: 1 }, "minecraft:dirt");
    await ctx.player.teleport({ x: 1.5, y: 73, z: 0.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_hammer");
    await ctx.player.inventory().selectHotbar(0);
    await assertItem(ctx, "hotbar.0", "arcanearmory:ruby_hammer", 'Inventory:[{Slot:0b,id:"arcanearmory:ruby_hammer"}]');
    await assertItem(ctx, "weapon.mainhand", "arcanearmory:ruby_hammer", 'SelectedItem:{id:"arcanearmory:ruby_hammer"}');
    await ctx.player.lookAt({ x: 4.5, y: 73.5, z: 0.5 });

    await ctx.player.mine(pos(4, 73, 0), { timeout: "8s" });
    await ctx.runtime.wait(300);

    for (let y = 72; y <= 74; y++) {
      for (let z = -1; z <= 1; z++) {
        const state = await ctx.world.block({ x: 4, y, z });
        const expected = y === 74 && z === 1 ? "minecraft:dirt" : "minecraft:air";
        if (state.id !== expected) {
          throw new Error(`Expected hammer to leave ${expected} at 4 ${y} ${z}, found ${state.id}`);
        }
      }
    }
    // One point of wear for each block mined: the target and its seven stone neighbours.
    await assertHeldDamage(ctx, version, "arcanearmory:ruby_hammer", 8);
    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
  });

  test("hammers mine the plane facing the player", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.assert("/item replace entity @s weapon.mainhand with arcanearmory:ruby_hammer");

    // Mining downward clears the floor plane under the player.
    await ctx.world.fill({ x: 3, y: 72, z: -1 }, { x: 5, y: 72, z: 1 }, "minecraft:stone");
    await ctx.world.fill({ x: 3, y: 73, z: -1 }, { x: 5, y: 74, z: 1 }, "minecraft:air");
    await ctx.player.teleport({ x: 4.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 4.5, y: 72.5, z: 0.5 });
    // The server ignores the new view until the client confirms the teleport, and the hammer reads it.
    await ctx.runtime.wait(500);
    await ctx.player.mine(pos(4, 72, 0), { timeout: "8s" });
    await ctx.runtime.wait(300);
    for (let x = 3; x <= 5; x++) {
      for (let z = -1; z <= 1; z++) {
        const state = await ctx.world.block({ x, y: 72, z });
        if (state.id !== "minecraft:air") {
          throw new Error(`Expected the floor plane to be cleared at ${x} 72 ${z}, found ${state.id}`);
        }
      }
    }

    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
  });

  test("hammer drops use the hammer's enchantments and tier", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
    await ctx.world.fill({ x: 4, y: 72, z: -1 }, { x: 4, y: 74, z: 1 }, "arcanearmory:ruby_ore");
    await ctx.commands.assert(`/item replace entity @s weapon.mainhand with ${silkTouch(version, "arcanearmory:ruby_hammer")}`);
    await ctx.player.teleport({ x: 1.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 4.5, y: 73.5, z: 0.5 });
    await ctx.player.mine(pos(4, 73, 0), { timeout: "12s" });
    await ctx.runtime.wait(500);
    const silkCount = await collectedCount(ctx, pos(4, 73, 0), "arcanearmory:ruby_ore");
    if (silkCount !== 9) {
      throw new Error(`A Silk Touch hammer should drop all nine ores, dropped ${silkCount}`);
    }

    // An aetheric (stone tier) hammer cannot harvest diamond ore, so it leaves the neighbours alone.
    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
    await ctx.world.fill({ x: 4, y: 72, z: -1 }, { x: 4, y: 74, z: 1 }, "minecraft:diamond_ore");
    await ctx.world.setBlock({ x: 4, y: 73, z: 0 }, "minecraft:stone");
    await ctx.commands.assert("/item replace entity @s weapon.mainhand with arcanearmory:aetheric_crystal_hammer");
    await ctx.player.mine(pos(4, 73, 0), { timeout: "8s" });
    await ctx.runtime.wait(300);
    if ((await ctx.world.block({ x: 4, y: 74, z: 0 })).id !== "minecraft:diamond_ore") {
      throw new Error("A stone-tier hammer should not break diamond ore around the target");
    }
    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
  });

  test("armor equips by use and pickaxes mine stone as tools", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_helmet");
    await ctx.player.inventory().selectHotbar(0);
    await ctx.player.useItem();
    await assertItem(ctx, "armor.head", "arcanearmory:ruby_helmet", 'Inventory:[{Slot:103b,id:"arcanearmory:ruby_helmet"}]');

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/setblock 5 73 0 minecraft:stone");
    await ctx.player.teleport({ x: 4.5, y: 72, z: 0.5 });
    await ctx.player.lookAt({ x: 5.5, y: 73.5, z: 0.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_pickaxe");
    await ctx.player.inventory().selectHotbar(0);
    await ctx.player.mine(pos(5, 73, 0), { timeout: "6s" });
    const state = await ctx.world.block({ x: 5, y: 73, z: 0 });
    if (state.id !== "minecraft:air") {
      throw new Error(`Expected pickaxe to mine stone, found ${state.id}`);
    }
  });

  test("representative materials retain distinct combat and armor attributes", async (ctx) => {
    await prepare(ctx);

    const version = (await ctx.runtime.health()).minecraftVersion ?? "";
    const modernAttributeIds = atLeast(version, "1.21.11");
    const attackDamage = modernAttributeIds ? "minecraft:attack_damage" : "minecraft:generic.attack_damage";
    const armor = modernAttributeIds ? "minecraft:armor" : "minecraft:generic.armor";
    const toughness = modernAttributeIds ? "minecraft:armor_toughness" : "minecraft:generic.armor_toughness";
    const cases = [
      { material: "aetheric_crystal", attack: 50, armor: 70, toughness: 0 },
      { material: "ruby", attack: 70, armor: 200, toughness: 0 },
      { material: "voidium", attack: 90, armor: 260, toughness: 80 },
    ] as const;

    await ctx.commands.run("/scoreboard objectives remove aa_stats", { requireSuccess: false });
    await ctx.commands.assert("/scoreboard objectives add aa_stats dummy");

    for (const expected of cases) {
      await ctx.commands.batch([
        `/item replace entity @s weapon.mainhand with arcanearmory:${expected.material}_sword`,
        `/item replace entity @s armor.head with arcanearmory:${expected.material}_helmet`,
        `/item replace entity @s armor.chest with arcanearmory:${expected.material}_chestplate`,
        `/item replace entity @s armor.legs with arcanearmory:${expected.material}_leggings`,
        `/item replace entity @s armor.feet with arcanearmory:${expected.material}_boots`,
      ]);
      await ctx.runtime.wait(100);

      await assertAttribute(ctx, attackDamage, expected.attack);
      await assertAttribute(ctx, armor, expected.armor);
      await assertAttribute(ctx, toughness, expected.toughness);
    }

    await ctx.commands.run("/scoreboard objectives remove aa_stats", { requireSuccess: false });
  });

  test("aetheric tools retain low durability and stone-tier harvesting", async (ctx) => {
    await prepare(ctx);

    const version = (await ctx.runtime.health()).minecraftVersion ?? "";
    const pickaxe = atLeast(version, "1.21.1")
      ? "arcanearmory:aetheric_crystal_pickaxe[minecraft:damage=57]"
      : "arcanearmory:aetheric_crystal_pickaxe{Damage:57}";

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.assert(`/item replace entity @s weapon.mainhand with ${pickaxe}`);
    await ctx.player.teleport({ x: 4.5, y: 72, z: 0.5 });

    await ctx.commands.run("/setblock 5 73 0 minecraft:stone");
    await ctx.player.lookAt({ x: 5.5, y: 73.5, z: 0.5 });
    await ctx.player.mine(pos(5, 73, 0), { timeout: "6s" });
    await assertHeldDamage(ctx, version, "arcanearmory:aetheric_crystal_pickaxe", 58);

    await ctx.commands.run("/setblock 5 73 0 minecraft:stone");
    await ctx.player.mine(pos(5, 73, 0), { timeout: "6s" });
    await ctx.player.inventory().waitForItemAbsent("arcanearmory:aetheric_crystal_pickaxe", {
      selected: true,
      timeout: "2s",
    });

    await ctx.commands.run("/kill @e[type=minecraft:item,distance=..16]");
    await ctx.commands.assert("/item replace entity @s weapon.mainhand with arcanearmory:aetheric_crystal_pickaxe");
    await ctx.commands.run("/setblock 5 73 0 minecraft:diamond_ore");
    await ctx.player.mine(pos(5, 73, 0), { timeout: "12s" });
    await ctx.runtime.wait(200);
    const forbiddenLoot = await ctx.loot.near(pos(5, 73, 0), { item: "minecraft:diamond", radius: 4 }).list();
    if (forbiddenLoot.length > 0) {
      throw new Error(`Aetheric pickaxe harvested diamond-tier loot: ${JSON.stringify(forbiddenLoot)}`);
    }
  });

  test("custom shields block incoming projectiles", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/effect clear @s");
    await ctx.commands.run("/effect give @s minecraft:instant_health 1 10 true");
    await ctx.commands.run("/effect give @s minecraft:saturation 1 10 true");
    await ctx.commands.assert("/item replace entity @s weapon.offhand with arcanearmory:ruby_shield");
    await assertItem(ctx, "weapon.offhand", "arcanearmory:ruby_shield", 'Inventory:[{Slot:-106b,id:"arcanearmory:ruby_shield"}]');
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..12]");
    await ctx.player.teleport({ x: 8.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 8.5, y: 73.5, z: 3.5 });

    await ctx.player.holdUse(true);
    await ctx.runtime.wait(1500);
    await ctx.commands.batch([
      "/setblock 8 73 3 minecraft:dispenser[facing=north]",
      "/item replace block 8 73 3 container.0 with minecraft:arrow 1",
      "/setblock 8 73 4 minecraft:redstone_block",
    ]);
    await ctx.runtime.wait(1000);
    await ctx.player.holdUse(false);

    await ctx.commands.assert("/execute if entity @s[nbt={Health:20.0f}]");
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..12]");
    await ctx.commands.run("/setblock 8 73 3 minecraft:air");
    await ctx.commands.run("/setblock 8 73 4 minecraft:air");
  });

  test("material bows preserve their configured projectile damage", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
    await ctx.commands.run("/fill 10 71 -2 22 71 18 minecraft:stone replace");
    await ctx.commands.run("/fill 10 72 -2 22 82 18 minecraft:air replace");
    await ctx.player.teleport({ x: 12.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 12.5, y: 78, z: 18.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:voidium_bow");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with minecraft:arrow 8");
    await ctx.player.inventory().selectHotbar(0);
    await assertItem(ctx, "weapon.mainhand", "arcanearmory:voidium_bow", 'SelectedItem:{id:"arcanearmory:voidium_bow"}');

    await ctx.player.holdUse(true);
    await ctx.runtime.wait(1600);
    await ctx.player.holdUse(false);
    await ctx.entities.query({
      origin: await ctx.player.position(),
      radius: 80,
      type: "minecraft:arrow",
    }).waitForCountAtLeast(1, { timeout: "3s", interval: "50ms" });

    await assertNearestArrowDamage(ctx, 3333, 3334);
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
  });

  test("solarflare gems burn eight times longer than coal and their blocks ten times longer still", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);

    for (const [fuel, burnTime] of [["arcanearmory:solarflare_gem", 2400], ["arcanearmory:solarflare_gem_block", 24000]] as const) {
      await ctx.commands.run("/setblock 14 73 0 minecraft:air");
      await ctx.commands.run("/setblock 14 73 0 minecraft:furnace[facing=north]");
      await ctx.commands.assert("/item replace block 14 73 0 container.0 with minecraft:sand 1");
      await ctx.commands.assert(`/item replace block 14 73 0 container.1 with ${fuel} 1`);
      await ctx.runtime.wait(1500);

      await ctx.commands.assert("/execute if block 14 73 0 minecraft:furnace[lit=true]");
      await assertFurnaceFuelDuration(ctx, version, burnTime);
    }
    await ctx.commands.run("/setblock 14 73 0 minecraft:air");
  });

  test("bows reach full power after a one-second draw and wear by one per shot", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/fill 10 71 -2 22 71 18 minecraft:stone replace");
    await ctx.commands.run("/fill 10 72 -2 22 82 18 minecraft:air replace");
    await ctx.player.teleport({ x: 12.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 12.5, y: 78, z: 18.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_bow");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with minecraft:arrow 8");
    await ctx.player.inventory().selectHotbar(0);

    for (const [drawMs, critical] of [[1300, true], [300, false]] as const) {
      await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
      await ctx.player.holdUse(true);
      await ctx.runtime.wait(drawMs);
      await ctx.player.holdUse(false);
      await ctx.entities.query({ origin: await ctx.player.position(), radius: 80, type: "minecraft:arrow" })
        .waitForCountAtLeast(1, { timeout: "3s", interval: "50ms" });
      const crit = await ctx.commands.run(
        "/execute if entity @e[type=minecraft:arrow,distance=..80,limit=1,sort=nearest,nbt={crit:1b}]",
        { requireSuccess: false },
      );
      if (crit.success !== critical) {
        throw new Error(`A ${drawMs}ms draw should ${critical ? "" : "not "}fire a full-power arrow`);
      }
    }
    await assertHeldDamage(ctx, version, "arcanearmory:ruby_bow", 2);
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
  });

  test("axes disable Arcane shields", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/difficulty normal");
    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/kill @e[type=minecraft:vindicator]");
    await ctx.commands.assert("/item replace entity @s weapon.offhand with arcanearmory:titanium_shield");
    await ctx.player.teleport({ x: 4.5, y: 72, z: 0.5 });
    await ctx.player.lookAt({ x: 6.5, y: 72.9, z: 0.5 });
    await ctx.player.holdUse(true);
    try {
      await ctx.runtime.wait(400);
      await ctx.commands.assert("/summon minecraft:vindicator 6.5 72 0.5");
      let disabled = false;
      for (let attempt = 0; attempt < 40 && !disabled; attempt++) {
        await ctx.runtime.wait(250);
        disabled = (await ctx.player.pose()).blocking === false;
      }
      if (!disabled) {
        throw new Error("The vindicator's axe never disabled the raised shield");
      }
      await ctx.commands.run("/kill @e[type=minecraft:vindicator]");
      await ctx.runtime.wait(500);
      if ((await ctx.player.pose()).blocking) {
        throw new Error("A disabled shield should not block again right away");
      }
    } finally {
      await ctx.player.holdUse(false);
      await ctx.commands.run("/kill @e[type=minecraft:vindicator]");
    }
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
  await ctx.commands.run("/tp @s 0.5 72 0.5");
  await loadTestArea(ctx);
  await ctx.commands.run("/fill -2 71 -2 6 71 2 minecraft:stone replace");
  await ctx.commands.run("/fill -2 72 -2 6 76 2 minecraft:air replace");
}

// The player picks drops up almost at once, so count both the ground and the inventory.
async function collectedCount(ctx: TeaKitTestContext, at: BlockPos, item: string): Promise<number> {
  const ground = await ctx.loot.near(at, { item: item as `${string}:${string}`, radius: 6 }).list();
  const inventory = await ctx.player.inventory();
  const held = inventory.items.filter((stack) => (stack["itemId"] ?? stack.id) === item);
  return [...ground, ...held].reduce((total, stack) => total + (stack.count ?? 1), 0);
}

async function minecraftVersion(ctx: TeaKitTestContext): Promise<string> {
  return (await ctx.runtime.health()).minecraftVersion ?? "";
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

async function assertFurnaceFuelDuration(ctx: TeaKitTestContext, version: string, ticks: number) {
  const field = atLeast(version, "1.21.2") ? "lit_total_time" : "BurnTime";
  await ctx.commands.run("/scoreboard objectives remove aa_fuel", { requireSuccess: false });
  await ctx.commands.assert("/scoreboard objectives add aa_fuel dummy");
  await ctx.commands.assert(`/execute store result score #fuel aa_fuel run data get block 14 73 0 ${field}`);
  // The pre-1.21.2 field counts down, so allow for the ticks spent getting here.
  await ctx.commands.assert(`/execute if score #fuel aa_fuel matches ${ticks - 100}..${ticks}`);
  await ctx.commands.run("/scoreboard objectives remove aa_fuel", { requireSuccess: false });
}

async function assertItem(ctx: TeaKitTestContext, slot: string, item: string, _legacyNbt: string) {
  const inventory = ctx.player.inventory();
  const itemId = item as `${string}:${string}`;
  if (slot === "weapon.mainhand") {
    await inventory.waitForItem(itemId, { selected: true, timeout: "2s" });
    return;
  }
  if (slot.startsWith("hotbar.")) {
    await inventory.waitForItem(itemId, { slot: Number.parseInt(slot.slice("hotbar.".length), 10), timeout: "2s" });
    return;
  }

  const equipmentSlot = slot.replace("weapon.", "").replace("armor.", "");
  await inventory.waitForItem(itemId, { equipmentSlot, timeout: "2s" });
}

async function assertAttribute(ctx: TeaKitTestContext, attribute: string, expected: number) {
  await ctx.commands.assert(
    `/execute store result score #actual aa_stats run attribute @s ${attribute} get 10`,
  );
  await ctx.commands.assert(`/execute if score #actual aa_stats matches ${expected}`);
}

async function assertHeldDamage(ctx: TeaKitTestContext, version: string, item: string, expectedDamage: number) {
  if (atLeast(version, "1.21.1")) {
    await ctx.commands.assert(
      `/execute if items entity @s weapon.mainhand ${item}[minecraft:damage=${expectedDamage}]`,
    );
    return;
  }

  await ctx.commands.assert(
    `/execute if entity @s[nbt={SelectedItem:{id:"${item}",tag:{Damage:${expectedDamage}}}}]`,
  );
}

async function assertNearestArrowDamage(ctx: TeaKitTestContext, minimum: number, maximum: number) {
  await ctx.commands.run("/scoreboard objectives remove aa_arrow", { requireSuccess: false });
  await ctx.commands.assert("/scoreboard objectives add aa_arrow dummy");
  await ctx.commands.assert(
    "/execute store result score #damage aa_arrow run data get entity @e[type=minecraft:arrow,distance=..80,limit=1,sort=nearest] damage 1000",
  );
  await ctx.commands.assert(`/execute if score #damage aa_arrow matches ${minimum}..${maximum}`);
  await ctx.commands.run("/scoreboard objectives remove aa_arrow", { requireSuccess: false });
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

function commandOutput(result: unknown): string {
  if (result && typeof result === "object" && "output" in result) {
    const output = (result as { output?: unknown }).output;

    if (Array.isArray(output)) {
      return output.join("\n");
    }

    if (typeof output === "string") {
      return output;
    }
  }

  return JSON.stringify(result);
}

// A normal world spawns the player away from the test area, so keep its chunks loaded.
async function loadTestArea(ctx: TeaKitTestContext) {
  await ctx.commands.run("/forceload add -16 -16 47 15");
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
