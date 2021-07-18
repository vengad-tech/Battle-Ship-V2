package com.battleship;

import com.battleship.battlearea.BattleArea;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlayerConfig {
    private Player player;
    private BattleArea battleArea;
    private List<PositionPair> firingPositions;
}
