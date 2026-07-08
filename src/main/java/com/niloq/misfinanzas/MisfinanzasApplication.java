package com.niloq.misfinanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //NOTIFICACIONES
@SpringBootApplication
public class MisfinanzasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MisfinanzasApplication.class, args);
	}

}
