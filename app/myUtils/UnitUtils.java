package myUtils;

import actors.GameActor;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;

import javax.print.DocFlavor;
import java.util.*;

import static myUtils.TileUtils.cancelTheCurrentWhiteHighLightTiles;
import static myUtils.TileUtils.isTileOccupiedByUnit;

public class UnitUtils {
    //deck 1 for human
    public static String[] unitNamesInDeck1 = {
            "Azure Herald",
            "Azurite Lion",
            "Comodo Charger",
            "Fire Spitter",
            "Hailstone Golem",
            "Ironcliff Guardian",
            "Pureblade Enforcer",
            "Silverguard Knight"
    };

    //deck 2 for AI
    public static String[] unitNamesInDeck2 = {
            "Blaze Hound",
            "Bloodshard Golem",
            "Hailstone Golem",
            "Planar Scout",
            "Pyromancer",
            "Serpenti",
            "Rock Pulveriser",
            "Windshrike"
    };


    public static String[] moveDirections = {
            "up",
            "down",
            "left",
            "right"
    };

    /**
     * 一号牌组的unit
     */

    //是不是充电兽，普通unit
    //mana 1
    //attack 1
    //health 3
    public static boolean isComodoCharger(Unit unit) {
        return true;
    }

    //是不是牌组1的冰岩格莱姆，普通unit
    //mana 4
    //attack 4
    //health 6
    public static boolean isHailstoneGolem_Deck1(Unit unit) {
        return true;
    }


    //是不是纯粹之刃PurebladeEnforcer
    //Mana cost 2
    //Attack 1
    //Health 4
    //对方每出一张咒术牌，它的攻击力和血量都加1 F
    public static boolean isPurebladeEnforcerUnit(Unit unit) {
        return true;
    }

    //是不是蔚蓝先驱
    //mana 2
    //attack 1
    //health 4
    //当这个unit被召唤出来时，你的角色化身恢复3点血量，不允许超过上限 F
    public static boolean isAzureHerald(Unit unit) {
        return true;
    }

    //是不是银盾骑士
    //mana 3
    //attack 1
    //health 5
    //嘲讽：临近的敌方单位会被它嘲讽，无法移动，并且只能攻击他，如果有远程单位对它进行了攻击，也会被嘲讽住
    //当己方的玩家化身受到伤害时，银盾骑士增加2点攻击力
    public static boolean isSilverguardKnight(Unit unit) {
        return true;
    }

    //是不是狮子
    //mana 3
    //attack 2
    //health 3
    //可以攻击两次 F
    public static boolean isAzuriteLion(Unit unit) {
        return true;
    }

    //是不是火焰射手
    //mana 4
    //attack 3
    //health 2
    //远程单位，可以攻击场上的任何敌人 F
    public static boolean isFireSpitter(Unit unit) {
        return true;
    }


    //是不是铁岭守护者
    //mana 5
    //attack 3
    //health 10
    //可以在场上任何地方被召唤出来 F
    //嘲讽：临近的敌方单位会被它嘲讽，无法移动，并且只能攻击他，如果有远程单位对它进行了攻击，也会被嘲讽住
    public static boolean isIroncliffGuardian(Unit unit) {
        return true;
    }


    /**
     * 二号牌组的unit
     */


    //是不是侦察者
    //mana 1
    //attack 2
    //health 1
    //可以在场上任何地方被召唤出来 F
    public static boolean isPlanarScout(Unit unit) {
        return true;
    }


    //是不是岩石粉碎者
    //mana 2
    //attack 1
    //health 4
    //嘲讽：临近的敌方单位会被它嘲讽，无法移动，并且只能攻击他，如果有远程单位对它进行了攻击，也会被嘲讽住
    public static boolean isRockPulveriser(Unit unit) {
        return true;
    }

    //是不是火法师
    //mana 2
    //attack 2
    //health 1
    //远程单位，可以攻击场上的任何敌人 F
    public static boolean isPyromancer(Unit unit) {
        return true;
    }

    //是不是血腥格莱姆，普通unit
    //mana 3
    //attack 4
    //health 3
    public static boolean isBloodshardGolem(Unit unit) {
        return true;
    }

    //是不是炙热猎犬
    //mana 3
    //attack 4
    //health 3
    //当这个unit被召唤出来时，两边的player都抽一张牌 F
    public static boolean isBlazeHound(Unit unit) {
        return true;
    }


    //是不是风之鸟
    //mana 4
    //attack 4
    //health 3
    //可以飞，也就是可以全地图移动 F
    //当这个单位阵亡时，它的主人player，可以抽一张牌 F
    public static boolean isWindshrike(Unit unit) {
        return true;
    }


