package com.battleship;

import com.battleship.battlearea.BattleArea;
import com.battleship.battlearea.DefaultBattleArea;
import com.battleship.ships.Ship;
import com.battleship.ships.ShipFactory;
import com.battleship.statement.GameStatement;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Get's input from stdin (size of battle area, player ship positions and missiles fired etc) and runs
 * GameOrchestrator and prints the result of game execution.
 */
public class Driver {

    public static final String INVALID_WIDTH_HEIGHT_ERROR = "Invalid input, need space separated height and width (ex: 5 E)";
    public static final String INVALID_SHIP_DETAILS_PROVIDED = "Invalid ship details provided";
    public static final String INVALID_SHIP_POSITION = "Invalid ship position";
    public static final String INVALID_FIRING_POSITION = "Invalid firing position %s";
    public static final String INVALID_SHIP_COUNT_PROVIDED = "Invalid ship count must be >=0 and <=%s";
    public static final String PLAYER_1_ID = "Player-1";
    public static final String PLAYER_2_ID = "Player-2";

    public static PositionPair getWidthAndHeight(BufferedReader reader) throws IOException {

        String line = reader.readLine();
        String[] lineParts = line.trim().split(" ");
        if (lineParts.length != 2) {
            System.err.println(INVALID_WIDTH_HEIGHT_ERROR);
            throw new IllegalArgumentException();
        }
        String width = lineParts[0];
        String height = lineParts[1];
        PositionPair positionPair = new PositionPair(Position.fromString(height), Position.fromString(width));
        return positionPair;
    }

    public static PositionPair getWidthAndHeight(String line) throws IOException {

        String[] lineParts = line.trim().split(" ");
        if (lineParts.length != 2) {
            System.err.println(INVALID_WIDTH_HEIGHT_ERROR);
            throw new IllegalArgumentException();
        }
        String width = lineParts[0];
        String height = lineParts[1];
        PositionPair positionPair = new PositionPair(Position.fromString(height), Position.fromString(width));
        return positionPair;
    }

    public static int getTotalShips(PositionPair heightAndWidth, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int totalShips = Integer.parseInt(line);
        int maxShips = heightAndWidth.getPosX().getInt() * heightAndWidth.getPosY().getInt();
        if (totalShips < 0 || totalShips > maxShips) {
            System.err.println(String.format(INVALID_SHIP_COUNT_PROVIDED, maxShips));
            throw new IllegalArgumentException();
        }
        return totalShips;
    }

    public static PositionPair extractShipPosition(String shipPosition) {
        if (StringUtils.isEmpty(shipPosition) || shipPosition.length() != 2) {
            throw new IllegalArgumentException(INVALID_SHIP_POSITION);
        }
        return new PositionPair(Position.fromChar(shipPosition.charAt(0)),
                Position.fromChar(shipPosition.charAt(1)));
    }

    public static BattleArea getBattleArea(PositionPair battleAreaHeightAndWidth, int totalShips, BufferedReader reader) throws Exception {
        String line = reader.readLine();
        String[] lineParts = line.trim().split(" ");

        DefaultBattleArea.Builder battleAreaBuilder = new DefaultBattleArea.Builder(battleAreaHeightAndWidth);
        if (lineParts.length != 4 * totalShips) {
            System.err.println(INVALID_SHIP_DETAILS_PROVIDED);
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < lineParts.length; i = i + 4) {
            String shipType = lineParts[i];
            String shipWidth = lineParts[i + 1];
            String shipHeight = lineParts[i + 2];
            PositionPair shipDimension = new PositionPair(Position.fromString(shipHeight),
                    Position.fromString(shipWidth));
            PositionPair shipPosition = extractShipPosition(lineParts[i + 3]);
            Ship ship = ShipFactory.createShip(shipType, shipDimension);
            battleAreaBuilder.addShip(shipPosition, ship);

        }
        return battleAreaBuilder.build();

    }

    public static List<PositionPair> getFiringPositions(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] lineParts = line.trim().split(" ");
        List<PositionPair> firingPositions = new ArrayList<>();
        for (String linePart : lineParts) {
            if (linePart.length() != 2) {
                System.err.println(String.format(INVALID_FIRING_POSITION, linePart));
                throw new IllegalArgumentException();
            }
            firingPositions.add(new PositionPair(Position.fromChar(linePart.charAt(0)),
                    Position.fromChar(linePart.charAt(1))));
        }
        return firingPositions;
    }

    public static void main(String args[]) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PositionPair battleAreaHeightAndWidth = getWidthAndHeight(reader);

        int totalShips = getTotalShips(battleAreaHeightAndWidth, reader);

        BattleArea player1BattleArea = getBattleArea(battleAreaHeightAndWidth, totalShips, reader);
        BattleArea player2BattleArea = getBattleArea(battleAreaHeightAndWidth, totalShips, reader);

        List<PositionPair> firingPositionsForPlayer1 = getFiringPositions(reader);
        List<PositionPair> firingPositionsForPlayer2 = getFiringPositions(reader);

        /*
        Playing as 2 players, how-ever the system is scalable to play for any number of users.
         */
        Player player1 = Player.builder().playerId(PLAYER_1_ID)
                .totalMissiles(firingPositionsForPlayer1.size()).build();
        Player player2 = Player.builder().playerId(PLAYER_2_ID)
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
        for (GameStatement gameStatement : statements) {
            System.out.println(gameStatement);
        }


    }
}
