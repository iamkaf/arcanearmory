import { Capability, describe, pos, test } from "@teakit/test";
import type { ScreenMenuSlotSnapshot, TeaKitTestContext } from "@teakit/test";

describe.configure({
  capabilities: [Capability.RuntimeLogs, Capability.RuntimeTiming, Capability.PlayerInventory, Capability.PlayerInteractions, Capability.ClientScreenshot, Capability.ClientRenderProbes, Capability.ClientScreens, Capability.WorldBlock, Capability.WorldEntities],
});

describe("Arcane Armory item rendering", () => {
  test("bows and shields resolve client item models while idle and in use", async (ctx) => {
    await prepare(ctx);
    await assertNoClientResourceErrors(ctx, "initial resource reload");

    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:ruby_bow");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with minecraft:arrow 16");
    await ctx.commands.assert("/item replace entity @s hotbar.2 with arcanearmory:ruby_shield");
    await ctx.commands.assert("/item replace entity @s weapon.offhand with arcanearmory:voidium_shield");
    await ctx.commands.assert("/item replace entity @s armor.head with arcanearmory:ruby_helmet");
    await ctx.commands.assert("/item replace entity @s armor.chest with arcanearmory:ruby_chestplate");
    await ctx.commands.assert("/item replace entity @s armor.legs with arcanearmory:ruby_leggings");
    await ctx.commands.assert("/item replace entity @s armor.feet with arcanearmory:ruby_boots");
    await ctx.commands.run("/gamemode survival");

    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-inventory-equipment");
    await ctx.client.closeMenus();

    await ctx.player.inventory().selectHotbar(0);
    await ctx.player.holdUse(true);
    await ctx.runtime.wait(1300);
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-ruby-bow-drawn");
    await ctx.player.holdUse(false);
    await ctx.runtime.wait(200);

    await ctx.player.inventory().selectHotbar(2);
    await ctx.player.holdUse(true);
    await ctx.runtime.wait(800);
    await ctx.client.waitForFrames(5);
    await ctx.client.screenshot("arcane-armory-ruby-shield-blocking");
    await ctx.player.holdUse(false);
    await ctx.runtime.wait(200);

    await assertNoClientResourceErrors(ctx, "bow and shield screenshots");
  });
});

describe("Arcane Armory blocks and creative tab", () => {
  test("Arcanthe and Potted Arcanthe render as cut-out plants", async (ctx) => {
    await prepare(ctx);

    await ctx.commands.assert("/setblock 1 71 3 minecraft:grass_block");
    await ctx.commands.assert("/setblock 1 72 3 arcanearmory:arcanthe");
    await ctx.commands.assert("/setblock -1 72 3 arcanearmory:potted_arcanthe");
    await ctx.player.teleport({ x: 0.5, y: 72, z: 0.5 });
    await ctx.player.lookAt({ x: 0.5, y: 72.4, z: 3.5 });
    await ctx.client.waitForFrames(10);

    for (const at of [pos(1, 72, 3), pos(-1, 72, 3)]) {
      const rendered = await ctx.render.block(at).inspect();
      if (rendered.missingModel || rendered.missingTexture) {
        throw new Error(`${rendered.id} at ${at.x} ${at.y} ${at.z} renders without its model or texture`);
      }
    }
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-arcanthe"));
    await assertNoClientResourceErrors(ctx, "Arcanthe rendering");
  });

  test("trimmed Arcane armor shows its trim worn and in the inventory", async (ctx) => {
    await prepare(ctx);

    const trim = '[minecraft:trim={material:"minecraft:gold",pattern:"minecraft:coast"}]';
    await ctx.commands.run("/kill @e[type=minecraft:armor_stand]");
    await ctx.commands.assert("/summon minecraft:armor_stand 0.5 72 2.5 {Rotation:[180f,0f]}");
    for (const [slot, piece] of [["head", "helmet"], ["chest", "chestplate"], ["legs", "leggings"], ["feet", "boots"]]) {
      await ctx.commands.assert(
        `/item replace entity @e[type=minecraft:armor_stand,limit=1,sort=nearest] armor.${slot} with arcanearmory:voidium_${piece}${trim}`,
      );
    }
    await ctx.commands.assert(`/item replace entity @s hotbar.0 with arcanearmory:ruby_helmet${trim}`);
    await ctx.commands.assert("/item replace entity @s hotbar.1 with arcanearmory:ruby_helmet");
    await ctx.player.teleport({ x: 0.5, y: 72, z: 0.5 });
    await ctx.player.lookAt({ x: 0.5, y: 73, z: 2.5 });
    await ctx.client.waitForFrames(10);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-trimmed-armor-worn"));

    await ctx.commands.run("/gamemode survival");
    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-trimmed-armor-icons"));
    await ctx.client.closeMenus();
    await ctx.commands.run("/kill @e[type=minecraft:armor_stand]");
    await assertNoClientResourceErrors(ctx, "trimmed armor rendering");
  });

  test("the creative tab lists the Arcane Armory items", async (ctx) => {
    await prepare(ctx);

    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    const slots = await openArcaneTab(ctx);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-armory-creative-tab"));
    await ctx.client.closeMenus();

    const listed = new Set(slots.map(slotItemId));
    const expected = [
      "arcanearmory:ruby",
      "arcanearmory:ruby_block",
      "arcanearmory:ruby_ore",
      "arcanearmory:deepslate_ruby_ore",
      "arcanearmory:raw_ruby",
      "arcanearmory:ruby_sword",
      "arcanearmory:ruby_hammer",
      "arcanearmory:ruby_bow",
      "arcanearmory:ruby_shield",
      "arcanearmory:ruby_helmet",
    ];
    const missing = expected.filter((id) => !listed.has(id));
    if (missing.length > 0) {
      throw new Error(`The creative tab is missing ${missing.join(", ")}; it shows ${[...listed].join(", ")}`);
    }
  });
});

// Creative tabs sit above and below the item grid, 27 GUI units apart. Their position is derived from the
// search box, which vanilla places at (left + 82, top + 6) on every loader.
async function openArcaneTab(ctx: TeaKitTestContext): Promise<ScreenMenuSlotSnapshot[]> {
  for (let page = 0; page < 4; page++) {
    const screen = await ctx.client.screen();
    const search = screen.widgets().all().find((widget) => widget.widgetClass.endsWith("EditBox"));
    if (!search) {
      throw new Error(`Expected the creative inventory, found ${screen.screenClass}`);
    }
    const left = search.x - 82;
    const top = search.y - 6;
    for (const tabY of [top - 28, top + 136 - 4]) {
      for (let column = 0; column < 7; column++) {
        await ctx.client.click({ x: left + 27 * column + 13, y: tabY + 16 });
        await ctx.client.waitForFrames(2);
        const slots = (await ctx.client.screen()).menu().slots().filter((slot) => slot.slot < 45);
        if (slots.some((slot) => slotItemId(slot).startsWith("arcanearmory:"))) {
          return slots;
        }
      }
    }
    const next = (await ctx.client.screen()).widgets().all().find((widget) => widget.label === ">" && widget.active);
    if (!next) {
      break;
    }
    await ctx.client.click({ x: next.x + next.width / 2, y: next.y + next.height / 2 });
    await ctx.client.waitForFrames(2);
  }
  throw new Error("No creative tab lists Arcane Armory items");
}

function slotItemId(slot: ScreenMenuSlotSnapshot): string {
  const item = slot.item as (Record<string, unknown> & { id?: string }) | null | undefined;
  const id = item?.["itemId"] ?? item?.id;
  return typeof id === "string" ? id : "";
}

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
