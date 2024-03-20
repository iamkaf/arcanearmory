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
    result[`item.arcanearmory.${material.name}_ingot`] = `${trainCase(material.name).replace('-', ' ')} Ingot`;
  } else {
    result[`item.arcanearmory.${material.name}`] = `${trainCase(material.name).replace('-', ' ')}`;
  }
  result[`item.arcanearmory.${material.name}_nugget`] = `${trainCase(material.name).replace('-', ' ')} Nugget`;
  result[`item.arcanearmory.raw_${material.name}`] = `Raw ${trainCase(material.name).replace('-', ' ')}`;
  result[`block.arcanearmory.raw_${material.name}_block`] = `Raw ${trainCase(material.name).replace('-', ' ')} Block`;
  result[`block.arcanearmory.${material.name}_block`] = `${trainCase(material.name).replace('-', ' ')} Block`;
  result[`block.arcanearmory.${material.name}_ore`] = `${trainCase(material.name).replace('-', ' ')} Ore`;
  result[`block.arcanearmory.deepslate_${material.name}_ore`] = `Deepslate ${trainCase(material.name).replace('-', ' ')} Ore`;
  result[`item.arcanearmory.${material.name}_helmet`] = `${trainCase(material.name).replace('-', ' ')} Helmet`;
  result[`item.arcanearmory.${material.name}_chestplate`] = `${trainCase(material.name).replace('-', ' ')} Chestplate`;
  result[`item.arcanearmory.${material.name}_leggings`] = `${trainCase(material.name).replace('-', ' ')} Leggings`;
  result[`item.arcanearmory.${material.name}_boots`] = `${trainCase(material.name).replace('-', ' ')} Boots`;
  result[`item.arcanearmory.${material.name}_shield`] = `${trainCase(material.name).replace('-', ' ')} Shield`;
  result[`item.arcanearmory.${material.name}_sword`] = `${trainCase(material.name).replace('-', ' ')} Sword`;
  result[`item.arcanearmory.${material.name}_shovel`] = `${trainCase(material.name).replace('-', ' ')} Shovel`;
  result[`item.arcanearmory.${material.name}_pickaxe`] = `${trainCase(material.name).replace('-', ' ')} Pickaxe`;
  result[`item.arcanearmory.${material.name}_axe`] = `${trainCase(material.name).replace('-', ' ')} Axe`;
  result[`item.arcanearmory.${material.name}_hoe`] = `${trainCase(material.name).replace('-', ' ')} Hoe`;
  result[`item.arcanearmory.${material.name}_hammer`] = `${trainCase(material.name).replace('-', ' ')} Hammer`;
  result[`item.arcanearmory.${material.name}_bow`] = `${trainCase(material.name).replace('-', ' ')} Bow`;
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
