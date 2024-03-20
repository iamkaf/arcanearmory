import { ensureDirExists } from './file-operations';
import { getItemModelFilePath } from './paths';
import { MaterialDatabase } from './types';
import fs from 'fs/promises';

const database: MaterialDatabase = require('../database.json');

export async function generateModels() {
  let generatedCount = 0;
  for (const material of database.materials) {
    console.log(`Generating models for ${material.name}`);

    // bows (bow, bow_pulling_0, bow_pulling_1, bow_pulling_2)
    const types = [
      ['bow', bow],
      ['bow_pulling_0', bow_pulling_0],
      ['bow_pulling_1', bow_pulling_1],
      ['bow_pulling_2', bow_pulling_2],
    ];

    for (const [itemType, json] of types) {
      const outFile = getItemModelFilePath(material, itemType);
      ensureDirExists(outFile);
      await fs.writeFile(outFile, JSON.stringify((json as any)(material), null, 2), 'utf-8');
      generatedCount++;
    }
  }

  return generatedCount;
}

const bow = ({ name }) => ({
  parent: 'item/generated',
  textures: {
    layer0: `arcanearmory:item/${name}_bow`,
    layer1: `arcanearmory:item/${name}_bow_overlay`,
  },
  display: {
    thirdperson_righthand: {
      rotation: [-80, 260, -40],
      translation: [-1, -2, 2.5],
      scale: [0.9, 0.9, 0.9],
    },
    thirdperson_lefthand: {
      rotation: [-80, -280, 40],
      translation: [-1, -2, 2.5],
      scale: [0.9, 0.9, 0.9],
    },
    firstperson_righthand: {
      rotation: [0, -90, 25],
      translation: [1.13, 3.2, 1.13],
      scale: [0.68, 0.68, 0.68],
    },
    firstperson_lefthand: {
      rotation: [0, 90, -25],
      translation: [1.13, 3.2, 1.13],
      scale: [0.68, 0.68, 0.68],
    },
  },
  overrides: [
    {
      predicate: {
        pulling: 1,
      },
      model: `arcanearmory:item/${name}_bow_pulling_0`,
    },
    {
      predicate: {
        pulling: 1,
        pull: 0.65,
      },
      model: `arcanearmory:item/${name}_bow_pulling_1`,
    },
    {
      predicate: {
        pulling: 1,
        pull: 0.9,
      },
      model: `arcanearmory:item/${name}_bow_pulling_2`,
    },
  ],
});

const bow_pulling_0 = ({ name }) => ({
  parent: `arcanearmory:item/${name}_bow`,
  textures: {
    layer0: `arcanearmory:item/${name}_bow_pulling_0`,
    layer1: `arcanearmory:item/${name}_bow_pulling_0_overlay`,
  },
});

const bow_pulling_1 = ({ name }) => ({
  parent: `arcanearmory:item/${name}_bow`,
  textures: {
    layer0: `arcanearmory:item/${name}_bow_pulling_1`,
    layer1: `arcanearmory:item/${name}_bow_pulling_1_overlay`,
  },
});

const bow_pulling_2 = ({ name }) => ({
  parent: `arcanearmory:item/${name}_bow`,
  textures: {
    layer0: `arcanearmory:item/${name}_bow_pulling_2`,
    layer1: `arcanearmory:item/${name}_bow_pulling_2_overlay`,
  },
});
