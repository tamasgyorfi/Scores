package hu.bets.points.web.model;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;

public class ResultResponse {

    private String responsePayload;
    private String error;

    public static ResultResponse success(String responsePayload) {
        return new ResultResponse(responsePayload, "");
    }

    public static ResultResponse error(String error) {
        return new ResultResponse("", error);
    }

    private ResultResponse(String responsePayload, String error) {
        this.responsePayload = responsePayload;
        this.error = error;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public String getError() {
        return error;
    }

    public String asJson() {
        return new Gson().toJson(this);
    }
}
