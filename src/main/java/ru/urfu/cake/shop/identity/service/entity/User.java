package ru.urfu.cake.shop.identity.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Data
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String email;
    private String password;

    // Личные данные
    private String firstName;
    private String lastName;
    private String middleName;
    private Boolean hasSugar; // диабет или особенности здоровья
    private String phoneNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    // Связи с другими сервисами
    private UUID cartId; // CartService
    private UUID avatarImageId; // ImageService (аватар)
    // Дополнительные картинки пользователя (например, галерея)
    @ElementCollection
    private Set<Long> imageIds = new HashSet<>(); // ImageService

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Описания
    @Column(length = 2000)
    private String publicDescription;  // описание, которое сам пользователь может редактировать

    @Column(length = 2000)
    private String internalDescription; // описание от админа, видно только поварам и админам
}