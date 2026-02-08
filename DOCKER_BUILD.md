# Docker Build - Instruções

## Arquitetura do Projeto

O projeto está organizado com um módulo compartilhado (`sus-shared-domain`) que contém as entidades e enums compartilhados entre os serviços:

```
fiap-hackaton/
├── sus-shared-domain/          # Módulo compartilhado
│   └── src/main/java/br/gov/sus/shared/domain/
│       ├── entity/             # Entidades compartilhadas
│       └── enums/              # Enums compartilhados
├── sus-agendamento/            # Serviço de Agendamento
├── sus-telemedicina/           # Serviço de Telemedicina
└── docker-compose.yml
```

## Build dos Containers

Os Dockerfiles foram configurados para:

1. **Primeiro Stage**: Build
   - Copiar o projeto `sus-shared-domain` para dentro do container
   - Instalar o `sus-shared-domain` no repositório Maven local do container
   - Copiar e compilar o projeto específico (agendamento ou telemedicina)
   - Usar Java 22 e Spring Boot 3.3.0

2. **Segundo Stage**: Runtime
   - Criar uma imagem leve apenas com o JRE 22
   - Copiar o JAR compilado
   - Configurar usuário não-root (apenas no agendamento)

## Como Buildar

### Buildar todos os serviços:
```bash
docker-compose build
```

### Buildar um serviço específico:
```bash
docker-compose build app-agendamento
docker-compose build app-telemedicina
```

### Iniciar os serviços:
```bash
docker-compose up -d
```

### Verificar logs:
```bash
docker-compose logs -f app-agendamento
docker-compose logs -f app-telemedicina
```

## Contexto de Build

O contexto de build está configurado na **raiz do projeto** (`.`) no `docker-compose.yml` para permitir acesso ao `sus-shared-domain`:

```yaml
app-agendamento:
  build:
    context: .                           # Contexto na raiz
    dockerfile: sus-agendamento/Dockerfile
```

Isso permite que os Dockerfiles possam copiar o `sus-shared-domain` corretamente.

## Versões

- **Java**: 22
- **Spring Boot**: 3.3.0
- **Maven**: 3.9
- **PostgreSQL**: 16
- **Node**: 18 (para mock API)

## Troubleshooting

### Erro: "Cannot find sus-shared-domain"
- Verifique se o contexto de build no `docker-compose.yml` está definido como `.` (raiz do projeto)
- Certifique-se de que a pasta `sus-shared-domain` existe na raiz do projeto

### Erro: "Unsupported class file major version"
- O projeto foi atualizado para Java 22. Certifique-se de ter o Docker atualizado
- As imagens base usam `eclipse-temurin:22`

### Rebuild completo
Se houver problemas, faça um rebuild completo:
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

