package com.battleship.statement;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class GameResultStatement implements GameStatement {
    private boolean isDraw;
    private String winnerPlayerId;

    public String toString() {
        if (isDraw) {
            return "Game draw";
        }
        return String.format("%s won the battle", this.winnerPlayerId);
    }
}
