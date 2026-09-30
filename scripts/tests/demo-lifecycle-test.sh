#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
COMMON="$ROOT/scripts/demo-common.sh"
fail() { printf 'FAIL: %s\n' "$1" >&2; exit 1; }
[[ -f "$COMMON" ]] || fail "shared demo lifecycle is missing"
# shellcheck source=scripts/demo-common.sh
source "$COMMON"

CALLS=()
MYSQL_READY=0
BACKEND_READY=0
FRONTEND_READY=0
MAILPIT_READY=0
SEED_STATE=empty
SEED_CALLS=0
SEED_SKIP_CALLS=0
SEED_SCHEMA_STDIN=''
SEED_DATA_STDIN=''
OBSERVED_JWT=''

# All external Docker calls are simulated. Tests never start or reset a real stack.
docker() {
  local args="$*"
  CALLS+=("docker $args")
  case " $args " in
    *'run --rm --no-deps --entrypoint sh mysql'*)
      # The isolated MySQL container prints only generated entropy for command substitution.
      printf '%064d' 7
      ;;
    *'exec -T mysql mysqladmin ping'*) return "$MYSQL_READY" ;;
    *'run --rm --no-deps probe'*)
      case "$args" in
        *'http://backend:8080/api/restaurantes?page=0&size=1'*) return "$BACKEND_READY" ;;
        *http://frontend/*) return "$FRONTEND_READY" ;;
        *http://mailpit:8025/*) return "$MAILPIT_READY" ;;
      esac
      return 22
      ;;
    *'exec -T mysql sh -c'*)
      SEED_SCHEMA_STDIN="$(command cat)"
      ;;
    *'exec -T mysql bash -c'*)
      [[ "$args" == *'--unbuffered'* ]] || fail "MySQL responses must be unbuffered"
      [[ "$args" == *'read -r -t'* ]] || fail "MySQL response reads must be bounded"
      [[ "$args" == *'kill -TERM'* && "$args" == *'kill -KILL'* ]] || fail "hung MySQL processes must be terminated"
      OBSERVED_JWT="${JWT_SECRET:-}"
      SEED_DATA_STDIN="$(command cat)"
      SEED_CALLS=$((SEED_CALLS + 1))
      [[ "$SEED_DATA_STDIN" == *'INSERT INTO '* ]] || return 90
      case "$SEED_STATE" in
        unmarked-users)
          printf 'Demo database contains users without the completion receipt; reset manually.\n' >&2
          return 1
          ;;
        fail-before)
          SEED_STATE=empty
          printf 'Demo seed failed; transaction rolled back.\n' >&2
          return 1
          ;;
        fail-after-users)
          # Model an INSERT failure after writes; InnoDB transaction rollback leaves an empty DB.
          SEED_STATE=empty
          printf 'Demo seed failed; transaction rolled back.\n' >&2
          return 1
          ;;
        receipt)
          SEED_SKIP_CALLS=$((SEED_SKIP_CALLS + 1))
          printf 'ALREADY_SEEDED\n'
          return 73
          ;;
        empty)
          SEED_STATE=receipt
          printf 'SEEDED\n'
          ;;
      esac
      ;;
    *)
      ;;
  esac
}
# Host HTTP and entropy tools are intentionally forbidden by the contract.
curl() { fail "readiness must run in containers, not host curl"; }
openssl() { fail "JWT entropy must come from a container, not host openssl"; }
sleep() { :; }

contains_call() {
  local expected="$1" call
  for call in "${CALLS[@]}"; do [[ "$call" == *"$expected"* ]] && return 0; done
  return 1
}
assert_compose_context() {
  local call
  for call in "${CALLS[@]}"; do
    [[ "$call" == *'compose '* ]] || continue
    [[ "$call" == *'--project-directory '*"$ROOT"*'-p mordisco-demo'*'-f '*"$ROOT/docker-compose.yml"* ]] ||
      fail "Compose invocation is not rooted and project-scoped: $call"
  done
}

