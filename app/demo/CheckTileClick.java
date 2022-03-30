package demo;

import akka.actor.ActorRef;
import myUtils.TileUtils;
import myUtils.UnitUtils;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.ArrayList;
import java.util.List;

public class CheckTileClick {
    public static void executeDemo(ActorRef out, GameState gameState) {
        //fixme 测试，模拟显示当前玩家unit的移动范围和攻击范围
        Unit unit = gameState.curPlayer.unitList.get(0);

        List<Tile> whiteHighLightTiles = new ArrayList<>();
        List<Tile> redHighLightTiles = new ArrayList<>();
        UnitUtils.checkWhiteAndRedTilesOfSelectedUnit(gameState, unit, 2, whiteHighLightTiles, redHighLightTiles);  //先去查找

        TileUtils.whiteHighLightAvailableTiles(whiteHighLightTiles, out, gameState);  //画出白色的

        TileUtils.redHighLightAvailableTiles(redHighLightTiles, out, gameState); //画出红色的
    }
}
