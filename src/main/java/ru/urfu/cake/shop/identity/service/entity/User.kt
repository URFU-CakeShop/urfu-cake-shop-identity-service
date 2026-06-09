package ru.urfu.cake.shop.identity.service.entity

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    val id: UUID? = null

    @Column(nullable = false, unique = true)
    var email: String = ""

    @Column(nullable = false)
    var password: String = ""

    // Личные данные
    var firstName: String? = null
    var lastName: String? = null
    var middleName: String? = null
    var hasSugar: Boolean? = null
    var phoneNumber: String? = null

    var createdAt: LocalDateTime? = null
    var updatedAt: LocalDateTime? = null
    var lastLogin: LocalDateTime? = null

    // Связи с другими сервисами
    var cartId: UUID? = null
    var avatarImageId: UUID? = null

    // Дополнительные картинки пользователя (например, галерея)
    @ElementCollection
    var imageIds: MutableSet<Long> = HashSet()

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    var address: Address? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = HashSet()

    @Column(length = 2000)
    var publicDescription: String? = null

    @Column(length = 2000)
    var internalDescription: String? = null
}