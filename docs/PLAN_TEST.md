# Plano de Testes e Qualidade (V&V) - EDA Flow Manager

## 1. Ferramentas de Teste e Validação
Para garantir a confiabilidade da arquitetura de microsserviços e da interface gráfica, a esteira de qualidade utilizará as seguintes ferramentas:

* **JUnit 5:** Framework base para execução de testes unitários e de integração no ecossistema Java/Quarkus.

* **Mockito:** Biblioteca para criação de objetos simulados (*Mocks* e *Stubs*). Essencial para isolar o `ProjectResource` interceptando as chamadas de rede do `AnalyticsClient`.

* **REST Assured:** Ferramenta para testes de componente/API, validando os contratos JSON (DTOs) e os códigos de status HTTP (200, 400, 500) das rotas `/api/physics` e `/api/projects`.

* **Cypress:** Framework para testes de Sistema (E2E - End-to-End). Utilizado para simular a interação do usuário com a interface gráfica (movimentação dos controles físicos e validação do retorno visual de sucesso/erro).

* **SonarQube / Checkstyle:** Ferramentas de verificação estática para garantir a padronização do código Java e identificar *code smells* ou vulnerabilidades de segurança.

* **GitHub Actions:** Plataforma de Integração Contínua (CI) responsável por rodar toda a suíte de testes automaticamente a cada nova submissão de código.

## 2. Procedimentos e Fluxo de Trabalho
O controle de versão e a integração de novos testes seguirão o fluxo de trabalho baseado em *Pull Requests* (PRs):

* **Branching Model:** O desenvolvimento não será feito diretamente na ramificação principal. Novas suítes de teste serão criadas em *branches* específicas (ex: `feature/testes-unitarios`, `feature/testes-e2e`).
* **Padrão de Commits:** Utilização de *Conventional Commits* para rastreabilidade (ex: `test: implementa teste unitario para o algoritmo DFS`, `ci: configura pipeline do GitHub Actions`).
* **Pull Requests (PR):** Ao abrir um PR para a `main`, a esteira do GitHub Actions será engatilhada automaticamente. 
* **Regra de Merge:** O código só poderá ser integrado à ramificação principal se 100% dos testes da esteira de CI passarem com sucesso (Status: *Build Passing*).

## 3. Requisitos, Restrições e Configurações
* **Isolamento de Banco de Dados:** Os testes de integração do Gerente de Projetos não devem sujar o banco de dados principal. O Quarkus será configurado com o *profile* `%test`, utilizando um banco H2 em memória exclusivo com a política `drop-and-create` a cada execução.
* **Isolamento de Rede:** Os testes do Web Service 1 (Gerente) não podem depender do Web Service 2 (Motor) estar online. O `AnalyticsClient` deve ser obrigatoriamente "mockado" durante os testes unitários.
* **Ambiente de Execução:** É necessário ter o JDK 17+ e o Maven devidamente configurados no *PATH* do sistema operacional para a execução local via `mvn clean test`. Para os testes E2E, o Node.js é pré-requisito para rodar o Cypress.

## 4. Matriz de Funcionalidades vs. Testes (Rastreabilidade)

| Funcionalidade (Módulo) | Tipo de Teste Alvo | Descrição e Critério de Aceite |
| :--- | :--- | :--- |
| **Cálculo da Física (Atraso RC/Isat)** | Unitário | Garantir que o `TransistorResource` aplique a matemática correta dados os valores de Vdd, W e C_load. |
| **Análise de Caminho Crítico (STA)** | Unitário | Submeter um Grafo fictício ao algoritmo DFS do `StaResource` e validar se ele encontra a rota de maior atraso. |
| **Contrato de API Física e Projetos** | Componente (API) | Validar via REST Assured se os endpoints aceitam e devolvem os JSONs estruturados corretamente. |
| **Orquestração de Regra de Negócio** | Integração (com Mock) | Simular um retorno do Motor com *fmax* = 4 GHz e garantir que o `ProjectResource` negue a persistência (FAILED). Simular 15 GHz e garantir persistência (PASSED). |
| **Submissão de Projetos (Interface)** | Sistema (E2E) | Simular o clique no botão "Executar V&V", interceptar a chamada e validar se a mensagem vermelha ou verde é exibida corretamente no HTML. |
| **Padrões de Projeto e Sintaxe** | Verificação Estática | Análise automatizada (linter) para validar cobertura de código e padronização. |