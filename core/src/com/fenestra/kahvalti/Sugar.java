package com.fenestra.kahvalti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by balsu on 8.04.2017.
 */

public class Sugar extends Sprite implements Observer {

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
    private Vector3 position;
    private Vector3 gravity;
    private Vector3 velocity;
    private boolean afk;
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


    private int texWidth=100, texHeight=100;

    private Texture [] texturesJumpRight;
    private Texture [] texturesIdle;

    private float sugTimer = 1;

    private int degree = 0;
    private int targetDegree=0;
    private Animate animJR;
    private Animate animIdle;
    private Animate currAnim;

    public Sugar(float x, float y) {
        super();

        texR = new Texture("sugar.png");


        position = new Vector3(x, y, 0);
        this.setTexture(texR);
        tr = new TextureRegion(getTexture(), 0, 0, texWidth, texHeight);
        setBounds(position.x, position.y, texWidth * 4/5 , texHeight * 4/5);
        setRegion(tr);



        afk = true;
        isWalk = true;


        hedefx = position.x;
        hedefy = position.y;

        _directionY = Direction.none;
        _directionX = Direction.right;
        _state = State.init;

        stairCounter = 2;

        afktimer = 0;


        texturesJumpRight = new Texture[3];
        texturesIdle = new Texture[2];

        texturesJumpRight[0] = new Texture("sugar.png");
        texturesJumpRight[1] = new Texture("sugar.png");
        texturesJumpRight[2] = new Texture("sugar.png");

        texturesIdle[0]=new Texture("sugar.png");
        texturesIdle[1]=new Texture("sugar.png");

        animJR=new Animate(texturesJumpRight,false,.2f);
        animIdle=new Animate(texturesIdle,true,.2f);

        currAnim = animIdle;
        landed=false;

        this.setOrigin(50,50);
    }


    public void resetPosition() {
        position.x = 617 + 144 * 4;
        position.y = 950 + 72 * 24;
        _state = Sugar.State.init;
        stairCounter = 2;
        _directionX = Sugar.Direction.left;
    }

    public Vector3 getPosition() {
        return position;
    }


    public void render(SpriteBatch batch){
        this.draw(batch);
    }


    public void Init() {
        _state = State.idle;
    }

    public void Setup() {


        landed = false;
        hedefx = position.x - 144;
        hedefy = position.y - 72;
        stairCounter--;
        if (stairCounter < 0) {

            stairCounter = 5;
            if (countLeft > 1) {
                changeDirection();
            }
        } else {
            if (countLeft == 1) {
                changeDirection();
                setHedefX();
            } else if (countLeft > 1) {
                setHedefX();
            }
        }

        if(stairCounter == 5){
            if (_directionX == Direction.left) {
                targetDegree += 90;
            } else if (_directionX == Direction.right) {
                targetDegree += 90;
            }
        }
        else {
            if (_directionX == Direction.left) {
                targetDegree += 90;
            } else if (_directionX == Direction.right) {
                targetDegree -= 90;
            }
        }




        currAnim=animJR;
        currAnim.reset();
        _state = State.run;
    }

    public void Action() {

        Walk(hedefx,hedefy);

        if (landed) {
            position.x = hedefx;
            position.y = hedefy;
            setPosition(position.x,position.y);
            _state = State.idle;
            sugTimer = 0.25f;
        }

    }

    public void Idle() {

        velocity = new Vector3(0, 0, 0);
        gravity = new Vector3(0, 0, 0);

        currAnim=animIdle;

        sugTimer -= Gdx.graphics.getDeltaTime();
        if(sugTimer <= 0) {
            countLeft++;
            _state = State.setup;
        }
    }

    public void Walk(float x,float y){

        if(stairCounter == 5 ){
            if(_directionX == Direction.left )
                degree += 90 / 15;
            if(_directionX == Direction.right )
                degree += 90 / 15;
        }
        else {
            if (_directionX == Direction.left)
                degree += 90 / 15;
            else if (_directionX == Direction.right)
                degree -= 90 / 15;
        }

        if(stairCounter != 5)
        {
            if(_directionX == Direction.left && degree >= targetDegree) {
                degree = targetDegree;
            }
            else if(_directionX == Direction.right && degree <= targetDegree) {
                degree = targetDegree;
            }
        }
        else{
            if(degree >= targetDegree)
                degree = targetDegree;
        }
        setRotation(degree);



        if (!landed) {

            int walkSpeed=10;
            Vector3 dir = new Vector3(x-position.x,y-position.y,0).nor();
            position= new Vector3(position.x + walkSpeed*dir.x,position.y + walkSpeed*dir.y,0);
            setPosition(position.x, position.y);



            if(position.y < hedefy){
                landed = true;
                isWalk=false;
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

    private void changeDirection(){
        if( _directionX == Direction.left){
            _directionX=Direction.right;
            position.y -= 10;

        } else if(_directionX == Direction.right){

            _directionX=Direction.left;
            position.y += 10;
        }
        this.flip(true,false);
    }

    @Override
    public void up() {
        position.x -= 24f;
        position.y -= 72f;
        hedefx -=24 ;
        hedefy -=72 ;
        setPosition(position.x,position.y);
    }

    @Override
    public void down() {
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
    

    @Override
    public void dispose() {
        texR.dispose();
        for(Texture t : texturesJumpRight)
            t.dispose();
        for(Texture t : texturesIdle)
            t.dispose();
    }
}
