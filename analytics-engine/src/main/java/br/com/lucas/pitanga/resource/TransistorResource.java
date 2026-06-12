package br.com.lucas.pitanga.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/transistor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransistorResource {

    // Entrada: O que o estudante vai alterar na interface
    public static class TransistorRequest {
        public double vdd;          // Tensão de Alimentação (V)
        public double widthNm;      // Largura do Transistor W (nm)
        public double cLoadFf;      // Capacitância de Carga C_load (fF)
    }

    // Saída: Os resultados físicos calculados pelo motor
    public static class TransistorResponse {
        public double saturationCurrentUa; // Corrente gerada (uA)
        public double propagationDelayPs;  // Atraso resultante (ps)
        public String operationRegion;     // Estado do canal do silício
    }

    @POST
    public TransistorResponse analyzePhysics(TransistorRequest request) {
        // Constantes físicas para uma tecnologia clássica de 180nm (0.18um)
        double knPrime = 250.0e-6; // Fator de condução do processo (A/V^2)
        double length = 180.0;     // Comprimento do canal fixo em 180nm
        double vth = 0.4;          // Tensão de limiar / Threshold Voltage (V)

        // 1. Cálculo da Corrente de Saturação (Shockley)
        // Isat = 0.5 * Kn' * (W/L) * (Vgs - Vth)^2  [Considerando Vgs = Vdd]
        double wOverL = request.widthNm / length;
        double vgsMinusVth = request.vdd - vth;
        
        double iSatAmperes = 0.0;
        if (vgsMinusVth > 0) {
            iSatAmperes = 0.5 * knPrime * wOverL * Math.pow(vgsMinusVth, 2);
        }

        // 2. Cálculo do Atraso de Propagação (t_pd)
        // t_pd = (C_load * Vdd) / (2 * Isat)
        double cLoadFarads = request.cLoadFf * 1.0e-15; // fF para Farads
        double tPdSeconds = 0.0;
        if (iSatAmperes > 0) {
            tPdSeconds = (cLoadFarads * request.vdd) / (2.0 * iSatAmperes);
        }

        // 3. Empacotamento dos resultados com ajustes de unidade
        TransistorResponse response = new TransistorResponse();
        response.saturationCurrentUa = Math.round((iSatAmperes * 1.0e6) * 100.0) / 100.0; // para uA
        response.propagationDelayPs = Math.round((tPdSeconds * 1.0e12) * 100.0) / 100.0;   // para ps
        response.operationRegion = request.vdd > vth ? "SATURAÇÃO" : "CORTE (DESLIGADO)";

        return response;
    }
}
