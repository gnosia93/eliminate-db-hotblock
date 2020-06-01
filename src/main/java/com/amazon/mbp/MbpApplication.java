package com.amazon.mbp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class MbpApplication {

	public static void main(String[] args) {
		SpringApplication.run(MbpApplication.class, args);
	}

}
