package com.battleship.battle;

import com.battleship.position.PositionPair;

public class MissileTargetStatus {
    public enum Status {
        HIT,
        MISS
    }

    Status status;
    private PositionPair position;

    public MissileTargetStatus(Status status, PositionPair position) {
        this.status = status;
        this.position = position;
    }

    public Status getStatus() {
        return this.status;
    }

    public PositionPair getPosition() {
        return this.position;
    }

}
