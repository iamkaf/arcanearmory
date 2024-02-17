#!/usr/bin/env node

const args = process.argv.slice(2);

const itemName = args.join(" ");

const template = `
  "item.arcanearmory.template": "Template",
  "tooltip.arcanearmory.material.template.ingot": "This is the Template material.",
  "block.arcanearmory.template_block": "Template Block",
  "block.arcanearmory.template_ore": "Template Ore",
  "item.arcanearmory.template_helmet": "Template Helmet",
  "item.arcanearmory.template_chestplate": "Template Chestplate",
  "item.arcanearmory.template_leggings": "Template Leggings",
  "item.arcanearmory.template_boots": "Template Boots",
  "item.arcanearmory.template_shield": "Template Shield",
  "item.arcanearmory.template_sword": "Template Sword",
  "item.arcanearmory.template_shovel": "Template Shovel",
  "item.arcanearmory.template_pickaxe": "Template Pickaxe",
  "item.arcanearmory.template_axe": "Template Axe",
  "item.arcanearmory.template_hoe": "Template Hoe",
  "item.arcanearmory.template_bow": "Template Bow",`;

const replaced = template
  .replace(/template/g, itemName.toLowerCase().replace(" ", "_"))
  .replace(/Template/g, itemName);

console.log(replaced);
