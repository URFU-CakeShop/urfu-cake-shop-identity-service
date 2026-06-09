package ru.urfu.cake.shop.identity.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.entity.Address;
import ru.urfu.cake.shop.identity.service.entity.Role;
import ru.urfu.cake.shop.identity.service.entity.User;
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException;
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException;
import ru.urfu.cake.shop.identity.service.model.AddressModel;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.repository.RoleRepository;
import ru.urfu.cake.shop.identity.service.repository.UserRepository;
import ru.urfu.cake.shop.identity.service.util.TimeUtil;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserModel login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setLastLogin(TimeUtil.now());
        userRepository.save(user);

        return toModel(user);
    }

    @Override
    @Transactional
    public UserModel register(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(TimeUtil.now());
        user.setUpdatedAt(TimeUtil.now());

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMiddleName(dto.getMiddleName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setHasSugar(dto.getHasSugar());

        if (dto.getCity() != null || dto.getStreet() != null || dto.getHouse() != null || dto.getApartment() != null) {
            Address address = new Address();
            address.setCity(dto.getCity());
            address.setStreet(dto.getStreet());
            address.setHouse(dto.getHouse());
            address.setApartment(dto.getApartment());
            user.setAddress(address);
        }

        user.setPublicDescription(dto.getPublicDescription());
        user.setCartId(null);
        user.setAvatarImageId(null);

        Role userRole = roleRepository.findByRole("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER не найдена"));

        user.getRoles().add(userRole);

        userRepository.save(user);

        return toModel(user);
    }

    private UserModel toModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .address(toModel(user.getAddress()))
                .phoneNumber(user.getPhoneNumber())
                .hasSugar(user.getHasSugar())
                .cartId(user.getCartId())
                .avatarImageId(user.getAvatarImageId())
                .imageIds(user.getImageIds())
                .publicDescription(user.getPublicDescription())
                .roles(user.getRoles().stream()
                        .map(Role::getRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AddressModel toModel(Address address) {
        if (address == null) {
            return null;
        }

        return AddressModel.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .apartment(address.getApartment())
                .build();
    }
}
