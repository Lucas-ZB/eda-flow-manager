# Visão Geral do Sistema: EDA Flow Manager

## 1. Descrição do Sistema
O **EDA Flow Manager** é um ecossistema de software voltado para *Electronic Design Automation* (EDA) estruturado em uma arquitetura de microsserviços. O sistema tem como objetivo principal orquestrar, simular e validar projetos de Microeletrônica. 

A aplicação permite que engenheiros analisem o comportamento físico de transistores CMOS (calculando correntes de saturação e atraso de propagação RC) e realizem a Análise de Tempo Estático (Static Timing Analysis - STA) para determinar a Frequência Máxima ($f_{max}$) de operação do circuito projetado. O sistema aplica regras rígidas de validação de *timing* antes de permitir a persistência dos dados em um banco relacional.

## 2. Tecnologias Utilizadas e Ecossistema

### Back-end (Microsserviços)
* **Java 17 & Quarkus Framework:** Escolhidos pela alta performance computacional, baixo consumo de memória e suporte nativo a microsserviços em nuvem.
* **REST Client (MicroProfile):** Utilizado para estabelecer a comunicação síncrona, desacoplada e fortemente tipada entre o Gerente de Projetos (Web Service 1) e o Motor Analítico (Web Service 2).
* **Hibernate ORM com Panache:** Implementação do padrão *Active Record* para simplificar o mapeamento objeto-relacional e manipulação de dados.
* **Banco de Dados H2:** Banco de dados relacional operando *in-memory*, configurado estrategicamente com políticas de ciclo de vida isoladas para desenvolvimento (`drop-and-create`) e testes.

### Front-end Reativo
* **HTML5, CSS3 e JavaScript (ES6):** Interface limpa e responsiva que consome as APIs assíncronas via *Fetch API*.
* **Chart.js:** Renderização do "Osciloscópio Virtual", plotando as curvas de carga capacitiva dos transistores em tempo real de acordo com as respostas do motor físico.

## 3. Funcionalidades Core

1. **Simulação da Física de Semicondutores:** Integração direta entre os controles deslizantes da interface (Tensão $V_{dd}$, Largura $W$ e Capacitância $C_{load}$) e o motor físico. O atraso de propagação ($t_{pd}$) calculado governa a simulação.
2. **Análise de Tempo Estático (STA):** Mapeamento do circuito em estruturas de Grafos. O algoritmo calcula o caminho crítico do chip para definir a viabilidade de frequência.
3. **Persistência Baseada em Regras (Gateway):** O microsserviço de gerência intercepta a requisição, analisa o *timing* e aplica a regra de negócio: circuitos com frequência máxima inferior a $10.0$ GHz são categorizados como `FAILED (Timing Violation)` e bloqueados de salvar, enquanto circuitos acima da meta recebem o status `PASSED`.

## 4. Arquitetura de Validação e Verificação (V&V) Implementada

O projeto adota uma pirâmide de testes automatizados e ferramentas de qualidade que garantem a estabilidade da aplicação:

### Verificação Estática (Linter)
* **Checkstyle:** Integrado diretamente ao ciclo de vida do Maven (`validate`). Aplica regras rígidas de formatação, proibição de *star imports* (`.*`), identificação de código morto (*unused imports*) e obrigatoriedade de chaves em blocos condicionais. O build é interrompido imediatamente caso haja violações de estilo.

### Testes Unitários e de Integração (Back-end)
* **JUnit 5 & Mockito:** Implementados no escopo do microsserviço principal. Utilizam `@InjectMock` para isolar o componente de rede do `AnalyticsClient`. Os testes validam de forma determinística os fluxos de aprovação ($15.0$ GHz $\rightarrow$ `PASSED`) e reprovação ($5.0$ GHz $\rightarrow$ `FAILED`), inspecionando as respostas JSON e códigos de status HTTP (`201 Created`).

### Testes de Sistema End-to-End (Front-end)
* **Cypress:** Robô de automação que valida o comportamento completo do usuário no navegador. A suíte executa testes automáticos para certificar a presença dos componentes visuais, o preenchimento correto dos formulários, o bloqueio de inputs vazios e a renderização das respostas dinâmicas em tela.

## 5. Esteira de Integração Contínua (CI)

A qualidade do código é assegurada por um pipeline automatizado via **GitHub Actions** (`ci.yml`). A cada evento de `push` ou `pull_request` direcionado às branches principais, o servidor de CI executa as seguintes etapas em um ambiente Linux (`ubuntu-latest`) isolado:

1. **Checkout do Código:** Clonagem do estado atual do repositório.
2. **Configuração do Ambiente:** Inicialização do JDK 17 (Distribuição Temurin) com cache ativo para o Maven.
3. **Auditoria de Estilo:** Execução do Checkstyle para garantir conformidade estética (`mvn checkstyle:check`).
4. **Execução da Suíte de Testes:** Rodada completa de testes unitários e de integração através do Maven (`mvn test`).

O estado de *Merge* fica condicionado ao sucesso absoluto de todas as etapas da esteira (Status: *Build Passing*).