package hu.bets.config;

import com.mongodb.client.MongoCollection;
import hu.bets.common.config.model.CommonConfig;
import hu.bets.common.util.hash.MD5HashGenerator;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.DefaultResultHandlerService;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.DefaultModelConverterService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.services.points.DefaultPointsCalculatorService;
import hu.bets.points.services.points.PointsCalculatorService;
import hu.bets.processor.AbstractProcessorTask;
import hu.bets.processor.CommonExecutor;
import hu.bets.processor.ProcessingResult;
import hu.bets.processor.ProcessorTaskFactory;
import hu.bets.processor.betbatch.BetBatchTask;
import hu.bets.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.processor.betbatch.processing.DefaultBetBatchProcessor;
import hu.bets.processor.betbatch.validation.BetBatchValidator;
import hu.bets.processor.betbatch.validation.DefaultBetBatchValidator;
import hu.bets.processor.betrequest.BetRequestTask;
import hu.bets.processor.betrequest.processing.BetRequestProcessor;
import hu.bets.processor.betrequest.processing.DefaultBetRequestProcessor;
import hu.bets.processor.betrequest.validation.BetRequestValidator;
import hu.bets.processor.betrequest.validation.DefaultBetRequestValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;

import java.util.Map;
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
    public BetRequestProcessor getBetRequestProcessor(ScoresServiceDAO scoresServiceDAO) {
        return new DefaultBetRequestProcessor(scoresServiceDAO);
    }

    @Bean
    public BetRequestValidator getDefaultBetRequestValidator() {
        return new DefaultBetRequestValidator();
    }

    @Bean
    public ProcessorTaskFactory processorTaskFactory(Map<String, AbstractProcessorTask<?>> tasks) {
        return new ProcessorTaskFactory(tasks);
    }

    @Bean
    public CommonExecutor getBetsBatchExecutor(CompletionService<ProcessingResult> completionService, ProcessorTaskFactory taskFactory) {
        return new CommonExecutor(completionService, taskFactory);
    }

    @Bean
    public PointsCalculatorService getPointsCalculatorService() {
        return new DefaultPointsCalculatorService();
    }

    @Bean("ACKNOWLEDGE_REQUEST")
    public BetBatchTask betBatchTask(DefaultBetBatchValidator defaultBetBatchValidator, DefaultBetBatchProcessor processor) {
        return new BetBatchTask(defaultBetBatchValidator, processor);
    }

    @Bean("BETS_REQUEST")
    public BetRequestTask betRequestTask(DefaultBetRequestValidator validator, DefaultBetRequestProcessor processor) {
        return new BetRequestTask(validator, processor);
    }

}
