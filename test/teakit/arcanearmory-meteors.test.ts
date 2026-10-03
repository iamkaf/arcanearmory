import { Capability, Readiness, describe, pos, test } from "@teakit/test";
import type { BlockPos, TeaKitTestContext } from "@teakit/test";

describe.configure({
  timeout: "8m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.ClientScreenshot,
    Capability.PlayerDriver,
    Capability.PlayerInteractions,
    Capability.PlayerInventory,
    Capability.PlayerReset,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldBlock,
    Capability.WorldEntities,
    Capability.WorldLoot,
  ],
});

const MINED = pos(5, 73, 0);

// A stone slab in the sky for the crater, clear of the terrain under the test area.
const CRATER_GROUND = { x: 30, y: 147, z: 0 };
const CRATER_AREA = "18 136 -12 42 160 12";

// A grass platform for a falling meteor, and a lookout far enough away to watch the whole fall.
const LANDING = { x: 16, y: 150, z: 0 };
const LOOKOUT = { x: -48, y: 150, z: 0 };

describe("Meteorite", () => {
  test("Meteorite needs an iron pickaxe and drops itself", async (ctx) => {
    await prepare(ctx);

    await expectDrops(ctx, "minecraft:stone_pickaxe", "arcanearmory:meteorite", []);
    await expectDrops(ctx, "minecraft:iron_pickaxe", "arcanearmory:meteorite", ["arcanearmory:meteorite"]);
  });
});

describe("Meteor craters", () => {
  // `/place` arrives in 1.19.
  test("a meteor crater leaves Meteorite around Star Corundum ore", { target: { minecraft: ">=1.19" } }, async (ctx) => {
    await prepare(ctx);
    await ctx.commands.run("/time set noon");
    const { x, y, z } = CRATER_GROUND;
    await ctx.commands.run(`/fill ${CRATER_AREA} minecraft:air`);
    await ctx.commands.assert(`/fill ${x - 12} ${y - 9} ${z - 12} ${x + 12} ${y} ${z + 12} minecraft:grass_block`);
    await ctx.commands.assert(`/place feature arcanearmory:meteor_crater ${x} ${y + 1} ${z}`);

    // Look down into the bowl from a post beside it.
    await ctx.commands.assert(`/setblock ${x} ${y + 5} ${z + 10} minecraft:stone`);
    await ctx.player.teleport({ x: x + 0.5, y: y + 6, z: z + 10.5 });
    await ctx.player.lookAt({ x: x + 0.5, y: y - 1, z: z + 0.5 });
    await ctx.client.waitForFrames(20);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-meteor-crater", { hideOverlay: true }));

    // Swapping a block away succeeds only where it is present.
    for (const block of ["arcanearmory:meteorite", "arcanearmory:star_corundum_ore"]) {
      const found = await ctx.commands.run(`/fill ${CRATER_AREA} minecraft:air replace ${block}`, { requireSuccess: false });
      if (!found.success) {
        throw new Error(`The crater holds no ${block}`);
      }
    }
    await ctx.commands.run(`/fill ${CRATER_AREA} minecraft:air`);
  });

  test("meteor craters generate in freshly generated chunks", async (ctx) => {
    await prepare(ctx);

    // One chunk in 64 rolls a crater, and craters skip wet ground, so search fresh areas until one turns up.
    for (let area = 0; area < 40; area++) {
      if (await craterFound(ctx, freshX())) {
        return;
      }
    }
    throw new Error("No meteor crater generated in 40 fresh Overworld areas");
  });
});

