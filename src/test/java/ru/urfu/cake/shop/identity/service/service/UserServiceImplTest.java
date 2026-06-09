package ru.urfu.cake.shop.identity.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.entity.Address;
import ru.urfu.cake.shop.identity.service.entity.Role;
import ru.urfu.cake.shop.identity.service.entity.User;
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException;
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.repository.RoleRepository;
import ru.urfu.cake.shop.identity.service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setRole("USER");

        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail(EMAIL);
        existingUser.setPassword(ENCODED_PASSWORD);
        existingUser.setFirstName("Иван");
        existingUser.setLastName("Иванов");
        existingUser.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        existingUser.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        existingUser.setInternalDescription("Секретное описание");
        existingUser.getRoles().add(userRole);
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserModel result = userService.login(EMAIL, RAW_PASSWORD);

        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getFirstName()).isEqualTo("Иван");
        assertThat(result.getRoles()).containsExactly("USER");
        assertThat(result.getInternalDescription()).isNull();
        assertThat(existingUser.getLastLogin()).isNotNull();

        verify(userRepository).save(existingUser);
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_wrongPassword() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> userService.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_success() {
        UserRegistrationDto dto = registrationDto();
        dto.setCity(null);
        dto.setStreet(null);
        dto.setHouse(null);
        dto.setApartment(null);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByRole("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        UserModel result = userService.register(dto);

        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getFirstName()).isEqualTo("Иван");
        assertThat(result.getLastName()).isEqualTo("Иванов");
        assertThat(result.getHasSugar()).isFalse();
        assertThat(result.getRoles()).isEqualTo(Set.of("USER"));
        assertThat(result.getAddress()).isNull();
        assertThat(result.getInternalDescription()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
        assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getRoles()).containsExactly(userRole);

        verify(passwordEncoder).encode(RAW_PASSWORD);
    }

    @Test
    void register_withAddress() {
        UserRegistrationDto dto = registrationDto();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByRole("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        UserModel result = userService.register(dto);

        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getCity()).isEqualTo("Екатеринбург");
        assertThat(result.getAddress().getStreet()).isEqualTo("Ленина");
        assertThat(result.getAddress().getHouse()).isEqualTo("1");
        assertThat(result.getAddress().getApartment()).isEqualTo("10");
    }

    @Test
    void register_emailAlreadyExists() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.register(registrationDto()))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(EMAIL);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void register_roleNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByRole("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(registrationDto()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Role USER");

        verify(userRepository, never()).save(any());
    }

    private UserRegistrationDto registrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail(EMAIL);
        dto.setPassword(RAW_PASSWORD);
        dto.setFirstName("Иван");
        dto.setLastName("Иванов");
        dto.setPhoneNumber("+79991234567");
        dto.setHasSugar(false);
        dto.setPublicDescription("Люблю торты");
        dto.setCity("Екатеринбург");
        dto.setStreet("Ленина");
        dto.setHouse("1");
        dto.setApartment("10");
        return dto;
    }
}