    //是不是牌组2的冰岩格莱姆，普通unit
    //mana 4
    //attack 4
    //health 6
    public static boolean isHailstoneGolem(Unit unit) {
        return true;
    }

    //是不是瑟芬提
    //mana 6
    //attack 7
    //health 4
    // 可以攻击两次 F
    public static boolean isSerpenti(Unit unit) {
        return true;
    }

    /**
     * 根据打出的Card判断是哪一个Unit
     * @param card 打出的卡
     *
     */
    public static void cardToUnit(Card card, Unit unit) {
        String cardName = card.getCardname();
        System.out.println("cardName : " + cardName);
        if (cardName.equals("Comodo Charger")) {
            unit.setName("Comodo Charger");
            unit.setDefaultAttack(1);
            unit.setDefaultHealth(3);
        } else if (cardName.equals("Hailstone Golem")) {
            unit.setName("Hailstone Golem");
            unit.setDefaultAttack(4);
            unit.setDefaultHealth(6);
        } else if (cardName.equals("Azure Herald")) {
            unit.setName("Azure Herald");
            unit.setDefaultAttack(1);
            unit.setDefaultHealth(4);
        } else if (cardName.equals("Azurite Lion")) {
            unit.setName("Azurite Lion");
            unit.setDefaultAttack(2);
            unit.setDefaultHealth(3);
            unit.setAttackTwice(true);
        } else if (cardName.equals("Fire Spitter")) {
            unit.setName("Fire Spitter");
            unit.setDefaultAttack(3);
            unit.setDefaultHealth(2);
            unit.setRange(true);
        } else if (cardName.equals("Ironcliff Guardian")) {
            unit.setName("Ironcliff Guardian");
            unit.setDefaultAttack(3);
            unit.setDefaultHealth(10);
            unit.setProvoke(true);
        } else if (cardName.equals("Pureblade Enforcer")) {
            unit.setName("Pureblade Enforcer");
            unit.setDefaultAttack(1);
            unit.setDefaultHealth(4);
        } else if (cardName.equals("Silverguard Knight")) {
            unit.setName("Silverguard Knight");
            unit.setDefaultAttack(1);
            unit.setDefaultHealth(5);
            unit.setProvoke(true);
        } else if (cardName.equals("Blaze Hound")) {
            unit.setName("Blaze Hound");
            unit.setDefaultAttack(4);
            unit.setDefaultHealth(3);
        } else if (cardName.equals("Bloodshard Golem")) {
            unit.setName("Bloodshard Golem");
            unit.setDefaultAttack(4);
            unit.setDefaultHealth(3);
        } else if (cardName.equals("Planar Scout")) {
            unit.setName("Planar Scout");
            unit.setDefaultAttack(2);
            unit.setDefaultHealth(1);
        } else if (cardName.equals("Pyromancer")) {
            unit.setName("Pyromancer");
            unit.setDefaultAttack(2);
            unit.setDefaultHealth(1);
            unit.setRange(true);
        } else if (cardName.equals("Rock Pulveriser")) {
            unit.setName("Rock Pulveriser");
            unit.setDefaultAttack(1);
            unit.setDefaultHealth(4);
            unit.setProvoke(true);
        } else if (cardName.equals("Serpenti")) {
            unit.setName("Serpenti");
            unit.setDefaultAttack(7);
            unit.setDefaultHealth(4);
            unit.setAttackTwice(true);
        } else if (cardName.equals("WindShrike")) {
            unit.setName("WindShrike");
            unit.setDefaultAttack(4);
            unit.setDefaultHealth(3);
            unit.setFlying(true);
        }
    }

    /**
     * 判断一个unit是否在场上
     *
     * @return
     */
    public static boolean isUnitOnStage(Unit unit, GameState gameState) {
        //注意，所有unit初始坐标是-1,-1
        //如果根据这个unit拿到的tile是null，那么就说明这个unit对应的位置是出界的，那么它就是-1，-1，也就是没上场，否则就上场了
        return TileUtils.getTileFromUnit(unit, gameState) != null;
    }


