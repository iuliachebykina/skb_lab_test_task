package ru.skblab.testtask.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skblab.testtask.aop.annotation.Loggable;
import ru.skblab.testtask.common.NameMapper;
import ru.skblab.testtask.dto.UserRegistrationInfo;
import ru.skblab.testtask.jpa.entity.User;
import ru.skblab.testtask.jpa.entity.UserVerification;
import ru.skblab.testtask.jpa.entity.valueType.Name;
import ru.skblab.testtask.jpa.repository.UserRepository;
import ru.skblab.testtask.service.UserService;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final NameMapper nameMapper;

    @Override
    @Loggable
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);

    }

    @Override
    @Loggable
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Loggable
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Loggable
    @Transactional
    public User createUser(UserRegistrationInfo userRegistrationInfo) {

        Name name = nameMapper.mapUserRegistrationInfoToName(userRegistrationInfo);
        User user = User.builder()
                .login(userRegistrationInfo.getLogin())
                .email(userRegistrationInfo.getEmail())
                .name(name)
                .password(passwordEncoder.encode(userRegistrationInfo.getPassword()))
                .build();

        user.setVerification(new UserVerification());
        return userRepository.save(user);
    }

    @Override
    @Loggable
    public Boolean isExistEmail(String email) {
        return getUserByEmail(email).isPresent();
    }

    @Override
    @Loggable
    public Boolean isExistLogin(String login) {
        return getUserByLogin(login).isPresent();
    }
}
