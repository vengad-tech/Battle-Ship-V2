package com.battleship;

import com.battleship.battlearea.BattleArea;
import com.battleship.statement.GameStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.battleship.Driver.*;

public class TestCases {

    @Test
    public void testSuccessStatePlayer2Wins() throws Exception {
        String input = "5 E\n" +
                "2\n" +
                "Q 1 1 A1 P 2 1 D4\n" +
                "Q 1 1 B2 P 2 1 C3\n" +
                "A1 B2 B2 B3\n" +
                "A1 B2 B3 A1 D1 E1 D4 D4 D5 D5";
        String output = "Player-1 fires a missile with target A1  which got miss\n" +
                "Player-2 fires a missile with target A1  which got hit\n" +
                "Player-2 fires a missile with target B2  which got miss\n" +
                "Player-1 fires a missile with target B2  which got hit\n" +
                "Player-1 fires a missile with target B2  which got miss\n" +
                "Player-2 fires a missile with target B3  which got miss\n" +
                "Player-1 fires a missile with target B3  which got miss\n" +
                "Player-2 fires a missile with target A1  which got miss\n" +
                "Player-2 fires a missile with target D1  which got miss\n" +
                "Player-2 fires a missile with target E1  which got miss\n" +
                "Player-2 fires a missile with target D4  which got hit\n" +
                "Player-2 fires a missile with target D4  which got miss\n" +
                "Player-2 fires a missile with target D5  which got hit\n" +
                "Player-2 won the battle\n";
        this.validate(input, output);
    }

    @Test
    public void testDraw() throws Exception {
        String input = "5 E\n" +
                "2\n" +
                "Q 1 1 A1 P 2 1 D4\n" +
                "Q 1 1 B2 P 2 1 C3\n" +
                "A1 B2\n" +
                "A1 B2";
        String output = "Player-1 fires a missile with target A1  which got miss\n" +
                "Player-2 fires a missile with target A1  which got hit\n" +
                "Player-2 fires a missile with target B2  which got miss\n" +
                "Player-1 fires a missile with target B2  which got hit\n" +
                "Game draw\n";
        this.validate(input, output);
    }
    public void validate(String input, String output) throws Exception {

        BufferedReader reader = new BufferedReader(new StringReader(input));
        PositionPair battleAreaHeightAndWidth = getWidthAndHeight(reader);
        System.out.println("height and width is " + battleAreaHeightAndWidth);

        int totalShips = getTotalShips(battleAreaHeightAndWidth, reader);

        BattleArea player1BattleArea = getBattleArea(battleAreaHeightAndWidth, totalShips, reader);
        BattleArea player2BattleArea = getBattleArea(battleAreaHeightAndWidth, totalShips, reader);

        System.out.println("player1BattleArea " + player1BattleArea);
        System.out.println("player2BattleArea " + player2BattleArea);

        List<PositionPair> firingPositionsForPlayer1 = getFiringPositions(reader);
        List<PositionPair> firingPositionsForPlayer2 = getFiringPositions(reader);

        System.out.println("firingPositionsForPlayer1 " + firingPositionsForPlayer1);
        System.out.println("firingPositionsForPlayer2 " + firingPositionsForPlayer2);

        Player player1 = Player.builder().playerId("Player-1")
                .totalMissiles(firingPositionsForPlayer1.size()).build();
        Player player2 = Player.builder().playerId("Player-2")
                .totalMissiles(firingPositionsForPlayer2.size()).build();

        List<PlayerConfig> playerConfigs = new ArrayList<>();
        playerConfigs.add(PlayerConfig.builder()
                .player(player1)
                .battleArea(player1BattleArea)
                .firingPositions(firingPositionsForPlayer1)
                .build());

        playerConfigs.add(PlayerConfig.builder()
                .player(player2)
                .battleArea(player2BattleArea)
                .firingPositions(firingPositionsForPlayer2)
                .build());

        GameOrchestrator orchestrator = new GameOrchestrator(playerConfigs);
        List<GameStatement> statements = orchestrator.playGame();
        StringBuilder result  = new StringBuilder();
        for(GameStatement gameStatement: statements){
            result.append(gameStatement+"\n");
        }
        System.out.println(output);
        Assertions.assertEquals(output,result.toString());

    }
}
