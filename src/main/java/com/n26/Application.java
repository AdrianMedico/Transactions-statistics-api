package com.n26;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CachingConfigurerSupport;

@SpringBootApplication
public class Application extends CachingConfigurerSupport {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

}
