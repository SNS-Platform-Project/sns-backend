package com.example.snsbackend;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class SnsBackendApplication {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("Server time zone has been configured to {} (UTC+9)", new Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(SnsBackendApplication.class, args);
        log.info("Stacks server started successfully! ^_^");
    }
}
