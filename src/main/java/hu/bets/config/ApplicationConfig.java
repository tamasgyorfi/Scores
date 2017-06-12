package hu.bets.config;

import com.mongodb.client.MongoCollection;
import hu.bets.common.config.model.CommonConfig;
import hu.bets.common.util.hash.MD5HashGenerator;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.messaging.processing.processor.BetBatchProcessor;
import hu.bets.messaging.processing.processor.DefaultBetBatchProcessor;
import hu.bets.messaging.processing.validation.BetBatchValidator;
import hu.bets.messaging.processing.validation.DefaultBetBatchValidator;
import hu.bets.model.ProcessingResult;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.DefaultResultHandlerService;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.DefaultModelConverterService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.services.points.DefaultPointsCalculatorService;
import hu.bets.points.services.points.PointsCalculatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

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

    @Bean
    public CompletionService<ProcessingResult> getExecutor() {
        return new ExecutorCompletionService<ProcessingResult>(Executors.newFixedThreadPool(3));
    }

    @Bean
    public BetBatchProcessor getBetBatchProcessor(ScoresServiceDAO scoresServiceDAO, PointsCalculatorService calculatorService) {
        return new DefaultBetBatchProcessor(scoresServiceDAO, calculatorService);
    }

    @Bean
    public BetBatchValidator getDefaultBetBatchValidator() {
        return new DefaultBetBatchValidator(new MD5HashGenerator());
    }

    @Bean
    public PointsCalculatorService getPointsCalculatorService() {
        return new DefaultPointsCalculatorService();
    }
}
