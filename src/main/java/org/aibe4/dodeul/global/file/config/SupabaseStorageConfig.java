// src/main/java/org/aibe4/dodeul/global/file/config/SupabaseStorageConfig.java
package org.aibe4.dodeul.global.file.config;

import org.aibe4.dodeul.global.file.service.SupabaseStorageClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SupabaseStorageProperties.class)
public class SupabaseStorageConfig {

    @Bean
    public RestClient supabaseRestClient(SupabaseStorageProperties props) {
        return RestClient.builder()
            .baseUrl(props.getUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getServiceRoleKey())
            .defaultHeader("apikey", props.getServiceRoleKey())
            .build();
    }

    @Bean
    public SupabaseStorageClient supabaseStorageClient(
        RestClient supabaseRestClient, SupabaseStorageProperties props) {
        return new SupabaseStorageClient(supabaseRestClient, props);
    }
}
