package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import myUtils.CardUtils;
import myUtils.TileUtils;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * <p>
 * {
 * messageType = “cardClicked”
 * position = <hand index position [1-6]>
 * }
 *
 * @author Dr. Richard McCreadie
 */
public class CardClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {


        //对于tile而言，0是没颜色，1是白色强调，2是红色强调
        //对于card而言，BasicCommands.drawCard(out, hailstone_golem, 1, 0);最后一位是0代表没高亮，最后一位是1代表高亮

        //没必要了，因为在添加方法里我集成了删除之前高亮的
        //还是有必要，因为你点击卡片的时候当前红色高亮也要清除。
        //要把当前的高亮的白色单元格取消，因为要高亮新一批单元格了，并且把并且要把gameState.currentWhiteHighLightTiles 清空
        TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);

        //要把当前的高亮的红色单元格取消，因为要高亮新一批单元格了，并且把并且要把gameState.currentRedHighLightTiles 清空
        TileUtils.cancelTheCurrentRedHighLightTiles(out, gameState);


        //Then, get the position of the hand card
        //take care, handPosition start with 1!!!
        int handPosition = message.get("position").asInt();

        //fixme  debug
        System.out.println("你点击的这张牌的position是" + handPosition);

        //Get the card from the position
        Card card = CardUtils.getCardFromHandPosition(handPosition, gameState);


        //首先，要把之前选中的卡片取消高亮，因为要高亮新的卡片了，我们的工具方法中集成了这一操作
        //		//first of all, we should cancel the former highlight cards. Clear the map to avoid the multiple selection
        //		//Draw and highlight the card
        //		//we select this card and set it mapping to value handPosition in hand card Mode Map
        CardUtils.drawAndHighLightTheCard(out, card, handPosition, gameState);

        //如果当前这张卡的mana比玩家的mana值要大，这张卡要高亮，但是单元格不亮
        if (card.getManacost() > gameState.curPlayer.getMana()) {
            //直接取消之前高亮的单元格即可
            TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);
            return;
        }


        // Get all available tiles when Clicking the card.
        List<Tile> availableTiles = CardUtils.getAllAvailableTilesWhenCalling(gameState, card);


        //Highlight all the available tiles
        TileUtils.whiteHighLightAvailableTiles(availableTiles, out, gameState);
    }

}
