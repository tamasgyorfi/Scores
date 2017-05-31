package hu.bets.points.services.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.common.util.SchemaValidator;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;

public class DefaultModelConverterService implements ModelConverterService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SchemaValidator schemaValidator;

    public DefaultModelConverterService(SchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    @Override
    public MatchResult convert(String matchId, String resultRequest) {

        validate(resultRequest);
        return convertResultRequest(matchId, resultRequest);
    }

    private void validate(String resultRequest) {
        schemaValidator.validatePayload(resultRequest, "matchResult.request.schema.json");
    }

    private MatchResult convertResultRequest(String matchId, String resultRequest) {
        try {
            Result result = MAPPER.readValue(resultRequest, Result.class);
            return new MatchResult(matchId, result);
        } catch (Exception e) {
            throw new IllegalJsonException(e);
        }
    }
}