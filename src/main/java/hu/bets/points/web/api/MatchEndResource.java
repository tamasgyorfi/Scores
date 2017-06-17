package hu.bets.points.web.api;

import hu.bets.points.model.SecureMatchResult;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.Type;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.web.model.ResultResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Component
@Path("/scores/football/v1")
public class MatchEndResource {

    private static final Logger LOGGER = Logger.getLogger(MatchEndResource.class);

    @Autowired
    private ResultHandlerService resultHandlerService;
    @Autowired
    private ModelConverterService modelConverterService;
    @Autowired
    private CommonExecutor commonExecutor;

    @GET
    @Path("info")
    public String info() {
        return "<html><h1>Football-Scores up and running</h1></html>";
    }

    @POST
    @Path("results/{matchId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultResponse postResult(@PathParam("matchId") String matchId, String resultRequest) {

        LOGGER.info("Post request invoked. " + matchId + ": " + resultRequest);
        try {
            validateAndConvert(matchId, resultRequest);
            commonExecutor.enqueue(Optional.of(resultRequest), Type.BETS_REQUEST);
            return ResultResponse.success(Response.Status.ACCEPTED, "Match results saved.");
        } catch (IllegalPayloadException e) {
            return ResultResponse.error(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResultResponse.error(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private SecureMatchResult validateAndConvert(String matchId, String resultRequest) {
        SecureMatchResult matchResult = modelConverterService.convert(matchId, resultRequest);
        if (!matchResult.getMatchResult().getResult().getMatchId().equals(matchId)) {
            throw new IllegalPayloadException("MatchId mismatch.");
        }
        LOGGER.info("MatchResult resulting from conversion: " + matchResult);

        return matchResult;
    }
}
