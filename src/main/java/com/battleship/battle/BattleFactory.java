package com.battleship.battle;

import com.battleship.battlearea.BattlePlayerConfig;

import java.util.List;
import java.util.function.Consumer;

public class BattleFactory {

    public static Battle newBattle(List<BattlePlayerConfig> battlePlayerConfigs, Consumer playerChooserCallback,
                                   Consumer missileTargetStatusCallback) {
        return new DefaultBattle(battlePlayerConfigs, playerChooserCallback, missileTargetStatusCallback);
    }
}
