package net.kaparis.game.supermariobros.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by David on 11/9/2016.
 */

public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
