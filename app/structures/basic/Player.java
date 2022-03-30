package structures.basic;

import java.util.*;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int health;
	int mana;

	Unit avatar_unit;

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}

	public Unit getAvatar_unit() {
		return avatar_unit;
	}

	public void setAvatar_unit(Unit avatar_unit) {
		this.avatar_unit = avatar_unit;
	}

	// Define the initial mana for players;
	public int defaultMana = 2;
	
	//  Define the initial health for players;
	//  max health is the same with this.
	public int defaultHealth = 20; 
	
	// Define the max mana for players;
	public int maxMana = 9;
	
	//	This is the units hold by this player
	//	Usage:
	//	Unit unit2 = gameState.humanPlayer.unitList.get(0);
	//	
	//	or
	//	
	//	ListIterator<Unit> listIterator = gameState.humanPlayer.unitList.listIterator();
	//	while (listIterator.hasNext()) {
	//		System.out.print(listIterator.next() + " ");
	//	}
	public List<Unit> unitList = new ArrayList<>();
	
	// the cards shows on the deck
	public List<Card> handcardList = new ArrayList<>();
	
	// This data is to show whether a card is being selected. 0 is not selected and the position of this card is selected.
	public Map<Card, Integer> handcardMode = new HashMap<>();

	//维护一个卡牌和卡牌position的映射关系
	public Map<Card, Integer> handcardPositionMap = new HashMap<>();

	// 记录unit被选中与否
	public Map<Unit, Integer> unitsMode = new HashMap<>();
	
	// This data is to get a Unit according to a related Card.
	public Map<Card, Unit> handcardUnit = new HashMap<>();
	
	// The cards queue for the player
	public Queue<Card> deck = new LinkedList<Card>();
	

}
