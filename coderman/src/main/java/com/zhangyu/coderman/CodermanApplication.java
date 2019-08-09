package com.zhangyu.coderman;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.zhangyu.coderman.dao")
@SpringBootApplication
@EnableTransactionManagement
public class CodermanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodermanApplication.class, args);
	}
}
