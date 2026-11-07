#!/usr/bin/env bash
# Run by the Android emulator action as ONE shell command, not line-by-line.
set -euo pipefail

PKG="com.playereden.gridmates.wordsearch.qa"
SAVEPATH="files/gridmates_wordsearch_solo_v1.json"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
BASELINE="/tmp/ws05j-v20.apk"
UPGRADE="/tmp/ws05j-v21.apk"

adb install "$BASELINE"
adb shell am start -W -n "$PKG/$ACTIVITY"
adb shell am force-stop "$PKG"

# Valid legacy schema-4 unfinished Solo run and chosen local profile identity.
fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"EASY","seed":42,"found":[],"hints":{},"seconds":42},"results":[],"daily":[],"identity":{"name":"Upgrade QA","portraitId":"woodland_owl"}}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVEPATH'"
adb shell "run-as $PKG cat $SAVEPATH" | tr -d '\r' > /tmp/ws05j-before.json
grep -F '"name":"Upgrade QA"' /tmp/ws05j-before.json

adb install -r "$UPGRADE" | tee /tmp/ws05j-install.txt
grep -F 'Success' /tmp/ws05j-install.txt
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 2
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG"

adb shell "run-as $PKG cat $SAVEPATH" | tr -d '\r' > /tmp/ws05j-after.json
cmp -s /tmp/ws05j-before.json /tmp/ws05j-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=21'

echo 'PASS: same-signature v20 to v21 in-place upgrade retained the private schema-4 save and launched the app.'
