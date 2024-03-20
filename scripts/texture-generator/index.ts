import { copyReplacementsAfterGeneration } from './file-operations';
import { generateTextures } from './image-processing';
import { generateModels } from './model-generator';

generateTextures()
  .then((generatedFilesCount) => {
    copyReplacementsAfterGeneration();
    console.log(`Done! Generated ${generatedFilesCount} textures`);
    return generateModels();
  })
  .then((generatedFilesCount) => {
    console.log(`Done! Generated ${generatedFilesCount} json models`);
  });
