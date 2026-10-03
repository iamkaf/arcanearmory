import { Capability, Readiness, describe, test } from "@teakit/test";
import type { TeaKitTestContext } from "@teakit/test";

describe.configure({
  timeout: "6m",
  readiness: [Readiness.World, Readiness.Player],
  capabilities: [
    Capability.ClientScreen,
    Capability.ClientScreenshot,
    Capability.PlayerInventory,
    Capability.PlayerReset,
    Capability.RuntimeTiming,
    Capability.ServerCommands,
    Capability.WorldEntities,
  ],
});

const target = "@e[type=minecraft:pig,tag=aa_trait_target,limit=1]";

// Numeric ids are for the effect NBT before 1.20.2.
const targetEffects = [
  { weapon: "amber_axe", effect: "slowness", legacyId: 2, amplifier: 1 },
  { weapon: "star_corundum_pickaxe", effect: "glowing", legacyId: 24, amplifier: 0 },
  { weapon: "voidium_hammer", effect: "levitation", legacyId: 25, amplifier: 0 },
] as const;

const setBonuses = [
  { material: "sapphire", effect: "haste", amplifier: 0 },
  { material: "black_diamond", effect: "resistance", amplifier: 0 },
  { material: "topaz", effect: "luck", amplifier: 0 },
  { material: "aquamarine", effect: "water_breathing", amplifier: 0 },
  { material: "aetheric_crystal", effect: "jump_boost", amplifier: 0 },
  { material: "coolpper", effect: "fire_resistance", amplifier: 0 },
  { material: "arcanthium", effect: "speed", amplifier: 0 },
] as const;

const armorSlots = [
  ["head", "helmet"],
  ["chest", "chestplate"],
  ["legs", "leggings"],
  ["feet", "boots"],
] as const;

