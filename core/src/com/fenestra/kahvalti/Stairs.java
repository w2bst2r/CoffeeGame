package com.fenestra.kahvalti;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by zamma on 31.03.2017.
 */

public class Stairs implements Observer {
    // Stair mapping to place letters.
    public static final int[][][] stairMapping = {
            {{792, 450}, {648, 522}, {504, 594}, {360, 666}},
            {{468, 900}, {612, 972}, {756, 1044}, {900, 1116}},

            {{1080, 1314}, {936, 1386}, {792, 1458}, {648, 1530}},
            {{756, 1764}, {900, 1836}, {1044, 1908}, {1188, 1980}},

            {{1368, 2178}, {1224, 2250}, {1080, 2322}, {936, 2394}},
            {{468 + 576, 900 + 1728}, {612 + 576, 972 + 1728}, {756 + 576, 1044 + 1728}, {900 + 576, 1116 + 1728}},

            {{792 + 864, 450 + 2592}, {648 + 864, 522 + 2592}, {504 + 864, 594 + 2592}, {360 + 864, 666 + 2592}},
            {{468 + 864, 900 + 2592}, {612 + 864, 972 + 2592}, {756 + 864, 1044 + 2592}, {900 + 864, 1116 + 2592}},

            {{792 + 1152, 450 + 3456}, {648 + 1152, 522 + 3456}, {504 + 1152, 594 + 3456}, {360 + 1152, 666 + 3456}},
            {{468 + 1152, 900 + 3456}, {612 + 1152, 972 + 3456}, {756 + 1152, 1044 + 3456}, {900 + 1152, 1116 + 3456}}
    };
    private final Vector2 position;
    private final Texture image;
    private boolean isTop;

    public Stairs(float x, float y, boolean bottom) {
        position = new Vector2(x, y);
        isTop = false;
        if(bottom)
            image = new Texture("s-bot36.png");
        else
            image = new Texture("s-mid36.png");
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    public boolean getIsTop() {
        return isTop;
    }

    public Texture getTexture() {
        return image;
    }

    @Override
    public void up() {
        position.x -= 24f;
        position.y -= 72f;
    }

    @Override
    public void down() {
        position.x += 24f;
        position.y += 72f;
    }

    public void dispose() {
        image.dispose();
    }

}
