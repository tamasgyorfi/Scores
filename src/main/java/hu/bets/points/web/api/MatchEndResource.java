package hu.bets.points.web.api;

import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.Type;
import hu.bets.points.services.ResultHandlerService;
import hu.bets.points.services.conversion.ModelConverterService;
import hu.bets.points.web.model.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Component
@Path("/scores/football/v1")
public class MatchEndResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEndResource.class);

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
    @Path("results")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postResult(String resultRequest) {

        LOGGER.info("Post request invoked. {}" , resultRequest);
        try {
            validate(resultRequest);
            commonExecutor.enqueue(Optional.of(resultRequest), Type.BETS_REQUEST);
            return ResultResponse.success(Response.Status.ACCEPTED, "Match results saved.").asJson();
        } catch (IllegalPayloadException e) {
            return ResultResponse.error(Response.Status.BAD_REQUEST, e.getMessage()).asJson();
        } catch (Exception e) {
            return ResultResponse.error(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage()).asJson();
        }
    }

    private void validate(String resultRequest) {
        MatchResultWithToken matchResult = modelConverterService.convert(resultRequest);
        LOGGER.info("MatchResult resulting from conversion: " + matchResult);
    }
}
