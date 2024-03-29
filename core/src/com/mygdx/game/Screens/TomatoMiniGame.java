package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Game.LOD;
import com.mygdx.game.Interfaces.Subscriber;
import com.mygdx.game.Logic.MyTimer;
import com.mygdx.game.Tools.Constants;
import com.mygdx.game.Tools.ResourceManager;
import com.mygdx.game.Objects.Hole;
import com.mygdx.game.Objects.Tomato;
import com.mygdx.game.Scenes.HUD;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TomatoMiniGame extends GameScreen implements Subscriber {
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private World world;    // World holding all the physical objects
    private ArrayList<Hole> holeList;
    
    private Texture backgroundTexture;
    private Sprite backgroundSprite;

    private Hole hole1;
    private Hole hole2;
    private Hole hole3;
    private Hole hole4;
    private Hole hole5;
    private Hole hole6;
    private Hole hole7;
    private Hole hole8;
    private Hole hole9;

    private Tomato tomato;
    private Tomato newTomato;

    private int tomatoesLeft;


    public TomatoMiniGame(LOD game, ResourceManager resourceManager, HUD HUD, MyTimer timer) {

        super(game, HUD, timer);
        tomatoesLeft = 10;

        // Creating tiled map
        backgroundTexture = new Texture("stone_background.png"); 
        backgroundSprite = new Sprite(backgroundTexture);


        world = new World(new Vector2(0, 0), true);
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(Constants.TILE_SIZE * 30 / Constants.PPM, Constants.TILE_SIZE * 17 / Constants.PPM, gameCam);
        gameCam.position.set(2, 77, 0);

        holeList = new ArrayList<Hole>();

        hole1 = new Hole(world,-200, -40, false, 1, 1/8f);
        hole2 = new Hole(world, 0, -40, false, 2, 1/30f);
        hole3 = new Hole(world, 200, -40, false, 3, 1/8f);
        hole4 = new Hole(world, -200, 30, false, 4, 1/6.4f);
        hole5 = new Hole(world, 0, 30, false, 5, 1/13f);
        hole6 = new Hole(world, 200, 30, false, 6, 1/6.4f);
        hole7 = new Hole(world, -200, 100, false, 7, 1/5.2f);
        hole8 = new Hole(world, 0, 100, false, 8, 1/7.5f);
        hole9 = new Hole(world, 200, 100, false, 9, 1/5.2f);

        tomato = new Tomato(world, 0, -100, hole1);
        newTomato = new Tomato(world, 0, -100, hole1);

        holeList.add(hole1);
        holeList.add(hole2);
        holeList.add(hole3);
        holeList.add(hole4);
        holeList.add(hole5);
        holeList.add(hole6);
        holeList.add(hole7);
        holeList.add(hole8);
        holeList.add(hole9);

        timer.start(3, null, this);
        
    }

    public void update(float delta) {

        handleInput();
        world.step(1/60f, 6, 2);
        gameCam.position.set(0, 0, 0);
        gameCam.update();

        timer.update(delta);
        for(Hole hole : holeList) {
            hole.update(delta);
        }
        tomato.update(delta);
        newTomato.update(delta);
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            if(tomatoesLeft <= 0) {
                tomatoesLeft = 10;
                game.changeScreen("city");
            }

            Vector2 mouseCoords = new Vector2(Gdx.input.getX()/Constants.PPM, Gdx.input.getY()/Constants.PPM);
            Hole foundHole = hardCodeHoles(mouseCoords);

            if(foundHole != null) {
                newTomato = new Tomato(world, 0, -100, foundHole);

                newTomato.launchTomato(timer, foundHole.getPosition(), foundHole);
                tomatoesLeft--;

                if(foundHole.isBuffoonHere()) {
                    HUD.updateCoinCount();
                    foundHole.get_tomatoed(timer);
                }

            }
        }
    }

    @Override
    public void render(float delta) {

        update(delta);

        // Clearing the screen
        Gdx.gl.glClearColor( 0.5f, 0.5f, 0.5f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT);
    

        game.batch.begin();
        game.batch.draw(backgroundSprite, -3, -2, 14, 13);
        game.batch.end();
        

        for(Hole hole : holeList) {
            hole.render(game.batch);
        }

        tomato.render(game.batch);

        if(!newTomato.tomatoHit()) newTomato.render(game.batch);

        //b2dr.render(world, gameCam.combined);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }
    public void notify(String flag) {
        randomBuffoon().peek(timer);
        timer.start(3f, "peek", this);
    }
    // Returns random hole
    public Hole randomBuffoon() {

        java.util.Random random = new java.util.Random();
        int randomHole = random.nextInt(9) + 1;
        for (Hole hole : holeList) {
            if(hole.getHoleID() == randomHole) {
                return hole;
            }
        }
        return null;
    }
    // Im sorry for this. @Gines
    public Hole hardCodeHoles(Vector2 mouseCords) {

        if((mouseCords.x >= 1 && mouseCords.x <= 2.3) && (mouseCords.y <= 1.81 && mouseCords.y >= 1)) {
            return hole7;
        } else if((mouseCords.x >= 8.89 && mouseCords.x <= 10.31) && (mouseCords.y <= 1.81 && mouseCords.y >= 1)) {
            return hole8;
        } else if((mouseCords.x >= 16.82 && mouseCords.x <= 18.25) && (mouseCords.y <= 1.81 && mouseCords.y >= 1)) {
            return hole9;
        }else if((mouseCords.x >= 1 && mouseCords.x <= 2.3) && (mouseCords.y <= 4.6 && mouseCords.y >= 3.8)) {
            return hole4;
        }else if((mouseCords.x >= 8.89 && mouseCords.x <= 10.31) && (mouseCords.y <= 4.6 && mouseCords.y >= 3.8)) {
            return hole5;
        }else if((mouseCords.x >= 16.82 && mouseCords.x <= 18.25) && (mouseCords.y <= 4.6 && mouseCords.y >= 3.8)) {
            return hole6;
        }else if((mouseCords.x >= 1 && mouseCords.x <= 2.3) && (mouseCords.y <= 7.36 && mouseCords.y >= 6.69)) {
            return hole1;
        }else if((mouseCords.x >= 8.89 && mouseCords.x <= 10.31) && (mouseCords.y <= 7.36 && mouseCords.y >= 6.69)) {
            return hole2;
        }else if((mouseCords.x >= 16.82 && mouseCords.x <= 18.25) && (mouseCords.y <= 7.36 && mouseCords.y >= 6.69)) {
            return hole3;
        }
        return null;
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

}
