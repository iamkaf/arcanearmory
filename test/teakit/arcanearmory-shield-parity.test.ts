import { Capability, describe, test } from "@teakit/test";
import type { TeaKitTestContext } from "@teakit/test";

describe.configure({
  capabilities: [Capability.RuntimeLogs, Capability.RuntimeTiming, Capability.PlayerInventory, Capability.PlayerInteractions, Capability.ClientScreenshot, Capability.ClientRenderProbes],
});

describe("Arcane Armory shield rendering parity", () => {
  test("Arcane shields use vanilla-like first-person blocking placement", async (ctx) => {
    await prepare(ctx);
    await assertNoClientResourceErrors(ctx, "initial resource reload");

    await ctx.commands.assert("/item replace entity @s hotbar.0 with minecraft:shield");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with arcanearmory:ruby_shield");

    await captureBlocking(ctx, 0, "arcane-armory-vanilla-shield-blocking-reference");
    await captureBlocking(ctx, 1, "arcane-armory-ruby-shield-blocking-parity");

    await assertNoClientResourceErrors(ctx, "shield parity screenshots");
  });
});

async function prepare(ctx: TeaKitTestContext) {
  await ctx.commands.run("/gamemode creative");
  await ctx.commands.run("/clear @s");
  await ctx.commands.run("/time set noon");
  await ctx.commands.run("/weather clear");
  await ctx.commands.run("/tp @s 0.5 72 0.5");
  await loadTestArea(ctx);
  await ctx.commands.run("/fill -3 71 -3 3 71 3 minecraft:stone replace");
  await ctx.commands.run("/fill -3 72 -3 3 76 3 minecraft:air replace");
}

async function captureBlocking(ctx: TeaKitTestContext, slot: number, name: string) {
  await ctx.player.inventory().selectHotbar(slot);
  await ctx.player.holdUse(true);
  await ctx.runtime.wait(800);
  await ctx.client.waitForFrames(5);
  await ctx.client.screenshot(name);
  await ctx.player.holdUse(false);
  await ctx.runtime.wait(200);
}

async function assertNoClientResourceErrors(ctx: TeaKitTestContext, phase: string) {
  const text = await ctx.logs.text({ limit: 12000 });
  const failures = text
    .split(/\r?\n/)
    .filter((line) => /missing item model|missing model|missing texture|unable to load model|could not load model|failed to load model|filenotfound/i.test(line));
  if (failures.length > 0) {
    throw new Error(`Client resource errors after ${phase}:\n${failures.join("\n")}`);
  }
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
