package br.com.desbravador.projetoacelera.email;

import br.com.desbravador.projetoacelera.users.domain.User;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;

public interface EmailService {

    void sendAccountRegistration(User user);
    void sendEmail(SimpleMailMessage msg);

    void sendHtmlAccountRegistration(User user);
    void sendHtmlEmail(MimeMessage msg);

    void sendNewPasswordEmail(User user, String newPass);
    void sendHtmlNewPasswordEmail(User user, String newPass);

    void sendResetPasswordEmail(String recipientEmail, String link);
    void sendHtmlResetPasswordEmail(String recipientEmail, String link);
}
