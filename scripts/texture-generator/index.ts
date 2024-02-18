import { copyReplacementsAfterGeneration } from './file-operations';
import { generateTextures } from './image-processing';

generateTextures().then((generatedFilesCount) => {
  copyReplacementsAfterGeneration();
  console.log(`Done! Generated ${generatedFilesCount} files`);
});
