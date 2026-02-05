#!/bin/bash

echo "=== Iniciando Spring Boot em background ==="
cd /home/guilherme/hackaton_fiap/sus
nohup ./mvnw spring-boot:run > /tmp/spring-boot.log 2>&1 &
SPRING_PID=$!
echo "Spring Boot PID: $SPRING_PID"

echo "Aguardando Spring Boot iniciar (15 segundos)..."
sleep 15

echo ""
echo "=== TESTE 1: Listar Especialidades ==="
curl -s http://localhost:8080/api/v1/especialidades | jq '.[0:3]' 2>/dev/null || curl -s http://localhost:8080/api/v1/especialidades | head -c 300

echo ""
echo ""
echo "=== TESTE 2: Listar Unidades do Mock ==="
curl -s http://localhost:3000/unidades | jq '.' 2>/dev/null || curl -s http://localhost:3000/unidades

echo ""
echo ""
echo "=== TESTE 3: Criar Agendamento ==="
RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "profissionalId": 1,
    "codigoCnesUnidade": "2269473",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-10T10:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina"
  }')
echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"

echo ""
echo ""
echo "=== TESTE 4: Buscar Agendamentos do Paciente 1 ==="
curl -s http://localhost:8080/api/v1/agendamentos/paciente/1 | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/v1/agendamentos/paciente/1

echo ""
echo "=== Testes concluídos ==="
echo "Spring Boot rodando com PID: $SPRING_PID"
echo "Para parar: kill $SPRING_PID"
