package com.battleship.position;

import lombok.ToString;

@ToString
public class Position {
    int position;

    private Position(int position) {
        this.position = position;
    }

    public int getInt() {
        return this.position;
    }

    public char getChar() {
        return (char) ((((int)'A')+this.position)-1);
    }

    public static Position fromString(String str) {
        if (str == null || str.length() > 1) {
            throw new IllegalArgumentException();
        }
        char pos = str.charAt(0);

        return fromChar(pos);

    }

    public static Position fromChar(char pos) {

        if (pos >= '0' && pos <= '9') {
            return new Position(Integer.parseInt(pos+""));
        }

        if ((pos < 'A' || pos > 'Z')) {
            throw new IllegalArgumentException();
        }
        return new Position((pos - 'A') + 1);

    }
}
