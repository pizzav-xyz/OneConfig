#!/usr/bin/env bash
set -euo pipefail

TEST_NAME="${1:-configui}"
WORLD_NAME="${2:-}"
TIMEOUT=300
LOGFILE="minecraft/run/logs/latest.log"
# Fallback for legacy run dir
if [ ! -d "minecraft/run" ]; then LOGFILE="run/logs/latest.log"; fi

# Quick-play requires Java 25 for 26.2
if [ -d "/usr/lib/jvm/java-25-openjdk" ]; then export JAVA_HOME=/usr/lib/jvm/java-25-openjdk; export PATH=$JAVA_HOME/bin:$PATH; fi

echo "=== Building ==="
./gradlew :minecraft:26.2-fabric:build --no-daemon -q 2>/dev/null || ./gradlew build --no-daemon -q

echo "=== Running E2E test: ${TEST_NAME} (world: ${WORLD_NAME:-auto}) ==="

rm -f "${LOGFILE}"

# Set system properties for the E2E test
export ONECONFIG_E2E_TEST="${TEST_NAME}"
if [ -n "${WORLD_NAME}" ]; then
    export ONECONFIG_E2E_TEST_WORLD="${WORLD_NAME}"
fi

# Run the client with test properties (quick-play via buildSrc programArgs, offline)
./gradlew :minecraft:26.2-fabric:runClient \
    -Pdevauth=false \
    -Doneconfig.test=true \
    -Doneconfig.e2e.test="${TEST_NAME}" \
    ${WORLD_NAME:+-Doneconfig.e2e.test.world="${WORLD_NAME}"} \
    --no-daemon &
CLIENT_PID=$!

# Tail the log, wait for result or timeout
RESULT=""
ELAPSED=0
while [ $ELAPSED -lt $TIMEOUT ]; do
    if [ -f "${LOGFILE}" ]; then
        if grep -q "\[TEST PASS\] all" "${LOGFILE}" 2>/dev/null; then
            RESULT="PASS"
            break
        fi
        if grep -q "\[TEST FAIL\] all" "${LOGFILE}" 2>/dev/null; then
            RESULT="FAIL"
            break
        fi
    fi
    sleep 1
    ELAPSED=$((ELAPSED + 1))
done

# Cleanup
kill "${CLIENT_PID}" 2>/dev/null || true
wait "${CLIENT_PID}" 2>/dev/null || true

# Report screenshots (Java-window pinned via ScreenshotHelper, stonecutter run dir is minecraft/run)
for dir in "minecraft/run/screenshots" "minecraft/run/run/screenshots" "run/screenshots"; do
    if [ -d "$dir" ]; then
        SCREENSHOTS=$(find "$dir" -name "*.png" -mmin -5 2>/dev/null | head -20 || true)
        if [ -n "${SCREENSHOTS}" ]; then
            echo "=== Screenshots captured (Java-window pinned) in $dir ==="
            echo "${SCREENSHOTS}"
            ls -lh $SCREENSHOTS 2>&1 | head -20 || true
        fi
    fi
done

if [ "$RESULT" = "PASS" ]; then
    echo "=== E2E TEST PASSED: ${TEST_NAME} ==="
    exit 0
elif [ "$RESULT" = "FAIL" ]; then
    echo "=== E2E TEST FAILED: ${TEST_NAME} ==="
    grep "\[TEST FAIL\]" "${LOGFILE}" 2>/dev/null || true
    exit 1
else
    echo "=== TIMEOUT after ${TIMEOUT}s ==="
    exit 2
fi
