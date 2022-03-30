package myUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import myUtils.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import commands.BasicCommands;
import events.CardClicked;
import events.EndTurnClicked;
import events.EventProcessor;
import events.Heartbeat;
import events.Initalize;
import events.OtherClicked;
import events.TileClicked;
import events.UnitMoving;
import events.UnitStopped;
import play.libs.Json;
import structures.GameState;
import utils.ImageListForPreLoad;
import play.libs.Json;
import structures.GameState;
import structures.basic.*;

public class EndTurnUtils {
	public static Map<String, EventProcessor> getAiEventProcessors() {
	// create class instances to respond to the various events that we might recieve
		Map<String, EventProcessor> aiEventProcessors = new HashMap<String,EventProcessor>();

		aiEventProcessors.put("initalize", new Initalize());
		aiEventProcessors.put("heartbeat", new Heartbeat());
		aiEventProcessors.put("unitmoving", new UnitMoving());
		aiEventProcessors.put("unitstopped", new UnitStopped());
		aiEventProcessors.put("tileclicked", new TileClicked());
		aiEventProcessors.put("cardclicked", new CardClicked());
		aiEventProcessors.put("endturnclicked", new EndTurnClicked());
		aiEventProcessors.put("otherclicked", new OtherClicked());
		
		return aiEventProcessors;
	}
	
	
	public static String iterateUsingEntrySet(Map<String, String> parameterMap) {
		String json = "{";
		
	    for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
	    	json += "\"" + entry.getKey() + "\"" + ":" + "\"" + entry.getValue() + "\",";
	    }
	    
	    json = json.substring(0, json.length() - 1);
	    
	    json += "}";
	    
