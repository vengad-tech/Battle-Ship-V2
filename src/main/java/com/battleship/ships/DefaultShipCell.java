package com.battleship.ships;


public class DefaultShipCell implements ShipCell {
    private int maxHits;
    private Ship ship;

    public DefaultShipCell(Ship ship, int maxHits){
        this.maxHits = maxHits;
        this.ship = ship;

    }

    public boolean doHit() {
        if (this.maxHits > 0) {
            this.maxHits -= 1;
            return true;
        }
        return false;
    }

    public Ship getShip(){
        return this.ship;
    }

    public boolean isEmpty() {
        return this.maxHits <= 0;
    }
}
