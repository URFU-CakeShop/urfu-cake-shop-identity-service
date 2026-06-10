//package ru.urfu.cake.shop.identity.service.service
//
//import org.assertj.core.api.Assertions.assertThat
//import org.assertj.core.api.Assertions.assertThatThrownBy
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.ArgumentCaptor
//import org.mockito.ArgumentMatchers
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.junit.jupiter.MockitoExtension
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.test.util.ReflectionTestUtils
//import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto
//import ru.urfu.cake.shop.identity.service.entity.Role
//import ru.urfu.cake.shop.identity.service.entity.User
//import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException
//import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException
//import ru.urfu.cake.shop.identity.service.repository.RoleRepository
//import ru.urfu.cake.shop.identity.service.repository.UserRepository
//import java.time.LocalDateTime
//import java.util.Optional
//import java.util.UUID
//
//@ExtendWith(MockitoExtension::class)
//internal class UserServiceImplTest {
//
//    @Mock
//    private lateinit var userRepository: UserRepository
//
//    @Mock
//    private lateinit var roleRepository: RoleRepository
//
//    @Mock
//    private lateinit var passwordEncoder: PasswordEncoder
//
//    @InjectMocks
//    private lateinit var userService: UserServiceImpl
//
//    private lateinit var existingUser: User
//    private lateinit var userRole: Role
//
//    @BeforeEach
//    fun setUp() {
//        userRole = Role().apply {
//            role = "USER"
//        }
//        // Устанавливаем приватный или val id через рефлексию
//        ReflectionTestUtils.setField(userRole, "id", UUID.randomUUID())
//
//        existingUser = User().apply {
//            email = EMAIL
//            password = ENCODED_PASSWORD
//            firstName = "Иван"
//            lastName = "Иванов"
//            createdAt = LocalDateTime.of(2026, 1, 1, 10, 0)
//            updatedAt = LocalDateTime.of(2026, 1, 1, 10, 0)
//            internalDescription = "Секретное описание"
//            roles.add(userRole)
//        }
//        ReflectionTestUtils.setField(existingUser, "id", UUID.randomUUID())
//    }
//
//    @Test
//    fun login_success() {
//        Mockito.`when`(userRepository.findByEmail(EMAIL))
//            .thenReturn(Optional.of(existingUser))
//        Mockito.`when`(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true)
//        Mockito.`when`(userRepository.save(existingUser)).thenReturn(existingUser)
//
//        val result = userService.login(EMAIL, RAW_PASSWORD)
//
//        assertThat(result.email).isEqualTo(EMAIL)
//        assertThat(result.firstName).isEqualTo("Иван")
//        assertThat(result.roles).containsExactly("USER")
//        assertThat(result.internalDescription).isNull()
//        assertThat(existingUser.lastLogin).isNotNull()
//
//        Mockito.verify(userRepository).save(existingUser)
//    }
//
//    @Test
//    fun login_userNotFound() {
//        Mockito.`when`(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty())
//
//        assertThatThrownBy { userService.login(EMAIL, RAW_PASSWORD) }
//            .isInstanceOf(InvalidCredentialsException::class.java)
//
//        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User::class.java))
//    }
//
//    @Test
//    fun login_wrongPassword() {
//        Mockito.`when`(userRepository.findByEmail(EMAIL))
//            .thenReturn(Optional.of(existingUser))
//        Mockito.`when`(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false)
//
//        assertThatThrownBy { userService.login(EMAIL, RAW_PASSWORD) }
//            .isInstanceOf(InvalidCredentialsException::class.java)
//
//        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User::class.java))
//    }
//
//    @Test
//    fun register_success() {
//        val dto = registrationDto().copy(
//            city = null,
//            street = null,
//            house = null,
//            apartment = null
//        )
//
//        Mockito.`when`(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty())
//        Mockito.`when`(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD)
//        Mockito.`when`(roleRepository.findByRole("USER")).thenReturn(Optional.of(userRole))
//        Mockito.`when`(userRepository.save(ArgumentMatchers.any(User::class.java)))
//            .thenAnswer { invocation ->
//                val saved = invocation.getArgument<User>(0)
//                ReflectionTestUtils.setField(saved, "id", UUID.randomUUID())
//                saved
//            }
//
//        val result = userService.register(dto)
//
//        assertThat(result.email).isEqualTo(EMAIL)
//        assertThat(result.firstName).isEqualTo("Иван")
//        assertThat(result.lastName).isEqualTo("Иванов")
//        assertThat(result.hasSugar).isFalse()
//        assertThat(result.roles).containsExactly("USER")
//        assertThat(result.address).isNull()
//        assertThat(result.internalDescription).isNull()
//
//        val userCaptor = ArgumentCaptor.forClass(User::class.java)
//        Mockito.verify(userRepository).save(userCaptor.capture())
//
//        val savedUser = userCaptor.value
//        assertThat(savedUser.email).isEqualTo(EMAIL)
//        assertThat(savedUser.password).isEqualTo(ENCODED_PASSWORD)
//        assertThat(savedUser.createdAt).isNotNull()
//        assertThat(savedUser.updatedAt).isNotNull()
//        assertThat(savedUser.roles).containsExactly(userRole)
//
//        Mockito.verify(passwordEncoder).encode(RAW_PASSWORD)
//    }
//
//    @Test
//    fun register_withAddress() {
//        val dto = registrationDto()
//
//        Mockito.`when`(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty())
//        Mockito.`when`(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD)
//        Mockito.`when`(roleRepository.findByRole("USER")).thenReturn(Optional.of(userRole))
//        Mockito.`when`(userRepository.save(ArgumentMatchers.any(User::class.java)))
//            .thenAnswer { invocation ->
//                val saved = invocation.getArgument<User>(0)
//                ReflectionTestUtils.setField(saved, "id", UUID.randomUUID())
//                saved
//            }
//
//        val result = userService.register(dto)
//
//        assertThat(result.address).isNotNull
//        assertThat(result.address?.city).isEqualTo("Екатеринбург")
//        assertThat(result.address?.street).isEqualTo("Ленина")
//        assertThat(result.address?.house).isEqualTo("1")
//        assertThat(result.address?.apartment).isEqualTo("10")
//    }
//
//    @Test
//    fun register_emailAlreadyExists() {
//        Mockito.`when`(userRepository.findByEmail(EMAIL))
//            .thenReturn(Optional.of(existingUser))
//
//        assertThatThrownBy { userService.register(registrationDto()) }
//            .isInstanceOf(UserAlreadyExistsException::class.java)
//            .hasMessageContaining(EMAIL)
//
//        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User::class.java))
//        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString())
//    }
//
//    @Test
//    fun register_roleNotFound() {
//        Mockito.`when`(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty())
//        Mockito.`when`(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD)
//        Mockito.`when`(roleRepository.findByRole("USER")).thenReturn(Optional.empty())
//
//        assertThatThrownBy { userService.register(registrationDto()) }
//            .isInstanceOf(IllegalStateException::class.java)
//            .hasMessageContaining("Role USER")
//
//        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User::class.java))
//    }
//
//    private fun registrationDto(): UserRegistrationDto {
//        return UserRegistrationDto(
//            email = EMAIL,
//            password = RAW_PASSWORD,
//            firstName = "Иван",
//            lastName = "Иванов",
//            phoneNumber = "+79991234567",
//            hasSugar = false,
//            publicDescription = "Люблю торты",
//            city = "Екатеринбург",
//            street = "Ленина",
//            house = "1",
//            apartment = "10"
//        )
//    }
//
//    companion object {
//        private const val EMAIL = "user@example.com"
//        private const val RAW_PASSWORD = "password123"
//        private const val ENCODED_PASSWORD = "\$2a\$10\$encoded"
//    }
//}