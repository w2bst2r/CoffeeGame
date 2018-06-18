package com.fenestra.kahvalti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by balsu on 1.05.2017.
 */

public class Animate {

    float delay;
    float maxTime;

    public Texture curr;
    int index =0;

    Texture [] textures;
    boolean isLoop;


    public Animate(Texture [] textures,boolean isLoop,float maxTime){


        curr=textures[0];
        this.textures=textures;
        this.maxTime=maxTime;
        this.isLoop=isLoop;

    }


    public void animateMe(){

        delay += Gdx.graphics.getDeltaTime();
        if(delay > maxTime)
        {
            curr = textures [index++];
            delay=0;
        }

        if(index >= textures.length)
            if(isLoop)
                index=0;
            else
                index=textures.length - 1;
    }

    public void reset(){
        index=0;
    }


}
