#!/usr/bin/env bash
# Real historical WS-06A APK -> WS-06B v0.6.1: same signer, preserve saved
# content when an additive schema-4 "races" field is introduced.
set -euo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
SAVE="files/gridmates_wordsearch_solo_v1.json"

adb install "$OLD"
adb shell am start -W -n "$PKG/$ACTIVITY"
adb shell am force-stop "$PKG"

# An actual older schema-4 profile does not yet have the new races field.
fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"MEDIUM","seed":42,"found":[],"hints":{},"seconds":96,"board":"BOOK_CLASSIC","challenge":"CLASSIC"},"results":[],"daily":[],"identity":{"name":"Preserved WS06A Profile","portraitId":"woodland_owl"}}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06b-before.json

adb install -r "$NEW" | tee /tmp/ws06b-install.log
grep -F 'Success' /tmp/ws06b-install.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 2
adb shell pidof "$PKG" > /dev/null
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06b-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=23'

# File bytes may legitimately differ because the new version adds "races":[].
# Verify all previous data exactly and the new journal starts empty.
python3 - <<'PY'
import json
with open('/tmp/ws06b-before.json') as file: old = json.load(file)
with open('/tmp/ws06b-after.json') as file: new = json.load(file)
for field in ('schema', 'active', 'results', 'daily', 'identity'):
    assert old[field] == new[field], f'Lost saved data in {field}'
assert new.get('races', []) == [], 'Unexpected fabricated multiplayer result'
print('PASS: real old v0.6.0 updated to v0.6.1, retained all old profile fields and clean new journal.')
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-06B complete Android instrumentation regression suite.'
