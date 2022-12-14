package ru.skblab.testtask.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.skblab.testtask.dto.NameInfo;
import ru.skblab.testtask.exeption.UserNotFoundException;
import ru.skblab.testtask.exeption.UserVerificationNotFoundException;
import ru.skblab.testtask.jpa.entity.User;
import ru.skblab.testtask.jpa.entity.UserVerification;
import ru.skblab.testtask.jpa.entity.valueType.Name;
import ru.skblab.testtask.service.SendMailer;
import ru.skblab.testtask.service.UserService;
import ru.skblab.testtask.service.UserVerificationService;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE)
class EmailNotificationServiceImplTest {

    @MockBean
    SendMailer sendMailer;
    @MockBean
    UserService userService;
    @MockBean
    UserVerificationService userVerificationService;

    @Autowired
    EmailNotificationServiceImpl emailNotificationService;

    final static NameInfo name = NameInfo.builder()
            .firstName("Юлия")
            .lastName("Чебыкина")
            .patronymic("Владимировна")
            .build();

    final static String email = "iulia@gmail.ru";

    final static String login = "iulia";
    final static String password = "qwerty";

    final static UserVerification userVerification = new UserVerification();




    final static User user = new User(2L,
            login,
            email,
            password,
            new Name(name.getFirstName(), name.getLastName(), name.getLastName()),
            userVerification,
            false);



    @Test
    public void userNotFoundException(){
        when(userService.getUser(1L)).thenReturn(Optional.empty());
        UndeclaredThrowableException exception = assertThrows(UndeclaredThrowableException.class, () -> emailNotificationService.notifyUserAboutVerification(1L));
        assertEquals(exception.getCause().getClass(), UserNotFoundException.class);

    }

    @Test
    public void userVerificationNotFoundException(){
        Long userId = 2L;
        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userVerificationService.getUserVerification(userId)).thenReturn(Optional.empty());
        UndeclaredThrowableException exception = assertThrows(UndeclaredThrowableException.class, () -> emailNotificationService.notifyUserAboutVerification(userId));
        assertEquals(exception.getCause().getClass(), UserVerificationNotFoundException.class);

    }

    @Test
    public void notifyUserWithThrow() throws TimeoutException {
        Long userId = 2L;
        userVerification.setIsVerified(false);
        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userVerificationService.getUserVerification(userId)).thenReturn(Optional.of(userVerification));
        Mockito.doThrow(new TimeoutException("Timeout")).when(sendMailer).sendMail(any(), any());
        emailNotificationService.notifyUserAboutVerification(userId);

        Mockito.verify(userVerificationService, Mockito.times(1)).setNotificationStatus(any(), eq(false));
        Mockito.verify(userVerificationService, Mockito.times(0)).setNotificationStatus(any(), eq(true));

    }



    @Test
    public void notifyUser() throws TimeoutException {
        Long userId = 2L;
        userVerification.setIsVerified(true);
        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userVerificationService.getUserVerification(userId)).thenReturn(Optional.of(userVerification));
        emailNotificationService.notifyUserAboutVerification(userId);
        Mockito.verify(userVerificationService, Mockito.times(1)).setNotificationStatus(any(), eq(true));
        Mockito.verify(userVerificationService, Mockito.times(0)).setNotificationStatus(any(), eq(false));
    }


}