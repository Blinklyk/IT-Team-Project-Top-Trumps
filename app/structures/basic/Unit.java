package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import myUtils.UnitUtils;


/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	String name;
	int defaultAttack;
	int defaultHealth;
	int curAttack;
	int curHealth;
	int stamina = 2;
	int alreadyAttackTimes;
	boolean provoke;
	boolean range;
	boolean flying;
	boolean attackTwice;
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, String cardname, int attack, int health){
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
		this.name = cardname;
		this.defaultAttack = attack;
		this.defaultHealth = health;
		this.curAttack = defaultAttack;
		this.curHealth = defaultHealth;

	}

	// unit
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile, String cardname, int attack, int health) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		this.name = cardname;
		this.defaultAttack = attack;
		this.defaultHealth = health;
		this.curAttack = defaultAttack;
		this.curHealth = defaultHealth;
	}
	
	
	// avatar
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction, String cardname, int attack, int health) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		this.name = cardname;
		this.defaultAttack = attack;
		this.defaultHealth = health;
		this.curAttack = defaultAttack;
		this.curHealth = defaultHealth;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultAttack(int defaultAttack) {
		this.defaultAttack = defaultAttack;
	}

	public int getDefaultAttack() {
		return defaultAttack;
	}

	public int getDefaultHealth() {
		return defaultHealth;
	}

	public void setDefaultHealth(int defaultHealth) {
		this.defaultHealth = defaultHealth;
	}

	public int getCurAttack() {
		return curAttack;
	}

	public void setCurAttack(int curAttack) {
		this.curAttack = curAttack;
	}

	public int getCurHealth() {
		return curHealth;
	}

	public void setCurHealth(int curHealth) {
		this.curHealth = curHealth;
	}

	public boolean isProvoke() {
		return provoke;
	}

	public void setProvoke(boolean provoke) {
		this.provoke = provoke;
	}

	public boolean isRange() {
		return range;
	}

	public void setRange(boolean range) {
		this.range = range;
	}

	public boolean isFlying() {
		return flying;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

	public static void setMapper(ObjectMapper mapper) {
		Unit.mapper = mapper;
	}

	public int getStamina() {
		return stamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public int getAlreadyAttackTimes() {
		return alreadyAttackTimes;
	}

	public void setAlreadyAttackTimes(int alreadyAttackTimes) {
		this.alreadyAttackTimes = alreadyAttackTimes;
	}

	public boolean isAttackTwice() {
		return attackTwice;
	}

	public void setAttackTwice(boolean attackTwice) {
		this.attackTwice = attackTwice;
	}

	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		System.out.println(tile.getTilex() + tile.getTiley());
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}

	public void setNameDefaultAttackHealth(Card card) {
		// 设置 name/defaultHealth/defaultAttack
		UnitUtils.cardToUnit(card, this);
		this.curHealth = defaultHealth;
		this.curAttack = defaultAttack;

	}

}
