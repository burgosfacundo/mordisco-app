#!/usr/bin/env bash

readonly DEMO_PROJECT_NAME='mordisco-demo'
DEMO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

_demo_compose() {
  local compose_args=(compose --project-directory "$DEMO_ROOT")
  if [[ -f "$DEMO_ROOT/.env" ]]; then
    compose_args+=(--env-file "$DEMO_ROOT/.env")
  fi
  compose_args+=(-p "$DEMO_PROJECT_NAME" -f "$DEMO_ROOT/docker-compose.yml")
  docker "${compose_args[@]}" "$@"
}

_demo_generate_jwt() {
  local candidate
  candidate="$(JWT_SECRET="${JWT_SECRET:-bootstrap-unused-only}" _demo_compose run --rm --no-deps \
    --entrypoint sh mysql -c 'od -An -N32 -tx1 /dev/urandom | tr -d " \\n"')" || {
    printf 'ERROR: could not generate the demo signing key inside a container.\n' >&2
    return 1
  }
  candidate="${candidate//[[:space:]]/}"
  if [[ ! "$candidate" =~ ^[[:xdigit:]]{64}$ ]]; then
    printf 'ERROR: the container returned invalid demo signing key material.\n' >&2
    return 1
  fi
  export JWT_SECRET="$candidate"
}

_demo_wait_for_mysql() {
  local count
  printf '   Waiting for MySQL...\n'
  for ((count = 1; count <= 60; count++)); do
    if _demo_compose exec -T mysql mysqladmin ping --host=127.0.0.1 --connect-timeout=3 --silent >/dev/null 2>&1; then
      printf '   MySQL is ready.\n'
      return 0
    fi
    sleep 2
  done
  printf 'ERROR: MySQL did not become ready in time. Inspect: docker compose -p %s logs mysql\n' \
    "$DEMO_PROJECT_NAME" >&2
  return 1
}

_demo_probe_http() {
  local label="$1" url="$2" count
  printf '   Waiting for %s...\n' "$label"
  for ((count = 1; count <= 60; count++)); do
    if _demo_compose run --rm --no-deps probe --fail --silent --show-error \
      --max-time 5 --output /dev/null "$url" >/dev/null 2>&1; then
      printf '   %s is ready.\n' "$label"
      return 0
    fi
    sleep 2
  done
  printf 'ERROR: %s did not become ready in time. Inspect: docker compose -p %s logs %s\n' \
    "$label" "$DEMO_PROJECT_NAME" "$label" >&2
  return 1
}

