package integration;

import com.google.gson.Gson;
import hu.bets.common.util.EnvironmentVarResolver;
import hu.bets.config.ApplicationConfig;
import hu.bets.config.MessagingConfig;
import hu.bets.config.WebConfig;
import hu.bets.steps.Given;
import hu.bets.steps.When;
import hu.bets.web.model.ResultResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

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
    public void InfoEndpointShouldReplyWIthOk() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/info";

        HttpResponse httpResponse = When.iMakeAGetRequest(endpoint);
        String response = EntityUtils.toString(httpResponse.getEntity());

        assertEquals("<html><h1>Football-Scores up and running</h1></html>", response);
    }

}
