package com.example.mybookshopapp;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author karl
 */

@SpringBootTest
class MyBockShopAppApplicationTests {

	@Value("${auth.secret}")
	String authSecret;

	private final MyBockShopAppApplication appApplication;

	@Autowired
	MyBockShopAppApplicationTests(MyBockShopAppApplication appApplication) {
		this.appApplication = appApplication;
	}

	@Test
	void contextLoads() {
		assertNotNull(appApplication);
	}

	@Test
	void verifyAuthSecret() {
		assertThat(authSecret, Matchers.containsString("cre"));
	}

}
