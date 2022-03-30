package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import myUtils.TileUtils;
import myUtils.UnitUtils;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;

import java.util.Arrays;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game
 * Actor, which you can use to trigger game logic.
 * <p>
 * {
 * String messageType = “heartbeat”
 * }
 *
 * @author Dr. Richard McCreadie
 */
public class Heartbeat implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        Player humanPlayer = gameState.humanPlayer;
        Player aiPlayer = gameState.aiPlayer;

        //首先是判断获胜，谁的血量小于0了，对手就获胜了
        if (humanPlayer.getHealth() <= 0) {
            BasicCommands.addPlayer1Notification(out, "AIPlayer Win", 20);
            System.out.println("AIPlayer Win");
            humanPlayer.setHealth(0);
            BasicCommands.setPlayer1Health(out, humanPlayer);
            try {
                Thread.sleep(100000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (aiPlayer.getHealth() <= 0) {
            BasicCommands.addPlayer1Notification(out, "HumanPlayer Win", 20);
            System.out.println("HumanPlayer Win");
            aiPlayer.setHealth(0);
            BasicCommands.setPlayer2Health(out, aiPlayer);
            try {
                Thread.sleep(100000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //然后是矫正血量，因为玩家/AI的实体受到伤害后，要同步其player的血量
        // 然后如果场上有诸如银盾骑士这类的，在玩家受到伤害时会加攻击力
        //是不是银盾骑士
        //mana 3
        //attack 1
        //health 5
        //嘲讽：临近的敌方单位会被它嘲讽，无法移动，并且只能攻击他，如果有远程单位对它进行了攻击，也会被嘲讽住
        //当己方的玩家化身受到伤害时，银盾骑士增加2点攻击力



        for (Unit unit : humanPlayer.unitList) {
            System.out.println(unit.getName());
        }



        for (Unit unit : aiPlayer.unitList) {
            System.out.println(unit.getName());
        }


        correctedPlayerHealthVolume(out, gameState, gameState.curPlayer);
        correctedPlayerHealthVolume(out, gameState, gameState.curEnemyPlayer);

        for (int i = 0; i < gameState.canvasLength; i++) {
            for (int j = 0; j < gameState.canvasWidth; j++) {
                if (TileUtils.isTileOccupiedByUnit(gameState.tilesCollection[j][i], gameState)) {
                    System.out.print("- ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println("\n");
        }


    }


    public static void correctedPlayerHealthVolume(ActorRef out, GameState gameState, Player player) {

        //然后血量不一致，要修改血量

        if (player.getAvatar_unit().getCurHealth() < player.getHealth()) {
            player.setHealth(player.getAvatar_unit().getCurHealth());
            if (player == gameState.humanPlayer) {
                BasicCommands.setPlayer1Health(out, player);
            } else if (player == gameState.aiPlayer) {
                BasicCommands.setPlayer2Health(out, player);
            }

            //fixme
            if (player == gameState.humanPlayer) {
                System.out.println("human player unit list size = " + player.unitList.size());
                for (Unit unit : player.unitList) {
                    System.out.println(unit.getName());
                }
            } else if (player == gameState.aiPlayer) {
                System.out.println("ai player unit list size = " + player.unitList.size());
                for (Unit unit : player.unitList) {
                    System.out.println(unit.getName());
                }
            }

            //这里要对银盾骑士的攻击力进行增加操作
            for (Unit unit : player.unitList) {

                System.out.println("check Silverguard Knight" + unit.getName());
                if (unit.getName().equals("Silverguard Knight")) {

                    unit.setCurAttack(unit.getCurAttack() + 2);

                    // 前端显示
                    BasicCommands.setUnitAttack(out, unit, unit.getCurAttack());
                    gameState.interval(200);
                    BasicCommands.setUnitHealth(out, unit, unit.getCurHealth());
                    gameState.interval(200);
                }
            }
        } else if (player.getAvatar_unit().getCurHealth() > player.getHealth()) {
            player.setHealth(player.getAvatar_unit().getCurHealth());
            BasicCommands.setPlayer1Health(out, player);
        }

    }

}
