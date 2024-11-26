package xyz.kbws.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author kbws
 * @date 2024/11/26
 * @description:
 */
@Data
@Configuration
public class AppConfig {

    @Value("${project.folder:}")
    private String projectFolder;
}
