package com.itea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.itea")
public class IteaApplication extends SpringBootServletInitializer {
	
	 @Override
     protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
         return application.sources(IteaApplication.class);
     }
	 

	public static void main(String[] args) {
		SpringApplication.run(IteaApplication.class, args);
	}
}
