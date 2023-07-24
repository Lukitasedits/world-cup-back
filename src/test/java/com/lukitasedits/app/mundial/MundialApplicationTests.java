package com.lukitasedits.app.mundial;

import com.lukitasedits.app.mundial.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MundialApplicationTests {


	@Autowired
	private EmailService emailService;

	@Test
	void contextLoads() {
	}

	@Test
	public void probarEmail(){
		emailService.sendEmail();
	}


}
