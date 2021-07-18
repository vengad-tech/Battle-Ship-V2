package com.battleship.battle;

import com.battleship.Player;
import com.battleship.PositionPair;
import com.battleship.battlearea.BattleArea;
import com.battleship.battlearea.BattlePlayerConfig;

import java.util.*;

public class DefaultBattle implements Battle {
    private Map<Player, BattlePlayerConfig> playerBattleConfigMap;
    private Player currentPlayer;
    private Player winner;
    private boolean isGameOver;

    public DefaultBattle(List<BattlePlayerConfig> battlePlayerConfigs) {

        if (battlePlayerConfigs == null || battlePlayerConfigs.size() == 0) {
            throw new IllegalArgumentException("battlePlayerConfigs cant be empty ");
        }

        this.playerBattleConfigMap = new HashMap<>();
        for (BattlePlayerConfig battlePlayerConfig : battlePlayerConfigs) {
            this.playerBattleConfigMap.put(battlePlayerConfig.getPlayer(), battlePlayerConfig);
        }

        this.chooseStartingPlayer(battlePlayerConfigs);

    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Current player provided fire the missile at given position.
     *
     * Currently opposition is chosen in round robin, but can be extended so that opponent can be passed as argument as
     * well.
     *
     * @param player
     * @param positionToFire
     * @return
     */
    public boolean fireMissile(Player player, PositionPair positionToFire) {

        if (player != this.getCurrentPlayer()) {
            throw new IllegalArgumentException("Missile can be fired only by " + player);
        }

        Player oppositePlayer = this.getOppositePlayer();
        BattlePlayerConfig oppositePlayerConfig = this.playerBattleConfigMap.get(oppositePlayer);
        BattleArea oppositePlayerArea = oppositePlayerConfig.getBattleArea();

        boolean isMissileRemaining = this.currentPlayer.useMissile();

        boolean isHit = false;
        if (isMissileRemaining) {
            isHit = oppositePlayerArea.doHit(positionToFire);
            if (!isHit) {
                this.currentPlayer = this.getNextPlayer();
            }
        }

        this.computeWinnerIfNeeded();

        return isHit;

    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Optional<Player> getWinner() {
        return Optional.ofNullable(this.winner);
    }

    public boolean isGameDraw() {
        if (this.isGameOver() && this.getWinner().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Chooses first player as starting player. This method can be overriden to implement
     * custom starting player choosing logic
     *
     * @param battlePlayerConfigs
     */
    private void chooseStartingPlayer(List<BattlePlayerConfig> battlePlayerConfigs) {
        this.currentPlayer = battlePlayerConfigs.get(0).getPlayer();
    }

    /**
     * Picks the next player to play.
     * Current uses round robin to pick next player who has missles.
     *
     * If custom player picking logic needs to be added then this method needs to be overriden.
     *
     * @return
     */
    private Player getNextPlayer() {
        for (Player player : this.playerBattleConfigMap.keySet()) {
            if (!player.equals(this.currentPlayer) && player.hasRemainingMissiles()) {
                return player;
            }
        }
        return this.currentPlayer;
    }

    /**
     * Computes if the game has reached the end state i.e whether we have a
     * winner or game is draw.
     *
     */
    private void computeWinnerIfNeeded() {
        Set<Player> playersWithNoShips = new HashSet<Player>();
        Set<Player> playersWithNoMissiles = new HashSet<Player>();

        for (Player player : this.playerBattleConfigMap.keySet()) {
            BattlePlayerConfig config = this.playerBattleConfigMap.get(player);
            BattleArea battleArea = config.getBattleArea();
            if (battleArea.getTotalShipsRemaining() <= 0) {
                playersWithNoShips.add(player);
            }
            if (player.getRemainingMissiles() <= 0) {
                playersWithNoMissiles.add(player);
            }

        }

        int totalPlayersCount = this.playerBattleConfigMap.size();

        if (playersWithNoShips.size() >= totalPlayersCount - 1) {
            this.isGameOver = true;
            this.winner = this.currentPlayer;
            return;
        }
        if (playersWithNoMissiles.size() == totalPlayersCount) {
            this.isGameOver = true;
            this.winner = null;
            return;
        }

    }

    private Player getOppositePlayer() {
        for (Player player : this.playerBattleConfigMap.keySet()) {
            if (!player.equals(this.currentPlayer)) {
                return player;
            }
        }
        return null;
    }
}
