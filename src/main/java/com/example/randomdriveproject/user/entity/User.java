package com.example.randomdriveproject.user.entity;

import com.example.randomdriveproject.history.entity.Route;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;



@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "userRole", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    private Long kakaoId;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Route> routes = new ArrayList<>();

    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = role;
    }

    public User(String username, String password, String email, UserRoleEnum role, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = role;
        this.kakaoId =kakaoId;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
