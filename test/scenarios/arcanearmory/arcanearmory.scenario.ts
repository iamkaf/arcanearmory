import { describe, test } from "@teakit/test";
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
    await ctx.scenario.run({
      name: "arcane-armory-select-hammer",
      steps: [{ action: "select_hotbar_slot", slot: 0 }],
    });
    await assertItem(ctx, "hotbar.0", "arcanearmory:ruby_hammer", 'Inventory:[{Slot:0b,id:"arcanearmory:ruby_hammer"}]');
    await assertItem(ctx, "weapon.mainhand", "arcanearmory:ruby_hammer", 'SelectedItem:{id:"arcanearmory:ruby_hammer"}');
    await ctx.player.lookAt({ x: 4.5, y: 73.5, z: 0.5 });

    const result = await ctx.scenario.run({
      name: "arcane-armory-hammer-break-plane",
      steps: [{ action: "break_block", x: 4, y: 73, z: 0, timeoutMs: 10000 }],
    });
    if (result.success === false) {
      throw new Error(`Hammer break action failed: ${JSON.stringify(result)}`);
    }

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
    await ctx.scenario.run({
      name: "arcane-armory-select-helmet",
      steps: [
        { action: "select_hotbar_slot", slot: 0 },
        { action: "use_item" },
      ],
    });
    await assertItem(ctx, "armor.head", "arcanearmory:ruby_helmet", 'Inventory:[{Slot:103b,id:"arcanearmory:ruby_helmet"}]');

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/setblock 5 73 0 minecraft:stone");
    await ctx.player.teleport({ x: 2.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 5.5, y: 73.5, z: 0.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_pickaxe");
    await ctx.scenario.run({
      name: "arcane-armory-select-pickaxe",
      steps: [{ action: "select_hotbar_slot", slot: 0 }],
    });

    const result = await ctx.scenario.run({
      name: "arcane-armory-pickaxe-break-stone",
      steps: [{ action: "break_block", x: 5, y: 73, z: 0, timeoutMs: 2500 }],
    });
    if (result.success === false) {
      throw new Error(`Pickaxe break action failed: ${JSON.stringify(result)}`);
    }
    const state = await ctx.world.block({ x: 5, y: 73, z: 0 });
    if (state.id !== "minecraft:air") {
      throw new Error(`Expected pickaxe to mine stone, found ${state.id}`);
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

    const result = await ctx.scenario.run({
      name: "arcane-armory-shield-block-projectile",
      steps: [
        { action: "set_use_held", held: true },
        { action: "wait_ms", durationMs: 1500 },
        { action: "command", command: "/setblock 8 73 3 minecraft:dispenser[facing=north]" },
        { action: "command", command: "/item replace block 8 73 3 container.0 with minecraft:arrow 1" },
        { action: "command", command: "/setblock 8 73 4 minecraft:redstone_block" },
        { action: "wait_ms", durationMs: 1000 },
        { action: "set_use_held", held: false },
      ],
    });
    if (result.success === false) {
      throw new Error(`Shield block action failed: ${JSON.stringify(result)}`);
    }

    await ctx.commands.assert("/execute if entity @s[nbt={Health:20.0f}]");
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..12]");
    await ctx.commands.run("/setblock 8 73 3 minecraft:air");
    await ctx.commands.run("/setblock 8 73 4 minecraft:air");
  });

  test("material bows apply their projectile damage bonus", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/gamemode survival");
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
    await ctx.commands.run("/fill 10 71 -2 22 71 18 minecraft:stone replace");
    await ctx.commands.run("/fill 10 72 -2 22 82 18 minecraft:air replace");
    await ctx.player.teleport({ x: 12.5, y: 73, z: 0.5 });
    await ctx.player.lookAt({ x: 12.5, y: 78, z: 18.5 });
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:voidium_bow");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with minecraft:arrow 8");
    await ctx.scenario.run({
      name: "arcane-armory-select-voidium-bow",
      steps: [{ action: "select_hotbar_slot", slot: 0 }],
    });
    await assertItem(ctx, "weapon.mainhand", "arcanearmory:voidium_bow", 'SelectedItem:{id:"arcanearmory:voidium_bow"}');

    const result = await ctx.scenario.run({
      name: "arcane-armory-fire-voidium-bow",
      steps: [
        { action: "set_use_held", held: true },
        { action: "wait_ms", durationMs: 1600 },
        { action: "set_use_held", held: false },
        { action: "wait_ms", durationMs: 300 },
      ],
    });
    if (result.success === false) {
      throw new Error(`Bow firing action failed: ${JSON.stringify(result)}`);
    }

    await assertNearestArrowDamage(ctx, "3.5d");
    await ctx.commands.run("/kill @e[type=minecraft:arrow,distance=..80]");
  });

  test("solarflare materials fuel furnaces", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.run("/setblock 14 73 0 minecraft:furnace[facing=north]");
    await ctx.commands.assert("/item replace block 14 73 0 container.0 with minecraft:sand 1");
    await ctx.commands.assert("/item replace block 14 73 0 container.1 with arcanearmory:solarflare_gem 1");

    const result = await ctx.scenario.run({
      name: "arcane-armory-solarflare-furnace-fuel",
      steps: [{ action: "wait_ms", durationMs: 1500 }],
    });
    if (result.success === false) {
      throw new Error(`Solarflare fuel wait failed: ${JSON.stringify(result)}`);
    }

    await ctx.commands.assert("/execute if block 14 73 0 minecraft:furnace[lit=true]");
    await ctx.commands.run("/setblock 14 73 0 minecraft:air");
  });
});

async function prepare(ctx: TeaKitTestContext) {
  await ctx.commands.run("/gamemode creative");
  await ctx.commands.run("/clear @s");
  await ctx.commands.run("/tp @s 0.5 72 0.5");
  await ctx.commands.run("/fill -2 71 -2 2 71 2 minecraft:stone replace");
  await ctx.commands.run("/fill -2 72 -2 2 76 2 minecraft:air replace");
}

async function assertItem(ctx: TeaKitTestContext, slot: string, item: string, legacyNbt: string) {
  try {
    await ctx.commands.assert(`/execute if items entity @s ${slot} ${item}`);
  } catch {
    await ctx.commands.assert(`/execute if entity @s[nbt={${legacyNbt}}]`);
  }
}

async function assertNearestArrowDamage(ctx: TeaKitTestContext, expectedDamage: string) {
  try {
    await ctx.commands.assert(
      `/execute if entity @e[type=minecraft:arrow,distance=..80,limit=1,sort=nearest,nbt={damage:${expectedDamage}}]`,
    );
  } catch {
    const result = await ctx.commands.run(
      "/execute as @e[type=minecraft:arrow,distance=..80,limit=1,sort=nearest] run data get entity @s damage",
      { captureOutput: true },
    );
    throw new Error(`Expected nearest fired arrow to have damage ${expectedDamage}, got ${commandOutput(result)}`);
  }
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
