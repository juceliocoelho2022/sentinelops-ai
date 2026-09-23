package br.com.jucelio.sentinelops.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class AiInvestigationConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AiInvestigationConfiguration.class);

    @Test
    void doesNotCreateSpringAiGatewayInDeterministicMode() {
        contextRunner
                .withPropertyValues("sentinelops.ai.mode=deterministic")
                .run(context -> assertThat(context).doesNotHaveBean(AiInvestigationGateway.class));
    }

    @Test
    void createsSpringAiGatewayWhenSpringAiModeAndBuilderAreAvailable() {
        ChatClient.Builder builder = org.mockito.Mockito.mock(ChatClient.Builder.class);
        ChatClient chatClient = org.mockito.Mockito.mock(ChatClient.class);
        org.mockito.Mockito.when(builder.build()).thenReturn(chatClient);

        contextRunner
                .withPropertyValues("sentinelops.ai.mode=spring-ai")
                .withBean(ChatClient.Builder.class, () -> builder)
                .run(context -> {
                    assertThat(context).hasSingleBean(AiInvestigationGateway.class);
                    assertThat(context.getBean(AiInvestigationGateway.class))
                            .isInstanceOf(SpringAiInvestigationGateway.class);
                });
    }
}
