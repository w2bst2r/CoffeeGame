package com.fenestra.kahvalti.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fenestra.kahvalti.Kahvalti;

/**
 * Created by emremrah on 24.03.2017.
 */

public class MenuState extends State {

    private Texture background, loadingBackground1, bar1;
    private BitmapFont font;
    private int highScore;
    private boolean isPlaystateLoading = false;
    private LoadingState loadingState;
    private float deltaTime = 0;
    private boolean touchEnabled = false;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("menuBackground.png");
        loadingBackground1 = new Texture("loadingBackground1.png");
        font = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/AUdimat-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        parameter.color = new Color(144 / 255f, 202 / 255f, 249 / 255f, 1);
        font = generator.generateFont(parameter);
        generator.dispose();
        highScore = Kahvalti.cafeDB.highestScore();
        loadingState = new LoadingState();
    }

    @Override
    public void handleInput() {
        if (Gdx.input.justTouched()) {
            isPlaystateLoading = true;
        }
    }

    @Override
    public void update(float delta) {
        if (touchEnabled) handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        if (isPlaystateLoading) {
            deltaTime += Gdx.graphics.getDeltaTime();
            loadingState.render(batch);
            // Don't know why but, if we push a new state immediately, loading screen won't be rendering. So;
            if (deltaTime > .1) gsm.push(new PlayState(gsm));
        } else {
            batch.begin();
            batch.draw(background, -792, -1296);
            font.draw(batch, "Yüksek Skor : " + highScore, -350, 620);
            batch.end();
        }
        touchEnabled = true;
    }

    @Override
    public void dispose() {
        background.dispose();
        loadingBackground1.dispose();
        bar1.dispose();
        font.dispose();
    }
}
