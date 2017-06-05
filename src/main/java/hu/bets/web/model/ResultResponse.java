package hu.bets.web.model;

public class ResultResponse {

    private int responseCode;
    private String responsePayload;
    private String error;

    public static ResultResponse success(int responseCode, String responsePayload) {
        return new ResultResponse(responseCode, responsePayload, "");
    }

    public static ResultResponse error(int responseCode, String error) {
        return new ResultResponse(responseCode, "", error);
    }

    private ResultResponse(int responseCode, String responsePayload, String error) {
        this.responseCode = responseCode;
        this.responsePayload = responsePayload;
        this.error = error;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public String getError() {
        return error;
    }
}
