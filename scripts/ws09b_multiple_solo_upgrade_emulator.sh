#!/usr/bin/env bash
# Real v0.8.7/code41 -> WS-09B v0.8.8/code42 stable-QA-signer upgrade
# with *valid, nonempty* partially found schema-4 Solo and both legacy Daily entries.
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
fixture="$(cat app/src/androidTest/assets/ws09a2_legacy_partial_v086.json)"
printf '%s' "$fixture" | adb shell "run-as $PKG sh -c 'cat > $SAVE'"

# Force actual old APK to read and flush this valid partially played profile.
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09b-before.json

python3 - <<'PY'
import json
with open('/tmp/ws09b-before.json', encoding='utf-8') as f: old = json.load(f)
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
assert old["identity"]["name"] == "WS09A2 Progress Tester"
assert len(old["races"]) == len(old["duels"]) == 1
print("PASS: actual v0.8.7 app read/flushed valid partial Solo and Daily content.")
PY

adb install -r "$NEW" | tee /tmp/ws09b-update.log
grep -F 'Success' /tmp/ws09b-update.log
adb shell am start -W -n "$PKG/$ACTIVITY"
sleep 3
adb shell pidof "$PKG" >/dev/null
# Let onStop commit decoded/validated profile back to disk; a truncation
# of found words, hints or records is caught by the exact JSON comparison.
adb shell input keyevent KEYCODE_HOME
sleep 2
adb shell am force-stop "$PKG"
adb shell "run-as $PKG cat $SAVE" | tr -d '\r' > /tmp/ws09b-after.json
adb shell dumpsys package "$PKG" | grep -F 'versionCode=42'

python3 - <<'PY'
import json
with open('/tmp/ws09b-before.json', encoding='utf-8') as f: old = json.load(f)
with open('/tmp/ws09b-after.json', encoding='utf-8') as f: new = json.load(f)
assert old["schema"] == 4, "Historical saved profile must begin on schema 4"
assert new["schema"] == 5, "New app did not migrate legacy profile to schema 5"
assert new["pausedSolo"] == [], "Migration fabricated or silently lost paused puzzle entries"
for field in ("active", "results", "daily", "identity", "races", "duels"):
    assert old[field] == new[field], f'Actual APK upgrade modified {field}'
assert new["active"]["found"] == ["RACCOON"]
assert new["active"]["hints"] == {"SAPLING": 2}
assert new["daily"][0]["run"]["found"] == ["PEONY"]
assert new["daily"][1]["run"]["found"] == ["BADGER"]
print("PASS: same-signer v0.8.7 -> v0.8.8 migrated schema4 to schema5; partial Solo, both Daily and Versus saves intact after onStop flush.")
PY

gradle --no-daemon :app:connectedDebugAndroidTest
echo 'PASS: WS-09B multiple Solo schema5 save tests, v1/v2/Daily goldens, signed partial schema4 migration and cumulative instrumentation suite.'
