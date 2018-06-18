package com.fenestra.kahvalti.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.fenestra.kahvalti.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static com.fenestra.kahvalti.Kahvalti.WORLD_WIDTH;
import static com.fenestra.kahvalti.Kahvalti.mainMusics;


/**
 * Created by emremrah on 26.03.2017.
 */

public class PlayState extends State implements Subject {
    // Filler shape things.
    private ShapeRenderer fillerShapeRenderer = new ShapeRenderer();
    private final Color fillerColor = new Color(71 / 255f, 35 / 255f, 22 / 255f, 1);

    // Word holder shape things.
    private ShapeRenderer wordHolderShapeRenderer = new ShapeRenderer();
    private final int wordHolderShapeOffset = 50;
    private boolean isProjectionMatrixSet = false;
    private final Color wordHolderColor = new Color(233 / 255f, 30 / 255f, 99 / 255f, 1);

    //Stair control.
    private Stairs[] stairs = new Stairs[5];
    private StairsState stairsState;
    private long numberOfFloors = 0;

    private enum StairsState {
        halt,
        up,
        down
    }

    //General purpose.
    private ArrayList<Observer> observers;
    private CoffeeBean bean;
    private Sugar sugar;
    private Random random = new Random();
    private int score = 0;
    private boolean isMusicPlaying = false;

    //GUI & Font elements.
    private GlyphLayout glyphLayout;
    private BitmapFont scoreFont, collectedLetterFont, currentCoffeeNameFont;
    private Texture caffeineFrame = new Texture(Gdx.files.internal("caffeine_bar/frame.png"));
    private Texture caffeineFluid = new Texture(Gdx.files.internal("caffeine_bar/fluid.png"));
    private Texture caffeineGlow = new Texture(Gdx.files.internal("caffeine_bar/glow.png"));
    private Texture caffeineText = new Texture(Gdx.files.internal("caffeine_bar/kafein.png"));
    private Texture caffeineSafety = new Texture(Gdx.files.internal("caffeine_bar/safety.png"));
    private Texture ghostMode = new Texture("ghostup.png"); //For ghostifying button.
    private Sprite ghost = new Sprite(ghostMode);

