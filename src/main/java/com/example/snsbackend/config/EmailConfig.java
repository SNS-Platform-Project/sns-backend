package com.example.snsbackend.config;

import com.example.snsbackend.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    // Gmail의 SMTP 서버 호스트
    @Value("${spring.mail.host}")
    private String host;

    // Gmail SMTP 서버는 587번 포트 사용
    @Value("${spring.mail.port}")
    private Integer port;

    // 전송 이메일 계정
    @Value("${spring.mail.username}")
    private String username;

    // 앱 비밀번호
    @Value("${spring.mail.password}")
    private String password;

    // SMTP 서버에 인증이 필요한 경우 true로 지정 (Gmail은 요구함)
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    // SMTP 연결의 암호화 여부 (활성화하면 이메일 전송 과정에서 데이터가 암호화됨)
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    // STARTTLS 필수 여부 (활성화하면 서버가 STARTTLS를 지원하지 않을 경우 연결 실패됨)
    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean starttlsRequired;

    // SMTP 서버에 연결할 때 최대 대기 시간 (시간 내에 서버에 연결되지 않으면 예외 발생)
    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private Integer connectionTimeout;

    // SMTP 서버와의 통신에서 읽기 작업이 완료될 때까지 최대 대기 시간 (시간 내에 응답이 없으면 예외 발생)
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private Integer timeout;

    // SMTP 서버에 데이터를 쓰는 작업 최대 대기 시간 (시간 내에 데이터 전송이 완료되지 않으면 예외 발생)
    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    private Integer writeTimeout;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }

    @Bean
    public Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttlsEnable);
        properties.put("mail.smtp.starttls.required", starttlsRequired);
        properties.put("mail.smtp.connectiontimeout", connectionTimeout);
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.smtp.writetimeout", writeTimeout);

        return properties;
    }
}
