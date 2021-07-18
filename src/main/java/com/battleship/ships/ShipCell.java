package com.battleship.ships;

public interface ShipCell {
    boolean doHit();

    Ship getShip();

    boolean isEmpty();
}
