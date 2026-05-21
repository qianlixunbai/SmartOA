package com.smartoa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.smartoa.mapper")
public class SmartoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartoaApplication.class, args);
    }

}
