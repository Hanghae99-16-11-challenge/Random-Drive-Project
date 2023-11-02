package com.example.randomdriveproject.navigation.random.entity;

import com.example.randomdriveproject.request.dto.DocumentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Nodes")
public class Nodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // latitude - y좌표
    @Column(nullable = false)
    private double y;

    // longitude - x좌표
    @Column(nullable = false)
    private double x;

    public Nodes(DocumentDto dto) {
        if(dto.getPlaceName() == null){
            this.name = "출발지";
        } else {
            this.name = dto.getPlaceName();
        }

        this.y = dto.getLatitude();
        this.x = dto.getLongitude();
    }

    public DocumentDto toDto() {
        return DocumentDto.builder()
                .placeName(this.name)
                .latitude(this.y)
                .longitude(this.x)
                .build();
    }

    public String toString(){
        return "Nodes{" +
                "id='" + id +
                ", name=" + name + '\'' +
                ", y='" + y +
                ", x=" + x +
                '}';
    }
}
