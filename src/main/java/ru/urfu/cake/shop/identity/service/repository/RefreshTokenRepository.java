package ru.urfu.cake.shop.identity.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.cake.shop.identity.service.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
}