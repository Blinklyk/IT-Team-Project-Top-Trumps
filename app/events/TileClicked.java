package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import myUtils.CardUtils;
import myUtils.InitalUtils;
import myUtils.TileUtils;
import myUtils.UnitUtils;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import javax.print.attribute.standard.Destination;
import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static myUtils.TileUtils.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * <p>
 * {
 * messageType = “tileClicked”
 * tilex = <x index of the tile>
 * tiley = <y index of the tile>
 * }
 *
 * @author Dr. Richard McCreadie
 */

public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();

        // 此部分具体逻辑见思维导图
		/*
		判断之前是否选中手牌/Unit/都没选中
		 */
        Boolean chosenACard = false;
        Boolean chosenAUnit = false;
        // 先遍历handcardMode看手牌状态是否不为0判断之前是否有手牌选中
        Card chosenCard = new Card();
        for (Map.Entry<Card, Integer> entry : gameState.curPlayer.handcardMode.entrySet()) {
            // 找到选中的是哪张卡
            if (entry.getValue() != 0) {
                chosenCard = entry.getKey();
                chosenACard = true;
            }
        }
        System.out.println("Chonsen a Card ? " + chosenACard);
        // 遍历之前是否选中Unit
        // 如果选中了，则本次
        Unit chosenUnit = new Unit();
        for (Map.Entry<Unit, Integer> entry : gameState.curPlayer.unitsMode.entrySet()) {
            if (entry.getValue() != 0) {
                chosenUnit = entry.getKey();
                chosenAUnit = true;
            }
        }
        System.out.println("Chosen a Unit ? " + chosenAUnit);

        if (chosenAUnit && chosenACard) {
            // 之前又选了Unit又选了Card，肯定是哪里的状态没清除干净，这里我们视为一次otherclick
            TileUtils.otherClick(out, gameState);
        }

		/*
		之前都没选中
		 */
		// 如果之前都没有选中
		if (!chosenACard && !chosenAUnit) {
            // 判断是否是一次otherclick，如果x,y没有对应友方unit的position则是otherclick
            Unit curUnit = new Unit();
            boolean otherclick = true;
            for (Unit u : gameState.curPlayer.unitList) {
                if (u.getPosition().getTilex() == tilex && u.getPosition().getTiley() == tiley) {
                    curUnit = u;
                    otherclick = false;
                }
            }
            System.out.println("Check is a otherclick ? " + otherclick);

            if (otherclick) {
                TileUtils.otherClick(out, gameState);
            } else {
                // 如果选中了友方unit，则curUnit为选中的Unit
                // 记录unit选中状态
                gameState.curPlayer.unitsMode.put(curUnit, 1);

                System.out.println("start lighting");

                // 调用unitUtil中的方法求白色高亮红色高亮范围
                // 先清除一遍原来的高亮
                TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);
                TileUtils.cancelTheCurrentRedHighLightTiles(out, gameState);
                //两个需要用到的内部list
                List<Tile> innerWhiteHighLightTiles = new ArrayList<>();
                List<Tile> innerRedHighLightTiles = new ArrayList<>();

                //这里要判断这个unit的属性值，先看它是不是那种能打两下的
                //如果是盗贼这种能打两下的单位，单独判断一下
                if (curUnit.isAttackTwice()) {
                    //如果这个盗贼还没有攻击过任何人，或者已经攻击过一次了
                    if (curUnit.getAlreadyAttackTimes() == 0 || curUnit.getAlreadyAttackTimes() == 1) {
                        //fixme 输出测试
                        if (curUnit.getAlreadyAttackTimes() == 0) {
                            System.out.println("这个盗贼还没有攻击过任何人");
                        } else if (curUnit.getAlreadyAttackTimes() == 1) {
                            System.out.println("这个盗贼已经攻击过一次了");
                        }

                        UnitUtils.getAvailableRedAndWhiteTilesWhenClickingAnUnit(gameState, curUnit,
                                innerWhiteHighLightTiles, innerRedHighLightTiles);

                    }

                    //else 盗贼攻击了两次了，没有攻击次数了
                    else if (curUnit.getStamina() == 2) {
                        //啥都不能干,当成otherclick
                        TileUtils.otherClick(out, gameState);
                    }
                }
                //如果是普通的只能攻击一次的unit
                else {
                    //如果这个普通的unit还没有攻击过任何人
                    if (curUnit.getAlreadyAttackTimes() == 0) {
                        UnitUtils.getAvailableRedAndWhiteTilesWhenClickingAnUnit(gameState, curUnit, innerWhiteHighLightTiles, innerRedHighLightTiles);
                    }
                    //如果这个普通的unit已经攻击过别人了
                    else if (curUnit.getAlreadyAttackTimes() == 1) {
                        //啥都不能干,当成otherclick
                        TileUtils.otherClick(out, gameState);
                        return;
                    }
                }

                // unit range技能，如果全图可以攻击的unit,而且还没有攻击过别人，则每次返回的红亮tile必是所有敌方unit所在tile
                if (curUnit.isRange() && curUnit.getAlreadyAttackTimes() == 0) {
                    // 先清空之前存的innerRedHighLightTiles
                    innerRedHighLightTiles.clear();
                    for (Unit enemyUnit : gameState.curEnemyPlayer.unitList) {
                        Tile enemyTile = TileUtils.getTileFromUnit(enemyUnit, gameState);
                        innerRedHighLightTiles.add(enemyTile);
                    }
                }

                //如果这个unit还有攻击次数，才会继续往下考虑
                // unit flying技能，全图移动，如果全图移动的unit,则每次返回的白亮tile必是所有未被占的tile
                // 每次返回的红亮必是所有敌方unit所占tile
                if (curUnit.isFlying() && curUnit.getAlreadyAttackTimes() == 0) {
                    // 先清空之前存的innerWhiteHighLightTiles和innerRedHighLightTiles
                    innerWhiteHighLightTiles.clear();
                    innerRedHighLightTiles.clear();
                    for (int i = 0; i < gameState.canvasWidth; i++) {
                        for (int j = 0; j < gameState.canvasLength; j++) {
                            Tile curTile = gameState.tilesCollection[i][j];
                            if (!TileUtils.isTileOccupiedByUnit(curTile, gameState)) {
                                innerWhiteHighLightTiles.add(curTile);
                            } else if (TileUtils.isTileOccupiedByCurEnemyUnit(curTile, gameState)) {
                                innerRedHighLightTiles.add(curTile);
                            }
                        }
                    }
                }

                // 在判断是否被嘲讽前还要判断这个unit还有没有攻击次数
                // 首先要判断这个选中的Unit是否周围有敌方嘲讽uni，如果有，则无法移动，且只能攻击周围的敌方嘲讽Unit
                // 即没有白色，红色为周围敌方Unit所在tile
                if (UnitUtils.checkIsProvoked(curUnit, gameState)) {
                    //如果该unit是盗贼，那么attack times 不能为2，如果是一般的unit，不能为1
                    if ((curUnit.isAttackTwice() && curUnit.getAlreadyAttackTimes() < 2) || (!curUnit.isAttackTwice() && curUnit.getAlreadyAttackTimes() == 0))
                    UnitUtils.unitIsProvokedSet(curUnit, gameState, innerWhiteHighLightTiles, innerRedHighLightTiles);
                }

                // 高亮白色红色tiles,方法里调整了对应的tileMode
                TileUtils.whiteHighLightAvailableTiles(innerWhiteHighLightTiles, out, gameState);
                TileUtils.redHighLightAvailableTiles(innerRedHighLightTiles, out, gameState);

            }
        }

		/*


		之前选中了一张手牌


		 */
        // 之前选择了手牌
        if (chosenACard && !chosenAUnit) {
            // 先判断是否在作用领域,不在则视为一次otherclick，在则使用法术牌/召唤
            boolean otherclick = true;
            Tile clickTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);
            if (gameState.currentWhiteHighLightTiles.contains(clickTile)) {
                otherclick = false;
            }
            System.out.println("Check is a otherclick when have chosen a card ? " + otherclick);
            if (otherclick) {
                // 执行otherclick
                TileUtils.otherClick(out, gameState);
            } else {
                // 已经判断确认目前点击tile为有效作用tile，确定开始使用卡牌
                // 如果是法术牌，则使用，已知法术牌一定是对单个Unit使用
                if (CardUtils.isSpellCard(chosenCard)) {
                    System.out.println("using a spellCard");
                    // 先获取本次点击的tile上的Unit
                    Tile effectTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);
                    Unit effectUnit = UnitUtils.getUnitFromTile(effectTile, gameState);
                    // 对effectUnit使用chosenCard法术
                    CardUtils.useSpellCard(out, gameState, chosenCard, effectUnit);
                    // 此处对Unit的curHealth进行了改动，因此要考虑是否死亡
                    System.out.println("effectUnit unit curHealth : " + effectUnit.getCurHealth());
                    if (effectUnit.getCurHealth() <= 0){
                        //如果unit在spell卡的影响下死亡了，才进入死亡结算
                        UnitUtils.deathCheckAndDelete(out, effectUnit, gameState);
                    }
                    else{
                        // 如果unit没死，则展示新的effectUnit信息
                        BasicCommands.setUnitAttack(out, effectUnit, effectUnit.getCurAttack());
                        BasicCommands.setUnitHealth(out, effectUnit, effectUnit.getCurHealth());
                    }
                    // 检测到魔法卡发动，触发有特殊效果的unit技能
                    UnitUtils.unitEffectWhenUseSpellCard(out, gameState);

                    // 相应手牌删除
                    CardUtils.deleteCardFromCurPlayer(out, gameState, chosenCard);
                    // 减去相应的mana值
                    gameState.curPlayer.setMana(gameState.curPlayer.getMana() - chosenCard.getManacost());
                    // 前端显示减去后的mana值
                    System.out.println("current mana : " + gameState.curPlayer.getMana());
                    // 找到相应的player，设置其前端mana显示值
                    InitalUtils.setPlayerMana(out, gameState);



                } else {
                    // 是unit牌，则召唤
                    // back end 召唤

                    // Unit在Initialize中已经初始化好了，包括它的name，attack和health，所以直接从handcardunit中拿来用就行
                    Unit newUnit = gameState.curPlayer.handcardUnit.get(chosenCard);
                    System.out.println("tile x, y : " + tilex + ", " + tiley);
                    System.out.println("newUnit.getCurAttack() : " + newUnit.getCurAttack());
                    newUnit.setPositionByTile(gameState.tilesCollection[tilex][tiley]);
                    // 添加进所属队列
                    gameState.curPlayer.unitList.add(newUnit);


                    // front end 召唤
                    BasicCommands.drawUnit(out, newUnit, gameState.tilesCollection[tilex][tiley]);
                    gameState.interval(250);
                    BasicCommands.setUnitAttack(out, newUnit, newUnit.getDefaultAttack());
                    gameState.interval(250);
                    BasicCommands.setUnitHealth(out, newUnit, newUnit.getDefaultHealth());
                    gameState.interval(250);

                    // 召唤成功后，触发特殊效果
                    UnitUtils.summonEffect(out, newUnit, gameState);

                    // 召唤成功后，相应手牌删除
                    CardUtils.deleteCardFromCurPlayer(out, gameState, chosenCard);
                    // 手牌Mode清除，tileMode清除，即可视为出发了一次otherclick
                    TileUtils.otherClick(out, gameState);

                    // 减去相应的mana值
                    gameState.curPlayer.setMana(gameState.curPlayer.getMana() - chosenCard.getManacost());
                    // 前端显示减去后的mana值
                    InitalUtils.setPlayerMana(out, gameState);
                }
            }
        }

		/*
		之前选中了unit
		 */
        if (chosenAUnit && !chosenACard) {
            // 判断是否是一次otherclick，如果x,y没有对应友方unit的position则是otherclick
            int tileMode = 0;
            //这个clickTile是当前选中的Tile
            Tile targetTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);
            Unit defenderUnit = UnitUtils.getUnitFromTile(targetTile, gameState);

            //oldTile是上一次选中的Tile，也是这一次unit所在的tile
            Tile attackerTile = TileUtils.getTileFromUnit(chosenUnit, gameState);

            //chosenUnit 是上一次选中的unit，也可以理解成就是选中的攻击者

            boolean otherclick = true;
            if (gameState.currentWhiteHighLightTiles.contains(targetTile)) {
                // 本次点击的是白色高亮tile
                tileMode = 1;
                otherclick = false;
            }
            if (gameState.currentRedHighLightTiles.contains(targetTile)) {
                //本次点击的是红色高亮tile
                tileMode = 2;
                otherclick = false;
            }
            System.out.println("Check is a otherclick ? " + otherclick);
            if (otherclick) {
                TileUtils.otherClick(out, gameState);
            } else {
                if (tileMode == 1) {
                    //当前点击的是白色的点
                    //要移动过去
                    //把chosenUnit移动到targetTile
                    //注意，是在前端先移动，再在后端更改值
                    BasicCommands.addPlayer1Notification(out, "move", 2);
                    BasicCommands.moveUnitToTile(out, chosenUnit, targetTile);

                    //注意这里要减少相应的体力值，一定是先减少体力值，再去修改unit的position
                    UnitUtils.reduceUnitCorrespondingStaminaAfterMoving(gameState, chosenUnit, targetTile);

                    //移动成功了，再在后台修改数据
                    chosenUnit.setPositionByTile(targetTile);

                    //调用一下otherclick
                    TileUtils.otherClick(out, gameState);


                } else if (tileMode == 2) {
                    // 如果是全图攻击的unit,则它直接发动攻击，不需要走过去
                    BasicCommands.addPlayer1Notification(out, "move and attack", 2);
                    if (!chosenUnit.isRange()) {
                        //当前点击的是红色的点
                        //先拿到所有能自动走过去的最优的位置
                        List<Tile> availabeDestinations = UnitUtils.getMovementDestinationWhenDirectlyLaunchAttack(gameState, chosenUnit, defenderUnit);
                        //当前点击的是白色的点
                        //要移动过去
                        //把chosenUnit移动到destinationTile
                        //注意，是在前端先移动，再在后端更改值
                        if (availabeDestinations.size() == 0) {
                            return;
                        }
                        //拿到最优的移动点
                        Tile destinationTile = availabeDestinations.get(0);

                        //注意这里要减少相应的体力值，一定是先减少体力值，再去修改unit的position
                        UnitUtils.reduceUnitCorrespondingStaminaAfterMoving(gameState, chosenUnit, destinationTile);

                        BasicCommands.moveUnitToTile(out, chosenUnit, destinationTile);
                        gameState.interval(1500);
                        //移动成功了，再在后台修改数据
                        chosenUnit.setPositionByTile(destinationTile);
                    }
                    TileUtils.otherClick(out, gameState);
					//开始发起进攻，拿到结果值
					int resOfAttack = UnitUtils.launchAttack(out, gameState, chosenUnit, defenderUnit);
                    TileUtils.otherClick(out, gameState);
				}
			}
		}
	}



}


