package com.example.snsbackend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${AUTH_CODE_EXPIRE_TIME}")
    private long AUTH_CODE_EXPIRE_TIME;

    // 인증번호 이메일 전송 (HTML)
    public void sendAuthCodeEmail(String to, String authCode) throws MessagingException {
        String subject = "[Stacks] 본인 인증을 위한 인증번호 안내";
        String text = setContext(authCode, String.valueOf(((AUTH_CODE_EXPIRE_TIME) / 1000) / 60));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // 활성화하면 첨부파일 추가 가능
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // 활성화하면 HTML 사용 가능

        mailSender.send(mimeMessage);
    }

    // thymeleaf를 통한 html 적용
    public String setContext(String authCode, String authCodeExp) {
        Context context = new Context();
        context.setVariable("authCode", authCode);
        context.setVariable("authCodeExp", authCodeExp);
        return templateEngine.process("mail", context);
    }
}
