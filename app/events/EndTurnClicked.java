package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
import structures.basic.*;
import structures.*;
import utils.ImageListForPreLoad;
import utils.StaticConfFiles;
import play.libs.Json;
/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        
    	// Switch current player to AI.
		Player switchPlayer;
		switchPlayer = gameState.curPlayer;
		gameState.curPlayer = gameState.curEnemyPlayer;
		gameState.curEnemyPlayer = switchPlayer;
		
		
		/*
		 *	AI turn logic.
		 *	example: cardclicked.
		 * 
		 *	Events Parameter:
		 *  heartbeat	-	{ messageType = “heartbeat” }
		 *  initalize	-	{ messageType = “initalize” }
		 *  unitMoving	-	{ messageType = “unitMoving”, id = <unit id> }
		 *  unitstopped	-	{ messageType = “unitStopped”, id = <unit id> }
		 *  tileclicked	-	{ messageType = “tileClicked”, tilex = <x index of the tile>, tiley = <y index of the tile> }
		 *  cardclicked	-	{ messageType = “cardClicked”, position = <hand index position [1-6]> }
		 *  endturnclicked	-	{ messageType = “endTurnClicked” }
		 *  otherclicked	-	{ messageType = “otherClicked” }
		 */	
		
		/*
		 * Add AI play cards logic
		 */
		// Clean the selected items.
		Map<String, String> parameters = new HashMap<>();
		parameters.put("messagetype", "otherclicked");
		EndTurnUtils.processEvent(out, gameState, parameters);
		gameState.interval(200);
		parameters.clear();	
		

		Queue<String> priorityList = new LinkedList<String>();
		Queue<Card> executionCardList = new LinkedList<Card>(); 
		priorityList.add("Planar Scout");
		priorityList.add("Bloodshard Golem");
		priorityList.add("Staff of Y'Kir'");
		priorityList.add("Entropic Decay");
		priorityList.add("Hailstone Golem");
		priorityList.add("Blaze Hound");
		priorityList.add("Pyromancer");
		priorityList.add("Serpenti");
		priorityList.add("Rock Pulveriser");
		priorityList.add("Windshrike");
		
		while(!priorityList.isEmpty()) {
			String oneCardName = priorityList.poll();
			
			// If the card exists in handcardList, put it in executionCardList.
			for (Card card : gameState.curPlayer.handcardList) {
				if (oneCardName.equals(card.getCardname()))
					executionCardList.add(card);
			}
		}

		
//		executionCardList.forEach(cardi -> {BasicCommands.addPlayer1Notification(out, cardi.getCardname(), 1); gameState.interval(500);});
		try {
			while(gameState.curPlayer.getMana() > 0) {
				Card selectedCard = null;
				
				for (Card card : executionCardList) {
					// Add more selecting card logic here?
					if (card.getManacost() < gameState.curPlayer.getMana()) {
						selectedCard = card;
						executionCardList.remove(card);
						break;
					}
				}
				
				if (selectedCard == null)
					break;
				
				// Get all available tiles when Clicking the card.
				List<Tile> availableTiles = CardUtils.getAllAvailableTilesWhenCalling(gameState, selectedCard);
				
				
				// click the Card.
				parameters = new HashMap<>();
				parameters.put("messagetype", "cardclicked");
				parameters.put("position", gameState.curPlayer.handcardPositionMap.get(selectedCard).toString());
	
				EndTurnUtils.processEvent(out, gameState, parameters);
				gameState.interval(200);
				parameters.clear();
				
				
				// Put card on canvas directly.
				parameters = new HashMap<>();
				parameters.put("messagetype", "tileclicked");
				parameters.put("tilex", String.format("%d", availableTiles.get(0).getTilex()));
				parameters.put("tiley", String.format("%d", availableTiles.get(0).getTiley()));
				
				EndTurnUtils.processEvent(out, gameState, parameters);
				gameState.interval(1000);
				parameters.clear();	
			}
			
			/*
			 *		If attack (primaryTarget = humanAvatar) permitted, launch attack to primaryTarget, 
			 *			otherwise attack any nearby unit possible. attackChance--
			 *			move to the closest tile to the primaryTarget min(dx*dx + dy*dy).	
			 *
			 */

		} catch(Exception e) {
			System.out.println(e + "endTurn set PriorityList");
		}
		
		
		/*
		 *		If attack (primaryTarget = humanAvatar) permitted, launch attack to primaryTarget, 
		 *			otherwise attack any nearby unit possible. attackChance--
		 *			move to the closest tile to the primaryTarget min(dx*dx + dy*dy).	
		 *
		 */
		try {
			List<Tile> innerWhiteHighLightTiles = new ArrayList<Tile>();
			List<Tile> innerRedHighLightTiles = new ArrayList<Tile>();	
			for(Unit unit : gameState.curPlayer.unitList) {
				EndTurnUtils.actionSequence(out, gameState, unit, innerWhiteHighLightTiles, innerRedHighLightTiles);
			}
		
		} catch (Exception e)  {
			System.out.println(e + " endTuen actionsequence");
		}
			

		
		// NEXT TURN BEGAIN:
    	// Switch current player.
		switchPlayer = gameState.curPlayer;
		gameState.curPlayer = gameState.curEnemyPlayer;
		gameState.curEnemyPlayer = switchPlayer;
		
		// Turn accumulation.
		gameState.turn ++;
		// Update both players' mana each turn
		InitalUtils.updateManaEachTurn(out, gameState);

		// Deal handcards to both sides
		if (gameState.curPlayer.handcardList.size() <= 5) {
			InitalUtils.dealHandcardsToPlayer(out, gameState, gameState.curPlayer, 1);
		} else {
			gameState.curPlayer.deck.poll();
		}
		
		if (gameState.curEnemyPlayer.handcardList.size() <= 5) {
			InitalUtils.dealHandcardsToPlayer(out, gameState, gameState.curEnemyPlayer, 1);
		} else {
			gameState.curEnemyPlayer.deck.poll();
		}
		
		
		// Clean the selected items.
		parameters = new HashMap<>();
		parameters.put("messagetype", "otherclicked");
		EndTurnUtils.processEvent(out, gameState, parameters);
		gameState.interval(200);
		parameters.clear();

		// Clean the selected items.
		parameters = new HashMap<>();
		parameters.put("messagetype", "heartbeat");
		EndTurnUtils.processEvent(out, gameState, parameters);
		gameState.interval(200);
		parameters.clear();

		EndTurnUtils.refreshUnitStaminaAndAlreadyAttackTimes(gameState);
		
	}
	

}
