package com.battleship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class PositionPair {
    private Position posX;
    private Position posY;

    public String toString() {
        return this.posX.getChar() + "" + this.posY.getInt();
    }

}
