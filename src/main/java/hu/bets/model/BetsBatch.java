package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collections;
import java.util.List;

public class BetsBatch {

    private int numberOfElements;
    @JsonProperty("userBets")
    private List<Bet> bets;
    private String hash;

    private BetsBatch() {

    }

    public BetsBatch(int numberOfElements, List<Bet> bets, String hash) {
        this.numberOfElements = numberOfElements;
        this.bets = bets;
        this.hash = hash;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    @JsonSerialize
    @JsonProperty("userBets")
    public List<Bet> getBets() {
        return Collections.unmodifiableList(bets);
    }

    public String getHash() {
        return hash;
    }
}
