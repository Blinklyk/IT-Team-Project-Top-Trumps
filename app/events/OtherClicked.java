package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import myUtils.CardUtils;
import myUtils.TileUtils;
import structures.GameState;
import structures.basic.Unit;

import java.util.Map;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * <p>
 * {
 * messageType = “otherClicked”
 * }
 *
 * @author Dr. Richard McCreadie
 */
public class OtherClicked implements EventProcessor {

    @Override
         public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        //要把上一回合选中的卡片取消高亮
        //first of all, we should cancel the former highlight. Clear the map to avoid the multiple selection
        CardUtils.cancelTheFormerHighLightCards(out, gameState);

        //要把当前的高亮的白色单元格取消,然后把list清空
        TileUtils.cancelTheCurrentWhiteHighLightTiles(out, gameState);

        //要把当前的高亮的红色单元格取消,然后把list清空
        TileUtils.cancelTheCurrentRedHighLightTiles(out, gameState);

        // 要清除当前的unit选中状态
        for (Map.Entry<Unit, Integer> entry : gameState.curPlayer.unitsMode.entrySet()) {
            if (entry.getValue() == 1) {
                entry.setValue(0);
            }
        }


    }

}


