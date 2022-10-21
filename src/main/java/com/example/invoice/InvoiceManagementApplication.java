package com.example.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.invoice",
		"com.example.invoice.repository",
		"com.example.invoice.model",
		"com.example.invoice.service.impl",
})
public class InvoiceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceManagementApplication.class, args);
	}

}
