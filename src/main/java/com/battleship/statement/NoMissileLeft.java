package com.battleship.statement;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class NoMissileLeft implements GameStatement {
    private String playerId;

    public String toString() {
        return String.format(String.format("%s has no more missiles left to launch", playerId));
    }
}
