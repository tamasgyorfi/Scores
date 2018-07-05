package hu.bets.points.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ToplistEntry {
    private String userId;
    private long points;

    @JsonCreator
    public ToplistEntry(@JsonProperty("userId") String userId, @JsonProperty("points") long points) {
        this.userId = userId;
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToplistEntry that = (ToplistEntry) o;
        return points == that.points &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, points);
    }

    public String getUserId() {
        return userId;
    }

    public long getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "{" +
                "userId='" + userId + '\'' +
                ", points=" + points +
                '}';
    }
}
