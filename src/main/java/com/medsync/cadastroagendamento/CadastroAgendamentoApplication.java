package com.medsync.cadastroagendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CadastroAgendamentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadastroAgendamentoApplication.class, args);
    }
}
