package com.battleship.statements;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class NoMissileLeftStatement implements GameStatement {
    private String playerId;

    public String toString() {
        return String.format(String.format("%s has no more missiles left to launch", playerId));
    }
}
