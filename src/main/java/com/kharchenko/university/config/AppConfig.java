package com.kharchenko.university.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.kharchenko.university", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
@PropertySource("classpath:application.properties")
public class AppConfig {


    @Value("${db.driver}")
    private String databaseDriver;
    @Value("${db.url}")
    private String databaseUrl;
    @Value("${db.user}")
    private String databaseUser;
    @Value("${db.password}")
    private String databasePassword;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseDriver);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUser);
        dataSource.setPassword(databasePassword);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}