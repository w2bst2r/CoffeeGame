package com.fenestra.kahvalti;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fenestra.kahvalti.database.CafeDB;
import com.fenestra.kahvalti.database.Score;
import com.fenestra.kahvalti.states.GameOver;
import com.fenestra.kahvalti.states.GameStateManager;
import com.fenestra.kahvalti.states.MenuState;
import com.fenestra.kahvalti.states.PlayState;
import com.badlogic.gdx.sql.*;

import java.util.ArrayList;


public class Kahvalti extends ApplicationAdapter {
	public static final int WORLD_WIDTH = 1584;
	public static final int WORLD_HEIGHT = 2592;
	public static Music[] mainMusics;

	public static final String title = "Kahvaltı";
	private SpriteBatch batch;
	private GameStateManager gsm;
	public static CafeDB cafeDB;

    // Coffee names list.
    public static ArrayList<String> coffeeNames;

	@Override
	public void create () {
        cafeDB = new CafeDB();

        // Get the coffee names list.
        coffeeNames = cafeDB.getCafeList();

		batch = new SpriteBatch();
		gsm = new GameStateManager();


        // Initialize musics.
        mainMusics = new Music[] {
                Gdx.audio.newMusic((Gdx.files.internal("sounds/supermilk-racetomars.mp3"))),
                Gdx.audio.newMusic((Gdx.files.internal("sounds/bensound.com/bensound-buddy.mp3"))),
                Gdx.audio.newMusic((Gdx.files.internal("sounds/bensound.com/bensound-littleidea.mp3")))
        };

		for (Music m : mainMusics) {
			m.setLooping(false);
            m.setVolume(0.75f);
        }

		Gdx.gl.glClearColor(150 / 255f, 120 / 255f, 100 / 255f, 1);
		gsm.push(new MenuState(gsm));
		//gsm.push(new PlayState(gsm));

    }

    public static Music getCurrentMusic() {
        for (Music m : mainMusics) {
            if (m.isPlaying()) return m;
        }
        return null;
    }

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	@Override
	public void resize(int width, int height) {
		gsm.resize(width, height);
	}

	@Override
	public void dispose () {
		super.dispose();
		cafeDB.closeDatabase();
	}
}
