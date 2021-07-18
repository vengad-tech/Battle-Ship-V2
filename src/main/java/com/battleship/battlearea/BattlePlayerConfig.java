package com.battleship.battlearea;

import com.battleship.Player;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BattlePlayerConfig {
    private Player player;
    private BattleArea battleArea;
}
