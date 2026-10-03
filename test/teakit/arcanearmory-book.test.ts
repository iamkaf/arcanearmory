import { Capability, describe, test } from "@teakit/test";
import type { ClientScreen, ScreenMenuSlotSnapshot, TeaKitTestContext } from "@teakit/test";

const BOOK_ID = "arcanearmory:arcane_compendium";

describe.configure({
  timeout: "6m",
  capabilities: [
    Capability.ClientScreen,
    Capability.ClientScreens,
    Capability.ClientScreenshot,
    Capability.PlayerInventory,
    Capability.PlayerUseItem,
    Capability.RuntimeLogs,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldRecipes,
  ],
});

describe("Arcane Compendium", () => {
  // Older TeaKit runtimes cannot target tests by loaded mod, so each test checks for Modonomicon itself.
  test("crafts from a book and an Aetheric Crystal, opens, and loads without errors", async (ctx) => {
    await prepare(ctx);
    if (!(await hasModonomicon(ctx))) {
      return;
    }

    await ctx.recipes.assertCrafting(2, 1, ["minecraft:book", "arcanearmory:aetheric_crystal"], "modonomicon:modonomicon");

    await ctx.commands.assert(`/give @s ${bookStack(await minecraftVersion(ctx))}`);
    // Modonomicon syncs unlocked categories after joining; opening earlier can show an empty book.
    await ctx.runtime.wait(3000);
    await ctx.player.inventory().selectHotbar(0);
    await openLanding(ctx);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-compendium-landing"));

    await openFirstEntry(ctx);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-compendium-entry"));
    await closeBook(ctx);

    await assertBookLoadedCleanly(ctx);
  });

  test("is listed in the Arcane Armory creative tab", async (ctx) => {
    await prepare(ctx);
    if (!(await hasModonomicon(ctx))) {
      return;
    }

    await ctx.client.openInventory();
    await ctx.client.waitForFrames(5);
    const grid = await openArcaneTab(ctx);
    // Modonomicon adds the book after Arcane Armory's own items, so scroll to the end of the tab.
    await ctx.client.scroll({ x: grid.x, y: grid.y, verticalAmount: -100 });
    await ctx.client.waitForFrames(5);
    const listed = (await ctx.client.screen()).menu().slots().filter((slot) => slot.slot < 45).map(slotItemId);
    await ctx.artifacts.attachScreenshot(await ctx.client.screenshot("arcane-compendium-creative-tab"));
    await ctx.client.closeMenus();
    if (listed.includes("modonomicon:modonomicon")) {
      return;
    }
    // Modonomicon 2.5.0, the newest NeoForge build for 26.2, lists books only in its own tab.
    const health = await ctx.runtime.health();
    if (health.minecraftVersion === "26.2" && health.loader === "neoforge") {
      await ctx.client.openInventory();
      await ctx.client.waitForFrames(5);
      await openTabWith(ctx, (id) => id === "modonomicon:modonomicon");
      await ctx.client.closeMenus();
      return;
    }
    throw new Error(`The Arcane Armory tab does not list the Arcane Compendium; its last page shows ${listed.join(", ")}`);
  });

  test("stays inert without Modonomicon", async (ctx) => {
    await prepare(ctx);
    if (await hasModonomicon(ctx)) {
      return;
    }

    const recipe = await ctx.commands.run("/recipe give @s arcanearmory:arcane_compendium", { requireSuccess: false });
    if (recipe.success === true) {
      throw new Error("The Arcane Compendium recipe loaded without Modonomicon");
    }
    await assertBookLoadedCleanly(ctx);
  });
});

// Modonomicon reopens a book where it was last left, so step back until its landing page shows.
async function openLanding(ctx: TeaKitTestContext) {
  for (let attempt = 0; attempt < 6; attempt++) {
    const screen = await ctx.client.screen();
    if (!screen.screenClass?.includes("modonomicon")) {
      await ctx.player.holdUse(true);
      await ctx.player.holdUse(false);
    } else if (isLanding(screen)) {
      return;
    } else {
      // Backspace steps back one screen; Escape would close the whole book.
      await ctx.client.key(259, { release: true });
    }
    await ctx.client.waitForFrames(20);
  }
  throw new Error(`Using the Arcane Compendium did not open its landing page; found ${(await ctx.client.screen()).screenClass}`);
}

// The index lists categories; a node map has the search button but, unlike a category index, no entry list.
function isLanding(screen: ClientScreen): boolean {
  const classes = screen.widgets().all().map((widget) => widget.widgetClass);
  return classes.some((name) => name.endsWith("CategoryListButton"))
    || (classes.some((name) => name.endsWith("SearchButton")) && !classes.some((name) => name.endsWith("EntryListButton")));
}

