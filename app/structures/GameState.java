package structures;

import java.util.*;

import commands.BasicCommands;
import akka.actor.ActorRef;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	// Define the size of the Canvas.
	public int canvasWidth = 9;
	public int canvasLength = 5;
	public int avatarAttack = 2;
	
	// The initial state of the game
	public boolean gameInitalised = false;
	// The victory state of game.
	public boolean endGame = false;
	// Each turn increase by 1.
	public int turn;
	
	// Define the player objects
	public Player humanPlayer;
	public Player aiPlayer;

	//define current player and current enemy player
	public Player curPlayer;
	public Player curEnemyPlayer;

//	// a reference to the Unit if that one is currently being selected.
//	public Unit unitSelected;
//	// a reference to the Card if that one is currently being selected.
//	public Card cardSelected;
	
	// Reflect the occupation states of tiles (e.g.tilesCollection[xpos][ypos] = TileInstance, 
	// if you know the tileA position is (1, 2), you can easily know whether there is an unit on it 
	// with checking tilesCollection[1][2].unit != null)
	public Tile[][] tilesCollection = new Tile[canvasWidth][canvasLength];


	public Map<Tile, Unit> tileUnitMap = new HashMap<>();

	// We have to store the current white highlighted tiles, so that every time we come to the next step or the other click event
	// We should cancel the current white highlight tiles
	//白色高亮的tiles
	public List<Tile> currentWhiteHighLightTiles = new ArrayList<>();

	//只给后端用的白色高亮list，前端不显示，因为有些白色格子后端必须要用到才能判定
	public List<Tile> currentWhiteHighLightTilesNotShow = new ArrayList<>();

	//红色高亮的tiles
	public List<Tile> currentRedHighLightTiles = new ArrayList<>();
	
	// The Map<Tiles, Integer> is generally used to check the destination state of a tile
	public Map<Tile, Integer> tilesMode = new HashMap<>();
	
    public String[] cardsResource1 = {
			StaticConfFiles.c_sundrop_elixir,
			StaticConfFiles.c_truestrike,
			StaticConfFiles.c_azure_herald,
			StaticConfFiles.c_azurite_lion,
			StaticConfFiles.c_comodo_charger,
			StaticConfFiles.c_fire_spitter,
			StaticConfFiles.c_hailstone_golem,
			StaticConfFiles.c_ironcliff_guardian,
			StaticConfFiles.c_pureblade_enforcer,
			StaticConfFiles.c_silverguard_knight,
    };
    
    public String[] cardsResource2 = {
			StaticConfFiles.c_staff_of_ykir,
			StaticConfFiles.c_entropic_decay,
			StaticConfFiles.c_blaze_hound,
			StaticConfFiles.c_bloodshard_golem,
			StaticConfFiles.c_hailstone_golem,
			StaticConfFiles.c_planar_scout,
			StaticConfFiles.c_pyromancer,
			StaticConfFiles.c_serpenti,
			StaticConfFiles.c_rock_pulveriser,
			StaticConfFiles.c_windshrike,
	};
	
    public static Map<String, String> unitsResource1 = new HashMap<String, String>() {{
        put("Azure Herald", StaticConfFiles.u_azure_herald);
        put("Azurite Lion", StaticConfFiles.u_azurite_lion);
        put("Comodo Charger", StaticConfFiles.u_comodo_charger);
        put("Fire Spitter", StaticConfFiles.u_fire_spitter);
        put("Hailstone Golem", StaticConfFiles.u_hailstone_golem);
        put("Ironcliff Guardian", StaticConfFiles.u_ironcliff_guardian);
        put("Pureblade Enforcer", StaticConfFiles.u_pureblade_enforcer);
        put("Silverguard Knight", StaticConfFiles.u_silverguard_knight);
    }};
    
    public static Map<String, String> unitsResource2 = new HashMap<String, String>() {{
        put("Blaze Hound", StaticConfFiles.u_blaze_hound);
        put("Bloodshard Golem", StaticConfFiles.u_bloodshard_golem);
        put("Hailstone Golem", StaticConfFiles.u_hailstone_golemR);
        put("Planar Scout", StaticConfFiles.u_planar_scout);
        put("Pyromancer", StaticConfFiles.u_pyromancer);
        put("Serpenti", StaticConfFiles.u_serpenti);
        put("Rock Pulveriser", StaticConfFiles.u_rock_pulveriser);
        put("Windshrike", StaticConfFiles.u_windshrike);
    }}; 

    
	public void interval(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	


}
