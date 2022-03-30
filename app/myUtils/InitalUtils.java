package myUtils;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.Map;

import akka.actor.ActorRef;
import utils.BasicObjectBuilders;

public class InitalUtils {
	// Initialize the board
	public static void initiateTheBoard(ActorRef out, GameState gameState) {
		for (int i = 0; i < gameState.canvasWidth; i++) {
			for (int j = 0; j < gameState.canvasLength; j++) {
				// Filling it with tiles
				gameState.tilesCollection[i][j] = BasicObjectBuilders.loadTile(i, j);
				
				BasicCommands.drawTile(out, gameState.tilesCollection[i][j], 0);
			}
		}	
	}
	
	// Load the Players' card deck
	public static void loadPlayerCardDeck(GameState gameState) {
		String[] cardsResource1 = null;
		Map<String, String> unitsResource1 = null;
		Player player = null;

		player = gameState.humanPlayer;
		cardsResource1 = gameState.cardsResource1;
		unitsResource1 = gameState.unitsResource1;					
		for (int t=0; t<2; t++) {
			for (int i=0; i<cardsResource1.length; i++) {
				Card card = BasicObjectBuilders.loadCard(cardsResource1[i], i, Card.class);
				gameState.interval(20);
				player.deck.add(card);
				
				if (unitsResource1.containsKey(card.getCardname())) {
					Unit unit = BasicObjectBuilders.loadUnit(unitsResource1.get(card.getCardname()), i, Unit.class);//0是avatar 所以从1开始
					unit.setNameDefaultAttackHealth(card);
					
					player.handcardUnit.put(card, unit);
					// 全部初始位置放在-1，-1
					UnitUtils.unitSetPositionToFakeTile(unit);
				}
			}			
		}
		

		String[] cardsResource2 = null;
		Map<String, String> unitsResource2 = null;
		Player player2 = null;
		player2 = gameState.aiPlayer;
		cardsResource2 = gameState.cardsResource2;
		unitsResource2 = gameState.unitsResource2;
		for (int t=0; t<2; t++) {
			for (int i=0; i<cardsResource2.length; i++) {
				Card card = BasicObjectBuilders.loadCard(cardsResource2[i], i, Card.class);
				gameState.interval(20);
				player2.deck.add(card);
				
				if (unitsResource2.containsKey(card.getCardname())) {
					Unit unit = BasicObjectBuilders.loadUnit(unitsResource2.get(card.getCardname()), i, Unit.class);//0是avatar 所以从1开始
					unit.setNameDefaultAttackHealth(card);

					player2.handcardUnit.put(card, unit);
					
					Tile fakeTile = BasicObjectBuilders.loadTile(-1, -1);
					unit.setPositionByTile(fakeTile);			
				}
			}			
		}	
	}
	
	public static void dealHandcardsToPlayer(ActorRef out, GameState gameState, int num) {
		for (int i=0; i<num; i++) {
			Card card;

			System.out.println("try to draw a card");
			card = gameState.curPlayer.deck.poll();
			gameState.curPlayer.handcardList.add(card);
			
			int max = 0;
		    for (Card key : gameState.curPlayer.handcardPositionMap.keySet()) {
		        if (gameState.curPlayer.handcardPositionMap.get(key) > max) {
		        	max = gameState.curPlayer.handcardPositionMap.get(key);
		        }
		    }

		    gameState.curPlayer.handcardPositionMap.put(card, max + 1);
			
			if (gameState.curPlayer == gameState.humanPlayer)
				BasicCommands.drawCard(out, card, max+1, 0);



			card = gameState.curEnemyPlayer.deck.poll();
			gameState.curEnemyPlayer.handcardList.add(card);

			max = 0;
		    for (Card key : gameState.curEnemyPlayer.handcardPositionMap.keySet()) {
		        if (gameState.curEnemyPlayer.handcardPositionMap.get(key) > max) {
		        	max = gameState.curEnemyPlayer.handcardPositionMap.get(key);
		        }
		    }

		    gameState.curEnemyPlayer.handcardPositionMap.put(card, max + 1);

			if (gameState.curEnemyPlayer == gameState.humanPlayer)
				BasicCommands.drawCard(out, card, max+1, 0);
		}
	}
	
	public static void dealHandcardsToPlayer(ActorRef out, GameState gameState, Player player, int num) {
		for (int i=0; i<num; i++) {
			Card card = player.deck.poll();
			player.handcardList.add(card);
			int max = 0;
			for (Card key : player.handcardPositionMap.keySet()) {
				if (player.handcardPositionMap.get(key) > max) {
					max = player.handcardPositionMap.get(key);
				}
			}

			player.handcardPositionMap.put(card, max + 1);
			if (player == gameState.humanPlayer) {
				BasicCommands.drawCard(out, card, max+1, 0);
			}
		}
	}
	
	// Update both players' mana each turn
	public static void updateManaEachTurn(ActorRef out, GameState gameState) {
		int mana = gameState.turn + 1;
		if(mana > 9) {
			mana = 9;
		}

		gameState.curPlayer.setMana(mana);
		BasicCommands.setPlayer1Mana(out, gameState.curPlayer);
		
		gameState.curEnemyPlayer.setMana(mana);
		BasicCommands.setPlayer2Mana(out, gameState.curEnemyPlayer);
	}
	
	// Update both players' mana each turn
	public static void setDefaultHealth(ActorRef out, GameState gameState) {
		gameState.curPlayer.setHealth(gameState.curPlayer.defaultHealth);
		BasicCommands.setPlayer1Health(out, gameState.curPlayer);

		gameState.curEnemyPlayer.setHealth(gameState.curEnemyPlayer.defaultHealth);
		BasicCommands.setPlayer2Health(out, gameState.curEnemyPlayer);
	}

	public static void setPlayerMana(ActorRef out, GameState gameState) {
		if (gameState.curPlayer == gameState.humanPlayer) {
			BasicCommands.setPlayer1Mana(out, gameState.curPlayer);
		} else if (gameState.curPlayer == gameState.aiPlayer) {
			BasicCommands.setPlayer2Mana(out, gameState.curPlayer);
		}
	}
}
