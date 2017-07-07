package hu.bets.points.web.model;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;

public class ResultResponse {

    private Response.Status responseCode;
    private String responsePayload;
    private String error;

    public static ResultResponse success(Response.Status responseCode, String responsePayload) {
        return new ResultResponse(responseCode, responsePayload, "");
    }

    public static ResultResponse error(Response.Status responseCode, String error) {
        return new ResultResponse(responseCode, "", error);
    }

    private ResultResponse(Response.Status responseCode, String responsePayload, String error) {
        this.responseCode = responseCode;
        this.responsePayload = responsePayload;
        this.error = error;
    }

    public Response.Status getResponseCode() {
        return responseCode;
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
