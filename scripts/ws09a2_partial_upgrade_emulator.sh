#!/usr/bin/env bash
# Real v0.8.6/code40 -> WS-09A2 v0.8.7/code41 stable-QA-signer upgrade
# with *valid, nonempty* partially found Solo and both legacy Daily entries.
set -euxo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
SAVE="files/gridmates_wordsearch_solo_v1.json"

adb install "$OLD"
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell am force-stop "$PKG"
adb shell "run-as $PKG sh -c 'mkdir -p files'"

# Target IDs were captured from an unchanged v0.8.6 generator, not invented.
# JSON numbers for 64-bit Daily seeds MUST be decimal literals (not JS doubles).
fixture='{"schema":4,"active":{"theme":"WOODLAND","difficulty":"MEDIUM","seed":42,"found":["RACCOON"],"hints":{"SAPLING":2},"seconds":734,"board":"BOOK_CLASSIC","challenge":"CLASSIC"},"results":[],"daily":[{"date":"2026-11-05","run":{"theme":"GARDEN","difficulty":"EASY","seed":2984779710590232787,"found":["PEONY"],"hints":{"BLOOM":1},"seconds":83}},{"date":"2026-11-06","run":{"theme":"WOODLAND","difficulty":"EASY","seed":4153857886328470466,"found":["BADGER"],"hints":{"MOUSE":2},"seconds":117}}],"identity":{"name":"WS09A2 Preserved Puzzle Player","portraitId":"woodland_owl"},"races":[{"room":"ws09a2-legacy-race","puzzle":"wordsearch:v2:en:GARDEN:BOOK_SHORT:RELAXED:1234","digest":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","theme":"GARDEN","board":"BOOK_SHORT","challenge":"RELAXED","seat":"HOST","own":8,"partner":4,"completedAt":1700000000000}],"duels":[{"room":"ws09a2-legacy-duel","puzzle":"wordsearch:v2:en:GARDEN:BOOK_SHORT:RELAXED:5678","digest":"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","theme":"GARDEN","board":"BOOK_SHORT","challenge":"RELAXED","seat":"HOST","winner":"HOST","own":12,"partner":4,"completedAt":1700000001000}]}'
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"

# Force actual old APK to read and flush this valid partially played profile.
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09a2-before.json

python3 - <<'PY'
import json
with open('/tmp/ws09a2-before.json', encoding='utf-8') as f: old = json.load(f)
assert old["schema"] == 4
assert old["active"]["seed"] == 42
assert old["active"]["found"] == ["RACCOON"], "Old APK lost existing found word"
assert old["active"]["hints"] == {"SAPLING": 2}, "Old APK lost saved hint"
assert old["active"]["seconds"] == 734
daily = {d["date"]: d["run"] for d in old["daily"]}
assert set(daily) == {"2026-11-05", "2026-11-06"}
assert daily["2026-11-05"]["seed"] == 2984779710590232787
assert daily["2026-11-05"]["found"] == ["PEONY"]
assert daily["2026-11-05"]["hints"] == {"BLOOM": 1}
assert daily["2026-11-06"]["seed"] == 4153857886328470466
assert daily["2026-11-06"]["found"] == ["BADGER"]
assert daily["2026-11-06"]["hints"] == {"MOUSE": 2}
assert old["identity"]["name"] == "WS09A2 Preserved Puzzle Player"
assert len(old["races"]) == len(old["duels"]) == 1
print("PASS: actual v0.8.6 app read/flushed valid partial Solo and Daily content.")
PY

adb install -r "$NEW" | tee /tmp/ws09a2-update.log
grep -F 'Success' /tmp/ws09a2-update.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell pidof "$PKG" >/dev/null
# Let onStop commit decoded/validated profile back to disk; a truncation
# of found words, hints or records is caught by the exact JSON comparison.
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09a2-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=41'

python3 - <<'PY'
import json
with open('/tmp/ws09a2-before.json', encoding='utf-8') as f: old = json.load(f)
with open('/tmp/ws09a2-after.json', encoding='utf-8') as f: new = json.load(f)
for field in ("schema", "active", "results", "daily", "identity", "races", "duels"):
    assert old[field] == new[field], f'Actual APK upgrade modified {field}'
assert new["active"]["found"] == ["RACCOON"]
assert new["active"]["hints"] == {"SAPLING": 2}
assert new["daily"][0]["run"]["found"] == ["PEONY"]
assert new["daily"][1]["run"]["found"] == ["BADGER"]
print("PASS: same-signer v0.8.6 -> v0.8.7 preserved partial Solo, both Daily and Versus saves after onStop flush.")
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-09A2 frozen historical v1/v2/Daily goldens, real signed partially solved APK upgrade, and cumulative instrumentation suite.'
