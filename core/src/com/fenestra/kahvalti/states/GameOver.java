package com.fenestra.kahvalti.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fenestra.kahvalti.*;
import com.fenestra.kahvalti.database.Score;

/**
 * Created by emremrah on 14.04.2017.
 */

public class GameOver extends State {
    private Texture background;
    private BitmapFont font ;
    private Score currentScore;
    private int highScore;
    private boolean isPlaystateLoading = false;
    private LoadingState loadingState;
    private float deltaTime = 0;

    public GameOver(GameStateManager gsm, int score) {
        super(gsm);
        background = new Texture("gameOverBackground.png");
        font = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/AUdimat-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        parameter.color = new Color(144 / 255f, 202 / 255f, 249 / 255f, 1);
        font = generator.generateFont(parameter);
        generator.dispose();
        loadingState = new LoadingState();

        // save current score to the database
        currentScore = new Score(score);
        Kahvalti.cafeDB.insertData(currentScore);

        // Get the high score:
        highScore = Kahvalti.cafeDB.highestScore();
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            isPlaystateLoading = true;
        }
    }

    @Override
    protected void update(float delta) {
        handleInput();
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        if (isPlaystateLoading) {
            deltaTime += Gdx.graphics.getDeltaTime();
            loadingState.render(batch);
            // Don't know why but, if we push a new state immediately, loading screen won't be rendering. So;
            if (deltaTime > .1) gsm.push(new PlayState(gsm));
        } else {
            batch.begin();
            batch.draw(background, -792, -1296);
            font.draw(batch, "Score : " + currentScore.getScore(), -250 , -400);
            font.draw(batch, "Hi-Score : " + highScore, -325 , -500);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
