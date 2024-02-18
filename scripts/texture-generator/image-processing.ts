import sharp from 'sharp';
import { Tint, ItemType, Material, MaterialDatabase, Modulate } from './types';
import { ITEM_TYPES, ARMOR_ITEM_TEXTURE_SIZE } from './constants';
import { ensureDirExists } from './file-operations';
import { getTemplateTexturePath, getOutFilePath, getOverlayTemplateTexturePath } from './paths';

const database: MaterialDatabase = require('../database.json');

export async function generateTextures() {
  let generatedFilesCount = 0;

  try {
    for (const material of database.materials) {
      console.log(`Generating texture for ${material.name}`);
      generatedFilesCount += await processMaterialTextures(material);
    }

    return generatedFilesCount;
  } catch (err) {
    console.error(err);
    throw err;
  }
}

async function processMaterialTextures(material: Material): Promise<number> {
  let generatedFilesCount = 0;

  for (const itemType of ITEM_TYPES) {
    const outFile1 = getOutFilePath(material, itemType, false);
    const outFile2 = getOutFilePath(material, itemType, true);

    let templateTexturePath = getTemplateTexturePath(material, itemType);
    const templateOverlayTexturePath = getOverlayTemplateTexturePath(material, itemType);

    if (!templateTexturePath || !outFile1) {
      continue;
    }
    ensureDirExists(outFile1);

    const tint = getTintForCurrentItemType(material, itemType);
    const modulation = getModulationForCurrentItemType(material, itemType);

    // for tools, the base and overlay are flipped
    if (isTool(itemType)) {
      tint.shift_base = !tint.shift_base;
      tint.shift_overlay = !tint.shift_overlay;
    }

    generatedFilesCount += await generateRegularTexture(templateTexturePath, outFile1, tint, modulation);

    if (!outFile2) {
      continue;
    }

    generatedFilesCount += await generateOverlayTexture(templateOverlayTexturePath, outFile2, tint, modulation);
  }

  return generatedFilesCount;
}

function getTintForCurrentItemType(material: Material, itemType: ItemType) {
  const defaultHueShift: Tint = {
    item_type: 'all',
    r: 0,
    g: 0,
    b: 0,
    shift_base: false,
    shift_overlay: false,
  };

  const tint = material.tint?.findLast((hs) => hs.item_type === itemType || hs.item_type === 'all') || defaultHueShift;

  return JSON.parse(JSON.stringify(tint));
}

function getModulationForCurrentItemType(material: Material, itemType: ItemType) {
  const defaultModulation: Modulate = {
    item_type: 'all',
    options: {
      brightness: 1,
      hue: 0,
      saturation: 1,
      lightness: 0,
    },
    modulate_base: false,
    modulate_overlay: false,
  };

  const modulation =
    material.modulate?.findLast((hs) => hs.item_type === itemType || hs.item_type === 'all') || defaultModulation;

  return JSON.parse(JSON.stringify(modulation));
}

async function generateRegularTexture(
  templateTexturePath: string,
  outFile: string,
  tint?: Tint,
  modulation?: Modulate,
): Promise<number> {
  const sharped = await sharp(templateTexturePath);
  if (tint) {
    if (tint.shift_base) {
      sharped.tint({ r: tint.r, g: tint.g, b: tint.b });
    }
  }

  if (modulation) {
    if (modulation.modulate_base) {
      sharped.modulate({
        brightness: modulation.options.brightness,
        hue: modulation.options.hue,
        saturation: modulation.options.saturation,
        lightness: modulation.options.lightness,
      });
    }
  }

  await sharped.toFile(outFile);
  return 1; // Return 1 as one file is generated
}

async function generateOverlayTexture(
  templateTexturePath: string | null,
  outFile: string,
  tint?: Tint,
  modulation?: Modulate,
): Promise<number> {
  const sharpInput = templateTexturePath || {
    create: {
      width: ARMOR_ITEM_TEXTURE_SIZE,
      height: ARMOR_ITEM_TEXTURE_SIZE,
      channels: 4,
      background: { r: 0, g: 0, b: 0, alpha: 0 },
    },
  };

  const sharped = sharp(sharpInput as any);

  if (tint) {
    if (tint.shift_overlay) {
      sharped.tint({ r: tint.r, g: tint.g, b: tint.b });
    }
  }

  if (modulation) {
    if (modulation.modulate_overlay) {
      sharped.modulate({
        brightness: modulation.options.brightness,
        hue: modulation.options.hue,
        saturation: modulation.options.saturation,
        lightness: modulation.options.lightness,
      });
    }
  }

  await sharped.toFile(outFile);
  return 1; // Return 1 as one file is generated
}

function isTool(itemType: ItemType): boolean {
  return ['sword', 'pickaxe', 'axe', 'shovel', 'hoe'].includes(itemType);
}