    /**
     * 从当前的Tile上拿到占据这个tile的unit，如果这个tile没人占，是空的。那么就返回null
     *
     * @param tile      tile
     * @param gameState gameState
     * @return
     */
    public static Unit getUnitFromTile(Tile tile, GameState gameState) {
        //首先去检查当前玩家和当前敌人的两个unit list
        List<Unit> res = new ArrayList<>();
        //找当前玩家的unit list
        for (Unit unit : gameState.curPlayer.unitList) {
            //如果找到了某一个unit的position和输入的tile的position一致，说明就是它了，加入res
            if (unit.getPosition().getTilex() == tile.getTilex()
                    && unit.getPosition().getTiley() == tile.getTiley()) {
                res.add(unit);
            }
        }

        //找当前敌人的unit list
        for (Unit unit : gameState.curEnemyPlayer.unitList) {
            //如果找到了某一个unit的position和输入的tile的position一致，说明就是它了，加入res
            if (unit.getPosition().getTilex() == tile.getTilex()
                    && unit.getPosition().getTiley() == tile.getTiley()) {
                res.add(unit);
            }
        }

        if (res.size() == 0) {
            //没找到返回null
            return null;
        } else if (res.size() == 1) {
            //找到一个说明是正确的，返回它即可
            return res.get(0);
        } else {
            //找到不止一个，那你的代码是有问题的！返回第一个，但是把错误信息打在服务器上
            System.err.println("Warning: There are multiple unites on the same tile!");
            return res.get(0);
        }

    }


    /**
     * 内部调用simulateUnitMove模拟移动，来实现找到当前选中unit的全部白色格子和红色格子
     * @param gameState  游戏状态
     * @param unit  选中的unit
     * @param stamina  体力值，一般为2
     */
    public static void checkWhiteAndRedTilesOfSelectedUnit(GameState gameState, Unit unit, int stamina,
                                                           List<Tile> innerWhiteHighLightTiles, List<Tile> innerRedHighLightTiles) {
        //如果这个unit都没上场，直接return
        if (!UnitUtils.isUnitOnStage(unit, gameState)) {
            return;
        }
        //todo 首先要判断这个unit是不是会飞，如果会飞，stamina设置成 canvasWidth = 9 + canvasLength = 5;
        //todo 如果怪物被嘲讽了，它在我们的simulateMove模拟移动方法里面是不会得到任何红色格子的，所以还需要在外面单独判断一次嘲讽他的是谁
        //先创造两个cache
        Map<Tile, Integer> whiteCache = new HashMap<>();
        Map<Tile, Integer> redCache = new HashMap<>();
        //希望从unit拿到unit所处的tile
        Tile tile = TileUtils.getTileFromUnit(unit, gameState);

        //然后开始模拟移动
        simulateUnitMove(gameState, tile, whiteCache, redCache, stamina);
        //模拟移动完了后，白色格子已经确定了都在whiteCache里，但红色格子没有考虑全，因为还有外面一圈的要考虑

        //fixme : debug words
        System.err.println("whiteCache size= " + whiteCache.size());
        System.err.println("redCache size = " + redCache.size());

        //创造一个map，保存拓展一圈后的攻击范围
        Map<Tile, Integer> attackRange = new HashMap<>();

        //先遍历whiteCache把白色的格子都放进gameState.currentWhiteHighLightTiles中
        //同时拿到map中每个白格子的周围的八个格子
        for (Map.Entry<Tile, Integer> entry : whiteCache.entrySet()) {
            //维护白色的结果list
            innerWhiteHighLightTiles.add(entry.getKey());

            //把当前格子，连同它周围的八个格子都丢进attackRange中
            //先丢当前格子
            if (!attackRange.containsKey(entry.getKey())){
                attackRange.put(entry.getKey(), 1);
            }
            //去拿当前格子周围的八个格子，可能因为出界问题没有八个，但无所谓，我们考虑了这种情况
            List<Tile> surroundingEightTiles = TileUtils.getSorroundingEightTiles(entry.getKey(), gameState);
            for (Tile surroundingTile : surroundingEightTiles) {
                //再把这些周围的格子也放进去
                if (!attackRange.containsKey(surroundingTile)){
                    attackRange.put(surroundingTile, 1);
                }
            }
        }

        //fixme debug
        System.err.println("currentWhiteHighLightTiles size = " + innerWhiteHighLightTiles.size());

        //遍历完成后，attackRange就初始化好了，之后只要遍历attackrange，检查每一个tile上面有没有敌人，有敌人的话，就是红色格子
        for (Map.Entry<Tile, Integer> entry : attackRange.entrySet()) {
            if (TileUtils.isTileOccupiedByCurEnemyUnit(entry.getKey(), gameState)){
                innerRedHighLightTiles.add(entry.getKey());
            }
        }

        System.err.println("innerRedHighLightTiles size = " + innerRedHighLightTiles.size());

    }


