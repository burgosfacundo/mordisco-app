#!/bin/bash

# ============================================
# MORDISCO APP - Script de Inicio
# ============================================

set -e

echo ""
echo "🍕 ============================================"
echo "   MORDISCO APP - Iniciando..."
echo "   ============================================"
echo ""

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    echo "   Por favor, inicia Docker Desktop y vuelve a intentarlo"
    exit 1
fi

echo "🗑️  Limpiando volúmenes anteriores para asegurar datos frescos..."
docker-compose down -v > /dev/null 2>&1 || true

echo "🚀 Iniciando servicios (esto puede tardar 3-5 minutos)..."
echo ""

docker-compose up -d --build

echo ""
echo "⏳ Esperando a que los servicios estén listos..."
echo ""

# Esperar a MySQL
echo "   📦 MySQL..."
MAX_TRIES=30
COUNT=0
until docker-compose exec -T mysql mysqladmin ping -h localhost -u root -proot --silent > /dev/null 2>&1; do
    COUNT=$((COUNT + 1))
    if [ $COUNT -ge $MAX_TRIES ]; then
        echo "   ❌ MySQL tardó demasiado en iniciar"
        exit 1
    fi
    sleep 2
done
echo "   ✅ MySQL listo"

# Esperar a Backend (Spring Boot creará las tablas)
echo "   🔧 Backend (creando tablas)..."
COUNT=0
until curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    COUNT=$((COUNT + 1))
    if [ $COUNT -ge $MAX_TRIES ]; then
        echo "   ❌ Backend tardó demasiado en iniciar"
        echo "   💡 Ver logs: docker-compose logs backend"
        exit 1
    fi
    sleep 3
done
echo "   ✅ Backend listo"

# Ahora cargar los datos
echo "   📊 Cargando datos de prueba..."
USER_COUNT=$(docker-compose exec -T mysql mysql -u mordisco_user -pmordisco_pass mordisco -sN -e "SELECT COUNT(*) FROM usuarios" 2>/dev/null || echo "0")

if [ "$USER_COUNT" -eq "0" ]; then
    docker-compose exec -T mysql mysql -u mordisco_user -pmordisco_pass --default-character-set=utf8mb4 mordisco < mordisco-api/src/main/resources/data.sql 2>/dev/null
    USER_COUNT=$(docker-compose exec -T mysql mysql -u mordisco_user -pmordisco_pass mordisco -sN -e "SELECT COUNT(*) FROM usuarios" 2>/dev/null || echo "0")
    echo "   ✅ Datos cargados: $USER_COUNT usuarios"
else
    echo "   ✅ Base de datos ya contiene $USER_COUNT usuarios"
fi

# Esperar a Frontend
echo "   🎨 Frontend..."
sleep 5
echo "   ✅ Frontend listo"

echo ""
echo "🎉 ============================================"
echo "   ¡TODO LISTO!"
echo "   ============================================"
echo ""
echo "   🌐 Frontend:  http://localhost:4200"
echo "   🔧 Backend:   http://localhost:8080"
echo "   📚 Swagger:   http://localhost:8080/swagger-ui/index.html"
echo ""
echo "   👤 CREDENCIALES DE PRUEBA:"
echo "   ├─ Admin:       mordiscoapp@gmail.com / Admin123!"
echo "   ├─ Restaurante: restaurante1@gmail.com / Mordisco123!"
echo "   ├─ Cliente:     usuario1@gmail.com / Mordisco123!"
echo "   └─ Repartidor:  repartidor1@gmail.com / Mordisco123!"
echo ""
echo "   📖 Más usuarios en: CREDENCIALES.txt"
echo ""
echo "   🛑 Para detener: ./stop.sh"
echo "   📋 Ver logs:     docker-compose logs -f"
echo ""
echo "============================================"
echo ""
