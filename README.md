# FIAP Feedback Report Generator

Este repositório contém o microsserviço de **Geração de relatórios** da plataforma de Feedback. Ele é responsável por gerar o arquivo PDF com o relatório semanal, salvando-o em um bucket S3.

## 📋 Visão Geral

O serviço opera em arquitetura **Serverless** utilizando AWS Lambda e Quarkus. Ele consome recebidas pela lambda report-data-collector (dentro de um fluxo de step functions), prepara um arquivo de relatório PDF com as informações fornecidas e envia para um bucket S3.

### Arquitetura da Solução

```mermaid
graph TD
    %% Atores Externos
    Student((Estudante))
    Admin((Administrador))

    %% Gatilhos de Entrada
    APIGateway_Feedback["API Gateway<br/>(POST /avaliacao)"]
    APIGateway_Admin["API Gateway<br/>(POST /admin/subscribe)"]
    Scheduler(EventBridge Scheduler<br/>Cron Semanal)

    %% BANCO DE DADOS (Centralizado)
    DB[("DynamoDB<br/>Tabela Feedbacks")]

    %% ---------------------------------------------------------
    %% MS 1: INGESTÃO
    %% ---------------------------------------------------------
    subgraph "fiap-feedback-ingest"
        Lambda_Ingest["Lambda: Ingestão"]
    end

    %% ---------------------------------------------------------
    %% MS 4: GESTÃO DE ADMINS
    %% ---------------------------------------------------------
    subgraph "fiap-feedback-admin"
        Lambda_Admin["Lambda: Cadastrar Admin"]
    end

    %% ---------------------------------------------------------
    %% MS 2: NOTIFICAÇÃO (Este Repositório)
    %% ---------------------------------------------------------
    subgraph "fiap-feedback-notifier"
        SQS_Queue[("SQS: FilaUrgencia<br/>(Payload Completo)")]
        Lambda_Notifier["Lambda: Notificação Worker"]
        SNS_Registry[("SNS: Tópico<br/>(Lista de Inscritos)")]
        SES_Service["Amazon SES<br/>(Envio de E-mail)"]
    end

    %% ---------------------------------------------------------
    %% MS 3: RELATÓRIO
    %% ---------------------------------------------------------
    subgraph "fiap-feedback-report"
        Lambda_Report["Lambda: Gerador Relatório"]
        SNS_Reports{"SNS: Tópico<br/>Relatórios"}
    end

    %% --- FLUXOS DE COMUNICAÇÃO ---

    %% Fluxo de Cadastro de Admin (MS 4)
    Admin -->|1. Cadastra E-mail| APIGateway_Admin
    APIGateway_Admin -->|Trigger| Lambda_Admin
    Lambda_Admin -- "2. Cria Subscription" --> SNS_Registry

    %% Fluxo de Entrada de Feedback (MS 1)
    Student -->|3. Envia Feedback| APIGateway_Feedback
    APIGateway_Feedback -->|Trigger| Lambda_Ingest
    
    %% Lógica MS 1
    Lambda_Ingest -->|4. Persiste| DB
    Lambda_Ingest -.->|"5. Se Nota < 5 (JSON Completo)"| SQS_Queue

    %% Lógica MS 2 (Worker com Template HTML)
    SQS_Queue -->|6. Consome| Lambda_Notifier
    Lambda_Notifier -- "7. Busca Lista de E-mails" --> SNS_Registry
    Lambda_Notifier -- "8. Envia HTML Formatado" --> SES_Service
    SES_Service -.->|9. Entrega E-mail| Admin

    %% Lógica MS 3 (Batch)
    Scheduler -->|10. Acorda| Lambda_Report
    Lambda_Report -->|"11. Scan/Query (Leitura)"| DB
    Lambda_Report -->|12. Publica Relatório| SNS_Reports
    SNS_Reports -.->|13. E-mail Semanal| Admin

    %% Estilização Visual
    style Lambda_Ingest fill:#f9f,stroke:#333,stroke-width:2px
    style Lambda_Notifier fill:#f9f,stroke:#333,stroke-width:2px
    style Lambda_Report fill:#f9f,stroke:#333,stroke-width:2px
    style Lambda_Admin fill:#f9f,stroke:#333,stroke-width:2px
    
    style SQS_Queue fill:#ff9900,stroke:#333,stroke-width:2px,color:white
    style SNS_Registry fill:#ff9900,stroke:#333,stroke-width:2px,color:white
    style SNS_Reports fill:#ff9900,stroke:#333,stroke-width:2px,color:white
    style SES_Service fill:#DD344C,stroke:#333,stroke-width:2px,color:white
    
    style DB fill:#336699,stroke:#333,stroke-width:2px,color:white
```

## 🚀 Tecnologias Utilizadas

*   **Java 17**: Linguagem de programação.
*   **Quarkus**: Framework Java Supersônico e Subatômico para microsserviços.
*   **AWS SAM (Serverless Application Model)**: Para IaC (Infraestrutura como Código) e deploy.
*   **AWS Lambda**: Computação serverless.
*   **Amazon S3**: Bucket para armazenamento de arquivos PDF gerados pela lambda.

## ⚙️ Pré-requisitos

*   Java 17 instalado.
*   Maven instalado.
*   AWS CLI configurado com suas credenciais.
*   AWS SAM CLI instalado.
*   Docker (opcional, para testes locais).

## 📦 Como Fazer o Deploy

1.  **Compile o projeto:**
    ```bash
    .\mvnw.cmd clean package -DskipTests
    ```

2.  **Execute o deploy guiado com base no `samconfig.toml` já existente:**
    ```bash
    sam deploy
    ```
## 🧪 Como Testar

Como este serviço faz parte de uma step function, ele não possui um endpoint HTTP direto. Para testá-lo, você deve iniciar a state machine `WeeklyReportStateMachine` para iniciar o fluxo completo (coleta de informações, geração de PDF e envio de relatório).

Outra alternativa é executar a lambda diretamente fornecendo as informações necessárias para que ele gere um PDF no input
**Exemplo de Payload (para utilizar na state machine):**
```json
{
  "reportType": "weekly",
  "timezone": "America/Sao_Paulo",
  "note": "Relatório semanal de urgência"
}

```

**Exemplo de Payload (para utilizar diretamente na lambda):**
```json
{
  "feedbacks": [
    { "id": 1, "message": "Sistema lento", "urgency": "alta", "date": "2025-12-10" },
    { "id": 4, "message": "Sistema não funciona corretamente", "urgency": "alta", "date": "2025-12-10" },
    { "id": 2, "message": "Interface confusa", "urgency": "media", "date": "2025-12-11" },
    { "id": 3, "message": "Interface feia", "urgency": "baixa", "date": "2025-12-12" }
  ],
  "countByDay": {
    "2025-12-10": 1,
    "2025-12-11": 1,
    "2025-12-12": 1
  },
  "countByUrgency": {
    "alta": 2,
    "media": 1,
    "baixa": 1
  }
}

```
**Desenvolvido para o Tech Challenge da FIAP - Fase de Cloud Computing & Serverless.**
