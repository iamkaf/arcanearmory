import path from 'path';
import { MOD_ID } from './constants';
import { makeFileName } from './naming';
import { ItemType } from './types';

const TEXTURE_TEMPLATES_BASE_DIR = './templates/texture';
const GENERATED_RESOURCES_BASE_DIR = '../../src/main/resources';
const REPLACEMENTS_RESOURCES_BASE_DIR = './replacements/resources';

export function getTemplateTexturePath(material, itemType: ItemType, gemOverlay = false) {
  const variant = (defaultTexture: string) => {
    if (material.overrides) {
      const override = material.overrides.find((o) => o.item_type === itemType);
      if (override) {
        return `variant/${override.variant_texture}.png`;
      }
    }
    return defaultTexture;
  };

  const getPath = () => {
    switch (itemType) {
      case 'helmet':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_helmet.png')}`;
      case 'chestplate':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_chestplate.png')}`;
      case 'leggings':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_leggings.png')}`;
      case 'boots':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_boots.png')}`;
      case 'sword':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_sword.png')}`;
      case 'pickaxe':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_pickaxe.png')}`;
      case 'axe':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_axe.png')}`;
      case 'shovel':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_shovel.png')}`;
      case 'hoe':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_hoe.png')}`;
      case 'layer_1':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_layer_1.png')}`;
      case 'layer_2':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_layer_2.png')}`;
      case 'ingot':
        if (material.type === 'gem') return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_gem.png')}`;
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_ingot.png')}`;
      case 'nugget':
        if (material.type === 'gem') return null;
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_nugget.png')}`;
      case 'raw':
        if (gemOverlay) return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_raw_item_gem_overlay.png')}`;
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_raw_item.png')}`;
      case 'block':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_block.png')}`;
      case 'raw_block':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_raw_block.png')}`;
      case 'ore':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_ore.png')}`;
      case 'deepslate_ore':
        return `${TEXTURE_TEMPLATES_BASE_DIR}/${variant('template_deepslate_ore.png')}`;
    }
  };

  const result = getPath();
  if (!result) return null;

  return path.join(__dirname, result);
}

export function getPathToArbitraryTexture(textureName: string) {
  return path.join(__dirname, `${TEXTURE_TEMPLATES_BASE_DIR}/${textureName}.png`);
}

export function getOutFilePath(material, itemType, isOverlay) {
  const isArmorModel = itemType === 'layer_1' || itemType === 'layer_2';
  const isBlock = ['block', 'raw_block', 'ore', 'deepslate_ore'].includes(itemType);
  const fileName = makeFileName(material, itemType, isOverlay);
  if (!fileName) {
    return null;
  }

  let baseDir = 'item';

  if (isArmorModel) {
    baseDir = 'models/armor';
  } else if (isBlock) {
    baseDir = 'block';
  }

  return path.join(__dirname, `${GENERATED_RESOURCES_BASE_DIR}/assets/${MOD_ID}/textures/${baseDir}/${fileName}`);
}
export function getOverlayTemplateTexturePath(material, itemType: string) {
  const TYPES_WITH_OVERLAY = ['sword', 'pickaxe', 'axe', 'shovel', 'hoe'];

  if (itemType === 'raw' && material.type === 'gem') {
    return path.join(__dirname, `${TEXTURE_TEMPLATES_BASE_DIR}/template_raw_item_gem_overlay.png`);
  }

  if (TYPES_WITH_OVERLAY.includes(itemType)) {
    return path.join(__dirname, `${TEXTURE_TEMPLATES_BASE_DIR}/template_${itemType}_overlay.png`);
  }

  return null;
}

export function getGeneratedDir() {
  return path.join(__dirname, GENERATED_RESOURCES_BASE_DIR);
}

export function getReplacementsDir() {
  return path.join(__dirname, REPLACEMENTS_RESOURCES_BASE_DIR);
}
