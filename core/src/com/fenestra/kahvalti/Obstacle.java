package com.fenestra.kahvalti;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by karim on 5/19/17.
 */

public class Obstacle {


    private static final float BOUNDS_RADIUS = 30f;
    private static final float SIZE = 2* BOUNDS_RADIUS;

    private float ySpeed = 10f;
    private float x = 0;
    private float y = 0;

    private Rectangle bounds;
//    private Circle bounds;

    public Obstacle(){
//        bounds = new Circle(x , y, BOUNDS_RADIUS);
        bounds = new Rectangle(x,y,50,50);
    }

    public void drawDebug( ShapeRenderer renderer){
//        renderer.circle(bounds.x , bounds.y , bounds.radius, 30);
        renderer.rect(bounds.x , bounds.y , 50 ,50);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
        updateBounds();
    }

    public void update(){
        setPosition(x, y -ySpeed);      //obstacle will appear on top then move down
    }

    public void updateBounds(){
        bounds.setPosition(x,y);
    }

    public boolean isPlayerColliding(CoffeeBean player){
        Rectangle playerBounds = player.getBoundingRectangle();
        return Intersector.overlaps(playerBounds, bounds);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static float getWidth() {
        return SIZE;
    }


}