    /**
     * dfs + 记忆表 进行unit的模拟移动，来得到所有的白色tile和一部分红色tile
     *
     * @param gameState  状态类
     * @param tile       输入的tile
     * @param whiteCache 缓存白色tile
     * @param redCache   缓存红色tile
     * @param stamina    体力值
     */
    public static void simulateUnitMove(GameState gameState, Tile tile,
                                        Map<Tile, Integer> whiteCache, Map<Tile, Integer> redCache, int stamina) {
        //todo 如果被嘲讽了，那么在这里就应该退出去，因为不允许走动
        //没有体力了，递归出口
        if (stamina == 0) {
            System.err.println("[" + tile.getTilex() +", " + tile.getTiley() + "]" + "体力值为0了，不能走");
            return;
        }

        //准备移动，先把体力值减少
        stamina--;
        //往四个方向自由的移动
        for (String moveDirection : UnitUtils.moveDirections) {
            //先拿当前的position
            int tilex = tile.getTilex();
            int tiley = tile.getTiley();

            //然后根据方向去移动
            if ("up".equals(moveDirection)) {
                tiley ++;
            } else if ("down".equals(moveDirection)) {
                tiley --;
            } else if ("left".equals(moveDirection)) {
                tilex --;
            } else if ("right".equals(moveDirection)) {
                tilex ++;
            }

            //检查是否出界,出界就减枝
            //check whether it's out of border, once true, terminate
            if (TileUtils.isOutOfBorder(tilex, tiley, gameState)) {
                System.err.println("[" + tilex +", " + tiley + "]" + "出界");
                continue;
            }

            //到这里说明没出界
            //生成新的tile，也就是移动后的当前tile
            Tile currentTile = TileUtils.getTileFromPosition(tilex, tiley, gameState);

            //如果当前tile拿到的是null，直接剪枝，虽然这种情况基本不可能出现
            if (currentTile == null) {
                continue;
            }

            //只要当前tile上有敌人的unit，说明还没走到终点就踩到了敌人或者是目的地上有敌人，这条路走不通，return 剪枝
            if (TileUtils.isTileOccupiedByCurEnemyUnit(currentTile, gameState)) {
                //我们可以当前的这个tile放进要标红的redCache中，因为这个tile是一定可以攻击到的
                //注意不要重复放了
                if (!redCache.containsKey(currentTile)) {
                    //把这个tile放进redCache中
                    redCache.put(currentTile, 1);
                }
                System.err.println("[" + tilex +", " + tiley + "]" + "有敌人，不能走");
                continue;
            }

            //如果stamina == 0，&& 当前tile上有友方的unit，都说明目的地已经被友方单位占了，走不通，剪枝
            if (stamina == 0 && TileUtils.isTileOccupiedByUnit(currentTile, gameState)) {
                System.err.println("[" + tilex +", " + tiley + "]" + "有友军，不能走");
                continue;
            }

            //到这里说明这一步是能走的，那么当前的更新后的currentTile属于是可移动到的范围，要放入whiteCache中
            //注意还是要判断是不是重复了，不要重复放
            //一定要注意，如果是在路程中，碰到了友军，此时可以穿过友军继续深入，但不能在友军那一格子里停下来
            //也就是不能把友军加入map
            //fixme
            if (TileUtils.isTileOccupiedByCurPlayerUnit(currentTile, gameState)){
                System.out.println("[" + tilex +", " + tiley + "]" + "这个格子上有友军，可以通过，但不能添加到map中");
            }
            if (!whiteCache.containsKey(currentTile) && !TileUtils.isTileOccupiedByCurPlayerUnit(currentTile, gameState)) {
                whiteCache.put(currentTile, 1);
            }

            //递归深入一层
            simulateUnitMove(gameState, currentTile, whiteCache, redCache, stamina);
        }
    }


    public static void deathCheckAndDelete(ActorRef out, Unit unit, GameState gameState) {
        // 如果生命值为0，则死亡
        //先在前端表现死亡动画
        if (unit.getCurHealth() <= 0){
            BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
            gameState.interval(2000);
        }

        // 如果是风之鸟，触发unit亡语效果，（对手）HumanPlayer抽一张牌
        if (unit.getName().equals("WindShrike")) {
            InitalUtils.dealHandcardsToPlayer(out, gameState, gameState.curEnemyPlayer, 1);
            gameState.interval(500);
        }

        // unit 死亡后的处理
        if (unit.getCurHealth() <= 0) {
            // 前端清除此unit
            BasicCommands.addPlayer1Notification(out, "deleteDeadUnit", 2);
            BasicCommands.deleteUnit(out, unit);
            gameState.interval(2000);

            // 后端清除此Unit的position为-1,-1, unitList清除此unit，curAttack/curHealth重新设置为初始状态
            unitSetPositionToFakeTile(unit);
            Iterator<Unit> iter = gameState.curPlayer.unitList.iterator();
            while (iter.hasNext()) {
                if (unit.getId() == iter.next().getId()) {
                    iter.remove();
                }
            }


            // 触发一次otherclick
            //要把上一回合选中的卡片取消高亮
            //first of all, we should cancel the former highlight. Clear the map to avoid the multiple selection
            TileUtils.otherClick(out, gameState);
        }
    }

