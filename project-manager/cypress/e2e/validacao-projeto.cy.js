describe('Testes de Validação E2E - EDA Flow Manager', () => {

    // === SOLUÇÃO DO PROBLEMA ===
    // Diz ao Cypress para ignorar erros de carregamento assíncrono (como o Chart.js demorando no CDN)
    // e não falhar o teste por causa disso.
    Cypress.on('uncaught:exception', (err, runnable) => {
        return false;
    });

    beforeEach(() => {
        // Antes de cada teste, o robô visita a URL do seu servidor Quarkus
        cy.visit('http://localhost:8082');
    });

    it('Deve exibir o Osciloscópio Virtual corretamente na tela', () => {
        cy.contains('h1', 'Inversor CMOS').should('be.visible');
        cy.get('#vdd').should('exist');
        cy.get('#w').should('exist');
        cy.get('#c').should('exist');
    });

    it('Deve aprovar o projeto quando transistores rápidos são configurados', () => {
        cy.get('#projectName').type('Robo Adder');
        cy.get('#authorName').type('Cypress Bot');
        cy.get('#targetBoard').type('FPGA');

        // Intercepta a chamada HTTP fingindo sucesso
        cy.intercept('POST', '/api/projects', {
            statusCode: 201,
            body: {
                verificationStatus: "PASSED",
                maxFreqGhz: 45.0
            }
        }).as('salvarProjeto');

        cy.contains('button', 'Executar V&V e Salvar Projeto').click();

        cy.wait('@salvarProjeto');
        cy.get('#resultadoBd')
          .should('be.visible')
          .and('contain', '✅ Projeto APROVADO');
    });

    it('Deve reprovar o projeto quando houver Timing Violation', () => {
        cy.get('#projectName').type('Chip Lento');
        cy.get('#authorName').type('Cypress Bot');
        cy.get('#targetBoard').type('ASIC');

        // Intercepta simulando erro de frequência baixa
        cy.intercept('POST', '/api/projects', {
            statusCode: 201,
            body: {
                verificationStatus: "FAILED (Timing Violation)",
                maxFreqGhz: 8.5
            }
        }).as('tentarSalvar');

        cy.contains('button', 'Executar V&V e Salvar Projeto').click();

        cy.wait('@tentarSalvar');
        cy.get('#resultadoBd')
          .should('be.visible')
          .and('contain', '❌ Projeto REPROVADO');
    });

    it('Deve bloquear tentativa de salvar com formulário em branco', () => {
        cy.contains('button', 'Executar V&V e Salvar Projeto').click();

        cy.get('#resultadoBd')
          .should('be.visible')
          .and('contain', '⚠️ Por favor, preencha todos os campos');
    });

});