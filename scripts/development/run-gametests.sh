#!/usr/bin/env bash
# Runs the GameTests. The game exits with success even when the mod fails to load, so a run only
# passes when the log confirms that every test passed.
set -euo pipefail

readonly SUCCESS_PATTERN='All [0-9]+ required tests passed'

root="$(git rev-parse --show-toplevel)"
readonly root
readonly log_file="${root}/build/gametest.log"
# The world keeps the saved networks, so every run starts from a new one, as in the CI (answer 96).
readonly world_dir="${root}/run/gametest/world"

mkdir -p "$(dirname "${log_file}")"
rm -rf "${world_dir}"
"${root}/gradlew" --project-dir "${root}" runGameTestServer | tee "${log_file}"

if ! grep -Eq "${SUCCESS_PATTERN}" "${log_file}"; then
  {
    echo "run-gametests: the log does not confirm that the tests passed."
    echo "If it cites 'invalid dist', common code referenced a client class."
    echo "Full log: ${log_file}"
  } >&2
  exit 1
fi
