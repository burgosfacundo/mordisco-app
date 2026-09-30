#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/demo-common.sh
source "$ROOT/scripts/demo-common.sh"

printf 'WARNING: this permanently deletes the Mordisco demo database and its volume.\n'
read -r -p "Type ${DEMO_PROJECT_NAME} to continue: " confirmation
demo_reset "$confirmation"
