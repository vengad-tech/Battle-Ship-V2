package com.battleship.battlearea;

import com.battleship.PositionPair;

public interface BattleArea {

    boolean doHit(PositionPair positionToFire);

    int getTotalShipsRemaining();
}
