import { trainCase } from 'change-case-all';
import { MaterialDatabase } from '../texture-generator/types';
import fs from 'fs';
import path from 'path';

const database: MaterialDatabase = require('../database.json');

const result = {
  'itemgroup.arcanearmory': 'Arcane Armory',
};

for (const material of database.materials) {
  if (material.type === 'ingot') {
    result[`item.arcanearmory.${material.name}_ingot`] = `${trainCase(material.name).replaceAll('-', ' ')} Ingot`;
  } else {
    result[`item.arcanearmory.${material.name}`] = `${trainCase(material.name).replaceAll('-', ' ')}`;
  }
  result[`item.arcanearmory.${material.name}_nugget`] = `${trainCase(material.name).replaceAll('-', ' ')} Nugget`;
  result[`item.arcanearmory.raw_${material.name}`] = `Raw ${trainCase(material.name).replaceAll('-', ' ')}`;
  result[`block.arcanearmory.raw_${material.name}_block`] = `Raw ${trainCase(material.name).replaceAll('-', ' ')} Block`;
  result[`block.arcanearmory.${material.name}_block`] = `${trainCase(material.name).replaceAll('-', ' ')} Block`;
  result[`block.arcanearmory.${material.name}_ore`] = `${trainCase(material.name).replaceAll('-', ' ')} Ore`;
  result[`block.arcanearmory.deepslate_${material.name}_ore`] = `Deepslate ${trainCase(material.name).replaceAll('-', ' ')} Ore`;
  result[`item.arcanearmory.${material.name}_helmet`] = `${trainCase(material.name).replaceAll('-', ' ')} Helmet`;
  result[`item.arcanearmory.${material.name}_chestplate`] = `${trainCase(material.name).replaceAll('-', ' ')} Chestplate`;
  result[`item.arcanearmory.${material.name}_leggings`] = `${trainCase(material.name).replaceAll('-', ' ')} Leggings`;
  result[`item.arcanearmory.${material.name}_boots`] = `${trainCase(material.name).replaceAll('-', ' ')} Boots`;
  result[`item.arcanearmory.${material.name}_shield`] = `${trainCase(material.name).replaceAll('-', ' ')} Shield`;
  result[`item.arcanearmory.${material.name}_sword`] = `${trainCase(material.name).replaceAll('-', ' ')} Sword`;
  result[`item.arcanearmory.${material.name}_shovel`] = `${trainCase(material.name).replaceAll('-', ' ')} Shovel`;
  result[`item.arcanearmory.${material.name}_pickaxe`] = `${trainCase(material.name).replaceAll('-', ' ')} Pickaxe`;
  result[`item.arcanearmory.${material.name}_axe`] = `${trainCase(material.name).replaceAll('-', ' ')} Axe`;
  result[`item.arcanearmory.${material.name}_hoe`] = `${trainCase(material.name).replaceAll('-', ' ')} Hoe`;
  result[`item.arcanearmory.${material.name}_hammer`] = `${trainCase(material.name).replaceAll('-', ' ')} Hammer`;
  result[`item.arcanearmory.${material.name}_bow`] = `${trainCase(material.name).replaceAll('-', ' ')} Bow`;
}

export function ensureDirExists(path) {
  // replaces / or \ and everything after it with an empty string
  const dir = path.replace(/(\/|\\)[^/\\]+$/, '');
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

ensureDirExists(path.join(__dirname, './generated'));

fs.writeFileSync(path.join(__dirname, './generated', 'en_us.json'), JSON.stringify(result, null, 2));
console.log('Done!');
