package com.dis.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "echart")
@PropertySource("classpath:config/echart.properties")
@Component
public class Echart {

    private int refresh;

    private int allTime;

    private int refreshs;

}
