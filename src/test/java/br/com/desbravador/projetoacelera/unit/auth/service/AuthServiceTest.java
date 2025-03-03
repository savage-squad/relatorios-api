package br.com.desbravador.projetoacelera.unit.auth.service;

import br.com.desbravador.projetoacelera.unit.BaseTests;
import br.com.desbravador.projetoacelera.application.auth.service.AuthService;
import br.com.desbravador.projetoacelera.application.user.entity.User;
import br.com.desbravador.projetoacelera.application.user.repository.UserRepository;
import br.com.desbravador.projetoacelera.web.exception.ResourceNotFoundException;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest extends BaseTests {

    @Spy
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepositoryMock;

    @BeforeEach
    public void setUp() {
        when(userRepositoryMock.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("deve salvar um token para atualizar a senha")
    public void test_should_save_a_token_to_update_password() {

        User user = getUser();

        String token = RandomString.make(30);
        String email = user.getEmail();

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));

        User userResult = authService.updateResetPasswordToken(token, email);

        assertEquals(token, userResult.getToken());
        assertEquals(email, userResult.getEmail());

    }

    @Test
    @DisplayName("deve lançar uma exceção ao tentar atualizar a senha de um usuário inexistente")
    public void test_should_throw_an_exception_when_trying_to_update_the_password_of_a_non_existent_user() {

        String token = RandomString.make(30);
        String email = "email@inexistente.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.updateResetPasswordToken(token, email));

    }

    @Test
    @DisplayName("deve atualizar a senha do usuario")
    public void test_should_update_the_user_password() {

        User user = getUser();
        user.setToken(RandomString.make(30));

        String newPassword = "senha123";

        authService.updatePassword(user, newPassword);

        ArgumentCaptor<User> result = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock).save(result.capture());

        assertNull(result.getValue().getToken());
        assertTrue(encoder.matches(newPassword, result.getValue().getPassword()));
    }

}
