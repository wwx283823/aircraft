package com.dis.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "sva")
@PropertySource("classpath:config/svaConfig.properties")
@Component
public class Sva {

    private String id;

    private String ip;

    private String username;

    private String password;

    private int status; //0关闭，1开启

    private String statusCode;

    private int type; // 0非匿名化，1匿名化，2指定用户, 3 高密重载

    private String idType;

    private String tokenPort;

    private String brokerPort;

    private String token;

}