    // 把位置放到-1，-1, 别占了棋盘上的Tile
    public static void unitSetPositionToFakeTile(Unit unit) {
        Tile fakeTile = BasicObjectBuilders.loadTile(-1, -1);
        unit.setPositionByTile(fakeTile);
    }

    // 设置avatar初始化
    public static void setAvatar(Unit unit, String name) {
        unit.setName(name);
        unit.setDefaultAttack(2);
        unit.setDefaultHealth(20);
        unit.setCurAttack(2);
        unit.setCurHealth(20);
    }

    // unit技能，被召唤时触发效果 2张
    public static void summonEffect(ActorRef out, Unit unit, GameState gameState) {
        if (unit.getName().equals("Azure Herald")) {
            Unit curPlayerAvatar = gameState.curPlayer.getAvatar_unit();
            curPlayerAvatar.setCurHealth(curPlayerAvatar.getCurHealth() <= 17 ? curPlayerAvatar.getCurHealth() + 3 : 20);
            // 更新前端化身的生命值
            BasicCommands.setUnitHealth(out, curPlayerAvatar, curPlayerAvatar.getCurHealth());
        } else if (unit.getName().equals("Blaze Hound")) {
            InitalUtils.dealHandcardsToPlayer(out, gameState, 1);
        }
    }

    // unit技能，对手使用spellcard时触发 1张
    public static void unitEffectWhenUseSpellCard(ActorRef out, GameState gameState) {
        // 遍历对手的unitList, 如果对手场上有Pureblade Enforcer这张牌，它获得+1+1
        for (Unit special : gameState.curEnemyPlayer.unitList) {
            if (special.getName().equals("Pureblade Enforcer")) {
                special.setCurHealth(special.getCurHealth() + 1);
                special.setCurAttack(special.getCurAttack() + 1);
                // 前端显示
                BasicCommands.setUnitHealth(out, special, special.getCurHealth());
                gameState.interval(20);
                BasicCommands.setUnitAttack(out, special, special.getCurAttack());
                gameState.interval(20);
            }
        }

    }

    // unit技能，如果是可以随处召唤的unit，则返回List<Tile>返回所有未被占据的Tile
    public static boolean unitEffectSummonAnywhere(Card card) {
        if (card.getCardname().equals("Ironcliff Guardian")) {
            return true;
        } else if (card.getCardname().equals("Planar Scout")) {
            return true;
        }
        return false;
    }

    public static void updateUnitHealth(ActorRef out, GameState gameState, Unit unit, int curHealth){
        unit.setCurHealth(curHealth);
        BasicCommands.setUnitHealth(out, unit, unit.getCurHealth());
        gameState.interval(50);
    }

    /**
     * 发起攻击，完成攻击的一系列结算
     * 如果攻击过程中无unit死亡，返回0
     * 如果攻击过程中，防守者被干掉了，返回1
     * 如果攻击过程中，攻击者被防守者反击干掉了，返回2
     * @param out
     * @param gameState
     * @param attacker
     * @param defender
     * @return
     */
    public static int launchAttack(ActorRef out, GameState gameState, Unit attacker, Unit defender){
        System.out.println("attacker name :" + attacker.getName() + ", defender name : " + defender.getName());
        int attackerCurAttack = attacker.getCurAttack();
        int defenderCurHealth = defender.getCurHealth();
        //发起进攻的动画
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        gameState.interval(1500);

        //攻击者进行过的攻击次数加1
        attacker.setAlreadyAttackTimes(attacker.getAlreadyAttackTimes() + 1);

        //如果攻击者攻击力大于等于防守者的血量，防守者就死掉了，无法反击
        if (attackerCurAttack >= defenderCurHealth){
            UnitUtils.updateUnitHealth(out, gameState, defender, defenderCurHealth - attackerCurAttack);
            UnitUtils.deathCheckAndDelete(out, defender, gameState);
            //防守者被攻击者干掉了
            System.out.println("Defender taken out by attacker!!!!!! No attack counter");
            return 1;
        }
        //否则，防守者先扣除血量，更新然后可以进行一次反击
        else{

            //防守者扣除血量并前端更新
            defenderCurHealth = defenderCurHealth - attackerCurAttack;
            UnitUtils.updateUnitHealth(out, gameState, defender, defenderCurHealth);

            // unit技能，range全图攻击，如果攻击者有range属性，且不在被攻击者的周围8个tile，则无法反击
            if (attacker.isRange()) {
                Tile defenderTile = TileUtils.getTileFromUnit(defender, gameState);
                Tile attackerTile = TileUtils.getTileFromUnit(attacker, gameState);
                if (!TileUtils.getSorroundingEightTiles(defenderTile, gameState).contains(attackerTile)) {
                    return 0;
                }
            }

            System.out.println("The defender is not taken out and can counterattack ATTACK COUNTER !!");
            //进行反击
            //表现动画
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
            gameState.interval(1500);

            int defenderCounterAttack = defender.getCurAttack();
            int attackerCurHealth = attacker.getCurHealth();

            //如果反击把攻击者干掉了，执行攻击者的死亡操作
            if (defenderCounterAttack >= attackerCurHealth){
                System.out.println("Defender's counterattack takes out attacker");
                UnitUtils.updateUnitHealth(out, gameState, attacker, attackerCurHealth - defenderCounterAttack);
                UnitUtils.deathCheckAndDelete(out, attacker, gameState);
                //攻击者被防守者反击干掉了，返回2
                return 2;
            }

            //如果攻击者没被反击干掉，那么就要扣除攻击者的相应血量
            else{
                System.out.println("The defender's counterattack does not kill the attacker, and no unit dies in this attack");
                attackerCurHealth = attackerCurHealth - defenderCounterAttack;
                UnitUtils.updateUnitHealth(out, gameState, attacker, attackerCurHealth);
                return 0;
            }
        }
    }


