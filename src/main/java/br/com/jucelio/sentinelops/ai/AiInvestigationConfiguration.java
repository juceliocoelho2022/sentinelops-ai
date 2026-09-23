package br.com.jucelio.sentinelops.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiInvestigationConfiguration {

    @Bean
    @Primary
    @ConditionalOnBean(ChatClient.Builder.class)
    @ConditionalOnProperty(name = "sentinelops.ai.mode", havingValue = "spring-ai")
    AiInvestigationGateway springAiInvestigationGateway(ChatClient.Builder builder) {
        return new SpringAiInvestigationGateway(builder.build());
    }
}
