# ⚡ EDA Flow Manager

Uma plataforma full-stack baseada em arquitetura de microsserviços para simulação de **Electronic Design Automation (EDA)**, Análise de Tempo Estático (STA) e laboratório didático de física de semicondutores.

## 🎯 Visão Geral
Este projeto foi desenvolvido para preencher a lacuna entre a Engenharia de Software Moderna e o Design de Semicondutores (VLSI). A plataforma permite que usuários projetem *netlists* lógicas, submetam circuitos para validação de frequência máxima (FMax) e explorem interativamente a física de atraso de propagação em transistores CMOS.

## 🏗️ Arquitetura de Microsserviços
O ecossistema é dividido em dois serviços autônomos desenvolvidos com **Java** e **Quarkus**, comunicando-se via REST Clients:

1. **Project Manager (Porta 8080):**
   - Atua como o *Gateway* e Interface do Usuário.
   - Gerencia a persistência de dados (H2 Database + Hibernate ORM Panache).
   - Aplica regras de negócio de Verificação e Validação (V&V).
2. **Analytics Engine (Porta 8081):**
   - O núcleo matemático isolado (Stateless).
   - Resolve grafos usando Busca em Profundidade (DFS) para encontrar o Caminho Crítico.
   - Calcula a corrente de saturação baseada nas Equações de Shockley e o atraso elétrico de inversores CMOS.

## ✨ Funcionalidades
- **Dashboard de Timing (STA):** Envio dinâmico de *netlists* via interface gráfica para cálculo automático do caminho crítico.
- **Osciloscópio Virtual (Laboratório):** Simulação em tempo real da curva de carga/descarga RC em portas lógicas, reagindo fisicamente à alteração de Voltagem, Largura do Canal e Capacitância.
- **Resiliência e Testes:** Suíte de testes automatizados de integração usando **JUnit**, **REST Assured** e **Mockito** (para isolamento de falhas de rede entre os serviços).

## 🚀 Tecnologias Utilizadas
* **Back-end:** Java 17, Quarkus, Hibernate, RESTEasy, Maven.
* **Front-end:** HTML/CSS/JS nativo, Chart.js (Osciloscópio Virtual).
* **Testes:** JUnit, Mockito, REST Assured.

## ⚙️ Como Executar Localmente

Certifique-se de ter o JDK 17+ e o Maven instalados. Você precisará de duas janelas de terminal.

**1. Inicie o Motor Analítico:**
```bash
cd analytics-engine
mvn quarkus:dev
```

**2. Inicie o Gerente de Projetos:**
```bash
cd project-manager
mvn quarkus:dev
```

Acesse a interface gráfica através do navegador em: `http://localhost:8080`

## 🧪 Como Rodar os Testes
Para executar a bateria de validação matemática e testes com Dublês de Rede (Mocks):
```bash
cd project-manager
mvn clean test
```
