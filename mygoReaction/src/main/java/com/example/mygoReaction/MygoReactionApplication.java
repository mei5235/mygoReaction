package com.example.mygoReaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MygoReactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MygoReactionApplication.class, args);
	}

}
