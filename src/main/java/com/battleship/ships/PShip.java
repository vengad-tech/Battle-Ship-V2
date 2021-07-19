package com.battleship.ships;

import com.battleship.position.PositionPair;
import com.battleship.ships.annotations.RegisterShip;

@RegisterShip(type ="P")
public class PShip extends AbstractShip {
    private static final int MAX_HITS = 1;

    PShip(PositionPair positionPair) {
        super(positionPair, MAX_HITS);
    }

}