    /**
     * 找到移动攻击时，最合适的移动目的地！(这个目的地是自动选择的)
     * @param gameState    游戏状态量
     * @param attackerUnit  发起攻击的unit
     * @param defenderUnit  防守的unit
     * @return
     */
    public static List<Tile> getMovementDestinationWhenDirectlyLaunchAttack(GameState gameState, Unit attackerUnit, Unit defenderUnit){
        //攻击者的格子
        Tile attackerTile = TileUtils.getTileFromUnit(attackerUnit, gameState);
        //防御者的格子defenderUnit一定要在标红的格子上
        Tile defenderTile = TileUtils.getTileFromUnit(defenderUnit, gameState);
        //如果defenderUnit不在标红的格子上，返回空list
        if (!gameState.currentRedHighLightTiles.contains(defenderTile)){
            return new ArrayList<>();
        }
        //拿到当前所有的白色格子
        //求sorroundingEightTiles和currentWhiteHighLightTiles的交集，交集都是可能的目的地，也就是在这些交集上一定能打到我们想打的unit
        //只是要考虑最优的目的地

        //这里有问题，因为currentWhiteHighLightTiles不包括攻击者自己的位置，显然要是攻击者可以不动就发起进攻的话是最近的
        //所以要把攻击者自己的格子也加进去

        //还有一个问题，就是要考虑  gameState.currentWhiteHighLightTilesNotShow
        //因为有的 tiles 本身就是不高亮的，但是在移动的时候必须要把他们当高亮的来处理

        List<Tile> currentWhiteHighLightTilesAndAttacker = null;
        if (gameState.currentWhiteHighLightTiles.size() != 0){
            //fixme
            System.out.println("we use currentWhiteHighLightTiles, the size is :" + gameState.currentWhiteHighLightTiles.size());
            currentWhiteHighLightTilesAndAttacker = new ArrayList<>(gameState.currentWhiteHighLightTiles);
        }
        else if (gameState.currentWhiteHighLightTilesNotShow.size() != 0){
            //fixme
            System.out.println("we use currentWhiteHighLightTilesNotShow, the size is :" + gameState.currentWhiteHighLightTilesNotShow.size());
            currentWhiteHighLightTilesAndAttacker = new ArrayList<>(gameState.currentWhiteHighLightTilesNotShow);
        }
        else{
            //fixme
            System.out.println("Both lists are empty, so it means that it is indeed impossible to move, because the stamina is used up" + gameState.currentWhiteHighLightTilesNotShow.size());
            //如果当前两个玩意儿都是空的，那么就说明确实是不能移动的，因为体力用完了
            //创建一个空的list，等着把攻击者本身这个tile加进去就完事了
            currentWhiteHighLightTilesAndAttacker = new ArrayList<>();
        }

        currentWhiteHighLightTilesAndAttacker.add(attackerTile);
        //根据当前拿到的defenderTile，找到它周围的八个tiles，可能因为出界没有八个，但我们考虑了这种情况，没有关系
        List<Tile> sorroundingEightTiles = TileUtils.getSorroundingEightTiles(defenderTile, gameState);


        //先求交集
        List<Tile> intersections = new ArrayList<>(currentWhiteHighLightTilesAndAttacker);
        intersections.retainAll(sorroundingEightTiles);


        // 然后求交集中和targetTile最近的格子
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int index = 0;
        for (Tile intersection : intersections) {
            //找到和attackerUnit最近的点
            int absoluteDistanceFromTiles = TileUtils.getAbsoluteDistanceFromTiles(attackerTile, intersection);
            if (min > absoluteDistanceFromTiles){
                //找到最小值，可能有多个，更新最小值
                min = absoluteDistanceFromTiles;
                //保存minIndex
                minIndex = index;
            }
            index ++;
        }
        List<Tile> res = new ArrayList<>();
        res.add(intersections.get(minIndex));

        return res;

    }

/* 冲突处理
    public static void checkMovePointAndAttackPointAndExecuteLightTile(ActorRef out, Unit curUnit, GameState gameState) {
        if (curUnit.getMovePoint() == 1 && curUnit.getAttackPoint() == 1) {
            // 攻击点数为1，移动点数为1，这一步要白色高亮红色高亮全部亮起
            unitMovePoint1AttackPoint1(out, curUnit, gameState);
        } else if ((curUnit.getMovePoint() == 0 && curUnit.getAttackPoint() == 1)) {

            // 攻击点数为1，移动点数为0，只遍历周围上下左右
            unitMovePoint0AttachPoint1(out, curUnit, gameState);
        } else if (curUnit.getMovePoint() == 1 && curUnit.getAttackPoint() == 0) {
            // 攻击点数为0，移动点数为1，视为一次otherclick
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
    }

    public static void unitMovePoint1AttackPoint1(ActorRef out, Unit curUnit, GameState gameState) {
        // 攻击点数为1，移动点数为1，这一步要白色高亮红色高亮全部亮起
        System.out.println("now the unit's both movePoint and attackPoint is 1");

        // 调用unitUtil中的方法求白色高亮红色高亮范围
        // 先清除一遍
        TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);
        List<Tile> innerWhiteHighLightTiles = new ArrayList<>();
        List<Tile> innerRedHighLightTiles = new ArrayList<>();
        UnitUtils.checkWhiteAndRedTilesOfSelectedUnit(gameState, curUnit, 2,
                innerWhiteHighLightTiles, innerRedHighLightTiles);

        // 高亮白色红色tiles,方法里调整了对应的tileMode
        TileUtils.whiteHighLightAvailableTiles(innerWhiteHighLightTiles, out, gameState);
        TileUtils.redHighLightAvailableTiles(innerRedHighLightTiles, out, gameState);
    }

    private static void unitMovePoint0AttachPoint1(ActorRef out, Unit curUnit, GameState gameState) {
        // 攻击点数为1，移动点数为0，只遍历周围上下左右
        System.out.println("now the unit's movePoint is 0 and attackPoint is 1");
        // 先清除一遍
        TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);
        List<Tile> innerRedHighLightTiles = new ArrayList<>();
        int[][] dirs = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int curTilex = curUnit.getPosition().getTilex(), curTiley = curUnit.getPosition().getTiley();
        for (int[] dir : dirs) {
            int checkTilex = curTilex + dir[0], checkTiley = curTiley + dir[1];
            // 遍历上下左右的四个tile，如果有敌方Unit，则亮红
            if (!TileUtils.isOutOfBorder(checkTilex, checkTiley, gameState)) {
                Tile checkTile = TileUtils.getTileFromPosition(checkTilex, checkTiley, gameState);
                Unit checkUnit = UnitUtils.getUnitFromTile(checkTile, gameState);
                if (gameState.curEnemyPlayer.unitList.contains(checkUnit)) {
                    innerRedHighLightTiles.add(checkTile);
                }
            }
        }
        TileUtils.redHighLightAvailableTiles(innerRedHighLightTiles, out, gameState);
    }


*/


