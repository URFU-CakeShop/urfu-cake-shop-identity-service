package ru.urfu.cake.shop.identity.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.urfu.cake.shop.identity.service.entity.Role
import java.util.*

interface RoleRepository : JpaRepository<Role?, UUID?> {
    fun findByRole(role: String): Optional<Role>
}