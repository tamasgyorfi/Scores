package hu.bets.points.services.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;
import hu.bets.model.SecureMatchResult;

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
            SecureMatchResult result = MAPPER.readValue(resultRequest, SecureMatchResult.class);
            return result.getMatchResult();
        } catch (Exception e) {
            throw new IllegalJsonException(e);
        }
    }
}