	    return json;
	}
	
	
	/*
	 *	AI turn logic.
	 *	example: cardclicked.
	 * 
	 *	Events Parameter:
	 *  heartbeat	-	{ String messageType = “heartbeat” }
	 *  initalize	-	{ String messageType = “initalize” }
	 *  unitMoving	-	{ messageType = “unitMoving”, id = <unit id> }
	 *  unitstopped	-	{ messageType = “unitStopped”, id = <unit id> }
	 *  tileclicked	-	{ messageType = “tileClicked”, tilex = <x index of the tile> tiley = <y index of the tile> }
	 *  cardclicked	-	{ messageType = “cardClicked”, position = <hand index position [1-6]> }
	 *  endturnclicked	-	{ messageType = “endTurnClicked” }
	 *  otherclicked	-	{ messageType = “otherClicked” }
	 */	
	public static void processEvent(ActorRef out, GameState gameState, Map<String, String> parameterMap) {
		String json = iterateUsingEntrySet(parameterMap);

		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode AIMessage = mapper.readTree(json);

			Map<String, EventProcessor> aiEventProcessors = EndTurnUtils.getAiEventProcessors();
			aiEventProcessors.get(AIMessage.get("messagetype").asText()).processEvent(out, gameState, AIMessage);
		} catch(Exception e) {
			System.out.println(e + "  + endTurnUtils processEvent: " + json);
		}
	}

	public static void refreshUnitStaminaAndAlreadyAttackTimes(GameState gameState) {
		for (Unit unit : gameState.curPlayer.unitList) {
			unit.setStamina(2);
			unit.setAlreadyAttackTimes(0);
		}

		for (Unit unit : gameState.curEnemyPlayer.unitList) {
			unit.setStamina(2);
			unit.setAlreadyAttackTimes(0);
		}
	}
	
	
	public static boolean actionSequence(ActorRef out, GameState gameState, Unit curUnit, List<Tile> innerWhiteHighLightTiles, List<Tile> innerRedHighLightTiles) {
        //这里要判断这个unit的属性值，先看它是不是那种能打两下的
        //如果是盗贼这种能打两下的单位，单独判断一下
        if (curUnit.isAttackTwice()) {
            //如果这个盗贼还没有攻击过任何人，或者已经攻击过一次了
            if (curUnit.getAlreadyAttackTimes() == 0 || curUnit.getAlreadyAttackTimes() == 1) {

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
                return false;
            }
        }

        // unit range技能，如果全图可以攻击的unit,则每次返回的红亮tile必是所有敌方unit所在tile
        if (curUnit.isRange()) {
            // 先清空之前存的innerRedHighLightTiles
            innerRedHighLightTiles.clear();
            for (Unit enemyUnit : gameState.curEnemyPlayer.unitList) {
                Tile enemyTile = TileUtils.getTileFromUnit(enemyUnit, gameState);
                innerRedHighLightTiles.add(enemyTile);
            }
        }

        // unit flying技能，全图移动，如果全图移动的unit,则每次返回的白亮tile必是所有未被占的tile
        // 每次返回的红亮必是所有敌方unit所占tile
        if (curUnit.isFlying()) {
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

        // 首先要判断这个选中的Unit是否周围有敌方嘲讽uni，如果有，则无法移动，且只能攻击周围的敌方嘲讽Unit
        // 即没有白色，红色为周围敌方Unit所在tile
        if (UnitUtils.checkIsProvoked(curUnit, gameState)) {
            UnitUtils.unitIsProvokedSet(curUnit, gameState, innerWhiteHighLightTiles, innerRedHighLightTiles);
        }
        
        if (innerWhiteHighLightTiles.isEmpty() && innerRedHighLightTiles.isEmpty()) {
        	return false;
        } else {
        	// execute the tiles.
        	Unit avatarUnit = gameState.curEnemyPlayer.getAvatar_unit();
			Tile avatarTile = TileUtils.getTileFromUnit(avatarUnit, gameState);
        	Tile curTile = TileUtils.getTileFromUnit(curUnit, gameState);
        	Tile targetTile;
        	
        	if (!innerRedHighLightTiles.isEmpty()) {
            	targetTile = innerRedHighLightTiles.get(0);      		
        	} else {
            	targetTile = innerWhiteHighLightTiles.get(0); 
            	
            	int distance = TileUtils.getAbsoluteDistanceFromTiles(targetTile, avatarTile);
            	
    			for(Tile tile : innerWhiteHighLightTiles) {
    				if (distance > TileUtils.getAbsoluteDistanceFromTiles(tile, avatarTile)) {
    					distance = TileUtils.getAbsoluteDistanceFromTiles(tile, avatarTile);
    					targetTile = tile;
    				}
    			}
        	}

			for(Tile tile : innerRedHighLightTiles) {
				// If avatar could be attacked, attack avatar first.
				if (tile == avatarTile) {
					targetTile = tile;
				}
			}
			
			try {
				// click the unit.
				Map<String, String> parameters = new HashMap<>();
				parameters.put("messagetype", "tileclicked");
				parameters.put("tilex", String.format("%d", curTile.getTilex()));
				parameters.put("tiley", String.format("%d", curTile.getTiley()));
				System.out.println("FRIEND: curTilex, y : " + curTile.getTilex() + " , " + curTile.getTiley());
				EndTurnUtils.processEvent(out, gameState, parameters);
				gameState.interval(1500);
				parameters.clear();	
				
				// click the enemy.
				parameters = new HashMap<>();
				parameters.put("messagetype", "tileclicked");
				parameters.put("tilex", String.format("%d", targetTile.getTilex()));
				parameters.put("tiley", String.format("%d", targetTile.getTiley()));
				System.out.println("ENEMY : curTilex, y : " + targetTile.getTilex() + " , " + targetTile.getTiley());
				EndTurnUtils.processEvent(out, gameState, parameters);
				gameState.interval(1500);
				parameters.clear();	
				
//				gameState.aiPlayer.handcardList.forEach(c -> {System.out.println("list:" + c.getCardname());});
//				gameState.aiPlayer.deck.forEach(a -> { System.out.println( "deck:" + a.getCardname());});
				
				innerWhiteHighLightTiles = new ArrayList<Tile>(); 
				innerRedHighLightTiles = new ArrayList<Tile>(); 
	        	actionSequence(out, gameState, curUnit, innerWhiteHighLightTiles, innerRedHighLightTiles);
			} catch (Exception e) {
				System.out.println("end Turn click Unit or click the enemy : " + e);
			}
			
        	return true;
        }
	}
	
}
