package com.example.payment.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.spring.webmvc.v6_0.SpringWebMvcTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        return AutoConfiguredOpenTelemetrySdk.builder()
            .setResultAsGlobal()
            .build()
            .getOpenTelemetrySdk();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.payment");
    }

    @Bean
    public Filter webMvcTracingFilter(OpenTelemetry openTelemetry) {
        return SpringWebMvcTelemetry.create(openTelemetry).createServletFilter();
    }
}
