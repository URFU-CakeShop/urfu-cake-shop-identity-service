package ru.urfu.cake.shop.identity.service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "addresses")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String street;

    @Column(nullable = true)
    private String house;

    @Column(nullable = true)
    private String apartment;

    @OneToOne(mappedBy = "address")
    private User user;
}