package com.fenestra.kahvalti.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fenestra.kahvalti.Kahvalti;

/**
 * Created by emremrah on 24.03.2017.
 */

public abstract class State {
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected Vector3 input;    //touch input
    protected GameStateManager gsm; //manages stages with stack

    protected State(GameStateManager gsm) {
        this.gsm = gsm;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Kahvalti.WORLD_WIDTH, Kahvalti.WORLD_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.apply();
        input = new Vector3();
    }

    protected abstract void handleInput();

    protected abstract void update(float delta);

    protected abstract void render(SpriteBatch spritebatch);

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    public abstract void dispose();
}
