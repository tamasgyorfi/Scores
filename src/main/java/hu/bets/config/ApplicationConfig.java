package hu.bets.config;

import com.mongodb.client.MongoCollection;
import hu.bets.common.config.model.CommonConfig;
import hu.bets.common.util.SchemaValidator;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.points.services.DefaultResultHandlerService;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.DefaultModelConverterService;
import hu.bets.points.services.conversion.ModelConverterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;

@Configuration
@Import(CommonConfig.class)
public class ApplicationConfig {

    @Bean
    public ModelConverterService modelConverterService(SchemaValidator schemaValidator) {
        return new DefaultModelConverterService(schemaValidator);
    }

    @Bean
    public ResultHandlerService resultHandlerService(ModelConverterService modelConverterService, ScoresServiceDAO scoresServiceDAO) {
        return new DefaultResultHandlerService(modelConverterService, scoresServiceDAO);
    }

    @Bean
    public ScoresServiceDAO matchDAO(@Qualifier("ResultsCollection") MongoCollection matchResultCollection,
                                     @Qualifier("ScoresCollection") MongoCollection scoresCollection,
                                     Jedis errorCollection) {
        return new MongoBasedScoresServiceDAO(matchResultCollection, scoresCollection, errorCollection);
    }
}
