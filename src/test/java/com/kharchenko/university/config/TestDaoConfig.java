package com.kharchenko.university.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.kharchenko.university.dao.impl")
@PropertySource("classpath:application.properties")
public class TestDaoConfig {

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