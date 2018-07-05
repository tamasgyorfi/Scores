package hu.bets.points.config;

import com.mongodb.client.MongoCollection;
import hu.bets.common.config.model.CommonConfig;
import hu.bets.common.util.EnvironmentVarResolver;
import hu.bets.common.util.hash.MD5HashGenerator;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.points.dbaccess.DefaultScoresServiceDAO;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.model.ProcessingResult;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.ProcessorTask;
import hu.bets.points.processor.ProcessorTaskFactory;
import hu.bets.points.processor.betbatch.BetBatchTask;
import hu.bets.points.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.points.processor.betbatch.processing.DefaultBetBatchProcessor;
import hu.bets.points.processor.betbatch.validation.BetBatchValidator;
import hu.bets.points.processor.betbatch.validation.DefaultBetBatchValidator;
import hu.bets.points.processor.betrequest.BetRequestTask;
import hu.bets.points.processor.betrequest.processing.BetRequestProcessor;
import hu.bets.points.processor.betrequest.processing.DefaultBetRequestProcessor;
import hu.bets.points.processor.betrequest.validation.BetRequestValidator;
import hu.bets.points.processor.betrequest.validation.DefaultBetRequestValidator;
import hu.bets.points.processor.retry.RetryTask;
import hu.bets.points.processor.retry.RetryTaskRunner;
import hu.bets.points.services.ToplistService;
import hu.bets.points.services.ToplistServiceImpl;
import hu.bets.points.services.conversion.DefaultModelConverterService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.services.points.DefaultPointsCalculatorService;
import hu.bets.points.services.points.PointsCalculatorService;
import hu.bets.servicediscovery.EurekaFacadeImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.JedisPool;

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
    public ScoresServiceDAO matchDAO(@Qualifier("ResultsCollection") MongoCollection matchResultCollection,
                                     @Qualifier("ScoresCollection") MongoCollection scoresCollection,
                                     @Qualifier("ToplistCollection") MongoCollection toplistCollection,
                                     JedisPool jedisPool) {
        return new DefaultScoresServiceDAO(matchResultCollection, scoresCollection, toplistCollection, jedisPool);
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
    public ProcessorTaskFactory processorTaskFactory(Map<String, ProcessorTask> tasks) {
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

    @Bean("RETRY_REQUEST")
    public RetryTask retryTask(ScoresServiceDAO scoresServiceDAO) {
        return new RetryTask(scoresServiceDAO);
    }

    @Bean(initMethod = "run", destroyMethod = "kill")
    public RetryTaskRunner retryTaskRunner(CommonExecutor commonExecutor) {
        return new RetryTaskRunner(commonExecutor, Executors.newSingleThreadScheduledExecutor());
    }

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setFileEncoding("UTF-8");
        configurer.setIgnoreResourceNotFound(false);
        configurer.setLocation(new ClassPathResource("values.properties"));

        return configurer;
    }

    @Bean
    public EurekaFacadeImpl eurekaFacade() {
        return new EurekaFacadeImpl(new EnvironmentVarResolver().getEnvVar("EUREKA_URL"));
    }

    @Bean
    public ToplistService toplistService(ScoresServiceDAO scoresServiceDAO) {
        return new ToplistServiceImpl(scoresServiceDAO);
    }
}
