package hu.bets.web.api;

import hu.bets.points.services.ResultHandlerService;
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
    @Path("results")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postResult(String resultRequest) {

        String matchId = "1";
        LOGGER.info("Post request invoked. " + matchId +": " +resultRequest);
        try {
            resultHandlerService.saveResult(matchId, resultRequest);
            return Response.accepted().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
