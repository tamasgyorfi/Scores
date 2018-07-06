package hu.bets.points.web.api;

import hu.bets.common.util.json.Json;
import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.model.ToplistEntry;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.Type;
import hu.bets.points.services.ToplistService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.web.model.ResultResponse;
import hu.bets.points.web.model.ToplistRequestPayload;
import hu.bets.points.web.model.ToplistResponsePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Component
@Path("/scores/football/v1")
public class ScoresResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoresResource.class);

    @Autowired
    private ModelConverterService modelConverterService;
    @Autowired
    private CommonExecutor commonExecutor;
    @Autowired
    private ToplistService toplistService;

    @GET
    @Path("info")
    public String info() {
        return "<html><h1>Football-Scores up and running</h1></html>";
    }

    @POST
    @Path("results")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postResult(String resultRequest) {

        LOGGER.info("Post request invoked. {}" , resultRequest);
        try {
            validate(resultRequest);
            commonExecutor.enqueue(Optional.of(resultRequest), Type.BETS_REQUEST);
            return Response.accepted(ResultResponse.success("Match results saved.")).build();
        } catch (IllegalPayloadException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ResultResponse.error(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ResultResponse.error(e.getMessage())).build();
        }
    }

    @POST
    @Path("toplist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScores(String resultRequest) {

        LOGGER.info("getScores method invoked. {}" , resultRequest);
        try {
            List<ToplistEntry> toplistScore = toplistService.getToplistScore(new Json().fromJson(resultRequest, ToplistRequestPayload.class).getUserIds());
            LOGGER.info("Returning toplist scores: {}", toplistScore);
            return Response.ok().entity(new ToplistResponsePayload(toplistScore, "")).build();
        } catch (IllegalPayloadException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private void validate(String resultRequest) {
        MatchResultWithToken matchResult = modelConverterService.convert(resultRequest);
        LOGGER.info("MatchResult resulting from conversion: " + matchResult);
    }
}
