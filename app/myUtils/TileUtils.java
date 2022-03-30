package myUtils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TileUtils {

    /**
     * 检查某个单元格是否出界
     */
    public static boolean isOutOfBorder(int tilex, int tiley,  GameState gameState){
        return  tilex < 0 || tilex >= gameState.canvasWidth || tiley < 0 || tiley >= gameState.canvasLength;
    }

    /**
     * 判断当前tile是否被unit占领
     * @param tile tile
     * @param gameState  gameState
     * @return
     */
    public static boolean isTileOccupiedByUnit(Tile tile, GameState gameState){
        return isTileOccupiedByCurPlayerUnit(tile, gameState) || isTileOccupiedByCurEnemyUnit(tile, gameState);
    }

    /**
     * 判断tile是否被当前玩家的unit占领
     * @param tile  current tile
     * @param gameState  gameState
     * @return
     */
    public static boolean isTileOccupiedByCurPlayerUnit(Tile tile, GameState gameState){
        for (Unit unit : gameState.curPlayer.unitList) {
            //如果找到了某一个unit的position和输入的tile的position一致，说明就是它了，加入res
            if (unit.getPosition().getTilex() == tile.getTilex()
                    && unit.getPosition().getTiley() == tile.getTiley()){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断tile是否被当前敌人的unit所占领
     * @param tile current tile
     * @param gameState
     * @return
     */
    public static boolean isTileOccupiedByCurEnemyUnit(Tile tile, GameState gameState){
        for (Unit unit : gameState.curEnemyPlayer.unitList) {
            //如果找到了某一个unit的position和输入的tile的position一致，说明就是它了，加入res
            if (unit.getPosition().getTilex() == tile.getTilex()
                    && unit.getPosition().getTiley() == tile.getTiley()){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过x,y坐标来拿到一个tile，如果没拿到就是null
     * @param tilex  x坐标
     * @param tiley  y坐标
     * @param gameState  游戏状态
     * @return
     */
    public static Tile getTileFromPosition(int tilex, int tiley, GameState gameState){
        //如果出界返回null
        if (isOutOfBorder(tilex, tiley, gameState)){
            return null;
        }
        //否则返回这个tile对象
        return gameState.tilesCollection[tilex][tiley];
    }

    /**
     * 根据一个unit对象，拿到它占据的单元格tile，如果这个unit还没有上场，那么就是null
     * @param unit  军团对象
     * @param gameState  游戏状态
     * @return
     */
    public static Tile getTileFromUnit(Unit unit, GameState gameState){
        int tilex = unit.getPosition().getTilex();
        int tiley = unit.getPosition().getTiley();
        //判断是否越界，如果越界了，返回null
        if (TileUtils.isOutOfBorder(tilex, tiley, gameState)){
            return null;
        }
        return TileUtils.getTileFromPosition(tilex, tiley, gameState);
    }


    public static List<Tile> getSorroundingEightTiles(Tile tile, GameState gameState){
        List<Tile> res = new ArrayList<>();
        String[] directions = new String[]{
                "left-up",
                "up",
                "right-up",
                "right",
                "right-down",
                "down",
                "left-down",
                "left"
        };

        //如果tile本身就出界了，
        if (TileUtils.isOutOfBorder(tile.getTilex(), tile.getTiley(), gameState)){
            return res;
        }

//        System.err.println("[" + tile.getTilex() +", " + tile.getTiley() + "]" + "是坐标点格子");
        for (String direction : directions) {
            int tilex = tile.getTilex();
            int tiley = tile.getTiley();
            int testtilex = tilex;
            int testtiley = tiley;

            if ("left-up".equals(direction)){
                tilex = tile.getTilex() - 1;
                tiley = tile.getTiley() + 1;
            }
            else if ("up".equals(direction)){
                tiley = tile.getTiley() + 1;
            }
            else if ("right-up".equals(direction)){
                tilex = tile.getTilex() + 1;
                tiley = tile.getTiley() + 1;
            }
            else if ("right".equals(direction)){
                tilex = tile.getTilex() + 1;
            }
            else if ("right-down".equals(direction)){
                tilex = tile.getTilex() + 1;
                tiley = tile.getTiley() - 1;
            }
            else if ("down".equals(direction)){
                tiley = tile.getTiley() - 1;
            }
            else if ("left-down".equals(direction)){
                tilex = tile.getTilex() - 1;
                tiley = tile.getTiley() - 1;
            }
            else if ("left".equals(direction)){
                tilex = tile.getTilex() - 1;
            }

            //如果出界了，continue
            if (TileUtils.isOutOfBorder(tilex, tiley, gameState)){
//                System.err.println("[" + tilex +", " + tiley + "]" + "出界了，不允许添加进surroundings");
                continue;
            }

            //没出界，添加进去

            //fixme
//            System.err.println("[" + tilex +", " + tiley + "]" + "是成功添加进surrounding的周围的格子，原始点是" + "[" + testtilex +", " + testtiley + "]");

            Tile newTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);
            res.add(newTile);
        }
        return res;
    }

    public static List<Tile> getSorroundingFourTiles(Tile tile, GameState gameState){
        List<Tile> res = new ArrayList<>();
        String[] directions = new String[]{
                "up",
                "right",
                "down",
                "left"
        };

        //如果tile本身就出界了，
        if (TileUtils.isOutOfBorder(tile.getTilex(), tile.getTiley(), gameState)){
            return res;
        }

//        System.err.println("[" + tile.getTilex() +", " + tile.getTiley() + "]" + "是坐标点格子");
        for (String direction : directions) {
            int tilex = tile.getTilex();
            int tiley = tile.getTiley();
            int testtilex = tilex;
            int testtiley = tiley;

            if ("up".equals(direction)){
                tiley = tile.getTiley() + 1;
            }
            else if ("right".equals(direction)){
                tilex = tile.getTilex() + 1;
            }
            else if ("down".equals(direction)){
                tiley = tile.getTiley() - 1;
            }
            else if ("left".equals(direction)){
                tilex = tile.getTilex() - 1;
            }

            //如果出界了，continue
            if (TileUtils.isOutOfBorder(tilex, tiley, gameState)){
//                System.err.println("[" + tilex +", " + tiley + "]" + "出界了，不允许添加进surroundings");
                continue;
            }

            //没出界，添加进去
            //fixme
//            System.err.println("[" + tilex +", " + tiley + "]" + "是成功添加进surroundingFour的周围的格子，原始点是" + "[" + testtilex +", " + testtiley + "]");
            Tile newTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);
            res.add(newTile);
        }
        return res;
    }



    /**
     * 把list中所有的tiles都白色高亮，并且添加进gameState.currentWhiteHighLightTiles 中
     * @param availableTiles  可以高亮的tiles list，可以选择从CardUtils的工具方法中获取
     * @param out   输出流
     * @param gameState     状态类
     */
    public static void whiteHighLightAvailableTiles(List<Tile> availableTiles, ActorRef out, GameState gameState){
        //要把当前的高亮的白色单元格取消，因为要高亮新一批单元格了，并且把并且要把gameState.currentWhiteHighLightTiles 清空
		TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);
		if (gameState.curPlayer == gameState.humanPlayer){
            if (availableTiles != null) {
                for (Tile availableTile : availableTiles) {
                    //highlight the available tiles
                    BasicCommands.drawTile(out, availableTile, 1);
                    gameState.interval(20);
                }
            }
        }

        //当前白色高亮的tiles要存进GameState类的List<Tile> currentWhiteHighLightTiles 中，以以后方便清除或者是检查状态
        if (availableTiles != null) {
            gameState.currentWhiteHighLightTiles.addAll(availableTiles);
        }
//        for (Tile availableTile : availableTiles) {
//            gameState.tilesMode.put(availableTile, 1);
//        }

    }


    /**
     * 把list中所有的tiles都红色高亮，并且添加进gameState.currentRedHighLightTiles 中
     * @param availableTiles  可以高亮的tiles list，可以选择从CardUtils的工具方法中获取
     * @param out  输出流
     * @param gameState  状态类
     */
    public static void redHighLightAvailableTiles(List<Tile> availableTiles, ActorRef out, GameState gameState){
        //首先要把当前的所有高亮的红色单元格取消，因为要高亮新一批单元格了,并且要把gameState.currentRedHighLightTiles 清空
        cancelTheCurrentRedHighLightTiles(out, gameState);
        if (gameState.curPlayer == gameState.humanPlayer){
            if (availableTiles != null) {
                for (Tile availableTile : availableTiles) {
                    //red highlight the available tiles
                    BasicCommands.drawTile(out, availableTile, 2);
                    gameState.interval(20);
                }
            }
        }

        //当前红色高亮的tiles要存进GameState类的List<Tile> currentRedHighLightTiles 中，以以后方便清除或者是检查状态
        if (availableTiles != null) {
            gameState.currentRedHighLightTiles.addAll(availableTiles);
        }
//        for (Tile availableTile : availableTiles) {
//            gameState.tilesMode.put(availableTile, 2);
//        }
    }

    /**
     * 把当前的所有高亮的白色单元格取消
     * @param out output stream
     * @param gameState game state parameter
     */
    public static void cancelTheCurrentWhiteHighLightTiles(ActorRef out, GameState gameState){
        //要把当前的所有高亮的白色单元格取消，因为要高亮新一批单元格了
        for (Tile currentWhiteHighLightTile : gameState.currentWhiteHighLightTiles) {
            BasicCommands.drawTile(out, currentWhiteHighLightTile, 0);
            gameState.interval(20);
        }
        //然后把list清空
        gameState.currentWhiteHighLightTiles.clear();
//        gameState.tilesMode.clear();
    }


    public static void refreshTheCurrentWhiteHighLightTilesNotShow(GameState gameState, List<Tile> newCurrentWhiteHighLightTilesNotShow){
        //把原来的清空
        gameState.currentWhiteHighLightTilesNotShow.clear();
        //然后再更新不显示的白色高亮格子集
        gameState.currentWhiteHighLightTilesNotShow.addAll(newCurrentWhiteHighLightTilesNotShow);
    }


    /**
     *  把当前所有高亮的红色单元格取消高亮
     * @param out  output stream
     * @param gameState  game state parameter
     */
    public static void cancelTheCurrentRedHighLightTiles(ActorRef out, GameState gameState){
        //要把当前的高亮的红色单元格取消，因为要高亮新一批单元格了
        for (Tile currentRedHighLightTile : gameState.currentRedHighLightTiles) {
            BasicCommands.drawTile(out, currentRedHighLightTile, 0);
            gameState.interval(20);
        }
        //然后把list清空
        gameState.currentRedHighLightTiles.clear();
//        gameState.tilesMode.clear();
    }

    /**
     * 得到两个tile之间的绝对距离，用平方和表示
     * @param tileA
     * @param tileB
     * @return
     */
    public static int getAbsoluteDistanceFromTiles(Tile tileA, Tile tileB){
        return (tileA.getTilex() - tileB.getTilex()) * (tileA.getTilex() - tileB.getTilex())
                + (tileA.getTiley() - tileB.getTiley()) * (tileA.getTiley() - tileB.getTiley());
    }


    public static void otherClick(ActorRef out, GameState gameState) {
        System.out.println("otherclick executes!");
        // 当成otherclick事件处理
        //要把上一回合选中的卡片取消高亮
        //first of all, we should cancel the former highlight. Clear the map to avoid the multiple selection
        CardUtils.cancelTheFormerHighLightCards(out, gameState);

        //要把当前的高亮的白色单元格取消,然后把list清空
        cancelTheCurrentWhiteHighLightTiles(out, gameState);

        //要把当前的高亮的红色单元格取消,然后把list清空
        TileUtils.cancelTheCurrentRedHighLightTiles(out, gameState);
        // 清除unitsMode选中状态
        for (Map.Entry<Unit, Integer> entry : gameState.curPlayer.unitsMode.entrySet()) {
            if (entry.getValue() == 1) {
                entry.setValue(0);
            }
        }
    }

    /**
     * 求两个单元格之间的相对距离，也就是水平竖直移动过去的距离
     * @param tileA
     * @param tileB
     * @return
     */
    public static int getRelativeDistanceFormTiles(Tile tileA, Tile tileB){
        return Math.abs(tileA.getTilex() - tileB.getTilex()) + Math.abs(tileA.getTiley() - tileB.getTiley());
    }

}
