package com.fenestra.kahvalti.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by emremrah on 12.06.2017.
 */

public class LoadingState {

    private Texture loadingBackground, loadingBar;
    private BitmapFont font;

    public LoadingState() {
        loadingBackground = new Texture("loadingBackground1.png");
        font = new BitmapFont();
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/AUdimat-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 120;
        fontParameter.color = new Color(Color.GOLDENROD);
        font = fontGenerator.generateFont(fontParameter);
        fontGenerator.dispose();
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(loadingBackground, -792, -1296);
        font.draw(batch, "YÜKLENiYOR", -275, -190);
        batch.end();
    }
}

