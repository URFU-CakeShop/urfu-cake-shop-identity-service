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

    @Column()
    private String city;

    @Column()
    private String street;

    @Column()
    private String house;

    @Column()
    private String apartment;

    @OneToOne(mappedBy = "address")
    private User user;
}