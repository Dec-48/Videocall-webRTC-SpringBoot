package com.calling.websk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.calling.websk" })
public class WebskApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebskApplication.class, args);
	}

}
