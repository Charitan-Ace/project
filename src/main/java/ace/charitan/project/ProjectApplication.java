package ace.charitan.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @PostConstruct
    public void logEnvironmentVariables() {
        System.out.println("SPRING_DATASOURCE_PROJECT_URL: " + System.getenv("SPRING_DATASOURCE_PROJECT_URL"));
        System.out
                .println("SPRING_DATASOURCE_PROJECT_USERNAME: " + System.getenv("SPRING_DATASOURCE_PROJECT_USERNAME"));
        System.out
                .println("SPRING_DATASOURCE_PROJECT_PASSWORD: " + System.getenv("SPRING_DATASOURCE_PROJECT_PASSWORD"));
    }

}
