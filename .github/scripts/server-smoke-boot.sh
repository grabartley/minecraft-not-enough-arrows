#!/usr/bin/env bash
set -euo pipefail

RUN_DIR=run
BOOT_TIMEOUT="${BOOT_TIMEOUT:-300}"
LOG="$(mktemp)"
FIFO="$(mktemp -u)"

mkdir -p "$RUN_DIR"
echo "eula=true" > "$RUN_DIR/eula.txt"

mkfifo "$FIFO"
./gradlew runServer --no-daemon --stacktrace --args="nogui" < "$FIFO" > >(tee "$LOG") 2>&1 &
SERVER_PID=$!
exec 3> "$FIFO"

deadline=$((SECONDS + BOOT_TIMEOUT))
until grep -q 'Done (' "$LOG"; do
  if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "::error::Server exited before reaching Done"
    exit 1
  fi
  if (( SECONDS >= deadline )); then
    echo "::error::Server did not reach Done within ${BOOT_TIMEOUT}s"
    kill "$SERVER_PID" 2>/dev/null || true
    exit 1
  fi
  sleep 1
done

echo "stop" >&3
exec 3>&-

status=0
wait "$SERVER_PID" || status=$?
rm -f "$FIFO"

if (( status != 0 )); then
  echo "::error::Server exited with status ${status} after stop"
  exit "$status"
fi

for marker in 'Not Enough Arrows server config loaded for world' 'All dimensions are saved'; do
  if ! grep -q "$marker" "$LOG"; then
    echo "::error::Server log is missing '${marker}'"
    exit 1
  fi
done

echo "Server reached Done and stopped cleanly"
