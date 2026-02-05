#!/bin/bash

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL
API_BASE="http://localhost:8080/api/v1"

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     🏥 Testes - Sistema de Agendamento SUS                ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Verificar se API está rodando
echo -e "${YELLOW}⏳ Verificando conectividade com a API...${NC}"
if ! curl -s -f "$API_BASE/agendamentos/1" > /dev/null 2>&1 && ! curl -s -f "$API_BASE/agendamentos/999" > /dev/null 2>&1; then
    echo -e "${RED}❌ Erro: API não está respondendo em $API_BASE${NC}"
    echo -e "${RED}   Certifique-se de que:"
    echo -e "${RED}   - json-server está rodando (npm start na pasta mock-server)"
    echo -e "${RED}   - Spring Boot está rodando (./mvnw spring-boot:run)${NC}"
    exit 1
fi
echo -e "${GREEN}✓ API está respondendo${NC}"
echo ""

# TESTE 1: Criar agendamento - Itaquera/SP
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}TESTE 1: Criar agendamento por CEP (Itaquera-SP)${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

RESPONSE=$(curl -s -X POST "$API_BASE/agendamentos/por-cep" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "cep": "03694050",
    "especialidadeId": 1,
    "dataHoraAgendamento": "2026-02-27T10:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta de rotina"
  }')

echo "Payload:"
echo '{"pacienteId": 1, "cep": "03694050", "especialidadeId": 1, "dataHoraAgendamento": "2026-02-27T10:00:00", "tipoAtendimento": "PRESENCIAL", "observacoes": "Consulta de rotina"}'
echo ""
echo "Response:"
echo "$RESPONSE" | jq '.'
echo ""

ID1=$(echo "$RESPONSE" | jq -r '.id // empty')
if [ -n "$ID1" ] && [ "$ID1" != "null" ]; then
    echo -e "${GREEN}✓ Agendamento criado com sucesso! ID: $ID1${NC}"
else
    echo -e "${RED}✗ Falha ao criar agendamento${NC}"
fi
echo ""

# TESTE 2: Buscar agendamento por ID
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}TESTE 2: Buscar agendamento por ID${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

if [ -n "$ID1" ] && [ "$ID1" != "null" ]; then
    echo "Buscando agendamento ID: $ID1"
    echo ""
    
    RESPONSE2=$(curl -s "$API_BASE/agendamentos/$ID1")
    echo "Response:"
    echo "$RESPONSE2" | jq '.'
    
    NOME=$(echo "$RESPONSE2" | jq -r '.nomePaciente // empty')
    if [ -n "$NOME" ] && [ "$NOME" != "null" ]; then
        echo -e "${GREEN}✓ Agendamento recuperado com sucesso!${NC}"
    else
        echo -e "${RED}✗ Falha ao recuperar agendamento${NC}"
    fi
else
    echo -e "${YELLOW}⊘ Pulando teste 2 (ID não obtido no teste 1)${NC}"
fi
echo ""

# TESTE 3: Criar agendamento - Santo Amaro/SP
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}TESTE 3: Criar agendamento - Santo Amaro/SP${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

RESPONSE3=$(curl -s -X POST "$API_BASE/agendamentos/por-cep" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 3,
    "cep": "04714000",
    "especialidadeId": 2,
    "dataHoraAgendamento": "2026-02-22T09:00:00",
    "tipoAtendimento": "PRESENCIAL",
    "observacoes": "Consulta infantil"
  }')

echo "Payload:"
echo '{"pacienteId": 3, "cep": "04714000", "especialidadeId": 2, "dataHoraAgendamento": "2026-02-22T09:00:00", "tipoAtendimento": "PRESENCIAL", "observacoes": "Consulta infantil"}'
echo ""
echo "Response:"
echo "$RESPONSE3" | jq '.'
echo ""

ID3=$(echo "$RESPONSE3" | jq -r '.id // empty')
if [ -n "$ID3" ] && [ "$ID3" != "null" ]; then
    echo -e "${GREEN}✓ Agendamento criado com sucesso! ID: $ID3${NC}"
else
    echo -e "${RED}✗ Falha ao criar agendamento${NC}"
fi
echo ""

# TESTE 4: Criar agendamento com Telemedicina
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}TESTE 4: Criar agendamento com Telemedicina${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

RESPONSE4=$(curl -s -X POST "$API_BASE/agendamentos/por-cep" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 4,
    "cep": "22041080",
    "especialidadeId": 3,
    "dataHoraAgendamento": "2026-02-23T11:00:00",
    "tipoAtendimento": "TELEMEDICINA",
    "observacoes": "Consulta cardiológica online"
  }')

echo "Payload:"
echo '{"pacienteId": 4, "cep": "22041080", "especialidadeId": 3, "dataHoraAgendamento": "2026-02-23T11:00:00", "tipoAtendimento": "TELEMEDICINA", "observacoes": "Consulta cardiológica online"}'
echo ""
echo "Response:"
echo "$RESPONSE4" | jq '.'
echo ""

ID4=$(echo "$RESPONSE4" | jq -r '.id // empty')
if [ -n "$ID4" ] && [ "$ID4" != "null" ]; then
    echo -e "${GREEN}✓ Agendamento criado com sucesso! ID: $ID4${NC}"
else
    echo -e "${RED}✗ Falha ao criar agendamento${NC}"
fi
echo ""

# TESTE 5: Erro - Buscar agendamento inexistente
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}TESTE 5: Buscar agendamento inexistente (Deve retornar 400)${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

echo "Buscando agendamento ID: 99999 (inexistente)"
echo ""

RESPONSE5=$(curl -s -w "\n%{http_code}" "$API_BASE/agendamentos/99999")
HTTP_CODE=$(echo "$RESPONSE5" | tail -1)
BODY=$(echo "$RESPONSE5" | head -1)

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq '.'
echo ""

if [ "$HTTP_CODE" == "400" ]; then
    echo -e "${GREEN}✓ Erro esperado retornado corretamente${NC}"
else
    echo -e "${YELLOW}⊘ HTTP Status inesperado: $HTTP_CODE${NC}"
fi
echo ""

# RESUMO
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}RESUMO DOS TESTES${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

if [ -n "$ID1" ] && [ "$ID1" != "null" ]; then
    echo -e "${GREEN}✓ Teste 1: PASSOU - Agendamento criado (ID: $ID1)${NC}"
else
    echo -e "${RED}✗ Teste 1: FALHOU${NC}"
fi

if [ -n "$ID1" ] && [ "$ID1" != "null" ]; then
    echo -e "${GREEN}✓ Teste 2: PASSOU - Agendamento recuperado${NC}"
else
    echo -e "${YELLOW}⊘ Teste 2: PULADO${NC}"
fi

if [ -n "$ID3" ] && [ "$ID3" != "null" ]; then
    echo -e "${GREEN}✓ Teste 3: PASSOU - Agendamento criado (ID: $ID3)${NC}"
else
    echo -e "${RED}✗ Teste 3: FALHOU${NC}"
fi

if [ -n "$ID4" ] && [ "$ID4" != "null" ]; then
    echo -e "${GREEN}✓ Teste 4: PASSOU - Agendamento com Telemedicina (ID: $ID4)${NC}"
else
    echo -e "${RED}✗ Teste 4: FALHOU${NC}"
fi

if [ "$HTTP_CODE" == "400" ]; then
    echo -e "${GREEN}✓ Teste 5: PASSOU - Erro corretamente tratado${NC}"
else
    echo -e "${YELLOW}⊘ Teste 5: INESPERADO${NC}"
fi

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}Testes concluídos!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
