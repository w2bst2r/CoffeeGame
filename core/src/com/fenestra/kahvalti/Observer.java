package com.fenestra.kahvalti;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by zamma on 09.05.2017.
 */

public interface Observer {
    void up();
    void down();
    void dispose();
}