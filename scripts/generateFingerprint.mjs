#!/usr/bin/env node

// Generate build fingerprint for tracking dependencies and changes
import { createHash } from 'crypto';
import { readFileSync } from 'fs';
import { join } from 'path';

function generateFingerprint() {
  const packageJson = JSON.parse(readFileSync('package.json', 'utf8'));
  const dependencies = Object.entries(packageJson.dependencies || {});
  const devDependencies = Object.entries(packageJson.devDependencies || {});
  
  const fingerprint = {
    timestamp: new Date().toISOString(),
    releaseFingerprint: createHash('md5')
      .update(JSON.stringify([...dependencies, ...devDependencies]))
      .digest('hex'),
    dependenciesList: [...dependencies, ...devDependencies]
  };
  
  if (process.argv.includes('--log')) {
    console.log(JSON.stringify(fingerprint, null, 2));
  } else {
    console.log(JSON.stringify(fingerprint));
  }
}

generateFingerprint();