import fs from 'fs';
import { getGeneratedDir, getReplacementsDir } from './paths';

export function copyReplacementsAfterGeneration() {
  const replacementsDir = getReplacementsDir();
  const generatedDir = getGeneratedDir();

  if (!fs.existsSync(replacementsDir)) {
    fs.mkdirSync(replacementsDir, { recursive: true });
  }

  // copy entire replacements directory into the generated directory
  fs.cpSync(replacementsDir, generatedDir, { recursive: true });
}

export function ensureDirExists(path) {
  // replaces / or \ and everything after it with an empty string
  const dir = path.replace(/(\/|\\)[^/\\]+$/, '');
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}