describe("Falling meteors", () => {
  test("/arcanearmory meteor streaks down and lands a boulder on the terrain", { target: { minecraft: ">=1.21.11" } }, async (ctx) => {
    await prepare(ctx);
    await ctx.commands.run("/time set midnight");
    await ctx.commands.run("/weather clear");
    const { x, y, z } = LANDING;
    await ctx.commands.run(`/fill ${x - 6} ${y} ${z - 6} ${x + 6} ${y + 10} ${z + 6} minecraft:air`);
    await ctx.commands.assert(`/fill ${x - 6} ${y} ${z - 6} ${x + 6} ${y} ${z + 6} minecraft:grass_block`);
    // A solid block in the boulder's shell, one where its core would go, and a tuft of grass it may bury.
    await ctx.commands.assert(`/setblock ${x + 1} ${y + 1} ${z} minecraft:glass`);
    await ctx.commands.assert(`/setblock ${x - 1} ${y + 2} ${z} minecraft:oak_planks`);
    await ctx.commands.assert(`/setblock ${x} ${y + 1} ${z - 1} minecraft:short_grass`);

    await ctx.commands.run(`/fill ${LOOKOUT.x - 1} ${LOOKOUT.y} ${LOOKOUT.z - 1} ${LOOKOUT.x + 1} ${LOOKOUT.y} ${LOOKOUT.z + 1} minecraft:stone`);
    await ctx.player.teleport({ x: LOOKOUT.x + 0.5, y: LOOKOUT.y + 1, z: LOOKOUT.z + 0.5 });
    await ctx.player.lookAt({ x: x + 0.5, y: y + 24, z: z + 0.5 });
    await ctx.client.waitForFrames(10);

    const center = pos(x, y + 2, z);
    await ctx.commands.assert(`/arcanearmory meteor ${x} ${y + 1} ${z}`);
    // The fall lasts three seconds; screenshots take a moment each, so check the landing spot first.
    await ctx.runtime.wait(1200);
    if ((await ctx.world.block(center)).id !== "minecraft:air") {
      throw new Error("The meteor landed before its fall was over");
    }
    await ctx.runtime.wait(300);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-meteor-falling", { hideOverlay: true }));
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-meteor-falling-late", { hideOverlay: true }));

    let landed = false;
    for (let attempt = 0; attempt < 40 && !landed; attempt++) {
      await ctx.runtime.wait(250);
      landed = (await ctx.world.block(center)).id === "arcanearmory:star_corundum_ore";
    }
    if (!landed) {
      throw new Error("No Star Corundum core landed on the grass platform");
    }
    await ctx.client.waitForFrames(4);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-meteor-impact", { hideOverlay: true }));

    await expectBlock(ctx, pos(x, y + 4, z), "arcanearmory:meteorite");
    await expectBlock(ctx, pos(x, y + 1, z - 1), "arcanearmory:meteorite");
    await expectBlock(ctx, pos(x + 1, y + 1, z), "minecraft:glass");
    await expectBlock(ctx, pos(x - 1, y + 2, z), "minecraft:oak_planks");
    const dug = await ctx.commands.run(`/fill ${x - 6} ${y} ${z - 6} ${x + 6} ${y} ${z + 6} minecraft:grass_block replace minecraft:air`, {
      requireSuccess: false,
    });
    if (dug.success) {
      throw new Error("The meteor dug into the ground it landed on");
    }

    await ctx.commands.assert(`/setblock ${x + 4} ${y + 3} ${z + 6} minecraft:stone`);
    await ctx.player.teleport({ x: x + 4.5, y: y + 4, z: z + 6.5 });
    await ctx.player.lookAt({ x: x + 0.5, y: y + 2, z: z + 0.5 });
    await ctx.runtime.wait(2000);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-meteor-landed", { hideOverlay: true }));
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
}

async function expectBlock(ctx: TeaKitTestContext, at: BlockPos, expected: string) {
  const found = (await ctx.world.block(at)).id;
  if (found !== expected) {
    throw new Error(`Expected ${expected} at ${at.x} ${at.y} ${at.z}, found ${found}`);
  }
}

async function expectDrops(ctx: TeaKitTestContext, tool: string, block: string, expected: string[]) {
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/clear @s");
  await ctx.commands.run("/gamemode survival");
  await ctx.commands.assert(`/item replace entity @s weapon.mainhand with ${tool}`);
  await ctx.commands.assert(`/setblock ${MINED.x} ${MINED.y} ${MINED.z} ${block}`);
  await ctx.player.teleport({ x: MINED.x - 1 + 0.5, y: MINED.y - 1, z: MINED.z + 0.5 });
  await ctx.player.lookAt({ x: MINED.x + 0.5, y: MINED.y + 0.5, z: MINED.z + 0.5 });
  await ctx.player.mine(MINED, { timeout: "20s" });
  await ctx.runtime.wait(500);

  // The player picks drops up almost at once, so count both the ground and the inventory, minus the tool.
  const ground = (await ctx.loot.near(MINED, { radius: 4 }).list()).map((entity) => entity.item ?? entity.itemId ?? "");
  const inventory = await ctx.player.inventory();
  const held = inventory.items
    .filter((stack) => !(stack.slot === inventory.selectedSlot && stackId(stack) === tool))
    .map(stackId);
  const dropped = [...new Set([...ground, ...held].filter((id) => id !== ""))].sort();
  if (JSON.stringify(dropped) !== JSON.stringify([...expected].sort())) {
    throw new Error(`Mining ${block} with ${tool} dropped [${dropped.join(", ")}], expected [${expected.join(", ")}]`);
  }
  await ctx.commands.run("/kill @e[type=minecraft:item]");
  await ctx.commands.run("/clear @s");
}

function stackId(stack: { id?: string; [key: string]: unknown }): string {
  const id = stack["itemId"] ?? stack.id;
  return typeof id === "string" ? id : "";
}

// Generates the 64-by-64 area at x, 0 and looks for Meteorite near the surface. Meteorite is
// swapped for bedrock and back, which no surface layer holds, so the fresh chunks keep their craters.
async function craterFound(ctx: TeaKitTestContext, x: number): Promise<boolean> {
  const z = 0;
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
      throw new Error("Fresh Overworld chunks did not load");
    }

    for (const dx of [0, 32]) {
      for (const dz of [0, 32]) {
        for (const [low, high] of [[48, 79], [80, 111], [112, 143]]) {
          const fill = `/fill ${x + dx} ${low} ${z + dz} ${x + dx + 31} ${high} ${z + dz + 31}`;
          const swapped = await ctx.commands.run(`${fill} minecraft:bedrock replace arcanearmory:meteorite`, { requireSuccess: false });
          if (swapped.success) {
            await ctx.commands.assert(`${fill} arcanearmory:meteorite replace minecraft:bedrock`);
            return true;
          }
        }
      }
    }
    return false;
  } finally {
    await ctx.commands.run(`/forceload remove ${x} ${z} ${x + 63} ${z + 63}`);
  }
}

// The test world persists, so each scan picks chunks no earlier run has generated.
function freshX(): number {
  return 4096 + 64 * Math.floor(Math.random() * 4096);
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
