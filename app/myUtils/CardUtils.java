package myUtils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardUtils {
    //一个人最多六张手牌，编号position为1到6
    //Spell card
    private static String[] spellCards = {
            "Sundrop Elixir",
            "Truestrike",
            "Entropic Decay",
            "Staff of Y'Kir'"
    };

    //get a card from its hand card position
    //从前端返回给你的手牌位置拿到那张牌的引用
    public static Card getCardFromHandPosition(int handPosition, GameState gameState) {
        //There should be a handPosition - 1
        return gameState.curPlayer.handcardList.get(handPosition - 1);
    }

    /**
     * 从牌库中增加一张牌给当前玩家(可能是AI)
     *
     * @param out
     * @param gameState
     * @param position  是你要增加的这张牌，在前端手牌中的位置 1 - 6
     */
    public static void addCardToCurPlayer(ActorRef out, GameState gameState, int position) {
        //如果牌库空了就什么都不做
        if (gameState.curPlayer.deck.size() == 0) {
            //如果当前角色是人类玩家，就会提醒人类玩家，牌库空了，如果是AI，因为AI没有提示指令，所以什么都不做
            if (gameState.curPlayer == gameState.humanPlayer) {
                BasicCommands.addPlayer1Notification(out, "Your card Deck is empty", 1);
            }
            return;
        }
        //从牌库中拿牌
        Card card = gameState.curPlayer.deck.poll();
        //增加到玩家的手牌中
        gameState.curPlayer.handcardList.add(card);
        //维护这张牌与手牌显示position的关系
        gameState.curPlayer.handcardPositionMap.put(card, position);
        //是人类玩家才要画出这张卡
        if (gameState.curPlayer == gameState.humanPlayer) {
            BasicCommands.drawCard(out, card, position, 0);
            gameState.interval(20);
        }
    }


    /**
     * 删掉玩家(AI)的一张手牌，应该是已经出掉了，右边的所有牌左移
     *
     * @param out
     * @param gameState
     * @param card      删除的卡牌
     * @return
     */
    public static boolean deleteCardFromCurPlayer(ActorRef out, GameState gameState, Card card) {
        // deleteCard
        //拿到这张卡的位置
        int position = CardUtils.getPositionOfCard(gameState.curPlayer, card);

        //fixme debug
        System.out.println("要删除的这张卡片 position是：" + position);

        //如果是-1，说明没有找到这张卡，删除失败
        if (position == -1) {
            return false;
        }
        //现在要找到被删除卡片右边的所有卡片
        List<Card> rightSideCards = new ArrayList<>();
        for (int i = position; i < gameState.curPlayer.handcardList.size(); i++) {
            rightSideCards.add(gameState.curPlayer.handcardList.get(i));
        }

        //fixme debug
        System.out.println("拿到了要被删除的卡片的右边卡片list，list 大小为：" + rightSideCards.size());

        //是人类就要在 在前端中删除这张卡片
        if (gameState.curPlayer == gameState.humanPlayer) {
            //fixme debug
            System.out.println("持有这张卡的是人类，我们要在前端删除这张卡");
            BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
            BasicCommands.deleteCard(out, position);
        }

        //在list和map中删除当前卡片
        System.out.println("在后端中删除这张卡");
        gameState.curPlayer.handcardList.remove(card);
        gameState.curPlayer.handcardPositionMap.remove(card);
        //不要忘记了在handcardMode中也要处理它。避免在后面的取消高亮函数中，又把这张卡给画出来了
        gameState.curPlayer.handcardMode.remove(card);

        //所有右边卡片都要往左移动一格
        if (rightSideCards.size() != 0) {
            for (Card rightSideCard : rightSideCards) {
                //是人类就要在 先在前端删掉这张卡片
                if (gameState.curPlayer == gameState.humanPlayer) {
                    BasicCommands.deleteCard(out, gameState.curPlayer.handcardPositionMap.get(rightSideCard));
                    gameState.interval(20);
                }
                //然后更新map
                gameState.curPlayer.handcardPositionMap.put(rightSideCard, gameState.curPlayer.handcardPositionMap.get(rightSideCard) - 1);
                //是人类，就要在前端左一格位置画出这张卡片
                if (gameState.curPlayer == gameState.humanPlayer) {
                    BasicCommands.drawCard(out, rightSideCard, gameState.curPlayer.handcardPositionMap.get(rightSideCard), 0);
                    gameState.interval(20);

                }
            }
        }
        return true;
    }

    /**
     * 根据玩家对象和card，拿到这张card在手牌中的位置（1-6），如果不在手牌中，返回-1
     *
     * @param player
     * @param card
     * @return
     */
    public static int getPositionOfCard(Player player, Card card) {
        //如果当前手牌映射里面没有这张卡，直接返回-1
        if (!player.handcardPositionMap.containsKey(card)) {
            return -1;
        }
        return player.handcardPositionMap.get(card);
    }

    /**
     * 高亮选中的卡牌，并调整当前玩家的handcardMode，也就是选中的卡片的map(自带了取消之前高亮的卡片)
     *
     * @param out          输出流
     * @param card         卡片
     * @param handPosition 卡片在手牌中的位置，可以从前端直接拿到 int handPosition = message.get("position").asInt();
     * @param gameState    状态类
     */
    public static void drawAndHighLightTheCard(ActorRef out, Card card, int handPosition, GameState gameState) {
        //首先应该要把之前选中的卡片取消高亮，因为要高亮新的卡片了,调用工具方法
        cancelTheFormerHighLightCards(out, gameState);
        //Draw and highlight the card
        if (gameState.curPlayer == gameState.humanPlayer) {
	        BasicCommands.drawCard(out, card, handPosition, 1);
	        gameState.interval(20);
        }
        //we select this card and set it mapping to value handPosition in hand card Mode Map
        //todo 这里记得放的是handPosition 而不是1，不然不好清除上回合的高亮，因为找不到手牌的position了
        gameState.curPlayer.handcardMode.put(card, handPosition);
    }

    /**
     * 取消之前高亮的卡片，并且清空humanPlayer.handcardMode
     *
     * @param out       输出流
     * @param gameState 状态类
     */
    public static void cancelTheFormerHighLightCards(ActorRef out, GameState gameState) {
        //对于tile而言，0是没颜色，1是白色强调，2是红色强调
        //对于card而言，BasicCommands.drawCard(out, hailstone_golem, 1, 0);最后一位是0代表没高亮，最后一位是1代表高亮

        //首先，要把之前选中的卡片取消高亮，因为要高亮新的卡片了
        //first of all, we should cancel the former highlight cards. Clear the map to avoid the multiple selection
        for (Map.Entry<Card, Integer> entry : gameState.curPlayer.handcardMode.entrySet()) {
            Card card = entry.getKey();
            int mapHandPosition = entry.getValue();
            if ((mapHandPosition != 0) && (gameState.curPlayer == gameState.humanPlayer)) {
                BasicCommands.drawCard(out, card, mapHandPosition, 0);
                gameState.interval(20);
            }
        }
        gameState.curPlayer.handcardMode.clear();
    }


    //是不是军团卡
    public static boolean isUnitCard(Card card) {
        return !isSpellCard(card);
    }

    //是不是咒术卡
    public static boolean isSpellCard(Card card) {
        for (String spellCard : spellCards) {
            if (spellCard.equals(card.getCardname())) {
                return true;
            }
        }
        return false;
    }

    //判断是不是真实伤害卡 给敌方造成两点伤害  Truestrike
    public static boolean isTruestrike(Card card) {
        return "Truestrike".equals(card.getCardname());
    }

    //判断是不是日落药水卡 给友方恢复5点血 Sundrop Elixir
    public static boolean isSundrop_Elixir(Card card) {
        return "Sundrop Elixir".equals(card.getCardname());
    }

    //判断是不是加攻击的卡片，给玩家的化身加2点攻击力
    public static boolean isStaff_of_Y_Kir(Card card) {
        return "Staff of Y'Kir'".equals(card.getCardname());
    }

    //判断是不是熵减，秒杀一个敌方单位，不能是敌人的化身
    public static boolean isEntropic_Decay(Card card) {
        return "Entropic Decay".equals(card.getCardname());
    }

    public static void useSpellCard(ActorRef out, GameState gameState, Card card, Unit unit) {
        String[] effects = {
                //add attack
                StaticConfFiles.f1_buff,
                //true strike
                StaticConfFiles.f1_inmolation,
                //death directly spell
                StaticConfFiles.f1_martyrdom,
                //add health
                StaticConfFiles.f1_summon
        };


        if (isTruestrike(card)) {
            EffectAnimation ef = BasicObjectBuilders.loadEffect(effects[1]);
            useTruestrike(unit);
            BasicCommands.playEffectAnimation(out, ef, TileUtils.getTileFromUnit(unit, gameState));
        } else if (isSundrop_Elixir(card)) {
            EffectAnimation ef = BasicObjectBuilders.loadEffect(effects[2]);
            useSundrop_Elixir(unit);
            BasicCommands.playEffectAnimation(out, ef, TileUtils.getTileFromUnit(unit, gameState));
        } else if (isStaff_of_Y_Kir(card)) {
            EffectAnimation ef = BasicObjectBuilders.loadEffect(effects[0]);
            useStaff_of_Y_Kir(unit);
            BasicCommands.playEffectAnimation(out, ef, TileUtils.getTileFromUnit(unit, gameState));
        } else if (isEntropic_Decay(card)) {
            EffectAnimation ef = BasicObjectBuilders.loadEffect(effects[3]);
            useEntropic_Decay(unit);
            BasicCommands.playEffectAnimation(out, ef, TileUtils.getTileFromUnit(unit, gameState));
        }
    }

    //使用真实伤害卡 给敌方造成两点伤害  Truestrike
    public static void useTruestrike(Unit unit) {
        unit.setCurHealth(unit.getCurHealth() - 2);
    }

    //使用日落药水卡 给友方恢复5点血 Sundrop Elixir
    public static void useSundrop_Elixir(Unit unit) {
        // 不能超过默认生命值
        unit.setCurHealth(unit.getCurHealth() <= unit.getDefaultHealth() - 5 ? unit.getCurHealth() + 5 : unit.getDefaultHealth());
    }

    //使用加攻击的卡片，给玩家的化身加2点攻击力
    public static void useStaff_of_Y_Kir(Unit unit) {
        unit.setCurAttack(unit.getCurAttack() + 2);
    }

    //使用熵减，秒杀一个敌方单位，不能是敌人的化身
    public static void useEntropic_Decay(Unit unit) {
        unit.setCurHealth(0);
    }


    //根据一组units直接拿到他们的tiles
    public static List<Tile> getTilesFormUnits(GameState gameState, List<Unit> unitList) {
        List<Tile> res = new ArrayList<>();
        for (Unit unit : unitList) {
            int tilex = unit.getPosition().getTilex();
            int tiley = unit.getPosition().getTiley();
            // once it's not out of border
            if (!TileUtils.isOutOfBorder(tilex, tiley, gameState)) {
                res.add(gameState.tilesCollection[tilex][tiley]);
            }
        }
        return res;
    }

    //get all occupied tiles which is occupied by current player's units
    public static List<Tile> getAllOccupiedTilesFromCurPlayer(GameState gameState) {
        //拿到当前玩家所有的unit所占有的tile
        return getTilesFormUnits(gameState, gameState.curPlayer.unitList);
    }

	// Get all available tiles when Clicking the card.
    public static List<Tile> getAllAvailableTilesWhenCalling(GameState gameState, Card card) {
    	List<Tile> availableTiles = new ArrayList<>();
    	//是不是军团卡
    	if (CardUtils.isUnitCard(card)){
    		availableTiles = CardUtils.getAllAvailableTilesWhenCallingNewUnit(gameState, card);
    	}
    	//是不是真实伤害
    	else if (CardUtils.isTruestrike(card)){
    		availableTiles = CardUtils.getAvailableTilesWhenClicking_truestrike(gameState);
    	}
    	//是不是日落药水
    	else if (CardUtils.isSundrop_Elixir(card)){
    		availableTiles = CardUtils.getAvailableTilesWhenClicking_sundrop_elixir(gameState);
    	}
    	//是不是加攻击的
    	else if (CardUtils.isStaff_of_Y_Kir(card)){
    		availableTiles = CardUtils.getAvailableTilesWhenClicking_staff_of_ykir(gameState);
    	}
    	//是不是熵减
    	else if (CardUtils.isEntropic_Decay(card)){
    		availableTiles = CardUtils.getAvailableTilesWhenClicking_entropic_decay(gameState);
    	}
		return availableTiles; 	
    }
    
    //拿到当前玩家选中军团卡片的时候应该高亮的单元格范围，也就是能够召唤的范围
    //get all available tiles which should be highlight when calling new unit
    public static List<Tile> getAllAvailableTilesWhenCallingNewUnit(GameState gameState, Card card) {
        List<Tile> res = new ArrayList<>();
        // 如果是可以随处召唤的unit，则返回List<Tile>返回所有未被占据的Tile
        if (UnitUtils.unitEffectSummonAnywhere(card)) {
            for (int x = 0; x < gameState.canvasWidth; x++) {
                for (int y = 0; y < gameState.canvasLength; y++) {
                    if (!TileUtils.isOutOfBorder(x, y, gameState) && !TileUtils.isTileOccupiedByUnit(gameState.tilesCollection[x][y], gameState)) {
                        res.add(gameState.tilesCollection[x][y]);
                    }
                }
            }
            return res;
        }
        // 否则正常遍历目前友方unit周围一圈
        List<Tile> occupiedTiles = CardUtils.getAllOccupiedTilesFromCurPlayer(gameState);
        for (Tile occupiedTile : occupiedTiles) {
            int tilex = occupiedTile.getTilex();
            int tiley = occupiedTile.getTiley();
            //把当前单元格扫描一圈，不能出界，也不能由有被别的unit占据
            //Scan the current tile in a circle
            for (int x = tilex - 1; x <= tilex + 1; x++) {
                for (int y = tiley - 1; y <= tiley + 1; y++) {
                    if (!TileUtils.isOutOfBorder(x, y, gameState) && !TileUtils.isTileOccupiedByUnit(gameState.tilesCollection[x][y], gameState)) {
                        res.add(gameState.tilesCollection[x][y]);
                    }
                }
            }

        }
        return res;
    }

    //真实伤害
    //deal 2 damage to an enemy unit
    //作用范围，所有敌方单位
    public static List<Tile> getAvailableTilesWhenClicking_truestrike(GameState gameState) {
        return getTilesFormUnits(gameState, gameState.curEnemyPlayer.unitList);
    }

    //日落药水
    //add 5 health to a unit, can't over its starting health value
    //作用范围，所有己方unit
    public static List<Tile> getAvailableTilesWhenClicking_sundrop_elixir(GameState gameState) {
        return getTilesFormUnits(gameState, gameState.curPlayer.unitList);
    }

    //加攻击，给自己的化身unit增加2点攻击力
    //作用范围：己方化身
    public static List<Tile> getAvailableTilesWhenClicking_staff_of_ykir(GameState gameState) {
        List<Tile> res = new ArrayList<>();
        for (Unit unit : gameState.curPlayer.unitList) {
            //when find the avatar unit
            if (unit == gameState.curPlayer.getAvatar_unit()) {
                int tilex = unit.getPosition().getTilex();
                int tiley = unit.getPosition().getTiley();
                // once it's not out of border
                if (!TileUtils.isOutOfBorder(tilex, tiley, gameState)) {
                    res.add(gameState.tilesCollection[tilex][tiley]);
                }
            }
        }
        return res;
    }

    // Entropic Decay  熵减
    // Reduce a non-avatar unit to 0 health 把一支非化身的军团血量减少为0，就是秒杀一个单位
    //作用范围：除敌方化身外的所有敌方unit //
    public static List<Tile> getAvailableTilesWhenClicking_entropic_decay(GameState gameState) {
        List<Tile> res = new ArrayList<>();
        for (Unit unit : gameState.curEnemyPlayer.unitList) {
            //when find the avatar unit
            if (!(unit == gameState.curEnemyPlayer.getAvatar_unit())) {
                int tilex = unit.getPosition().getTilex();
                int tiley = unit.getPosition().getTiley();
                // once it's not out of border
                if (!TileUtils.isOutOfBorder(tilex, tiley, gameState)) {
                    res.add(gameState.tilesCollection[tilex][tiley]);
                }
            }
        }
        return res;

    }


}
