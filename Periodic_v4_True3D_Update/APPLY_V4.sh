#!/usr/bin/env bash
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
patch_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cp -R "$patch_dir/app/." app/
mkdir -p .github/workflows
cp "$patch_dir/.github/workflows/build-v4.yml" .github/workflows/build-v4.yml
git add app .github/workflows/build-v4.yml
git commit -m "Install Periodic v4 true 3D engine" || true
echo
echo "V4 is committed locally. Push or Sync Changes to start the APK build."
