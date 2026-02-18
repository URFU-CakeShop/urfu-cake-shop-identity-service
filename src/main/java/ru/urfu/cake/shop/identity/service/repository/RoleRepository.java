package ru.urfu.cake.shop.identity.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.cake.shop.identity.service.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRole(String role);
}