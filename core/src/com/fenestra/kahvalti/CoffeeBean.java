package com.fenestra.kahvalti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by balsu on 8.04.2017.
 */

public class CoffeeBean extends Sprite implements Observer {

    private enum Direction {
        up,
        down,
        none,
        left,
        right

    }

    private enum State {
        init,
        setup,
        run,
        idle
    }

    private State _state;

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    private Vector3 position;
    private Vector3 gravity;
    private Vector3 velocity;
    private boolean afk;

    public boolean isLanded() {
        return landed;
    }

    public void setLanded(boolean landed) {
        this.landed = landed;
    }

    private boolean landed;
    private boolean isWalk;

    private float hedefx, hedefy;
    private float afktimer;
    private int stairCounter;
    private Direction _directionY;
    private Direction _directionX;
    private TextureRegion tr;
    private Texture texR,texL;


    private int countRight =1;
    private int countLeft =0;


    private int texWidth=100, texHeight=150;

    private Texture [] texturesJumpRight;
    private Texture [] texturesIdle;

    private Animate animJR;
    private Animate animIdle;

    private Animate currAnim;

    private float denemeT =0;




    public CoffeeBean(float x, float y) {
        super();

        texR = new Texture("idle1.png");


        position = new Vector3(x, y, 0);
        this.setTexture(texR);
        tr = new TextureRegion(getTexture(), 0, 0, texWidth, texHeight);
        setBounds(position.x, position.y, texWidth , texHeight );
        setRegion(tr);



        afk = false;
        isWalk = false;


        hedefx = position.x;
        hedefy = position.y;

        _directionY = Direction.none;
        _directionX = Direction.right;
        _state = State.init;

        stairCounter = 2;

        afktimer = 0;


        texturesJumpRight = new Texture[3];
        texturesIdle = new Texture[2];

        //texturesJumpRight[0] = new Texture("idle1.png");
        texturesJumpRight[0] = new Texture("Jump-up.png");
        texturesJumpRight[1] = new Texture("Jump-fall.png");
        texturesJumpRight[2] = new Texture("idle1.png");

        texturesIdle[0]=new Texture("idle1.png");
        texturesIdle[1]=new Texture("idle2.png");

        animJR=new Animate(texturesJumpRight,false,.2f);
        animIdle=new Animate(texturesIdle,true,.2f);

        currAnim = animIdle;
        landed=true;


    }





    public void render(SpriteBatch batch){
        this.draw(batch);
    }


    public void Init() {
        _state = State.idle;
    }

    public boolean collide(Rectangle object) {
        return object.overlaps(this.getBoundingRectangle());
    }

    public void Setup() {

        landed = false;

        if ( _directionY == Direction.up) {

            hedefx = position.x + 144;
            hedefy = position.y + 72;

            stairCounter++;

            if (stairCounter > 5){
                stairCounter = 0;
                isWalk=true;
                if(countRight > 1)
                {
                    changeDirection();
                }

            }else if (countRight == 1){
                changeDirection();
                setHedefX();

            }else if(countRight > 1){
                setHedefX();
            }


        } else if (_directionY==Direction.down) {

            velocity = new Vector3(-95f, 0, 0);

            hedefx = position.x - 144;
            hedefy = position.y - 72;

            stairCounter--;
            if (stairCounter < 0  ){
                stairCounter = 5;
                isWalk=true;
                if(countLeft > 1)
                {
                    changeDirection();
                }
            }else if (countLeft == 1 ){
                changeDirection();
                setHedefX();
            }else if(countLeft > 1){
                setHedefX();
            }
        }

        currAnim=animJR;

        currAnim.reset();

        _state = State.run;
    }

    public void Action() {

        Walk(hedefx,hedefy);

        //afktimer = 0;
        if (landed) {
            position.x = hedefx;
            position.y = hedefy;
            setPosition(position.x,position.y);
            _state = State.idle;
        }


    }

    public void Idle() {

        velocity = new Vector3(0, 0, 0);
        gravity = new Vector3(0, 0, 0);
        //_directionY = Direction.none;

        currAnim=animIdle;
        denemeT=0;

    }

    public void Walk(float x,float y){


        if (!landed) {

            int walkSpeed=40;
            Vector3 dir = new Vector3(x-position.x,y-position.y,0).nor();
            position= new Vector3(position.x + walkSpeed*dir.x,position.y + walkSpeed*dir.y,0);
            setPosition(position.x, position.y);

            if (!afk && position.y > hedefy) {
                landed = true;
                isWalk=false;
                //System.out.println(denemeT);
            }else if(afk && position.y < hedefy){
                landed = true;
                isWalk=false;
                //System.out.println(denemeT);
            }
        }

    }


    public void MoveParabolic(float x, float y) {

        //System.out.println("I'm mowin like hell");

        denemeT += Gdx.graphics.getDeltaTime();

        Vector3 step;


        if (!landed) {
            step = new Vector3(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime(), 0);
            velocity.y = velocity.y + gravity.y * Gdx.graphics.getDeltaTime() ;
            position.add(step);

            setPosition(position.x, position.y);

            if (velocity.y < 0 && position.y <= hedefy ) {
                landed = true;
                //System.out.println(denemeT);
            }

        }
    }


    public void setHedefX () {
        if(_directionX == Direction.left){
            hedefx = position.x - 144;
        }
        else if(_directionX == Direction.right) {
            hedefx = position.x + 144;
        }
    }

    public void changeDirection(){
        if( _directionX == Direction.left){
            _directionX=Direction.right;

        } else if(_directionX == Direction.right){

            _directionX=Direction.left;
        }
        this.flip(true,false);
    }

    @Override
    public void up() {
        _directionY = CoffeeBean.Direction.up;
        afk = false;
        countRight++;
        countLeft = 0;
        _state = State.setup;

        position.x -= 24f;
        position.y -= 72f;
        hedefx -=24 ;
        hedefy -=72 ;
        setPosition(position.x,position.y);
    }

    @Override
    public void down() {
        _directionY = CoffeeBean.Direction.down;
        afk = false;
        countRight =0;
        countLeft++;
        _state = State.setup;

        position.x += 24f ;
        position.y += 72f ;
        hedefx +=24 ;
        hedefy +=72 ;
        setPosition(position.x,position.y);
    }

    public void update() {

        currAnim.animateMe();
        setTexture(currAnim.curr);
        this.setPosition(position.x,position.y);

        switch (_state) {
            case init:
                Init();
                break;
            case setup:
                Setup();
                break;
            case run:
                Action();
                break;
            case idle:
                Idle();
                break;
            default:
                break;

        }

    }

    public boolean isFacedRight() {
        return _directionX == Direction.right;
    }

    @Override
    public void dispose() {
        texR.dispose();
        for(Texture t : texturesJumpRight)
            t.dispose();
        for(Texture t : texturesIdle)
            t.dispose();
    }

}
