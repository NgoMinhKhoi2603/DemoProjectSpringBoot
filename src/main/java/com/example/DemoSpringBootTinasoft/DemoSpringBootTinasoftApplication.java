package com.example.DemoSpringBootTinasoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Kích hoạt Cron Job
@EnableAsync
public class DemoSpringBootTinasoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoSpringBootTinasoftApplication.class, args);
	}

}
