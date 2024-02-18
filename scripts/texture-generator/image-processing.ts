import sharp from 'sharp';
import { HueShift, ItemType, Material, MaterialDatabase } from './types';
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

    const hueShift = getHueShiftForCurrentItemType(material, itemType);

    generatedFilesCount += await generateRegularTexture(templateTexturePath, outFile1, hueShift.amount);

    if (!outFile2) {
      continue;
    }

    generatedFilesCount += await generateOverlayTexture(templateOverlayTexturePath, outFile2, hueShift.amount);
  }

  return generatedFilesCount;
}

function getHueShiftForCurrentItemType(material: Material, itemType: ItemType) {
  const defaultHueShift = {
    item_type: 'all',
    amount: 0,
    shift_base: false,
    shift_overlay: false,
  };

  return material.hue_shift.findLast((hs) => hs.item_type === itemType || hs.item_type === 'all') || defaultHueShift;
}

async function generateRegularTexture(templateTexturePath: string, outFile: string, hueShift: number): Promise<number> {
  const sharped = await sharp(templateTexturePath);
  if (hueShift !== 0) {
    sharped.modulate({ hue: hueShift });
  }
  await sharped.resize(ARMOR_ITEM_TEXTURE_SIZE, ARMOR_ITEM_TEXTURE_SIZE).toFile(outFile);
  return 1; // Return 1 as one file is generated
}

async function generateOverlayTexture(
  templateTexturePath: string | null,
  outFile: string,
  hueShift: number,
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
  if (hueShift !== 0) {
    sharped.modulate({ hue: hueShift });
  }
  await sharped.toFile(outFile);
  return 1; // Return 1 as one file is generated
}
