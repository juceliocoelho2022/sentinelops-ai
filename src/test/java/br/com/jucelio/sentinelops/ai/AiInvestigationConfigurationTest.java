package br.com.jucelio.sentinelops.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class AiInvestigationConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AiInvestigationConfiguration.class));

    @Test
    void shouldKeepDeterministicModeWhenSpringAiIsNotExplicitlyEnabled() {
        contextRunner.run(context ->
                assertThat(context).doesNotHaveBean("springAiInvestigationGateway"));
    }

    @Test
    void shouldNotCreateSpringAiGatewayWithoutChatClientBuilderEvenWhenEnabled() {
        contextRunner
                .withPropertyValues("sentinelops.ai.mode=spring-ai")
                .run(context ->
                        assertThat(context).doesNotHaveBean("springAiInvestigationGateway"));
    }
}
