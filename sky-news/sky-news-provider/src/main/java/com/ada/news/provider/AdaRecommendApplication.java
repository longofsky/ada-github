package com.ada.news.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Description recommend服务
 * @Author litianlong
 * @Date 2019-06-11 17:22
 */
@SpringBootApplication(scanBasePackages = {"com.ada"})
@EnableDiscoveryClient
@MapperScan("com.ada.news.dao.mapper")
@EnableSwagger2
@EnableScheduling
public class AdaRecommendApplication {

	public static void main(String[] args) {

		SpringApplication.run(AdaRecommendApplication.class, args);
	}

}