    public static void reduceUnitCorrespondingStaminaAfterMoving(GameState gameState, Unit unit, Tile targetTile){
        Tile beginningTile = TileUtils.getTileFromUnit(unit, gameState);
        //算出相对距离
        int relativeDistance = 0;
        if (beginningTile != null) {
            relativeDistance = TileUtils.getRelativeDistanceFormTiles(beginningTile, targetTile);
        }
        //用体力值减去这个相对距离，得到新的体力值，进行更新
        unit.setStamina(unit.getStamina() - relativeDistance);
    }

    /**
     * 拿到周围的八个格子里面可能标红的(有敌人)的tiles的一个list
     * @param unit 军团
     * @param gameState 状态类
     * @return
     */
    public static List<Tile> getAvailableRedTilesFromSurroundingEightTiles(Unit unit, GameState gameState){
        List<Tile> res = new ArrayList<>();
        //根据这个unit拿到tile
        Tile tile = TileUtils.getTileFromUnit(unit, gameState);
        if (tile == null){
            return new ArrayList<>();
        }
        //拿到周围的八个格子，已经考虑了出界的情况
        List<Tile> sorroundingEightTiles = TileUtils.getSorroundingEightTiles(tile, gameState);
        //遍历它
        for (Tile curTile : sorroundingEightTiles) {
            //如果当前格子上有敌人，我就把它加进res
            if (TileUtils.isTileOccupiedByCurEnemyUnit(curTile, gameState)) {
                res.add(curTile);
            }
        }
        return res;
    }

