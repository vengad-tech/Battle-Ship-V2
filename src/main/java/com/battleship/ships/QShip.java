package com.battleship.ships;

import com.battleship.PositionPair;
import com.battleship.ships.annotations.RegisterShip;

@RegisterShip(type ="Q")
public class QShip extends AbstractShip {
    private static final int MAX_HITS = 2;

    QShip(PositionPair positionPair) {
        super(positionPair, MAX_HITS);
    }


    protected int getMaxHits() {
        return MAX_HITS;
    }
}
