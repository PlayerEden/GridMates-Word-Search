#!/usr/bin/env bash
# Exact previous stable-signed v0.7.6 -> WS-07A v0.7.7 on a real API35
# emulator, preserving schema-4 Solo/Daily/profile plus both Race/Duel journals.
set -euxo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
SAVE="files/gridmates_wordsearch_solo_v1.json"

adb install "$OLD"
adb shell am start -W -n "$PKG/$ACTIVITY"
# A freshly installed old app can return from am start before Compose has
# initialized its files/ directory on slower API35 emulators.
sleep 3
adb shell am force-stop "$PKG"
adb shell "run-as $PKG sh -c 'mkdir -p files'"
echo 'WS-07A: old signed package started; private files directory prepared.'

fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"MEDIUM","seed":42,"found":[],"hints":{},"seconds":96,"board":"BOOK_CLASSIC","challenge":"CLASSIC"},"results":[],"daily":[],"identity":{"name":"Preserved WS06G Profile","portraitId":"woodland_owl"},"races":[{"room":"ws07a-legacy-race","puzzle":"wordsearch:v2:en:GARDEN:BOOK_SHORT:RELAXED:1234","digest":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","theme":"GARDEN","board":"BOOK_SHORT","challenge":"RELAXED","seat":"HOST","own":8,"partner":4,"completedAt":1700000000000}],"duels":[{"room":"ws07a-legacy-duel","puzzle":"wordsearch:v2:en:GARDEN:BOOK_SHORT:RELAXED:5678","digest":"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","theme":"GARDEN","board":"BOOK_SHORT","challenge":"RELAXED","seat":"HOST","winner":"HOST","own":12,"partner":4,"completedAt":1700000001000}]}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"
echo 'WS-07A: legacy Race + Duel fixture written.'
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws07a-before.json

adb install -r "$NEW" | tee /tmp/ws07a-update.log
grep -F 'Success' /tmp/ws07a-update.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 2
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws07a-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=31'

python3 - <<'PY'
import json
with open('/tmp/ws07a-before.json') as f: old = json.load(f)
with open('/tmp/ws07a-after.json') as f: new = json.load(f)
for field in ("schema","active","results","daily","identity","races","duels"):
    assert old[field] == new[field], f'Upgrade altered previously saved {field}'
assert len(new.get('duels', [])) == 1, 'Upgrade lost Duel result'
assert new["races"][0]["own"] == 8
assert new["duels"][0]["own"] == 12
assert new["races"][0]["partner"] == 4
print('PASS: real v0.7.6 installed in place to v0.7.7; Solo/Daily/identity/Versus journal retained.')
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-07A touch-input regression, fixed-layout navigation and cumulative instrumentation suite.'
