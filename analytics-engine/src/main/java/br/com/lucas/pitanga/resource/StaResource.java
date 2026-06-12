package br.com.lucas.pitanga.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/api/sta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StaResource {

    // 1. DTOs: Estruturas de Dados do Grafo (Microeletrônica)
    public static class Node { public String id; public String type; public int delayPs; }
    public static class Edge { public String from; public String to; }
    public static class GraphRequest { public List<Node> nodes; public List<Edge> edges; }
    
    public static class StaResult { 
        public int criticalPathDelayPs; 
        public List<String> criticalPathNodes; 
        public double maxFreqGhz; 
    }

    // 2. O Endpoint que recebe o Grafo do Front-end/Gerente
    @POST
    public StaResult analyzeTiming(GraphRequest request) {
        Solver solver = new Solver(request);
        return solver.solve();
    }

    // 3. O Algoritmo Core: Isolado para ser Thread-Safe
    private static class Solver {
        Map<String, Node> nodeMap = new HashMap<>();
        Map<String, List<String>> adj = new HashMap<>();
        Set<String> hasIncoming = new HashSet<>();
        
        int maxGlobalDelay = 0;
        List<String> bestGlobalPath = new ArrayList<>();

        public Solver(GraphRequest request) {
            for (Node n : request.nodes) nodeMap.put(n.id, n);
            if (request.edges != null) {
                for (Edge e : request.edges) {
                    adj.computeIfAbsent(e.from, k -> new ArrayList<>()).add(e.to);
                    hasIncoming.add(e.to);
                }
            }
        }

        public StaResult solve() {
            // Encontra as "Entradas" (Pads) do chip
            for (Node n : nodeMap.values()) {
                if (!hasIncoming.contains(n.id)) {
                    dfs(n.id, new ArrayList<>(), 0);
                }
            }

            StaResult res = new StaResult();
            res.criticalPathDelayPs = maxGlobalDelay;
            res.criticalPathNodes = bestGlobalPath;
            // Cálculo Físico: Frequência = 1 / Tempo (1000 / ps = GHz)
            res.maxFreqGhz = maxGlobalDelay > 0 ? Math.round((1000.0 / maxGlobalDelay) * 100.0) / 100.0 : 0.0;
            return res;
        }

        // Busca em Profundidade para mapear todos os caminhos dos elétrons
        private void dfs(String current, List<String> path, int currentDelay) {
            path.add(current);
            Node n = nodeMap.get(current);
            currentDelay += (n != null ? n.delayPs : 0);

            List<String> neighbors = adj.getOrDefault(current, new ArrayList<>());
            if (neighbors.isEmpty()) {
                // Chegou na Saída: Verifica se é o caminho mais lento até agora
                if (currentDelay > maxGlobalDelay) {
                    maxGlobalDelay = currentDelay;
                    bestGlobalPath = new ArrayList<>(path);
                }
            } else {
                for (String next : neighbors) {
                    dfs(next, path, currentDelay);
                }
            }
            path.remove(path.size() - 1);
        }
    }
}
