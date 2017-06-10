package integration;

import com.google.gson.Gson;
import hu.bets.common.util.EnvironmentVarResolver;
import hu.bets.config.ApplicationConfig;
import hu.bets.config.MessagingConfig;
import hu.bets.config.WebConfig;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.steps.Given;
import hu.bets.steps.When;
import hu.bets.steps.util.ApplicationContextHolder;
import hu.bets.web.model.ResultResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static utils.TestUtils.getRecord;

public class SaveMatchIntegrationTest {

    private static final String CORRECT_MATCH_END_PAYLOAD = "{\n" +
            "  \"awayTeamGoals\": 0,\n" +
            "  \"awayTeamId\": \"1001\",\n" +
            "  \"competitionId\": \"champions\",\n" +
            "  \"homeTeamGoals\": 2,\n" +
            "  \"homeTeamId\": \"2063\"\n" +
            "}";

    @BeforeClass
    public static void setup() throws Exception {
        Given.environmentIsUpAndRunning(ApplicationConfig.class,
                FakeDatabaseConfig.class,
                MessagingConfig.class,
                WebConfig.class);
        
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void resultShouldBeRejectedIfSchemaValidationFails() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/AA-777-vxF";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, "{\"payload\":\"none\"}");
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(500, resultResponse.getResponseCode());
        assertTrue(resultResponse.getError().contains("org.everit.json.schema.ValidationException"));
        assertEquals("", resultResponse.getResponsePayload());
    }

    @Test
    public void resultShouldBeSavedIfSchemaValidationPasses() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/101LLLAAA";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, CORRECT_MATCH_END_PAYLOAD);
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(200, resultResponse.getResponseCode());
        assertEquals("", resultResponse.getError());
        assertEquals("Match results saved.", resultResponse.getResponsePayload());
    }

    @Test
    public void infoEndpointShouldReplyWIthOk() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/info";

        HttpResponse httpResponse = When.iMakeAGetRequest(endpoint);
        String response = EntityUtils.toString(httpResponse.getEntity());

        assertEquals("<html><h1>Football-Scores up and running</h1></html>", response);
    }

    @Test
    public void testRedisMongoInterpay() {
        MongoBasedScoresServiceDAO matchDAO = ApplicationContextHolder.getBean(MongoBasedScoresServiceDAO.class);

        LocalDateTime out = matchDAO.getCurrentTime().minusHours(48);
        LocalDateTime in = matchDAO.getCurrentTime().minusHours(8);

        matchDAO.saveMatch(getRecord(in, "match1"));
        matchDAO.saveMatch(getRecord(out, "match2"));
        matchDAO.saveMatch(getRecord(in, "match3"));
        matchDAO.saveMatch(getRecord(in, "match4"));
        matchDAO.saveMatch(getRecord(out, "match5"));
        matchDAO.saveMatch(getRecord(in, "match6"));
        matchDAO.saveMatch(getRecord(in, "match7"));
        matchDAO.saveMatch(getRecord(out, "match8"));
        matchDAO.saveMatch(getRecord(out, "match9"));
        matchDAO.saveMatch(getRecord(in, "match10"));

        matchDAO.betProcessingFailedFor("match1");
        matchDAO.betProcessingFailedFor("match2");
        matchDAO.betProcessingFailedFor("match9");
        matchDAO.betProcessingFailedFor("match10");

        assertEquals(Arrays.asList("match1", "match10"), matchDAO.getFailedMatchIds());
    }

}
