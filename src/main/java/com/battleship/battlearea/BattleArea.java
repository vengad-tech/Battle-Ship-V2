package com.battleship.battlearea;

import com.battleship.position.PositionPair;

public interface BattleArea {

    boolean doHit(PositionPair positionToFire);

    int getTotalShipsRemaining();
}
