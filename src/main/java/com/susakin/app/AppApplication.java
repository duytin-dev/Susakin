package com.susakin.app;

import com.susakin.app.config.RenderDatabaseBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		RenderDatabaseBootstrap.configure();
		SpringApplication.run(AppApplication.class, args);
	}

}
