#!/usr/bin/env bash
# Prueba de humo del microservicio desplegado.
# Uso: bash scripts/smoke-test.sh http://localhost:8080
set -euo pipefail

URL="${1:-http://localhost:8080}"

echo "1) Health check"
for i in $(seq 1 30); do
  if curl -fs "$URL/actuator/health" | grep -q '"status":"UP"'; then
    echo "   UP"
    break
  fi
  [ "$i" -eq 30 ] && { echo "   El servicio no respondió UP"; exit 1; }
  sleep 2
done

echo "2) Crear producto (POST /api/productos)"
CODE=$(curl -s -o /tmp/respuesta.json -w '%{http_code}' -X POST "$URL/api/productos" \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Producto smoke test","stock":5,"precio":990}')
[ "$CODE" = "201" ] || { echo "   Se esperaba 201 y llegó $CODE"; exit 1; }
echo "   201 Created: $(cat /tmp/respuesta.json)"

echo "3) Validación (POST con datos inválidos debe responder 400)"
CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$URL/api/productos" \
  -H 'Content-Type: application/json' -d '{"nombre":"","stock":-1,"precio":0}')
[ "$CODE" = "400" ] || { echo "   Se esperaba 400 y llegó $CODE"; exit 1; }
echo "   400 Bad Request"

echo "4) Listar productos (GET /api/productos)"
CODE=$(curl -s -o /dev/null -w '%{http_code}' "$URL/api/productos")
[ "$CODE" = "200" ] || { echo "   Se esperaba 200 y llegó $CODE"; exit 1; }
echo "   200 OK"

echo "Prueba de humo OK"
