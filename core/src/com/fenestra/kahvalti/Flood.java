package com.fenestra.kahvalti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by karim on 5/23/17.
 */

public class Flood implements Observer {

    private Texture floodTexture;
    private Sprite floodSprite;
    private Vector2 position;
    private float level;

    public Flood() {
        floodTexture = new Texture(Gdx.files.internal("flood.png"));
        level = -floodTexture.getHeight();
        floodSprite = new Sprite(floodTexture);
        floodSprite.setAlpha(.50f);
        position = new Vector2(0, level);
        floodSprite.setPosition(0, level);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getFloodTexture() {
        return floodTexture;
    }

    public float getLevel() {
        return level;
    }

    public void render(SpriteBatch batch){
        batch.begin();
        floodSprite.draw(batch);
        batch.end();
    }

    public void updateLevel(float offset){
        level = level + offset;
        floodSprite.setPosition(0, level);
        position = new Vector2(0, level);
    }

    @Override
    public void up() {
        updateLevel(-3);
    }

    @Override
    public void down() {
        updateLevel(5);
    }

    @Override
    public void dispose() {
        floodTexture.dispose();
    }
}