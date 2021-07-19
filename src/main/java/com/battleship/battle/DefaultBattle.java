package com.battleship.battle;

import com.battleship.player.Player;
import com.battleship.position.PositionPair;
import com.battleship.battlearea.BattleArea;
import com.battleship.battlearea.BattlePlayerConfig;
import com.battleship.statements.NoMissileLeftStatement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

public class DefaultBattle implements Battle {
    private Map<Player, BattlePlayerConfig> playerBattleConfigMap;
    private Player currentPlayer;
    private Player winner;
    private boolean isGameOver;
    private Consumer<Pair<Player, PlayerChosenStatus>> playerChooserCallback;
    private Consumer<Pair<Player, MissileTargetStatus>> missileTargetStatusCallback;

    DefaultBattle(List<BattlePlayerConfig> battlePlayerConfigs) {
        this(battlePlayerConfigs, null, null);
    }


    DefaultBattle(List<BattlePlayerConfig> battlePlayerConfigs, Consumer playerChooserCallback,
                  Consumer missileTargetStatusCallback) {

        this.playerChooserCallback = playerChooserCallback;
        this.missileTargetStatusCallback = missileTargetStatusCallback;

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
     * <p>
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
                sendMissileTargetStatus(player, new MissileTargetStatus(MissileTargetStatus.Status.MISS, positionToFire));
                this.currentPlayer = this.getNextPlayer();

            } else {
                sendMissileTargetStatus(player, new MissileTargetStatus(MissileTargetStatus.Status.HIT, positionToFire));
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
     * <p>
     * If custom player picking logic needs to be added then this method needs to be overriden.
     *
     * @return
     */
    private Player getNextPlayer() {
        for (Player player : this.playerBattleConfigMap.keySet()) {
            if (!player.equals(this.currentPlayer)) {
                if (player.hasRemainingMissiles()) {
                    sendPlayerChosenCallback(player, PlayerChosenStatus.PLAYER_CHOSEN);
                    return player;
                } else {
                    sendPlayerChosenCallback(player, PlayerChosenStatus.PLAYER_SKIPPED_DUE_TO_NO_MISSILE);
                }

            }
        }
        return this.currentPlayer;
    }

    private void sendPlayerChosenCallback(Player player, PlayerChosenStatus status) {
        if (this.playerChooserCallback != null) {
            this.playerChooserCallback.accept(new ImmutablePair<>(player, status));
        }
    }

    private void sendMissileTargetStatus(Player player, MissileTargetStatus status) {
        if (this.playerChooserCallback != null) {
            this.missileTargetStatusCallback.accept(new ImmutablePair<>(player, status));
        }
    }

    /**
     * Computes if the game has reached the end state i.e whether we have a
     * winner or game is draw.
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
