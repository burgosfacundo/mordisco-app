#!/bin/bash

# ============================================
# MORDISCO APP - Script de Detención
# ============================================

echo ""
echo "🛑 Deteniendo y eliminando Mordisco App..."
echo ""

docker-compose down -v

echo ""
echo "✅ Todo eliminado (servicios + volúmenes + datos)"
echo ""
echo "   🔄 Para volver a iniciar: ./start.sh"
echo ""