describe("Arcane Armory traits", () => {
  // /damage, which stands in for a melee hit, arrived in 1.19.4.
  test("on-hit traits fire for the material's melee gear in the main hand", { target: { minecraft: ">=1.19.4" } }, async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);
    await ctx.commands.run("/scoreboard objectives remove aa_traits", { requireSuccess: false });
    await ctx.commands.assert("/scoreboard objectives add aa_traits dummy");

    await hitWith(ctx, "ruby_sword");
    await ctx.commands.assert(`/execute store result score #fire aa_traits run data get entity ${target} Fire`);
    await ctx.commands.assert("/execute if score #fire aa_traits matches 1..");

    // Bows do not carry the trait.
    await hitWith(ctx, "ruby_bow");
    await ctx.commands.assert(`/execute store result score #fire aa_traits run data get entity ${target} Fire`);
    await ctx.commands.assert("/execute unless score #fire aa_traits matches 1..");

    for (const expected of targetEffects) {
      await hitWith(ctx, expected.weapon);
      // Newer saves omit a zero amplifier, so only match the amplifier when it is set.
      const amplifier = expected.amplifier > 0 ? `,amplifier:${expected.amplifier}b` : "";
      const effectNbt = atLeast(version, "1.20.2")
        ? `active_effects:[{id:"minecraft:${expected.effect}"${amplifier}}]`
        : `ActiveEffects:[{Id:${expected.legacyId}${amplifier.replace("amplifier", "Amplifier")}}]`;
      await ctx.commands.assert(`/execute if entity ${target.replace("]", `,nbt={${effectNbt}}]`)}`);
    }

    // Survival with low food outside peaceful, so no regeneration hides the heal.
    await ctx.commands.run("/difficulty normal");
    await ctx.player.reset({ gameMode: "survival", health: 10, food: 17, saturation: 0, effects: "clear" });
    await hitWith(ctx, "bloodfire_garnet_shovel");
    const health = await ctx.player.health();
    if (Math.abs(health - 11) > 0.01) {
      throw new Error(`A Bloodfire Garnet hit should heal the wielder to 11 health, found ${health}`);
    }

    await ctx.commands.run("/kill @e[type=minecraft:pig,tag=aa_trait_target]");
    await ctx.commands.run("/scoreboard objectives remove aa_traits", { requireSuccess: false });
  });

  test("a full armor set grants its bonus until a piece comes off", async (ctx) => {
    await prepare(ctx);

    for (const expected of setBonuses) {
      const effectId = `minecraft:${expected.effect}`;
      await equipSet(ctx, expected.material);
      const effect = await ctx.player.waitForEffect(effectId, { timeout: "3s" });
      if (effect.amplifier !== expected.amplifier || effect.ambient === false || effect.visible === true) {
        throw new Error(`${expected.material} set gave an unexpected ${effectId}: ${JSON.stringify(effect)}`);
      }

      await ctx.commands.assert("/item replace entity @s armor.head with minecraft:air");
      await waitForEffectGone(ctx, effectId);
    }

    // Night Vision flickers in its last ten seconds, so the set keeps it above that.
    await equipSet(ctx, "chrysoberyl");
    await ctx.player.waitForEffect("minecraft:night_vision", { timeout: "3s" });
    await ctx.runtime.wait(1500);
    const nightVision = (await ctx.player.effects()).find((effect) => effect.effectId === "minecraft:night_vision");
    if (!nightVision || nightVision.duration <= 200) {
      throw new Error(`The chrysoberyl set should keep Night Vision above 200 ticks, found ${JSON.stringify(nightVision)}`);
    }
    await ctx.commands.assert("/item replace entity @s armor.head with minecraft:air");
    await ctx.commands.run("/effect clear @s");
  });

  test("every item of a trait material names its trait in the tooltip", async (ctx) => {
    await prepare(ctx);
    await ctx.commands.run("/gamemode survival");
    await ctx.commands.assert("/item replace entity @s hotbar.0 with arcanearmory:chrysoberyl_helmet");
    await ctx.commands.assert("/item replace entity @s hotbar.1 with arcanearmory:ruby");

    // The survival inventory places its recipe book button at (leftPos + 104, topPos + 61).
    const screen = await ctx.client.openInventory();
    const recipeBook = screen.widgets().all()[0];
    if (!recipeBook) {
      throw new Error("The inventory screen should have its recipe book button");
    }
    const left = recipeBook.x - 104;
    const top = recipeBook.y - 61;
    for (const [slot, name] of [[0, "set-bonus"], [1, "on-hit"]] as const) {
      // Scrolling by nothing only moves the cursor, which hovers the hotbar slot.
      await ctx.client.scroll({ x: left + 16 + slot * 18, y: top + 150, verticalAmount: 0 });
      await ctx.client.waitForFrames(5);
      await ctx.artifacts.attachScreenshot(await ctx.client.screenshot(`arcane-armory-trait-tooltip-${name}`));
    }
    await ctx.client.closeMenus();
  });

  test("titanium armor adds knockback resistance per piece", async (ctx) => {
    await prepare(ctx);
    const version = await minecraftVersion(ctx);
    const attribute = atLeast(version, "1.21.2") ? "minecraft:knockback_resistance" : "minecraft:generic.knockback_resistance";
    await ctx.commands.run("/scoreboard objectives remove aa_traits", { requireSuccess: false });
    await ctx.commands.assert("/scoreboard objectives add aa_traits dummy");

    await ctx.commands.assert("/item replace entity @s armor.head with arcanearmory:titanium_helmet");
    await ctx.runtime.wait(100);
    await assertAttributeTimesHundred(ctx, attribute, 10);

    await equipSet(ctx, "titanium");
    await ctx.runtime.wait(100);
    await assertAttributeTimesHundred(ctx, attribute, 40);

    await ctx.commands.run("/scoreboard objectives remove aa_traits", { requireSuccess: false });
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
  await ctx.commands.run("/kill @e[type=minecraft:pig,tag=aa_trait_target]");
}

// A fresh target for every hit, so invulnerability frames never swallow one.
async function hitWith(ctx: TeaKitTestContext, item: string) {
  await ctx.commands.run("/kill @e[type=minecraft:pig,tag=aa_trait_target]");
  await ctx.commands.assert('/summon minecraft:pig 2.5 72 0.5 {NoAI:1b,Silent:1b,Tags:["aa_trait_target"]}');
  await ctx.commands.assert(`/item replace entity @s weapon.mainhand with arcanearmory:${item}`);
  await ctx.commands.assert(`/damage ${target} 1 minecraft:player_attack by @s`);
  await ctx.runtime.wait(100);
}

async function equipSet(ctx: TeaKitTestContext, material: string) {
  await ctx.commands.batch(
    armorSlots.map(([slot, piece]) => `/item replace entity @s armor.${slot} with arcanearmory:${material}_${piece}`),
  );
}

// Set bonuses refresh every second and last two, so a broken set loses its effect within three.
async function waitForEffectGone(ctx: TeaKitTestContext, effectId: string) {
  for (let attempt = 0; attempt < 20; attempt++) {
    if (!(await ctx.player.effects()).some((effect) => effect.effectId === effectId)) {
      return;
    }
    await ctx.runtime.wait(250);
  }
  throw new Error(`${effectId} should end soon after the set is broken`);
}

async function assertAttributeTimesHundred(ctx: TeaKitTestContext, attribute: string, expected: number) {
  await ctx.commands.assert(`/execute store result score #actual aa_traits run attribute @s ${attribute} get 100`);
  await ctx.commands.assert(`/execute if score #actual aa_traits matches ${expected}`);
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
