package xyz.kbws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class KLiveAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(KLiveAdminApplication.class, args);
    }
}
