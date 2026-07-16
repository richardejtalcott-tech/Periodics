#!/usr/bin/env bash
set -euo pipefail
VERSION=8.10.2
BASE="${HOME}/.gradle/bootstrap"
DIST="${BASE}/gradle-${VERSION}"
if [ ! -x "${DIST}/bin/gradle" ]; then
  mkdir -p "${BASE}"
  ZIP="${BASE}/gradle-${VERSION}-bin.zip"
  echo "Downloading Gradle ${VERSION}..."
  curl -fL "https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip" -o "${ZIP}"
  unzip -q -o "${ZIP}" -d "${BASE}"
fi
exec "${DIST}/bin/gradle" "$@"
