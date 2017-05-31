package hu.bets.config;

import hu.bets.common.config.CommonWebConfig;
import hu.bets.common.config.model.Resources;
import hu.bets.web.api.MatchEndResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonWebConfig.class)
public class WebConfig {

    @Bean
    public Resources resources(MatchEndResource matchEndResource) {
        return new Resources().addResource(matchEndResource);
    }

    @Bean
    public MatchEndResource matchEndResource() {
        return new MatchEndResource();
    }
}
