package com.battleship.ships;

import com.battleship.position.PositionPair;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractShip implements Ship {

    private int height;
    private int width;

    private boolean isDestroyed;

    private Set<ShipCell> shipCells;

    AbstractShip(PositionPair positionPair, int maxHitsPerCell){
        this.height = positionPair.getPosX().getInt();
        this.width = positionPair.getPosY().getInt();
        this.shipCells = new HashSet<>();
        for(int i=0; i< this.height*this.width; i++){
            this.shipCells.add(new DefaultShipCell(this, maxHitsPerCell));
        }
    }

    public Set<ShipCell> getShipCells(){
        return this.shipCells;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean doHit(ShipCell shipCell) {
        if(this.shipCells.contains(shipCell)) {
            return shipCell.doHit();
        }
        return false;
    }

    /**
     * Returns if the ship has been destroyed.
     *
     * if at-least one cell of the ship is still remaining then ship is considered not destroyed.
     *
     * This method can be overridden to implement custom destruction logic.
     *
     * @return
     */
    public boolean isDestroyed() {
        if (isDestroyed) {
            return true;
        }
        isDestroyed = true;
        for(ShipCell shipCell: shipCells) {
            if (!shipCell.isEmpty()) {
                isDestroyed = false;
                break;
            }
        }
        return isDestroyed;
    }
}
