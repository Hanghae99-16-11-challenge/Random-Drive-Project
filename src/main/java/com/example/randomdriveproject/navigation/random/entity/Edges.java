package com.example.randomdriveproject.navigation.random.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Edges")
public class Edges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node1_id")
    private Nodes node1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node2_id")
    private Nodes node2;

    public Edges(Nodes node1, Nodes node2, double weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public String toString(){
        return "Edges{" +
                "id='" + id +
                ", weight=" + weight +
                ", node1='" + node1 +
                ", node2=" + node2 +
                '}';
    }

}
