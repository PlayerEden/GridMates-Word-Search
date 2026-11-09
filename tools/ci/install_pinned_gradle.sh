#!/usr/bin/env bash
# WS-14D: install the exact official Gradle release without relying on the
# setup-gradle action's distribution-provisioning endpoint (intermittent 504).
# The checksum is independently pinned from https://gradle.org/release-checksums/
set -euo pipefail

version="8.11.1"
expected_sha="f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6"
zip_path="${RUNNER_TEMP:?}/gradle-${version}-bin.zip"
install_root="${RUNNER_TEMP}/gradle-${version}"

echo "Installing official Gradle ${version} (checksum pinned)"
if ! curl --fail --location --retry 1 --connect-timeout 15 --max-time 240 \
  "https://downloads.gradle.org/distributions/gradle-${version}-bin.zip" \
  --output "${zip_path}"; then
  echo "::warning::Official direct distribution endpoint failed; try official services endpoint once"
  rm -f "${zip_path}"
  curl --fail --location --retry 1 --connect-timeout 15 --max-time 240 \
    "https://services.gradle.org/distributions/gradle-${version}-bin.zip" \
    --output "${zip_path}"
fi

echo "${expected_sha}  ${zip_path}" | sha256sum --check --status || {
  echo "::error::Gradle ${version} official SHA-256 verification failed"
  exit 1
}
unzip -q "${zip_path}" -d "${RUNNER_TEMP}"
test -x "${install_root}/bin/gradle" || {
  echo "::error::Gradle executable missing after verified unzip"
  exit 1
}
echo "${install_root}/bin" >> "${GITHUB_PATH:?}"
echo "Installed SHA-256 verified Gradle ${version}; setup-gradle will only configure caching."
