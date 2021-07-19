package com.battleship;

import com.battleship.battle.Battle;
import com.battleship.battle.BattleFactory;
import com.battleship.battle.MissileTargetStatus;
import com.battleship.battle.PlayerChosenStatus;
import com.battleship.battlearea.BattlePlayerConfig;
import com.battleship.player.Player;
import com.battleship.player.PlayerConfig;
import com.battleship.position.PositionPair;
import com.battleship.statements.GameCompletedStatement;
import com.battleship.statements.GameStatement;
import com.battleship.statements.MissileFiredStatement;
import com.battleship.statements.NoMissileLeftStatement;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.annotation.NotThreadSafe;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Consumer;

/**
 * Orchestrator helps simulating the game by playing as all the user's
 * i.e gets current user playing and input's the current user's firing position
 * and keeps repeating until game gets over (win or draw)
 */
@NotThreadSafe
public class GameOrchestrator {
    public static final String GAME_ALREADY_OVER = "Game already over and can be played once";
    List<PlayerConfig> playerConfigs;
    boolean isGameOver = false;

    GameOrchestrator(@NotNull List<PlayerConfig> playerConfigs) {
        this.playerConfigs = playerConfigs;

    }

    /**
     * Plays the game and returns the output as list of steps performed by each user and the final result.
     * Currently we return the result as list, in future we can try returning a subscribable object so end user need
     * not wait for execution and get instant updates as the game progress
     *
     * @return List<GameStatement>
     */
    public List<GameStatement> playGame() {

        if (this.isGameOver) {
            throw new IllegalStateException(GAME_ALREADY_OVER);
        }

        List<GameStatement> gameResultStatements = new ArrayList<>();

        List<BattlePlayerConfig> battlePlayerConfigs = new ArrayList<>();
        Map<Player, Iterator<PositionPair>> playerToFiringPositionsMap = new HashMap<>();

        for (PlayerConfig playerConfig : playerConfigs) {
            battlePlayerConfigs.add(BattlePlayerConfig.builder().
                    player(playerConfig.getPlayer())
                    .battleArea(playerConfig.getBattleArea())
                    .build());
            playerToFiringPositionsMap.put(playerConfig.getPlayer(), playerConfig.getFiringPositions().iterator());
        }

        /*
        We are sending this callback to the battle to let us notified when next player turn is chosen or skipped
        to log necessary progress.
         */
        Consumer<Pair<Player, PlayerChosenStatus>> playerChosenCallback = chosenStatusPair -> {
            if (chosenStatusPair.getRight().equals(PlayerChosenStatus.PLAYER_SKIPPED_DUE_TO_NO_MISSILE)) {
                gameResultStatements.add(NoMissileLeftStatement.builder()
                        .playerId(chosenStatusPair.getLeft().getPlayerId())
                        .build());
            }
        };

        /*
        We are sending this callback to the battle to let us notified when missile has hit or missed the target to log
        necessary progress.
         */
        Consumer<Pair<Player, MissileTargetStatus>> missileTargetStatusCallback = missileStatus -> {
            gameResultStatements.add(MissileFiredStatement.builder()
                    .isHit(missileStatus.getRight().getStatus().equals(MissileTargetStatus.Status.HIT))
                    .target(missileStatus.getRight().getPosition())
                    .playerId(missileStatus.getLeft().getPlayerId())
                    .build());
        };

        Battle battle = BattleFactory.newBattle(battlePlayerConfigs, playerChosenCallback, missileTargetStatusCallback);


        while (!battle.isGameOver()) {
            Player currentPlayer = battle.getCurrentPlayer();
            PositionPair missileFiringPos = playerToFiringPositionsMap.get(currentPlayer).next();
            battle.fireMissile(currentPlayer, missileFiringPos);
        }

        String winner = battle.getWinner().orElse(Player.builder().build()).getPlayerId();
        // ToDo: this can be logged via callback as well
        gameResultStatements.add(GameCompletedStatement.builder().isDraw(battle.isGameDraw()).winnerPlayerId(winner).build());
        this.isGameOver = true;
        return gameResultStatements;
    }
}
