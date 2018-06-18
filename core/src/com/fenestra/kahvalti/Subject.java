package com.fenestra.kahvalti;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by zamma on 09.05.2017.
 */

public interface Subject {
    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
    void upAllObservers();
    void downAllObservers();
    void disposeAllObservers();

}

