package com.battleship.battlearea;

import com.battleship.PositionPair;
import com.battleship.exceptions.ShipLocationAlreadyOccupied;
import com.battleship.exceptions.ShipLocationOutOfBounds;
import com.battleship.ships.Ship;
import com.battleship.ships.DefaultShipCell;
import com.battleship.ships.ShipCell;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultBattleArea implements BattleArea {

    private int height;
    private int width;

    private ShipCell[][] area;
    private Set<Ship> ships;

    private DefaultBattleArea(int height, int width, ShipCell[][] area, Set<Ship> ships) {
        this.height = height;
        this.width = width;
        this.area = area;
        this.ships = ships;
    }


    public boolean doHit(PositionPair positionToFire) {
        ShipCell shipCellAtPosition = this.area[positionToFire.getPosX().getInt() - 1][positionToFire.getPosY().getInt() - 1];
        if (shipCellAtPosition != null) {
            Ship shipAtPosition = shipCellAtPosition.getShip();

            boolean isHit = shipAtPosition.doHit(shipCellAtPosition);
            boolean isDestroyed = shipAtPosition.isDestroyed();
            if (isDestroyed && this.ships.contains(shipAtPosition)) {
                this.ships.remove(shipAtPosition);
            }
            return isHit;

        }

        return false;
    }

    public int getTotalShipsRemaining() {
        return this.ships.size();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ShipCell shipCell = this.area[i][j];
                if (shipCell != null) {
                    builder.append('x');
                } else {
                    builder.append('-');
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static class Builder {
        private int height;
        private int width;

        private ShipCell[][] area;
        private Set<Ship> ships;

        public Builder(PositionPair positionPair) {
            this.height = positionPair.getPosX().getInt();
            this.width = positionPair.getPosY().getInt();

            this.area = new DefaultShipCell[height][width];
            this.ships = new HashSet<Ship>();

        }

        public void addShip(PositionPair positionPair, Ship ship) throws ShipLocationAlreadyOccupied, ShipLocationOutOfBounds {
            int x = positionPair.getPosX().getInt() - 1;
            int y = positionPair.getPosY().getInt() - 1;
            if ((x + ship.getHeight() > this.height) || (y + ship.getWidth() > this.width)) {
                throw new ShipLocationOutOfBounds();
            }

            for (int i = x; i < x + ship.getHeight(); i++) {
                for (int j = y; j < y + ship.getWidth(); j++) {
                    if (this.area[i][j] != null) {
                        throw new ShipLocationAlreadyOccupied();
                    }
                }
            }
            Iterator<ShipCell> shipCells = ship.getShipCells().iterator();
            for (int i = x; i < x + ship.getHeight(); i++) {
                for (int j = y; j < y + ship.getWidth(); j++) {
                    System.out.println("adding ship at " + i + j);
                    this.area[i][j] = shipCells.next();
                }
            }
            this.ships.add(ship);
        }

        public BattleArea build() {
            return new DefaultBattleArea(this.height, this.width, this.area, this.ships);
        }


    }
}
