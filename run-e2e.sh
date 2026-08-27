#!/usr/bin/env bash
set -euo pipefail

TEST_NAME="${1:-configui}"
WORLD_NAME="${2:-}"
TIMEOUT=300
LOGFILE="run/logs/latest.log"

echo "=== Building ==="
./gradlew :minecraft:1.21.1-fabric:build --no-daemon -q 2>/dev/null || ./gradlew build --no-daemon -q

echo "=== Running E2E test: ${TEST_NAME} (world: ${WORLD_NAME:-auto}) ==="

rm -f "${LOGFILE}"

# Set system properties for the E2E test
export ONECONFIG_E2E_TEST="${TEST_NAME}"
if [ -n "${WORLD_NAME}" ]; then
    export ONECONFIG_E2E_TEST_WORLD="${WORLD_NAME}"
fi

# Run the client with test properties
./gradlew :minecraft:1.21.1-fabric:runClient \
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

# Report screenshots
if [ -d "run/screenshots" ]; then
    SCREENSHOTS=$(find run/screenshots -name "*.png" -mmin -5 2>/dev/null | head -20)
    if [ -n "${SCREENSHOTS}" ]; then
        echo "=== Screenshots captured ==="
        echo "${SCREENSHOTS}"
    fi
fi

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