unset JWT_SECRET || true
CALLS=()
demo_start >/dev/null || fail "fresh demo should start and seed successfully"
OBSERVED_JWT="${JWT_SECRET:-}"
[[ "${#OBSERVED_JWT}" -ge 32 && "$OBSERVED_JWT" =~ ^[[:xdigit:]]+$ ]] || fail "start must provide validated generated JWT entropy"
contains_call 'mysqladmin ping' || fail "MySQL readiness must be container-based"
contains_call 'http://backend:8080/api/restaurantes?page=0&size=1' || fail "API readiness must request a paginated data-backed route"
contains_call 'http://frontend/' || fail "frontend readiness must be checked"
contains_call 'http://mailpit:8025/' || fail "Mailpit readiness must be checked"
contains_call '--max-time' || fail "every HTTP probe must have a request timeout"
[[ "$SEED_SCHEMA_STDIN" == *'demo_seed_receipts'* && "$SEED_SCHEMA_STDIN" == *'ENGINE=InnoDB'* ]] || fail "receipt schema must be installed before seeding"
[[ "$SEED_DATA_STDIN" == *'INSERT INTO '* ]] || fail "existing data.sql must stream to container stdin"
contains_call 'exec mysql --default-character-set=utf8mb4' || fail "receipt-table SQL client must explicitly use UTF-8"
contains_call 'mysql --default-character-set=utf8mb4 --protocol=socket' || fail "transactional seed SQL client must explicitly use UTF-8"
expected_seed_sql="$(command cat "$ROOT/mordisco-api/src/main/resources/data.sql")"
[[ "$SEED_DATA_STDIN" == "$expected_seed_sql" ]] || fail "seed SQL must reach the MySQL container unchanged"
assert_compose_context

# A successful retry after failures before or after attempted user inserts must seed exactly once.
for failure in fail-before fail-after-users; do
  SEED_STATE="$failure"
  before=$SEED_CALLS
  if demo_start >/dev/null 2>&1; then fail "$failure must not report success"; fi
  [[ "$SEED_STATE" == empty ]] || fail "$failure must leave the database retryable"
  demo_start >/dev/null 2>&1 || fail "$failure must permit a later successful retry"
  [[ "$SEED_STATE" == receipt ]] || fail "successful retry must atomically record completion"
  [[ "$SEED_CALLS" -eq $((before + 2)) ]] || fail "failed seed must be retried exactly once"
done

# Existing users without a receipt are never overwritten by another seed.
SEED_STATE=unmarked-users
before=$SEED_CALLS
if demo_start >/dev/null 2>&1; then fail "unmarked existing users must require manual reset"; fi
[[ "$SEED_CALLS" -eq $((before + 1)) ]] || fail "existing unmarked data must not be seeded again"

# Completed receipt skips duplicate inserts on subsequent starts.
SEED_STATE=receipt
before=$SEED_CALLS
before_skips=$SEED_SKIP_CALLS
demo_start >/dev/null 2>&1 || fail "a completed demo must start successfully"
[[ "$SEED_CALLS" -eq $((before + 1)) ]] || fail "receipt inspection must happen on every start"
[[ "$SEED_SKIP_CALLS" -eq $((before_skips + 1)) ]] || fail "completed receipt must skip duplicate inserts"

# Each unavailable service blocks seeding; probes run with a bounded container request timeout.
for service in backend frontend mailpit; do
  case "$service" in
    backend) BACKEND_READY=22 ;;
    frontend) FRONTEND_READY=22 ;;
    mailpit) MAILPIT_READY=22 ;;
  esac
  before=$SEED_CALLS
  if demo_start >/dev/null 2>&1; then fail "unready $service must fail startup"; fi
  [[ "$SEED_CALLS" -eq "$before" ]] || fail "unready $service must not seed"
  case "$service" in
    backend) BACKEND_READY=0 ;;
    frontend) FRONTEND_READY=0 ;;
    mailpit) MAILPIT_READY=0 ;;
  esac
done

# Teardown works without a JWT, preserves volumes, and reset requires exact confirmation.
unset JWT_SECRET || true
CALLS=()
demo_stop >/dev/null 2>&1 || fail "stop must not need a JWT"
contains_call ' down' || fail "stop must bring the project down"
! contains_call --volumes || fail "stop must preserve the database volume"
CALLS=()
if demo_reset '' >/dev/null 2>&1; then fail "reset must reject missing confirmation"; fi
! contains_call ' down' || fail "unconfirmed reset must not invoke Compose down"
CALLS=()
demo_reset mordisco-demo >/dev/null 2>&1 || fail "reset must accept exact confirmation"
contains_call 'down --volumes --remove-orphans' || fail "reset must remove only this project's volumes"
! contains_call 'prune' || fail "reset must never invoke global Docker prune"
assert_compose_context

printf 'PASS: persistent demo lifecycle and transactional seed contract\n'
