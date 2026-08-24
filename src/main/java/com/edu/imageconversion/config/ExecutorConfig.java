package com.edu.imageconversion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class ExecutorConfig {

    @Bean
    public ForkJoinPool imageProcessingPool() {
        int parallelism = Math.max(
                2,
                Runtime.getRuntime().availableProcessors()
        );

        return new ForkJoinPool(parallelism);
    }
}