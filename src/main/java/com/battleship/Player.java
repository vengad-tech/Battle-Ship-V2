package com.battleship;

import lombok.*;

import java.util.Objects;

@Getter
@ToString
public class Player {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId.equals(player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    private String playerId;
    private int totalMissiles;
    private int remainingMissiles;

    @Builder
    Player(String playerId, int totalMissiles) {
        this.playerId = playerId;
        this.totalMissiles = totalMissiles;
        this.remainingMissiles = this.totalMissiles;
    }

    public boolean useMissile() {
        if (this.hasRemainingMissiles()) {
            this.remainingMissiles -= 1;
            return true;
        }
        return false;
    }

    public boolean hasRemainingMissiles() {
        return this.remainingMissiles > 0;
    }
}
