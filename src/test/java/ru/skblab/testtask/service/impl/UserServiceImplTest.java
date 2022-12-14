package ru.skblab.testtask.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.skblab.testtask.dto.UserRegistrationInfo;
import ru.skblab.testtask.jpa.entity.User;
import ru.skblab.testtask.jpa.entity.UserVerification;
import ru.skblab.testtask.jpa.entity.valueType.Name;
import ru.skblab.testtask.jpa.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    UserServiceImpl userService;


    final static String login = "iulia";
    final static String email = "iulia@mail.ru";
    final static String password = "qwerty";
    final static String passwordSalt = "salt";
    final static String firstName = "Iulia";
    final static String lastName = "Chebykina";


    final static User savedUser = new User(
            1L,
            login,
            email,
            password + passwordSalt,
            new Name(firstName, lastName),
            new UserVerification(),
            false);

    final static UserRegistrationInfo USER_REGISTRATION_INFO = UserRegistrationInfo.builder()
            .firstName(firstName)
            .lastName(lastName)
            .password(password)
            .login(login)
            .email(email)
            .build();

    @Test
    void getUserTest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(savedUser));
        Optional<User> user = userService.getUser(1L);
        assertTrue(user.isPresent());
    }

    @Test
    void getUserWithEmptyTest() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        Optional<User> user = userService.getUser(1L);
        assertTrue(user.isEmpty());
    }


    @Test
    void getUserByEmailTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(savedUser));
        Optional<User> user = userService.getUserByEmail("email");
        assertTrue(user.isPresent());
    }

    @Test
    void getUserByEmailWithEmptyTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Optional<User> user = userService.getUserByEmail("wrong email");
        assertTrue(user.isEmpty());
    }

    @Test
    void getUserByLoginTest() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(savedUser));
        Optional<User> user = userService.getUserByLogin("login");
        assertTrue(user.isPresent());
    }

    @Test
    void getUserByLoginWithEmptyTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Optional<User> user = userService.getUserByLogin("wrong login");
        assertTrue(user.isEmpty());
    }

    @Test
    void createUserTest() {
        when(passwordEncoder.encode(password)).thenReturn(password+passwordSalt);
        when(userRepository.save(any())).thenReturn(savedUser);
        User user = userService.createUser(USER_REGISTRATION_INFO);
        assertEquals(login, user.getLogin());
        assertEquals(email, user.getEmail());
        assertEquals(password+passwordSalt, user.getPassword());
        assertEquals(firstName, user.getName().getFirstName());
        assertEquals(lastName, user.getName().getLastName());
        assertNull(user.getName().getPatronymic());
    }

    @Test
    void isNotExistEmailTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Boolean existEmail = userService.isExistEmail(email);
        assertFalse(existEmail);

    }

    @Test
    void isExistEmailTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(savedUser));
        Boolean existEmail = userService.isExistEmail(email);
        assertTrue(existEmail);

    }

    @Test
    void isNotExistLoginTest() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());
        Boolean existLogin = userService.isExistLogin(email);
        assertFalse(existLogin);

    }

    @Test
    void isExistLoginTest() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(savedUser));
        Boolean existLogin = userService.isExistLogin(email);
        assertTrue(existLogin);

    }

}