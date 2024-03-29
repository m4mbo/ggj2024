package com.mygdx.game.RoleCast;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Game.LOD;
import com.mygdx.game.Objects.Item;
import com.mygdx.game.Sprites.B2Sprite;
import com.mygdx.game.Tools.Constants;
import com.mygdx.game.Tools.FunCalculator;
import com.mygdx.game.Tools.ResourceManager;
import java.util.Random;
import java.util.LinkedList;

public class King extends B2Sprite {
    private Constants.KSTATE currState;     // Current animation state
    private Constants.KSTATE prevState;     // Previous animation state
    private final FunCalculator funCalculator;
    private Constants.COMEDYTYPE favouredType;
    private final Constants.COMEDYTYPE[] comedytypes;
    private final ResourceManager resourceManager;
    private final LOD game;

    public King(int x, int y, World world, ResourceManager resourceManager, LOD game) {

        this.resourceManager = resourceManager;
        this.game = game;

        funCalculator = new FunCalculator();

        comedytypes = new Constants.COMEDYTYPE[5];
        comedytypes[0] = Constants.COMEDYTYPE.ROMANTIC;
        comedytypes[1] = Constants.COMEDYTYPE.SELF_DEPRECATING;
        comedytypes[2] = Constants.COMEDYTYPE.DARK;
        comedytypes[3] = Constants.COMEDYTYPE.ABSURD;
        comedytypes[4] = Constants.COMEDYTYPE.TRAGIC;

        BodyDef bdef = new BodyDef();
        bdef.position.set(x / Constants.PPM, y / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();

        //Create body fixture
        polygonShape.setAsBox(32 / Constants.PPM, 32 / Constants.PPM, new Vector2(0, 0), 0);
        fdef.shape = polygonShape;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData("king");

        polygonShape.setAsBox(32 / Constants.PPM, 64 / Constants.PPM, new Vector2(0, 0), 0);
        fdef.shape = polygonShape;
        fdef.friction = 0;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.BIT_KING;
        b2body.createFixture(fdef).setUserData(this);

        wakeUp();

        loadSprites();
        currState = Constants.KSTATE.IDLE;
        prevState = Constants.KSTATE.LAUGH3;
        setAnimation(TextureRegion.split(resourceManager.getTexture("king_idle"), 64, 64)[0], 1/4f, false, 1f, 1);
    }

    public void loadSprites() {
        resourceManager.loadTexture("King/king_idle.png", "king_idle");
        resourceManager.loadTexture("King/king_laughing_level-3.png", "king_laugh3");
    }

    public void handleAnimation() {
        switch (currState)  {
            case IDLE:
                setAnimation(TextureRegion.split(resourceManager.getTexture("king_idle"), 64, 64)[0], 1/4f, false, 1f, 1);
                break;
            case LAUGH1:
                break;
            case LAUGH2:
                break;
            case LAUGH3:
                setAnimation(TextureRegion.split(resourceManager.getTexture("king_laugh3"), 64, 64)[0], 1/4f, false, 1f, 1);
                break;
        }
    }

    public void update(float delta) {
        if (currState != prevState) {
            prevState = currState;
            handleAnimation();
        }
        animation.update(delta);
    }

    public void wakeUp() {
        Random rand = new Random();
        int index = rand.nextInt(5);
        while (comedytypes[index] == null) {
            index = rand.nextInt(5);
        }
        favouredType = comedytypes[index];
        comedytypes[index] = null;
    }

    public void presentItems(LinkedList<Item> items) {
        float listRating = funCalculator.evaluate(items, favouredType);
        if (listRating > 0.5) {
            currState = Constants.KSTATE.LAUGH3;
            game.cutSceneActive = true;
            game.cutScene.setText("HA! HA! HA!");
        }
    }

    public void reset() {
        currState = Constants.KSTATE.IDLE;
    }

}