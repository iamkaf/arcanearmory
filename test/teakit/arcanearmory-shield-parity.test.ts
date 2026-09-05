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
