package com.fenestra.kahvalti.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by emremrah on 24.03.2017.
 */

/*
    GSM manages a stack of game states. The state which on top of the stack will be rendering and
    updating.
*/

public class GameStateManager {

    private Stack<State> states;

    public GameStateManager() {
        states = new Stack<State>();
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop() {
        states.pop().dispose();
    }

    //When we want to pop and instantly push a new state, here is "set" method.
    public void set(State state) {
        pop();
        push(state);
    }

    //With the peek method, the state which is on top of the stack can be updated and rendered.
    public void update(float delta) {
        states.peek().update(delta);
    }

    public void render(SpriteBatch spriteBatch) {
        states.peek().render(spriteBatch);
    }

    public void resize(int width, int height) {
        states.peek().resize(width, height);
    }

}
