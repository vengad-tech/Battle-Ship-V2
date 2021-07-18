package com.battleship.ships;

import java.util.Set;

public interface Ship {

    int getHeight();
    int getWidth();

    Set<ShipCell> getShipCells();

    boolean doHit(ShipCell shipCell);
    boolean isDestroyed();
}
