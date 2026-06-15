# Plano de Teste - Pitanga WebRunner & Monitor

Este documento descreve a estratégia de Verificação e Validação (V&V) aplicada ao sistema de emulação virtual e análise de circuitos digitais (RTL).

## 1. Ferramentas Utilizadas
* **Análise Estática:** Checkstyle e Maven Compiler Plugin (integrados ao ciclo de build).
* **Testes Unitários:** JUnit 5 e Mockito (foco nas lógicas de parsing de Verilog e matriz de estímulos).
* **Testes de Componentes/API:** REST Assured (validação dos contratos JSON entre os microsserviços).
* **Testes de Sistema/E2E:** Cypress (simulação do fluxo do estudante enviando um projeto no front-end).
* **Integração Contínua (CI/CD):** GitHub Actions.

## 2. Requisitos, Restrições e Configurações de Ambiente
* **Restrição de Linguagem:** O ambiente de testes rodará sob o Java JDK 17.
* **Banco de Dados de Teste:** Os testes de persistência do `project-manager` utilizarão o H2 Database em memória ou Testcontainers (PostgreSQL) para não poluir o banco de produção.
* **Dependência de EDA:** O `analytics-engine` pressupõe a existência de ferramentas de linting RTL no host (ex: Verilator) ou a simulação do retorno destas via *Mocks* durante os testes unitários em CI.

## 3. Matriz de Funcionalidades vs. Testes

| Funcionalidade / Módulo | Teste Estático | Teste Unitário | Teste de API | Teste de Sistema |
| :--- | :---: | :---: | :---: | :---: |
| **Service A:** CRUD de Projetos | Sim (Checkstyle) | Sim | Sim (REST Assured) | Não aplicável |
| **Service A:** Configuração da Placa Virtual | Sim (Checkstyle) | Sim | Sim (REST Assured) | Não aplicável |
| **Service B:** Validação Sintática Verilog | Sim (Checkstyle) | Sim (Mocks) | Sim (REST Assured) | Não aplicável |
| **Service B:** Geração de Waveform/Tabela | Sim (Checkstyle) | Sim (Lógica) | Sim (REST Assured) | Não aplicável |
| **Fluxo Completo:** Upload RTL até Relatório | Sim | Não aplicável | Não aplicável | Sim (Cypress) |