    //Letter control.
    private Letter[] lettersOnStairs = {
            new Letter("i", Stairs.stairMapping[2][3]),
            new Letter("a", Stairs.stairMapping[3][2]),
            new Letter("z", Stairs.stairMapping[4][2]),
            new Letter("a", Stairs.stairMapping[8][1]),
            new Letter("w", Stairs.stairMapping[9][1]),
            //new Letter("o", Stairs.stairMapping[7][1]),
            //new Letter("a", Stairs.stairMapping[6][1]),
    };
    private Letter dummyLetter;
    private static final String[] allLetters = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z"
    };
    private int letterOffsetCounter = 0;
    private final int maxOffsetCount = 12;

    // Letter collection.
    private String collectedLetters = "";
    private String currentCoffeeName;
    private int coffeePointer = 0;
    private int coffeeLetterPointer = 0;
    private Letter letterToBeAnimated;
    private boolean collectAnimationEnabled = false;

    //Caffeine control.
    private boolean trig=true;
    private final int maxCaffeineAmount = 717; // Height of the caffeine bar.
    private float caffeine = 0;
    private boolean isBeanGhost = false;
    private boolean isCaffeineFull = false;

    //SFX stuff.
    private Music teaspoon = Gdx.audio.newMusic(Gdx.files.internal("sounds/teaspoon.mp3"));
    private Music letterCollect = Gdx.audio.newMusic(Gdx.files.internal("sounds/noiseforfun.com-letter.wav"));
    private Music wordCompleted = Gdx.audio.newMusic(Gdx.files.internal("sounds/noiseforfun.com-word.wav"));
    private Music ghostUp = Gdx.audio.newMusic(Gdx.files.internal("sounds/noiseforfun.com-ghostup.wav"));
    private Music deathBubble = Gdx.audio.newMusic(Gdx.files.internal("sounds/deathBubble.mp3"));
    private Music criticalBubble = Gdx.audio.newMusic(Gdx.files.internal("sounds/criticBubble.mp3"));

    // Bean - Sugar collision.
    private boolean isSugarCollisionEnabled = true;
    private float sugarTimer = 0;
    private int sugarVisibleTime;
    private boolean collisionEnabled = true;

    // Flood variables.
    private Flood flood;

    public PlayState(GameStateManager gsm) {
        // Initial setup.
        super(gsm);

        // Set the camera & viewport.
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize observer list.
        observers = new ArrayList<Observer>();

        // Set the stairs up.
        initStairs();

        // Instantiate a bean, a sugar and a flood.
        bean = new CoffeeBean(617, 950);
        sugar = new Sugar(-500, -5000);
        flood = new Flood();

        // Set a random value for sugar visibility intervals.
        sugarVisibleTime = random.nextInt(15) + 15;

        // Set position of the ghost icon above the caffeine bar.
        ghost.setPosition(60, 2235);

        // Set the current coffee name to collect its letters.
        currentCoffeeName = Kahvalti.coffeeNames.get(coffeePointer);

        // Initialize the dummy letter above the bean (in fact, above the screen).
        // This is important since we are incrementing the letter just above the bean,
        // if there is no letter on the stairs above the bean, then there is this dummy letter.
        // Although it will never show up, it will handle the situation for a short time until
        // a letter shows up on a stairs.
        dummyLetter = new Letter("dummy", new int[]{-1000, Kahvalti.WORLD_HEIGHT + 1000});

        // Letter randomization.
        for(Letter l : lettersOnStairs) {
            l.setLetter(allLetters[random.nextInt(allLetters.length - 1)]);
            int[] pos = {(random.nextInt(7) + 2), random.nextInt(4)};
            boolean occupied = false;
            for (Letter le : lettersOnStairs) {
                if (Arrays.equals(le.getFootPrint(), Stairs.stairMapping[pos[0]][pos[1]])) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) {
                l.setPosition(Stairs.stairMapping[pos[0]][pos[1]]);
            }
        }

        // Register observers:
        this.registerObserver(bean);
        this.registerObserver(sugar);
        this.registerObserver(flood);
        for (Letter le : lettersOnStairs) {
            this.registerObserver(le);
        }
        for(Stairs s : stairs)
            this.registerObserver(s);

        // Score text.
        scoreFont = new BitmapFont();
        FreeTypeFontGenerator scoreFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/Pacifico.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter scoreFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        scoreFontParameter.size = 100;
        scoreFontParameter.color = Color.GOLDENROD;
        scoreFont = scoreFontGenerator.generateFont(scoreFontParameter);
        scoreFontGenerator.dispose();

        /* Current word and collected letters properties. */
        // You can change size and color here. Word holder shape will set itself accordingly.
        collectedLetterFont = new BitmapFont();
        FreeTypeFontGenerator wordHolderFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/AUdimat-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter wordHolderFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        wordHolderFontParameter.size = 100;
        // For collected letters:
        wordHolderFontParameter.color = new Color(233 / 255f, 249 / 255f, 144 / 255f, 1);
        collectedLetterFont = wordHolderFontGenerator.generateFont(wordHolderFontParameter);
        // For the current word:
        wordHolderFontParameter.color = new Color(144 / 255f, 202 / 255f, 249 / 255f, 1);
        currentCoffeeNameFont = wordHolderFontGenerator.generateFont(wordHolderFontParameter);
        wordHolderFontGenerator.dispose();
        glyphLayout = new GlyphLayout();

        //One-off sound initialization.
        teaspoon.setLooping(false);
        teaspoon.setVolume(0.5f);
        teaspoon.play();
        ghostUp.setVolume(0.15f);
        letterCollect.setVolume(0.3f);
        wordCompleted.setVolume(0.6f);
        deathBubble.setVolume(0.7f);
        criticalBubble.setVolume(0.6f);
        keepMusic();    // This is necessary to avoid null object reference
        Kahvalti.getCurrentMusic().setVolume(0.75f);
    }

    @Override
    protected void handleInput() {
        // Does player touch the screen and is bean able to move?
        if(Gdx.input.justTouched() && bean.isLanded()) {
            // Increment caffeine faster when user touches the screen.
            if (!isBeanGhost) caffeine += 10;
            // Get the input coordinates and unproject them.
            Vector3 mouseCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mouseCoordinates);

            // If the player touched the right side of the screen,
            if (mouseCoordinates.x > Kahvalti.WORLD_WIDTH / 2) {
                clickedRight();

            // else if, the player touched around the caffeine bar,
            } else if (mouseCoordinates.x < Kahvalti.WORLD_WIDTH / 4 &&
                    mouseCoordinates.y > (Kahvalti.WORLD_HEIGHT * 2) / 3) {
                fireTheCaffeine();
            }

            // else if, the player touched the left side of the screen.
            else if (mouseCoordinates.x < Kahvalti.WORLD_WIDTH / 2 &&
                    stairs[0].getPosition().y < 0) {
                clickedLeft();
            }
        }
    }

    @Override
    protected void update(float delta) {
        // First thing is to handle the input.
        handleInput();

        /* Secondly, check word-letter issues. */
        checkForLevelUp();

        // Randomize letters.
        replaceLetters();

        // Add some sugar to the game.
        addSomeSugar(delta);

        // Stairs related issues.
        switch (stairsState) {
            case up:
                upStairs();
                break;
            case down:
                downStairs();
                break;
        }

        // Update flood level.
        updateFlood(delta);

        // Check for collisions.
        if (bean.getPosition().y > sugar.getPosition().y) isSugarCollisionEnabled = false;
        checkCollisions();

        // Ghostifying the bean && caffeine related things.
        updateCaffeine();

        // Main music control & randomization
        keepMusic();

        // Play bubble sound if our hero is going to di.. I can't say it.
        if (flood.getLevel() > -1700 && flood.getLevel() < bean.getPosition().y + bean.getTexture().getHeight()) {
            criticalBubble.play();
        }

        // Set glyphLayout's text. This is necessary for text measurements.
        glyphLayout.setText(currentCoffeeNameFont, currentCoffeeName);

        checkGameOver();
        bean.update();
        sugar.update();
        camera.update();
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);

        // fillerShapeRenderer draws the right side filler rectangle.
        fillerShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        fillerShapeRenderer.setColor(fillerColor);
        fillerShapeRenderer.rect(Gdx.graphics.getWidth() / 2 + 50, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        fillerShapeRenderer.end();

        // Here, we are drawing stairs, score text, bean, sugar and letters.
        batch.begin();
        for (Stairs s : stairs)
            batch.draw(s.getTexture(), s.getPosition().x, s.getPosition().y);
        scoreFont.draw(batch, "Skor: " + score, 10, 2580);
        for (Letter l : lettersOnStairs) {
            l.draw(batch);
        }
        bean.draw(batch);
        sugar.draw(batch);
        batch.end();
        if (collectAnimationEnabled) collect(letterToBeAnimated, batch);

        //Draw Flood
        flood.render(batch);

        // Draw the word holder to the bottom of the screen.
        drawWordHolder(batch);

        // Draw current coffee name to collect its letters and currently collected letters as string.
        drawWordAndCollectedLetters(batch);

        // Draw the caffeine bar and ghost icon.
        drawCaffeineBar(batch);
    }

    private void clickedRight() {
        if (!isBeanGhost) {
            // Increment the bottom-most letter above the bean. With the help of dummyLetter.
            Letter lowestLetter = dummyLetter;
            for(Letter l : lettersOnStairs) {
                if(l.getPosition().y < bean.getPosition().y)
                    continue;
                if(l.getPosition().y < lowestLetter.getPosition().y)
                    lowestLetter = l;
            }
            lowestLetter.incrementLetter();
        }
        stairsState = StairsState.up;
        upAllObservers();
    }

    private void clickedLeft () {
        stairsState = StairsState.down;
        downAllObservers();
    }

    private void fireTheCaffeine() {
        trig = false;
        isBeanGhost = true;
        ghostUp.setVolume(0.15f);
        ghostUp.play();
        isCaffeineFull = false;
        bean.setAlpha(0.75f);
    }

    private void replaceLetters() {
        // TODO: Discrete arrays for letters on the stairs and letters under the screen.
        //Letter randomization.
        for (Letter l : lettersOnStairs) {
            // If a letter is below the bounds of the screen and
            // stairs placement is in a position that is stable (namely,
            // identical to initial position), then move the letter to
            // an appropriate stairs above the screen.
            if (l.getPosition().y < 0 && letterOffsetCounter == maxOffsetCount) {
                l.setLetter(allLetters[random.nextInt(allLetters.length - 1)]);
                int[] pos = {(random.nextInt(7) + 2), random.nextInt(4)};
                boolean occupied = false;
                for (Letter le : lettersOnStairs) {
                    if (Arrays.equals(le.getFootPrint(), Stairs.stairMapping[pos[0]][pos[1]])) {
                        occupied = true;
                        break;
                    }
                }
                if (!occupied) {
                    l.setPosition(Stairs.stairMapping[pos[0]][pos[1]]);
                }
            }
        }
    }

    private void checkForLevelUp() {
        // If coffeePointer points to the last word of the game and
        // coffeeLetterPointer also points out the last letter of that word and
        // collectedLetters compose that coffeeName
        // than the game is successfully over.
        if (coffeePointer == Kahvalti.coffeeNames.size() - 1 &&
                coffeeLetterPointer == Kahvalti.coffeeNames.get(coffeePointer).length() - 1 &&
                collectedLetters.equals(currentCoffeeName)
                ) {
            gsm.push(new GameOver(gsm, score));
        }

        // If bean collected all the letters necessary for this word, level up.
        if (collectedLetters.equals(currentCoffeeName)) {
            // Increment coffeePointer and update currentCoffeeName.
            currentCoffeeName = Kahvalti.coffeeNames.get(++coffeePointer);

            // Reset coffeeLetterPointer.
            coffeeLetterPointer = 0;

            // Reset collectedLetters.
            collectedLetters = "";

            // Increment score by 10.
            score += 10;

            // Decrement flood level by 500.
            flood.updateLevel(-500f);

            // Play word completion sound.
            wordCompleted.play();
        }
    }

    private void updateFlood(float delta) {
        flood.updateLevel(.5f);
    }

    private void collect(Letter letter, SpriteBatch batch) {
        float diffX = letter.getPosition().x - Kahvalti.WORLD_WIDTH / 2;
        float diffY = letter.getPosition().y - 50;
        float deltaTime = 0;
        while (letter.getPosition().y >= 50) {
            deltaTime += Gdx.graphics.getDeltaTime();
            if (deltaTime > .3) {
                if (diffX <= 0) {
                    letter.setPosition(letter.getPosition().x + Math.abs(diffX) / 8, letter.getPosition().y - diffY / 8);
                } else if (diffX > 0) {
                    letter.setPosition(letter.getPosition().x - Math.abs(diffX) / 8, letter.getPosition().y - diffY / 8);
                }
                batch.begin();
                letter.draw(batch);
                batch.end();
                deltaTime = 0;
            }
        }
        collectAnimationEnabled = false;
        letter.setPosition(-500, -5000);
    }


    private void checkCollisions() {
        // Check for bean - letter collision.
        for (Letter l : lettersOnStairs) {
            if (bean.getPosition().y <= l.getPosition().y &&
                    bean.getPosition().y + 50 >= l.getPosition().y) {
                // Collect the letter only if it is the next letter our bean needs.
                String tempSafeLetter = l.getLetter();
                if (tempSafeLetter.contains("i"))
                    tempSafeLetter = tempSafeLetter.replace("i", "I");
                if (tempSafeLetter.toUpperCase().equals(String.valueOf(currentCoffeeName.charAt(coffeeLetterPointer)))) {
                    // Collect the letter:
                    letterToBeAnimated = l;
                    collectAnimationEnabled = true;
                    // There is an issue with capital i. Below collects the letter + fixes the problem.
                    collectedLetters += !l.getLetter().equals("i") ? l.getLetter().toUpperCase() : 'I';

                    // Increment coffeeLetterPointer
                    coffeeLetterPointer++;

                    // If the next letter pointed by coffeeLetterPointer is a space,
                    // char at coffeeLetterPointer is not null unless bean completes the word
                    // so we are surrounding if with try-catch.
                    try {
                        if (currentCoffeeName.charAt(coffeeLetterPointer) == ' ') {
                            // then, increment coffeeLetterPoiner again.
                            coffeeLetterPointer++;
                            collectedLetters += " ";
                        }
                    } catch(StringIndexOutOfBoundsException e) {
                        System.out.println("Next word...");
                    }

                    // Throw it to somewhere very deep.
//                    l.setPosition(-500, -5000);

                    // Increment the score:
                    score++;

                    // Decrement the flood a little bit.
                    flood.updateLevel(-40f);

                    //Play the sfx
                    letterCollect.play();
                }
            }
        }

        // Bean - Sugar Collision.
        if (!isBeanGhost && bean.collide(sugar.getBoundingRectangle()) && isSugarCollisionEnabled) {
            isSugarCollisionEnabled = false;
            // If there is at least one letter collected,
            if(!collectedLetters.equals("")) {
                // then, remove the last letter collected.
                collectedLetters = collectedLetters.substring(0, collectedLetters.length() - 1);

                // decrement coffeeLetterPointer.
                coffeeLetterPointer--;

                // If the last letter of collectedLetters became " " after the decrement done above,
                try {
                    if (currentCoffeeName.charAt(coffeeLetterPointer) == ' ') {
                        // then, decrement coffeeLetterPointer again.
                        coffeeLetterPointer--;

                        // Remove that " ".
                        collectedLetters = collectedLetters.substring(0, collectedLetters.length() - 1);
                    }
                } catch(StringIndexOutOfBoundsException e) {
                    System.out.println("Previous word...");
                }

                // Decrement the score by one.
                score--;
            } else
                gsm.push(new GameOver(gsm, score));
        } else if(Math.abs(bean.getPosition().y - sugar.getPosition().y) > 150 && bean.getPosition().y < sugar.getPosition().y) {
            // This condition is the same as "bean.getPosition().y - sugar.getPosition().y) < -150"
            isSugarCollisionEnabled = true;
        }
    }

    private void addSomeSugar(float delta) {
        sugarTimer += delta;
        if(sugar.getPosition().y < 0 && letterOffsetCounter == maxOffsetCount &&
                sugarTimer > sugarVisibleTime) {
            sugar.resetPosition();
            sugarTimer = 0;
            sugarVisibleTime = random.nextInt(15) + 15;
        }
    }

    private void updateCaffeine() {
        if (caffeine == 0 && ghostUp.isPlaying()) ghostUp.setVolume(0);
        if (trig)
            caffeine += 1;
        else {
            caffeine -= 8;
            isCaffeineFull = false;
        }

        if (caffeine >= maxCaffeineAmount) {
            caffeine = maxCaffeineAmount;
            isCaffeineFull = true;
        } else if (caffeine <= 0) {
            caffeine = 0;
            isCaffeineFull = false;
            trig=true;
            isBeanGhost = false;
            bean.setAlpha(1);
        }
        ghost.setAlpha(caffeine / 1000);
    }

    private void drawWordHolder(SpriteBatch batch) {
        // Don't ever try to read the code. It just draws a container for collected letters text.
        batch.begin();
        if(!isProjectionMatrixSet) {
            wordHolderShapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            isProjectionMatrixSet = true;
        }
        wordHolderShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        wordHolderShapeRenderer.setColor(wordHolderColor);

        wordHolderShapeRenderer.rect(
                WORLD_WIDTH / 2 - (glyphLayout.width + wordHolderShapeOffset) / 2,
                50 - (wordHolderShapeOffset / 2),
                glyphLayout.width + wordHolderShapeOffset,
                glyphLayout.height + wordHolderShapeOffset
        );
        wordHolderShapeRenderer.circle(
                (WORLD_WIDTH / 2 - (glyphLayout.width + wordHolderShapeOffset) / 2) + wordHolderShapeOffset / 2 - 10,
                50 - (wordHolderShapeOffset / 2) + (glyphLayout.height + wordHolderShapeOffset) / 2,
                (glyphLayout.height + wordHolderShapeOffset) / 2) ;
        wordHolderShapeRenderer.circle(
                (WORLD_WIDTH / 2 - (glyphLayout.width + wordHolderShapeOffset) / 2) + glyphLayout.width + wordHolderShapeOffset / 2 + 10,
                50 - (wordHolderShapeOffset / 2) + (glyphLayout.height + wordHolderShapeOffset) / 2,
                (glyphLayout.height + wordHolderShapeOffset) / 2) ;
        wordHolderShapeRenderer.end();
        batch.end();
    }

    private void drawWordAndCollectedLetters(SpriteBatch batch) {
        batch.begin();
        // Current word.
        currentCoffeeNameFont.draw(
                batch,
                currentCoffeeName,
                Kahvalti.WORLD_WIDTH / 2 - glyphLayout.width / 2,
                glyphLayout.height + 50
        );

        // Collected letters.
        collectedLetterFont.draw(
                batch,
                collectedLetters,
                WORLD_WIDTH / 2 - glyphLayout.width / 2,
                glyphLayout.height + 50
        );
        batch.end();
    }

    private void drawCaffeineBar(SpriteBatch batch) {
        batch.begin();
        batch.draw(caffeineFluid, 40, 1500, caffeineFluid.getWidth(), caffeine);
        batch.draw(caffeineText, 40, 1500, caffeineText.getWidth(), caffeineText.getHeight());
        batch.draw(caffeineGlow, 40, 1500, caffeineGlow.getWidth(), caffeineGlow.getHeight());
        batch.draw(caffeineFrame, 40, 1500, caffeineFrame.getWidth(), caffeineFrame.getHeight());
        batch.draw(caffeineSafety, 40, 1500, caffeineSafety.getWidth(), caffeineSafety.getHeight());
        ghost.draw(batch);
        batch.end();
    }

    private void keepMusic() {
        for (Music m : Kahvalti.mainMusics) {
            if (m.isPlaying()) {
                isMusicPlaying = true;
                break;
            } else isMusicPlaying = false;
        }
        if (!isMusicPlaying) {
            Kahvalti.mainMusics[random.nextInt(2)].play();
        }
    }

    @Override
    public void dispose() {
        disposeAllObservers();
        teaspoon.dispose();
        letterCollect.dispose();
        wordCompleted.dispose();
    }

    private void initStairs() {
        stairsState = StairsState.halt;
        stairs[0] = new Stairs(0, 0, true);
        stairs[1] = new Stairs(288, 1224, false);
        stairs[2] = new Stairs(576, 2088, false);
        stairs[3] = new Stairs(864, 2952, false);
        stairs[4] = new Stairs(1152, 3816, false);
        stairs[4].setIsTop(true);
    }

    // Method for appending the bottom-most stairs to the top.
    private void upStairs() {
        for(Stairs s : stairs) {
            if (s.getPosition().y <= -936f && s != stairs[0]) {
                s.setPosition(720, 2520);
                Stairs oldTopStairs = getTopStairs();
                oldTopStairs.setIsTop(false);
                s.setIsTop(true);
                numberOfFloors++;
                break;
            }
        }
    }

    // Method for prepending the top-most stairs to the bottom.
    private void downStairs() {
        Stairs topStairs = getTopStairs();
        if(topStairs.getPosition().y == 2592 &&
                stairs[0].getPosition().y != 0 &&
                stairs[0].getPosition().y != -1224) {
            topStairs.setPosition(-408, -864);
            topStairs.setIsTop(false);
            numberOfFloors--;
            setTopStairs();
        }
    }

    private Stairs getTopStairs() {
        for(Stairs s : stairs)
            if(s.getIsTop())
                return s;
        return null;
    }

    private void setTopStairs() {
        Stairs tmp = stairs[1];
        for(int i = 2; i < stairs.length; i++) {
            if(stairs[i].getPosition().y > tmp.getPosition().y)
                tmp = stairs[i];
        }
        tmp.setIsTop(true);
    }

    private void checkGameOver() {
        if (bean.getPosition().y + bean.getHeight() < flood.getPosition().y + flood.getFloodTexture().getHeight()) {
            if (criticalBubble.isPlaying()) criticalBubble.stop();
            if (Kahvalti.getCurrentMusic() != null) {
                    Kahvalti.getCurrentMusic().pause();
            }
            deathBubble.play();
            gsm.push(new GameOver(gsm, score));
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void upAllObservers() {
        for(Observer o : observers) {
            o.up();
        }
        if(letterOffsetCounter < maxOffsetCount)
            letterOffsetCounter++;
        else
            letterOffsetCounter = 1;
    }

    @Override
    public void downAllObservers() {
        for(Observer o : observers) {
            o.down();
        }
        if(letterOffsetCounter > 1)
            letterOffsetCounter--;
        else
            letterOffsetCounter = maxOffsetCount;
    }

    @Override
    public void disposeAllObservers() {
        for(Observer o : observers)
            o.dispose();
    }
}

