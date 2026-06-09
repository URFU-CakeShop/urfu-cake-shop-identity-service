package ru.urfu.cake.shop.identity.service.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto
import ru.urfu.cake.shop.identity.service.entity.Address
import ru.urfu.cake.shop.identity.service.entity.User
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException
import ru.urfu.cake.shop.identity.service.model.AddressModel
import ru.urfu.cake.shop.identity.service.model.UserModel
import ru.urfu.cake.shop.identity.service.repository.RoleRepository
import ru.urfu.cake.shop.identity.service.repository.UserRepository
import ru.urfu.cake.shop.identity.service.util.TimeUtil
import kotlin.jvm.optionals.getOrNull

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Transactional
    override fun login(email: String, rawPassword: String): UserModel {
        val user = userRepository.findByEmail(email).getOrNull()
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw InvalidCredentialsException()
        }

        user.lastLogin = TimeUtil.now()
        userRepository.save(user)

        return toModel(user)
    }

    @Transactional
    override fun register(dto: UserRegistrationDto): UserModel {
        val email = dto.email

        if (userRepository.findByEmail(email).isPresent) {
            throw UserAlreadyExistsException(email)
        }

        val user = User().apply {
            this.email = email
            this.password = passwordEncoder.encode(dto.password)
            this.createdAt = TimeUtil.now()
            this.updatedAt = TimeUtil.now()
            this.firstName = dto.firstName
            this.lastName = dto.lastName
            this.middleName = dto.middleName
            this.phoneNumber = dto.phoneNumber
            this.hasSugar = dto.hasSugar
            this.publicDescription = dto.publicDescription
            this.cartId = null
            this.avatarImageId = null

            if (dto.city != null || dto.street != null || dto.house != null || dto.apartment != null) {
                this.address = Address().apply {
                    city = dto.city ?: ""
                    street = dto.street ?: ""
                    house = dto.house ?: ""
                    apartment = dto.apartment ?: ""
                }
            }
        }

        val userRole = roleRepository.findByRole("USER")
            .orElseThrow { IllegalStateException("Role USER не найдена") }

        user.roles.add(userRole)
        userRepository.save(user)

        return toModel(user)
    }

    private fun toModel(user: User): UserModel {
        return UserModel(
            id = user.id,
            email = user.email,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            lastLogin = user.lastLogin,
            firstName = user.firstName,
            lastName = user.lastName,
            middleName = user.middleName,
            address = toModel(user.address),
            phoneNumber = user.phoneNumber,
            hasSugar = user.hasSugar,
            cartId = user.cartId,
            avatarImageId = user.avatarImageId,
            imageIds = user.imageIds.toSet(),
            publicDescription = user.publicDescription,
            roles = user.roles.map { it.role }.toSet()
        )
    }

    private fun toModel(address: Address?): AddressModel? = address?.let {
        AddressModel(
            city = it.city,
            street = it.street,
            house = it.house,
            apartment = it.apartment
        )
    }
}