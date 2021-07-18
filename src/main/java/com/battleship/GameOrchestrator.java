package com.battleship;

import com.battleship.battle.Battle;
import com.battleship.battle.DefaultBattle;
import com.battleship.battlearea.BattlePlayerConfig;
import com.battleship.statement.FireMissileStatement;
import com.battleship.statement.GameResultStatement;
import com.battleship.statement.GameStatement;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Orchestrator helps simulating the game by playing as all the user's
 * i.e gets current user playing and input's the current user's firing position
 * and keeps repeating until game gets over (win or draw)
 */
@AllArgsConstructor
public class GameOrchestrator {
    List<PlayerConfig> playerConfigs;

    /**
     * Plays the game and returns the output as list of steps performed by each user and the final result.
     * Currently we return the result as list, in future we can try returning a subscribable object so end user need
     * not wait for execution and get instant updates as the game progress
     *
     * @return List<GameStatement>
     */
    public List<GameStatement> playGame() {

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

        Battle battle = new DefaultBattle(battlePlayerConfigs);


        while (!battle.isGameOver()) {
            Player currentPlayer = battle.getCurrentPlayer();
            PositionPair missileFiringPos = playerToFiringPositionsMap.get(currentPlayer).next();
            boolean isHit = battle.fireMissile(currentPlayer, missileFiringPos);
            gameResultStatements.add(FireMissileStatement.builder()
                    .isHit(isHit)
                    .target(missileFiringPos)
                    .playerId(currentPlayer.getPlayerId())
                    .build());

        }
        String winner = battle.getWinner().orElse(Player.builder().build()).getPlayerId();
        gameResultStatements.add(GameResultStatement.builder().isDraw(battle.isGameDraw()).winnerPlayerId(winner).build());
        return gameResultStatements;
    }
}
