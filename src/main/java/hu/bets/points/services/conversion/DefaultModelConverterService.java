package hu.bets.points.services.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.points.model.MatchResultWithToken;

public class DefaultModelConverterService implements ModelConverterService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SchemaValidator schemaValidator;

    public DefaultModelConverterService(SchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    @Override
    public MatchResultWithToken convert(String resultRequest) {

        validate(resultRequest);
        return convertResultRequest(resultRequest);
    }

    private void validate(String resultRequest) {
        schemaValidator.validatePayload(resultRequest, "matchResult.request.schema.json");
    }

    private MatchResultWithToken convertResultRequest(String resultRequest) {
        try {
            return MAPPER.readValue(resultRequest, MatchResultWithToken.class);
        } catch (Exception e) {
            throw new IllegalJsonException(e);
        }
    }
}
