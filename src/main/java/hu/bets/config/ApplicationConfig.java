package hu.bets.config;

import hu.bets.web.api.MatchEndResource;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    private static final Logger LOGGER = Logger.getLogger(ApplicationConfig.class);

    @Bean
    public MatchEndResource footballBetResource() {
        return new MatchEndResource();
    }

}
