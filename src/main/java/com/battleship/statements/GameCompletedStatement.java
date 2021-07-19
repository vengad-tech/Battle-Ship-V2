package com.battleship.statements;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class GameCompletedStatement implements GameStatement {
    private boolean isDraw;
    private String winnerPlayerId;

    public String toString() {
        if (isDraw) {
            return "Game draw";
        }
        return String.format("%s won the battle", this.winnerPlayerId);
    }
}
