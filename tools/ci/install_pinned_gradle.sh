#!/usr/bin/env bash
# WS-14D: install the exact official Gradle release without relying on the
# setup-gradle action's distribution-provisioning endpoint (intermittent 504).
# The checksum is independently pinned from https://gradle.org/release-checksums/
set -euo pipefail

version="8.11.1"
expected_sha="f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6"
zip_path="${RUNNER_TEMP:?}/gradle-${version}-bin.zip"
install_root="${RUNNER_TEMP}/gradle-${version}"

echo "Installing checksum-pinned Gradle ${version}"
# First use official Gradle distribution endpoints. If BOTH return HTTP 504,
# try two established distribution mirrors. NEVER trust the mirror's own hash:
# the official upstream SHA-256 above is independently pinned.
sources=(
  "https://downloads.gradle.org/distributions/gradle-${version}-bin.zip"
  "https://services.gradle.org/distributions/gradle-${version}-bin.zip"
  "https://mirrors.aliyun.com/gradle/distributions/v${version}/gradle-${version}-bin.zip"
  "https://mirrors.huaweicloud.com/repository/toolkit/gradle/gradle-${version}-bin.zip"
)
downloaded=0
for source in "${sources[@]}"; do
  echo "Trying distribution source: ${source}"
  rm -f "${zip_path}"
  if curl --fail --location --silent --show-error --connect-timeout 12 --max-time 180 \
    "${source}" --output "${zip_path}"; then
    downloaded=1
    break
  fi
  echo "::warning::Gradle distribution source unavailable (next bounded source)"
done
if [ "${downloaded}" -ne 1 ]; then
  echo "::error::All configured Gradle distribution sources were unavailable"
  exit 1
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
