#!/usr/bin/env bash
set -euo pipefail
cd /workspaces/Periodics
test "$(git branch --show-current)" = "v2-development" || { echo "Switch to v2-development first."; exit 1; }
cp -R Periodic_v2_Update/. .
rm -rf Periodic_v2_Update Periodic_v2_Update.zip
chmod +x gradlew
git add .
git commit -m "Build Periodic v2 interface and element explorer"
git push origin v2-development
echo "V2 pushed. Open GitHub Actions and download Periodic-v2-debug-apk after the build passes."
