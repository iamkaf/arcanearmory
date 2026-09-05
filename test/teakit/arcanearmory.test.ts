import { Capability, Readiness, describe, pos, test } from "@teakit/test";
import type { TeaKitTestContext } from "@teakit/test";

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
  "aristeum",
  "voidium",
] as const;

const ingots = new Set(["coolpper", "titanium", "aristeum", "voidium"]);
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
  "aristeum",
  "voidium",
]);
const shields = new Set(["ruby", "coolpper", "titanium", "aristeum", "voidium"]);

describe.configure({
  timeout: "8m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.PlayerInteractions,
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
    await ctx.commands.assert("/give @s arcanearmory:aristea 1");
  });

  test("owned alloy recipes resolve through the recipe manager", async (ctx) => {
    await prepare(ctx);

    const result = await ctx.recipes.assertCrafting(
      2,
      1,
      ["minecraft:iron_ingot", "arcanearmory:raw_amber"],
      "arcanearmory:amber",
      { resultCount: 2 },
    );
    if (!result.recipeId?.includes("amber_from_alloying")) {
      throw new Error(`Expected amber alloying recipe, got ${commandOutput(result)}`);
    }
  });

  test("hammers mine a 3x3 plane around the target block", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.world.fill({ x: 4, y: 72, z: -1 }, { x: 4, y: 74, z: 1 }, "minecraft:stone");
    await ctx.player.teleport({ x: 1.5, y: 73, z: 0.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_hammer");
    await ctx.player.inventory().selectHotbar(0);
    await assertItem(ctx, "hotbar.0", "arcanearmory:ruby_hammer", 'Inventory:[{Slot:0b,id:"arcanearmory:ruby_hammer"}]');
    await assertItem(ctx, "weapon.mainhand", "arcanearmory:ruby_hammer", 'SelectedItem:{id:"arcanearmory:ruby_hammer"}');
    await ctx.player.lookAt({ x: 4.5, y: 73.5, z: 0.5 });

    await ctx.player.mine(pos(4, 73, 0), { timeout: "8s" });

    for (let y = 72; y <= 74; y++) {
      for (let z = -1; z <= 1; z++) {
        const state = await ctx.world.block({ x: 4, y, z });
        if (state.id !== "minecraft:air") {
          throw new Error(`Expected hammer to clear 4 ${y} ${z}, found ${state.id}`);
        }
      }
    }
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

  test("solarflare materials fuel furnaces", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/setblock 14 73 0 minecraft:furnace[facing=north]");
    await ctx.commands.assert("/item replace block 14 73 0 container.0 with minecraft:sand 1");
    await ctx.commands.assert("/item replace block 14 73 0 container.1 with arcanearmory:solarflare_gem 1");

    await ctx.runtime.wait(1500);

    await ctx.commands.assert("/execute if block 14 73 0 minecraft:furnace[lit=true]");
    await ctx.commands.run("/setblock 14 73 0 minecraft:air");
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
  await ctx.commands.run("/fill -2 71 -2 6 71 2 minecraft:stone replace");
  await ctx.commands.run("/fill -2 72 -2 6 76 2 minecraft:air replace");
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
