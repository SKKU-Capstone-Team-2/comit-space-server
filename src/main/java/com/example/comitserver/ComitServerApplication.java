package com.example.comitserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class ComitServerApplication {

	public static void main(String[] args) {
		// 예시: Spring Shell 또는 간단한 main 클래스에서 실행
//		System.out.println("This is admin01");
//		System.out.println(new BCryptPasswordEncoder().encode("admin01"));
//		System.out.println("This is comit01");
//		System.out.println(new BCryptPasswordEncoder().encode("comit01"));

		SpringApplication.run(ComitServerApplication.class, args);
	}
}