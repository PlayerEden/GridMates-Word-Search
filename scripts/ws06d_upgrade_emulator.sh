#!/usr/bin/env bash
# Exact previous stable-signed v0.7.0 -> WS-06D v0.7.1 on a real API35
# emulator, preserving existing schema-4 Solo/Daily/portrait/Race journal.
set -euo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
SAVE="files/gridmates_wordsearch_solo_v1.json"

adb install "$OLD"
adb shell am start -W -n "$PKG/$ACTIVITY"
adb shell am force-stop "$PKG"

fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"MEDIUM","seed":42,"found":[],"hints":{},"seconds":96,"board":"BOOK_CLASSIC","challenge":"CLASSIC"},"results":[],"daily":[],"identity":{"name":"Preserved WS06C Profile","portraitId":"woodland_owl"},"races":[{"room":"ws06d-legacy-race","puzzle":"wordsearch:v2:en:GARDEN:BOOK_SHORT:RELAXED:1234","digest":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","theme":"GARDEN","board":"BOOK_SHORT","challenge":"RELAXED","seat":"HOST","own":8,"partner":4,"completedAt":1700000000000}]}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06d-before.json

adb install -r "$NEW" | tee /tmp/ws06d-update.log
grep -F 'Success' /tmp/ws06d-update.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 2
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws06d-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=25'

python3 - <<'PY'
import json
with open('/tmp/ws06d-before.json') as f: old = json.load(f)
with open('/tmp/ws06d-after.json') as f: new = json.load(f)
for field in ("schema","active","results","daily","identity","races"):
    assert old[field] == new[field], f'Upgrade altered previously saved {field}'
assert new.get('duels', []) == [], 'Upgrade invented Duel results'
assert new["races"][0]["own"] == 8
assert new["races"][0]["partner"] == 4
print('PASS: real v0.7.0 installed in place to v0.7.1; Solo/Daily/identity/Versus journal retained.')
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-06C full private-duel and historical Android instrumentation suite.'
