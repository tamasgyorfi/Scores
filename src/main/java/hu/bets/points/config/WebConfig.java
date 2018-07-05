package hu.bets.points.config;

import hu.bets.common.config.CommonWebConfig;
import hu.bets.common.config.model.Resources;
import hu.bets.points.web.api.ScoresResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonWebConfig.class)
public class WebConfig {

    @Bean
    public Resources resources(ScoresResource scoresResource) {
        return new Resources().addResource(scoresResource);
    }

    @Bean
    public ScoresResource matchEndResource() {
        return new ScoresResource();
    }
}
