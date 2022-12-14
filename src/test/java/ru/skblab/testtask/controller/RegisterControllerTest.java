package ru.skblab.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skblab.testtask.dto.UserRegistrationInfo;
import ru.skblab.testtask.exeption.EmailExistException;
import ru.skblab.testtask.exeption.LoginExistException;
import ru.skblab.testtask.service.RegistrationService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class RegisterControllerTest {
    @MockBean
    RegistrationService registrationService;


    final static String email = "iulia@gmail.com";


    @Autowired
    MockMvc mockMvc;

    final static UserRegistrationInfo user = UserRegistrationInfo.builder()
            .login("iulia")
            .password("qwerty")
            .email(email)
            .lastName("Чебыкина")
                    .firstName("Юлия")
                    .patronymic("Владимировна")

            .build();

    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registerControllerOkTest() throws Exception {

        mockMvc
                .perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/success"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void registerControllerWithExistingLoginTest() throws Exception {
        Mockito.doThrow(new LoginExistException(user.getLogin())).when(registrationService).registerUser(any());
        mockMvc
                .perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/register"))
                .andExpect(model().hasErrors());
    }


    @Test
    public void registerControllerWithExistingEmailTest() throws Exception {
        Mockito.doThrow(new EmailExistException(user.getEmail())).when(registrationService).registerUser(any());

        mockMvc.perform(post("/register/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/register"))
                .andExpect(model().hasErrors());
    }


    @Test
    void homeAtIndexTest() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk());
    }

    @Test
    void homeTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void successRegistrationTest() throws Exception {
        mockMvc.perform(get("/success"))
                .andExpect(status().isOk());
    }

    @Test
    void showRegistrationFormTest() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"));
    }


}