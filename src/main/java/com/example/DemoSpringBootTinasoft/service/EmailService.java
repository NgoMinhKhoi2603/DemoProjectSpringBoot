package com.example.DemoSpringBootTinasoft.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendActivationEmail(String to, String username, String activationLink) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMsg = "<h3>Chào " + username + ",</h3>"
                + "<p>Cảm ơn bạn đã đăng ký. Vui lòng nhấp vào liên kết dưới đây để kích hoạt tài khoản của bạn:</p>"
                + "<a href=\"" + activationLink + "\">" + activationLink + "</a>"
                + "<br><p>Trân trọng,</p>";

        helper.setText(htmlMsg, true);
        helper.setTo(to);
        helper.setSubject("Kích hoạt tài khoản của bạn");
        helper.setFrom("ngominhkhoi262003@gmail.com");

        mailSender.send(mimeMessage);
    }
}
