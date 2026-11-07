#!/usr/bin/env bash
# Same Bash process throughout: emulator-runner would otherwise discard variables.
set -euo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
SAVE="files/gridmates_wordsearch_solo_v1.json"

# 1. Install the actual previous WS-05J v0.5.9 APK, not a same-source rebuild.
adb install "$OLD"
adb shell am start -W -n "$PKG/$ACTIVITY"
adb shell am force-stop "$PKG"

# 2. Write a schema4 private unfinished puzzle with custom identity.
fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"EASY","seed":42,"found":[],"hints":{},"seconds":96},"results":[],"daily":[],"identity":{"name":"Race Upgrade QA","portraitId":"woodland_owl"}}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06a-before.json
grep -F '"name":"Race Upgrade QA"' /tmp/ws06a-before.json

# 3. Actual in-place Android package update. No uninstall or data clear.
adb install -r "$NEW" | tee /tmp/ws06a-install.log
grep -F 'Success' /tmp/ws06a-install.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 2
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06a-after.json
cmp -s /tmp/ws06a-before.json /tmp/ws06a-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=22'
echo "PASS: real prior v0.5.9 APK upgraded in place to v0.6.0 with exact saved progress."

# 4. Execute every current Android instrumentation test (v5 packet, UI, regressions).
gradle --no-daemon :app:connectedDebugAndroidTest
echo "PASS: WS-06A device instrumentation suite completed."
