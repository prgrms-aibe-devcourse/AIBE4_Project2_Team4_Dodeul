// src/main/java/org/aibe4/dodeul/global/file/config/SupabaseStorageProperties.java
package org.aibe4.dodeul.global.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "supabase")
public class SupabaseStorageProperties {

    private String url;
    private String serviceRoleKey;
    private Storage storage = new Storage();

    @Getter
    @Setter
    public static class Storage {
        private String bucket;
    }
}
