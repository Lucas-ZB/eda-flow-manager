# Visão Geral do Sistema: EDA Flow Manager

## 1. Descrição do Sistema
O **EDA Flow Manager** é um simulador focado em *Electronic Design Automation* (EDA) construído sob uma arquitetura de microsserviços. 
O sistema tem como objetivo principal orquestrar e validar simulações de circuitos de Microeletrônica, permitindo que engenheiros analisem o comportamento físico de transistores CMOS (cálculo de atraso RC e correntes de saturação) e realizem a Análise de Tempo Estático (Static Timing Analysis - STA) para determinar a Frequência Máxima ($f_{max}$) de operação do chip projetado. O sistema valida as regras de negócio de *timing* e persiste os projetos aprovados.

## 2. Tecnologias Utilizadas
A arquitetura foi projetada com foco em alta performance, desacoplamento e facilidade de V&V (Validação e Verificação), utilizando as seguintes tecnologias:

### Back-end (Microsserviços)
* **Java 17:** Linguagem principal, garantindo tipagem forte e ecossistema maduro para algoritmos matemáticos complexos (Teoria dos Grafos).
* **Quarkus Framework:** Framework Java *Cloud-Native*, utilizado para prover injeção de dependências (CDI), tempos de inicialização rápidos e roteamento HTTP (JAX-RS).
* **REST Client (MicroProfile):** Utilizado para a comunicação síncrona, segura e tipada (via DTOs) entre os microsserviços.
* **Hibernate ORM com Panache:** Implementação do padrão *Active Record* para mapeamento objeto-relacional de forma enxuta.
* **Banco de Dados H2:** Banco de dados relacional operando *in-memory*, ideal para ambientes de simulação rápida e testes isolados.

### Front-end
* **HTML5, CSS3 e JavaScript (ES6):** Interface nativa e reativa que consome as APIs RESTful do back-end utilizando a *Fetch API*.
* **Chart.js:** Biblioteca de renderização gráfica utilizada para criar o "Osciloscópio Virtual", plotando as curvas de carga capacitiva dos transistores em tempo real.

### Stack Prevista para Validação e Verificação (V&V)
* **JUnit 5:** Framework base para a construção dos testes unitários e de integração.
* **Mockito:** Framework de *mocking* utilizado para criar "dublês de teste" (ex: interceptar chamadas HTTP do REST Client), permitindo testar os microsserviços de forma determinística e isolada.
* **REST Assured:** Ferramenta para testes de componentes e contratos de API (Endpoints REST).

## 3. Funcionalidades Core
O sistema é dividido em dois serviços principais que se comunicam para realizar as seguintes funções:

1. **Simulação da Física de Semicondutores:** O sistema recebe parâmetros físicos primários (Tensão Vdd, Largura do Canal W, Capacitância C_load) e calcula, em tempo real, a Corrente de Saturação e o Atraso de Propagação ($t_{pd}$) de uma porta lógica base.
2. **Orquestração e V&V de Circuitos (STA):** O sistema recebe a topologia física (Netlist) de um projeto e a analisa utilizando Busca em Profundidade (DFS). O algoritmo localiza o "Caminho Crítico" do chip e determina sua Frequência Máxima de operação.
3. **Persistência Baseada em Regras (Gateway):** O serviço atua como *Front Desk*, interceptando a requisição do usuário, consultando o motor analítico isolado e aplicando regras de negócio de engenharia (ex: aprovação restrita a projetos que alcancem pelo menos $10.0$ GHz de frequência). Apenas projetos que não possuam violações de *timing* são salvos no banco de dados.