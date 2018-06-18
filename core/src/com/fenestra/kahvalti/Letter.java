package com.fenestra.kahvalti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by leyenda on 4/4/17.
 */
public class Letter implements Observer {
    private Vector2 position;
    private String letter;
    private TextureAtlas atlas;

    public TextureRegion getCurrentLetter() {
        return currentLetter;
    }

    private TextureRegion currentLetter;

    private float levitationDelay;
    private final float maxDelay = .2f;
    private final int levitationAmount = 7;
    private int levitationCounter = 0;
    private boolean levitationStatusUp = true;

    private int[] footPrint;

    public Letter(String letter, int[] position) {
        this.letter = letter;
        atlas = new TextureAtlas(Gdx.files.internal("letters/letters.pack"));
        currentLetter = atlas.findRegion(letter);
        this.position = new Vector2();
        setPosition(position);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(int[] position) {
        int[] clone = new int[position.length];
        System.arraycopy(position, 0, clone, 0, position.length);
        this.position.x = clone[0];
        this.position.y = clone[1];
        footPrint = clone;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
        currentLetter = atlas.findRegion(letter);
    }

    public void incrementLetter() {
        char c = letter.toCharArray()[0];
        if(c == 'z')
            c = 'a';
        else
            c++;
        setLetter(String.valueOf(c));
    }

    public void decrementLetter() {
        char c = letter.toCharArray()[0];
        if(c == 'a')
            c = 'z';
        else
            c--;
        setLetter(String.valueOf(c));
    }

    public void levitate() {
        levitationDelay += Gdx.graphics.getDeltaTime() / 2;
        if(levitationDelay > maxDelay) {
            if(levitationStatusUp) {
                if(levitationCounter < 3) {
                    levitationCounter++;
                    position.y += levitationAmount;
                    levitationDelay = 0;
                } else
                    levitationStatusUp = false;
            } else {
                if(levitationCounter >= 0) {
                    levitationCounter--;
                    position.y -= levitationAmount;
                    levitationDelay = 0;
                } else
                    levitationStatusUp = true;
            }
        }
    }

    public void draw(Batch batch) {
        batch.draw(currentLetter, position.x, position.y);
    }

    public int[] getFootPrint() {
        return footPrint;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

    @Override
    public void up() {
        position.x -= 24f;
        position.y -= 72f;
        footPrint[0] -= 24f;
        footPrint[1] -= 72f;
    }

    @Override
    public void down() {
        position.x += 24f;
        position.y += 72f;
        footPrint[0] += 24f;
        footPrint[1] += 72f;
    }
}
