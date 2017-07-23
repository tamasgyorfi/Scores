package hu.bets.points.services.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.common.util.schema.SchemaValidator;
import hu.bets.points.model.SecureMatchResult;

public class DefaultModelConverterService implements ModelConverterService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SchemaValidator schemaValidator;

    public DefaultModelConverterService(SchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    @Override
    public SecureMatchResult convert(String resultRequest) {

        validate(resultRequest);
        return convertResultRequest(resultRequest);
    }

    private void validate(String resultRequest) {
        schemaValidator.validatePayload(resultRequest, "matchResult.request.schema.json");
    }

    private SecureMatchResult convertResultRequest(String resultRequest) {
        try {
            return MAPPER.readValue(resultRequest, SecureMatchResult.class);
        } catch (Exception e) {
            throw new IllegalJsonException(e);
        }
    }
}
