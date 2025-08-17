#!/usr/bin/env node

// Inject fingerprint into build assets
import { writeFileSync, readFileSync, existsSync } from 'fs';
import { execSync } from 'child_process';

try {
  // Generate current fingerprint
  const fingerprintJson = execSync('node scripts/generateFingerprint.mjs', { encoding: 'utf8' });
  const fingerprint = JSON.parse(fingerprintJson);
  
  // Update runtime fingerprint
  const runtimePath = 'core/fingerprintRuntime.js';
  if (existsSync(runtimePath)) {
    const content = `// Fingerprint runtime
export const currentFingerprintSegments = ${JSON.stringify(fingerprint, null, 2)};`;
    writeFileSync(runtimePath, content);
    console.log('Fingerprint injected into', runtimePath);
  }
} catch (error) {
  console.warn('Failed to inject fingerprint:', error.message);
}