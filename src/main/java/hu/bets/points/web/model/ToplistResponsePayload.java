package hu.bets.points.web.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.bets.points.model.ToplistEntry;

import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ToplistResponsePayload {

    private List<ToplistEntry> entries;
    private String token;

    @JsonCreator
    public ToplistResponsePayload(@JsonProperty("entries") List<ToplistEntry> entries, @JsonProperty("token") String token) {
        this.entries = entries;
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToplistResponsePayload that = (ToplistResponsePayload) o;
        return Objects.equals(entries, that.entries) &&
                Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, token);
    }

    @Override
    public String toString() {
        return "ToplistResponsePayload{" +
                "entries=" + entries +
                ", token='" + token + '\'' +
                '}';
    }
}
