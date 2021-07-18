package com.battleship.battle;

import com.battleship.Player;
import com.battleship.PositionPair;

import java.util.Optional;

public interface Battle {

    Player getCurrentPlayer();

    boolean fireMissile(Player player, PositionPair positionToFire);

    boolean isGameOver();

    Optional<Player> getWinner();

    boolean isGameDraw();
}
