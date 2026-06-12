package br.com.lucas.pitanga.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class CircuitProject extends PanacheEntity {
    public String name;
    public String authorName;
    public String targetBoard;
    public String verificationStatus;
    
    // Novas métricas de Microeletrônica!
    public int criticalPathPs;
    public double maxFreqGhz;
}
