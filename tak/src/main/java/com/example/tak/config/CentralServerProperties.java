package com.example.tak.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "central.server")
@Getter
@Setter
public class CentralServerProperties {
    private String baseUrl;
}
