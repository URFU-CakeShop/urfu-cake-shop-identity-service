package ru.urfu.cake.shop.identity.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.urfu.cake.shop.identity.service.entity.User
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
}