_demo_seed() {
  local runner seed_result
  runner=$(cat <<'DEMO_SEED_RUNNER'
set -euo pipefail
seed_file="$(mktemp)"
locked=0
mysql_in=''
mysql_out=''
mysql_pid=''
cleanup() {
  local status=$? attempts=0
  # Closing the socket rolls back an open transaction and releases the advisory lock.
  if [[ -n "$mysql_in" ]]; then exec {mysql_in}>&- 2>/dev/null || true; fi
  if [[ -n "$mysql_out" ]]; then exec {mysql_out}<&- 2>/dev/null || true; fi
  if [[ -n "$mysql_pid" ]] && kill -0 "$mysql_pid" 2>/dev/null; then
    kill -TERM "$mysql_pid" 2>/dev/null || true
    while kill -0 "$mysql_pid" 2>/dev/null && (( attempts < 10 )); do
      sleep 0.2
      attempts=$((attempts + 1))
    done
    kill -KILL "$mysql_pid" 2>/dev/null || true
  fi
  rm -f "$seed_file"
  return "$status"
}
trap cleanup EXIT
# The host streams only the existing SQL seed to this temporary file inside the MySQL container.
cat > "$seed_file"
# Lock, checks, seed statements, and receipt all use this one MySQL connection.
coproc DEMO_DB { MYSQL_PWD="$MYSQL_PASSWORD" mysql --default-character-set=utf8mb4 --protocol=socket --connect-timeout=5 \
  --user="$MYSQL_USER" "$MYSQL_DATABASE" --batch --skip-column-names --silent --unbuffered 2>/dev/null; }
mysql_in="${DEMO_DB[1]}"
mysql_out="${DEMO_DB[0]}"
mysql_pid="$DEMO_DB_PID"
send_sql() { printf '%s\n' "$1" >&"$mysql_in"; }
read_result() { local wait_seconds="${2:-15}"; IFS= read -r -t "$wait_seconds" "$1" <&"$mysql_out"; }
send_sql "SELECT GET_LOCK('mordisco-demo-seed-v1', 30);"
read_result lock_result 35
if [[ "$lock_result" != 1 ]]; then
  printf 'ERROR: could not acquire the demo seed lock; no seed data was written.\n' >&2
  exit 1
fi
locked=1
send_sql "SELECT COUNT(*) FROM demo_seed_receipts WHERE seed_id='recruiter-demo-v1';"
read_result receipt_count || { printf 'ERROR: could not inspect demo seed receipt.\n' >&2; exit 1; }
if [[ "$receipt_count" == 1 ]]; then
  send_sql "SELECT RELEASE_LOCK('mordisco-demo-seed-v1');"
  read_result _release_result || true
  locked=0
  exit 73
fi
if [[ "$receipt_count" != 0 ]]; then
  printf 'ERROR: invalid demo seed receipt state; inspect the demo database before retrying.\n' >&2
  exit 1
fi
send_sql "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE() AND engine='InnoDB' AND table_name IN ('calificaciones_pedido','calificaciones_repartidor','configuracion_sistema','direcciones','ganancias_repartidor','horarios_atencion','imagenes','menus','pagos','pedidos','productos','productos_pedidos','promocion_productos','promociones','restaurantes','roles','usuarios','demo_seed_receipts');"
read_result engine_count || { printf 'ERROR: could not verify transactional demo tables.\n' >&2; exit 1; }
if [[ "$engine_count" != 18 ]]; then
  printf 'ERROR: demo seed requires all seed tables to use InnoDB; no seed data was written.\n' >&2
  exit 1
fi
send_sql 'SELECT COUNT(*) FROM usuarios;'
read_result user_count || { printf 'ERROR: could not inspect existing demo users.\n' >&2; exit 1; }
if [[ ! "$user_count" =~ ^[0-9]+$ ]]; then
  printf 'ERROR: the demo database returned an invalid user count.\n' >&2
  exit 1
fi
if (( user_count > 0 )); then
  printf 'ERROR: demo users exist without a completion receipt. Refusing to reseed; use ./reset-demo.sh after confirming data can be erased.\n' >&2
  exit 1
fi
send_sql 'START TRANSACTION;'
while IFS= read -r line || [[ -n "$line" ]]; do
  send_sql "$line"
done < "$seed_file"
send_sql "INSERT INTO demo_seed_receipts (seed_id) VALUES ('recruiter-demo-v1');"
send_sql 'COMMIT;'
send_sql "SELECT 'SEEDED';"
read_result seed_result 120 || { printf 'ERROR: demo seed failed or timed out; the transaction was rolled back. Retry ./start.sh after checking Docker logs.\n' >&2; exit 1; }
if [[ "$seed_result" != SEEDED ]]; then
  printf 'ERROR: demo seed did not confirm its commit; inspect the demo database before retrying.\n' >&2
  exit 1
fi
send_sql "SELECT RELEASE_LOCK('mordisco-demo-seed-v1');"
read_result _release_result || { printf 'ERROR: seed committed but lock release was not confirmed. Retry ./start.sh.\n' >&2; exit 1; }
locked=0
exit 0
DEMO_SEED_RUNNER
)

  # Install the idempotent receipt table before entering the seed transaction.
  # shellcheck disable=SC2016 # Expand these variables inside the container, not on the host.
  _demo_compose exec -T mysql sh -c \
    'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql --default-character-set=utf8mb4 --protocol=socket --user="$MYSQL_USER" "$MYSQL_DATABASE"' \
    < "$DEMO_ROOT/scripts/demo-seed.sql" >/dev/null 2>&1 || {
      printf 'ERROR: could not prepare demo seed receipt storage.\n' >&2
      return 1
    }

  if _demo_compose exec -T mysql bash -c "$runner" \
    < "$DEMO_ROOT/mordisco-api/src/main/resources/data.sql"; then
    printf 'Demo seed committed.\n'
  else
    seed_result=$?
    if [[ "$seed_result" == 73 ]]; then
      printf 'Demo seed receipt found; seed skipped.\n'
    else
      printf 'ERROR: demo seed failed; the transaction was not recorded. Retry only after checking the database state.\n' >&2
      return 1
    fi
  fi
}

demo_start() {
  if ! docker info >/dev/null 2>&1; then
    printf 'ERROR: Docker is unavailable. Start Docker Desktop and try again.\n' >&2
    return 1
  fi
  if [[ -z "${JWT_SECRET:-}" ]]; then
    _demo_generate_jwt || return 1
  elif [[ "${#JWT_SECRET}" -lt 32 ]]; then
    printf 'ERROR: JWT_SECRET must contain at least 32 characters.\n' >&2
    return 1
  fi
  printf 'Starting the Mordisco demo without removing existing data...\n'
  _demo_compose up --build -d || return 1
  _demo_wait_for_mysql || return 1
  _demo_probe_http backend 'http://backend:8080/api/restaurantes?page=0&size=1' || return 1
  _demo_probe_http frontend 'http://frontend/' || return 1
  _demo_probe_http mailpit 'http://mailpit:8025/' || return 1
  _demo_seed || return 1
  printf '\nDemo is ready:\n  App:      http://localhost:4200\n  API:      http://localhost:8080\n  Mailpit:  http://localhost:8025\n'
}

demo_stop() {
  printf 'Stopping the Mordisco demo; database volume will be preserved...\n'
  JWT_SECRET="${JWT_SECRET:-teardown-only-unused-key-not-for-startup}" _demo_compose down
}

demo_reset() {
  local confirmation="${1:-}"
  if [[ "$confirmation" != "$DEMO_PROJECT_NAME" ]]; then
    printf 'Reset cancelled. To permanently erase this demo database, confirm by typing: %s\n' \
      "$DEMO_PROJECT_NAME" >&2
    return 1
  fi
  printf 'Removing only Compose project %s and its volumes...\n' "$DEMO_PROJECT_NAME"
  JWT_SECRET="${JWT_SECRET:-teardown-only-unused-key-not-for-startup}" \
    _demo_compose down --volumes --remove-orphans
}