// Index-mode books (Modonomicon for 1.21.1 and newer) list categories and entries as buttons. Older builds
// open on the first category's node map, where the first entry sits at grid cell -4, -2 from the center.
async function openFirstEntry(ctx: TeaKitTestContext) {
  const landing = await ctx.client.screen();
  const widgets = landing.widgets().all();
  let before = landing;
  if (widgets.some((widget) => widget.widgetClass.endsWith("CategoryListButton"))) {
    await landing.widgets().find({ label: "Materials" }).click();
    await ctx.client.waitForFrames(10);
    before = await ctx.client.screen();
    await before.widgets().find({ label: "Ruby" }).click();
  } else {
    const search = widgets.find((widget) => widget.widgetClass.endsWith("SearchButton"));
    if (!search) {
      throw new Error(`No category list or search button on ${landing.screenClass}: ${JSON.stringify(widgets)}`);
    }
    // The search button sits 31 units left of the right edge and 43 above the bottom. The map centers on
    // the inner area, which spans the screen less 74 by 34; cells are 30 units, entries are 26 wide with
    // a 2 unit margin, and the map opens at 0.7 zoom.
    const width = search.x + 31;
    const height = search.y + 43;
    await ctx.client.click({
      x: Math.round((width - 74) / 2 + (-4 * 30 + 15) * 0.7),
      y: Math.round((height - 34) / 2 + (-2 * 30 + 15) * 0.7),
    });
  }
  await ctx.client.waitForFrames(10);
  const entry = await ctx.client.screen();
  if (!entry.screenClass?.includes("modonomicon") || signature(entry) === signature(before)) {
    throw new Error(`Expected a book entry to open from ${signature(before)}, found ${signature(entry)}`);
  }
}

async function hasModonomicon(ctx: TeaKitTestContext): Promise<boolean> {
  const given = (await ctx.commands.run("/give @s modonomicon:modonomicon", { requireSuccess: false })).success === true;
  await ctx.commands.run("/clear @s");
  return given;
}

// Creative tabs sit above and below the item grid, 27 GUI units apart. Their position is derived from the
// search box, which vanilla places at (left + 82, top + 6) on every loader. Returns the item grid's center.
async function openArcaneTab(ctx: TeaKitTestContext): Promise<{ x: number; y: number }> {
  return openTabWith(ctx, (id) => id.startsWith("arcanearmory:"));
}

async function openTabWith(ctx: TeaKitTestContext, matches: (id: string) => boolean): Promise<{ x: number; y: number }> {
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
        if (slots.some((slot) => matches(slotItemId(slot)))) {
          return { x: left + 90, y: top + 63 };
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
  throw new Error("No creative tab lists the expected item");
}

function slotItemId(slot: ScreenMenuSlotSnapshot): string {
  const item = slot.item as (Record<string, unknown> & { id?: string }) | null | undefined;
  const id = item?.["itemId"] ?? item?.id;
  return typeof id === "string" ? id : "";
}

function signature(screen: ClientScreen): string {
  return `${screen.screenClass} [${screen.widgets().all().map((widget) => widget.label).join(", ")}]`;
}

// Closing a Modonomicon screen steps back through the book, so press Escape until the game shows.
async function closeBook(ctx: TeaKitTestContext) {
  for (let attempt = 0; attempt < 6; attempt++) {
    const screen = await ctx.client.screen();
    if (!screen.screenClass?.includes("modonomicon")) {
      return;
    }
    await ctx.client.key(256, { release: true });
    await ctx.client.waitForFrames(5);
  }
  throw new Error("The Arcane Compendium did not close");
}

async function prepare(ctx: TeaKitTestContext) {
  await ctx.commands.run("/gamemode creative");
  await ctx.commands.run("/clear @s");
  await closeBook(ctx);
  await ctx.client.closeMenus();
}

function bookStack(version: string): string {
  return atLeast(version, "1.20.5")
    ? `modonomicon:modonomicon[modonomicon:book_id="${BOOK_ID}"]`
    : `modonomicon:modonomicon{"modonomicon:book_id":"${BOOK_ID}"}`;
}

// Modonomicon logs book load failures as errors and missing recipe pages as warnings.
async function assertBookLoadedCleanly(ctx: TeaKitTestContext) {
  const text = await ctx.logs.text({ limit: 20000 });
  const failures = text
    .split(/\r?\n/)
    .filter((line) =>
      /arcane_compendium|arcanearmory:.*not found|Couldn't parse.*arcanearmory|Parsing error.*arcanearmory/i.test(line)
      && /error|warn|not found|couldn't|failed/i.test(line));
  if (failures.length > 0) {
    throw new Error(`The Arcane Compendium logged problems:\n${failures.join("\n")}`);
  }
}

async function minecraftVersion(ctx: TeaKitTestContext): Promise<string> {
  return (await ctx.runtime.health()).minecraftVersion ?? "";
}

function atLeast(version: string, minimum: string): boolean {
  const left = version.split(".").map((part) => Number.parseInt(part, 10));
  const right = minimum.split(".").map((part) => Number.parseInt(part, 10));
  for (let index = 0; index < Math.max(left.length, right.length); index++) {
    const difference = (left[index] ?? 0) - (right[index] ?? 0);
    if (difference !== 0) return difference > 0;
  }
  return true;
}
