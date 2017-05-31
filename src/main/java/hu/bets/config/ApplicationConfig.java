package hu.bets.config;

import hu.bets.common.config.model.CommonConfig;
import hu.bets.common.util.SchemaValidator;
import hu.bets.points.services.DefaultResultHandlerService;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.DefaultModelConverterService;
import hu.bets.points.services.conversion.ModelConverterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonConfig.class)
public class ApplicationConfig {

    @Bean
    public ModelConverterService modelConverterService(SchemaValidator schemaValidator) {
        return new DefaultModelConverterService(schemaValidator);
    }

    @Bean
    public ResultHandlerService resultHandlerService(ModelConverterService modelConverterService) {
        return new DefaultResultHandlerService(modelConverterService, null);
    }
}
