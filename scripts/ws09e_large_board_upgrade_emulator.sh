#!/usr/bin/env bash
# Real v0.9.0/code44 -> WS-09E v0.9.1/code45 stable-QA-signer upgrade
# with *valid, nonempty* partially found schema-5 Solo and two paused games plus both Daily entries.
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
fixture="$(cat app/src/androidTest/assets/ws09c_schema5_multisolo_v088.json)"
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"

# Force actual old APK to read and flush this valid partially played profile.
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09e-before.json

python3 - <<'PY'
import json
with open('/tmp/ws09e-before.json', encoding='utf-8') as f: old = json.load(f)
assert old["schema"] == 5
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
assert len(old["pausedSolo"]) == 2, "Old v0.8.8 must accept two paused games"
assert old["pausedSolo"][0]["found"] == ["SHUTTLE"]
assert old["pausedSolo"][0]["hints"] == {"CRATER": 2}
assert old["pausedSolo"][1]["found"] == ["BUTTERFLY"]
assert old["pausedSolo"][1]["hints"] == {"LAVENDER": 1}
assert old["identity"]["name"] == "WS09A2 Progress Tester"
assert len(old["races"]) == len(old["duels"]) == 1
print("PASS: actual v0.9.0 app read/flushed active/paused Solo and Daily content.")
PY

adb install -r "$NEW" | tee /tmp/ws09e-update.log
grep -F 'Success' /tmp/ws09e-update.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell pidof "$PKG" >/dev/null
# Let onStop commit decoded/validated profile back to disk; a truncation
# of found words, hints or records is caught by the exact JSON comparison.
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09e-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=45'

python3 - <<'PY'
import json
with open('/tmp/ws09e-before.json', encoding='utf-8') as f: old = json.load(f)
with open('/tmp/ws09e-after.json', encoding='utf-8') as f: new = json.load(f)
assert old["schema"] == 5, "Historical saved profile must begin on schema 5"
assert new["schema"] == 5, "New app did not preserve schema 5"
assert len(new["pausedSolo"]) == 2, "Update erased paused Solo puzzles"
for field in ("active", "pausedSolo", "results", "daily", "identity", "races", "duels"):
    assert old[field] == new[field], f'Actual APK upgrade modified {field}'
assert new["active"]["found"] == ["RACCOON"]
assert new["active"]["hints"] == {"SAPLING": 2}
assert new["daily"][0]["run"]["found"] == ["PEONY"]
assert new["daily"][1]["run"]["found"] == ["BADGER"]
print("PASS: same-signer v0.9.0 -> v0.9.1 retained active Solo, two paused puzzles, Daily and Versus records.")
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-09E versioned v3 25/30 puzzles, frozen old generator goldens and signed multi-session upgrade; cumulative Android suite.'
