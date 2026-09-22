package br.com.jucelio.sentinelops.incident;

import br.com.jucelio.sentinelops.incident.api.IncidentRequest;
import br.com.jucelio.sentinelops.incident.api.IncidentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class IncidentPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("sentinelops")
                    .withUsername("sentinelops")
                    .withPassword("sentinelops");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired IncidentService service;

    @Test
    void shouldPersistIncidentUsingPostgresAndFlyway() {
        IncidentResponse created = service.create(new IncidentRequest(
                "Database latency", "ledger-service", Severity.HIGH, "Slow database calls"));

        IncidentResponse loaded = service.findById(created.id());

        assertThat(loaded.id()).isNotNull();
        assertThat(loaded.serviceName()).isEqualTo("ledger-service");
        assertThat(loaded.status()).isEqualTo(IncidentStatus.OPEN);
    }
}
