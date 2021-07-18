package com.battleship.statement;

import com.battleship.PositionPair;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class FireMissileStatement implements GameStatement {
    private String playerId;
    private PositionPair target;
    private boolean isHit;

    private String hitOrMiss() {
        if (isHit) {
            return "hit";
        } else {
            return "miss";
        }
    }

    public String toString() {
        return String.format("%s fires a missile with target %s  which got %s", this.playerId, this.target.toString(), this.hitOrMiss());
    }
}
