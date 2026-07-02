import { Capability, describe, test } from "@teakit/test";
import type { TeaKitTestContext } from "@teakit/test";

describe.configure({
  capabilities: [Capability.RuntimeLogs, Capability.ClientScreenshot, Capability.ClientRenderProbes, Capability.ClientScreens],
});

describe("Arcane Armory item rendering", () => {
  test("bows and shields resolve client item models while idle and in use", async (ctx) => {
    await prepare(ctx);
    await assertNoClientResourceErrors(ctx, "initial resource reload");

    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_bow");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with minecraft:arrow 16");
    await ctx.commands.assert("/item replace entity @s hotbar.2 with arcanearmory:ruby_shield");
    await ctx.commands.assert("/item replace entity @s weapon.offhand with arcanearmory:voidium_shield");

    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-inventory-bow-shield");
    await ctx.client.closeMenus();

    await ctx.scenario.run({
      name: "arcane-armory-render-ruby-bow-use",
      steps: [
        { action: "select_hotbar_slot", slot: 0 },
        { action: "set_use_held", held: true },
        { action: "wait_ms", durationMs: 1300 },
      ],
    });
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-ruby-bow-drawn");
    await ctx.scenario.run({
      name: "arcane-armory-release-ruby-bow-render",
      steps: [
        { action: "set_use_held", held: false },
        { action: "wait_ms", durationMs: 200 },
      ],
    });

    await ctx.scenario.run({
      name: "arcane-armory-render-ruby-shield-use",
      steps: [
        { action: "select_hotbar_slot", slot: 2 },
        { action: "set_use_held", held: true },
        { action: "wait_ms", durationMs: 800 },
      ],
    });
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-ruby-shield-blocking");
    await ctx.scenario.run({
      name: "arcane-armory-release-ruby-shield-render",
      steps: [
        { action: "set_use_held", held: false },
        { action: "wait_ms", durationMs: 200 },
      ],
    });

    await assertNoClientResourceErrors(ctx, "bow and shield screenshots");
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

async function assertNoClientResourceErrors(ctx: TeaKitTestContext, phase: string) {
  const text = await ctx.logs.text({ limit: 12000 });
  const failures = text
    .split(/\r?\n/)
    .filter((line) => /missing item model|missing model|missing texture|unable to load model|could not load model|failed to load model|filenotfound/i.test(line));
  if (failures.length > 0) {
    throw new Error(`Client resource errors after ${phase}:\n${failures.join("\n")}`);
  }
}
