package com.example.mygoReaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.DelegatingFilterProxy;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class MygoReactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MygoReactionApplication.class, args);
	}


}
