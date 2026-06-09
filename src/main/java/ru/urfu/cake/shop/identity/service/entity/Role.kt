package ru.urfu.cake.shop.identity.service.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "roles")
class Role {
    @Id
    val id: UUID? = null

    @Column(unique = true, nullable = false)
    var role: String = ""
}