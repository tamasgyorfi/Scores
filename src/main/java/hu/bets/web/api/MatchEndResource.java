package hu.bets.web.api;

import hu.bets.points.services.ResultHandlerService;
import hu.bets.web.model.ResultResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/scores/football/v1")
public class MatchEndResource {

    private static final Logger LOGGER = Logger.getLogger(MatchEndResource.class);

    @Autowired
    private ResultHandlerService resultHandlerService;

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

        LOGGER.info("Post request invoked. " + matchId +": " +resultRequest);
        try {
            resultHandlerService.saveMatchResult(matchId, resultRequest);
            return ResultResponse.success(Response.Status.ACCEPTED, "Match results saved.");
        } catch (Exception e) {
            return ResultResponse.error(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