    // 检查是否被嘲讽
    public static boolean checkIsProvoked(Unit unit, GameState gameState) {
        boolean isProvoked = false;
        Tile curTile = TileUtils.getTileFromUnit(unit, gameState);
        for (Tile tile : TileUtils.getSorroundingFourTiles(curTile, gameState)) {
            if (TileUtils.isTileOccupiedByCurEnemyUnit(tile, gameState)) {
                Unit u = UnitUtils.getUnitFromTile(tile, gameState);
                if (u.isProvoke()) {
                    isProvoked = true;
                }
            }
        }
        return isProvoked;
    }

    // 已经确定被嘲讽了，白色没有，红色只显示周围4个格子中的嘲讽怪
    public static void unitIsProvokedSet(Unit unit, GameState gameState, List<Tile> innerWhiteHighLightTiles, List<Tile> innerRedHighLightTiles) {
        innerWhiteHighLightTiles.clear();
        innerRedHighLightTiles.clear();
        Tile curTile = TileUtils.getTileFromUnit(unit, gameState);
        for (Tile tile : TileUtils.getSorroundingFourTiles(curTile, gameState)) {
            if (TileUtils.isTileOccupiedByCurEnemyUnit(tile, gameState)) {
                Unit u = UnitUtils.getUnitFromTile(tile, gameState);
                if (u.isProvoke()) {
                    Tile provokeTile = TileUtils.getTileFromUnit(u, gameState);
                    innerRedHighLightTiles.add(provokeTile);
                }
            }
        }
    }


    public static void getAvailableRedAndWhiteTilesWhenClickingAnUnit(GameState gameState, Unit curUnit,
                                       List<Tile> innerWhiteHighLightTiles, List<Tile> innerRedHighLightTiles){
        //在还没有走过的情况下(剩余体力值为2)，点击unit，显示以2点体力递归探测到的白色可移动格子和红色可攻击格子
        if (curUnit.getStamina() == 2) {
            UnitUtils.checkWhiteAndRedTilesOfSelectedUnit(gameState, curUnit, curUnit.getStamina(),
                    innerWhiteHighLightTiles, innerRedHighLightTiles);
        }
        //在走了一步的情况下(剩余体力值为1)，点击unit，显示以1点体力递归探测到的红色可攻击格子，不显示白色格子，但是要加不显示颜色的白色格子
        else if (curUnit.getStamina() == 1) {
            UnitUtils.checkWhiteAndRedTilesOfSelectedUnit(gameState, curUnit, curUnit.getStamina(),
                    innerWhiteHighLightTiles, innerRedHighLightTiles);
            //先把扫描到的白色格子放进去(更新)，然后再把innerWhiteHighLightTiles清空
            //fixme
            System.out.println("This unit has a little energy left, he can only show the red squares");
            TileUtils.refreshTheCurrentWhiteHighLightTilesNotShow(gameState, innerWhiteHighLightTiles);
            System.out.println("innerWhiteHighLightTiles.size = " + innerWhiteHighLightTiles.size());
            System.out.println("gameState.currentWhiteHighLightTilesNotShow.size: " + gameState.currentWhiteHighLightTilesNotShow.size());
            innerWhiteHighLightTiles.clear();
        }
        //在走了两步的情况下(剩余体力值为0)，点击unit，只显示周围八格的可能的红色可攻击格子，不显示白色格子
        else if (curUnit.getStamina() == 0) {
            //这里必须用填充的方法，不然引用是拷贝的一份引用，所以在方法内把引用指向一个堆新的对象是没有意义的
            innerRedHighLightTiles.addAll(UnitUtils.getAvailableRedTilesFromSurroundingEightTiles(curUnit, gameState));
            //只管红色的，白色的不管，就是空 list
        }
    }


//    public static boolean checkIsProvoked(Unit curUnit) {
//
//    }

}
