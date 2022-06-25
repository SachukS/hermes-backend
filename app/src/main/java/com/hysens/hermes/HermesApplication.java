package com.hysens.hermes;

import com.hysens.hermes.telegram.client.Telegram;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HermesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HermesApplication.class, args);
		///ToDo normal init and login, below is temporary solution
		Telegram telegram = new Telegram();
//		try {
//			WhatsAppClient whatsAppClient = new WhatsAppClient();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
