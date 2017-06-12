package hu.bets.points.services.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.model.FinalMatchResult;
import hu.bets.model.Result;

public class DefaultModelConverterService implements ModelConverterService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SchemaValidator schemaValidator;

    public DefaultModelConverterService(SchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    @Override
    public FinalMatchResult convert(String matchId, String resultRequest) {

        validate(resultRequest);
        return convertResultRequest(matchId, resultRequest);
    }

    private void validate(String resultRequest) {
        schemaValidator.validatePayload(resultRequest, "matchResult.request.schema.json");
    }

    private FinalMatchResult convertResultRequest(String matchId, String resultRequest) {
        try {
            Result result = MAPPER.readValue(resultRequest, Result.class);
            return new FinalMatchResult(matchId, result);
        } catch (Exception e) {
            throw new IllegalJsonException(e);
        }
    }
}
