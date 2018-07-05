package hu.bets.points.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ToplistRequestPayload {
    private List<String> userIds;
    private String token;

    @JsonCreator
    public ToplistRequestPayload(@JsonProperty("userIds") List<String> userIds, @JsonProperty("token") String token){
        this.userIds = userIds;
        this.token = token;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public String getToken() {
        return token;
    }
}
