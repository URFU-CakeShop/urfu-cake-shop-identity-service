package ru.urfu.cake.shop.identity.service.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "addresses")
class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    var city: String = ""

    @Column(nullable = false)
    var street: String = ""

    @Column(nullable = false)
    var house: String = ""

    @Column
    var apartment: String = ""

    @OneToOne(mappedBy = "address")
    val user: User? = null
}