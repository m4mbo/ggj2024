package com.mygdx.game.RoleCast;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Game.LOD;
import com.mygdx.game.Objects.Item;
import com.mygdx.game.Sprites.B2Sprite;
import com.mygdx.game.Tools.Constants;
import com.mygdx.game.Tools.ResourceManager;
import java.util.LinkedList;

public class Buffoon extends B2Sprite {
    private Constants.ASTATE currAState;     // Current animation state
    private Constants.ASTATE prevAState;     // Previous animation state
    private float currAlpha;
    private float prevAlpha;
    private final ResourceManager resourceManager;
    private NPC targetnpc;
    private King king;
    private LOD game;

    public Buffoon(int x, int y, World world, ResourceManager resourceManager, LOD game) {

        this.game = game;
        this.resourceManager = resourceManager;
        currAState = Constants.ASTATE.IDLE_DOWN;
        prevAState = Constants.ASTATE.IDLE_DOWN;
        currAlpha = 1;
        prevAlpha = 1;

        king = null;

        loadSprites();

        setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_idle_down"), 32, 32)[0], 1/4f, false, 1f, currAlpha);

        BodyDef bdef = new BodyDef();
        bdef.position.set(x / Constants.PPM, y / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();

        //Create body fixture
        polygonShape.setAsBox(8 / Constants.PPM, 16 / Constants.PPM, new Vector2(0, 0), 0);
        fdef.shape = polygonShape;
        fdef.friction = 0;
        fdef.filter.maskBits = Constants.BIT_GROUND | Constants.BIT_TREE | Constants.BIT_ITEM | Constants.BIT_TRANSPARENCY | Constants.BIT_NPC | Constants.BIT_DOOR | Constants.BIT_KING;
        b2body.createFixture(fdef).setUserData("buffoon");

    }

    public void loadSprites() {
        resourceManager.loadTexture("buffoon_run_up.png", "buffoon_run_up");
        resourceManager.loadTexture("buffoon_run_down.png", "buffoon_run_down");
        resourceManager.loadTexture("buffoon_run_left.png", "buffoon_run_left");
        resourceManager.loadTexture("buffoon_run_right.png", "buffoon_run_right");
        resourceManager.loadTexture("buffoon_idle_down.png", "buffoon_idle_down");
        resourceManager.loadTexture("buffoon_idle_up.png", "buffoon_idle_up");
        resourceManager.loadTexture("buffoon_idle_left.png", "buffoon_idle_left");
        resourceManager.loadTexture("buffoon_idle_right.png", "buffoon_idle_right");
    }

    public void handleAnimation() {
        switch (currAState) {
            case RUN_UP:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_run_up"), 32, 32)[0], 1/10f, false, 1f, currAlpha);
                break;
            case RUN_DOWN:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_run_down"), 32, 32)[0], 1/10f, false, 1f, currAlpha);
                break;
            case RUN_LEFT:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_run_left"), 32, 32)[0], 1/10f, false, 1f, currAlpha);
            break;
            case RUN_RIGHT:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_run_right"), 32, 32)[0], 1/10f, false, 1f, currAlpha);
                break;
            case IDLE_DOWN:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_idle_down"), 32, 32)[0], 1/4f, false, 1f, currAlpha);
                break;
            case IDLE_UP:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_idle_up"), 32, 32)[0], 1/4f, false, 1f, currAlpha);
                break;
            case IDLE_LEFT:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_idle_left"), 32, 32)[0], 1/4f, false, 1f, currAlpha);
                break;
            case IDLE_RIGHT:
                setAnimation(TextureRegion.split(resourceManager.getTexture("buffoon_idle_right"), 32, 32)[0], 1/4f, false, 1f, currAlpha);
                break;
        }
    }

    public void update(float delta) {

        if (currAState != prevAState) {
            prevAState = currAState;
            handleAnimation();
        }
        if (currAlpha != prevAlpha) {
            prevAlpha = currAlpha;
            handleAnimation();
        }
        animation.update(delta);
    }

    public void moveUp() {
        currAState = Constants.ASTATE.RUN_UP;
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, Constants.MAX_SPEED);
    }

    public void moveDown() {
        currAState = Constants.ASTATE.RUN_DOWN;
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, -Constants.MAX_SPEED);
    }

    public void moveLeft() {
        facingRight = false;
        currAState = Constants.ASTATE.RUN_LEFT;
        b2body.setLinearVelocity(-Constants.MAX_SPEED, b2body.getLinearVelocity().y);
    }

    public void moveRight() {
        facingRight = true;
        currAState = Constants.ASTATE.RUN_RIGHT;
        b2body.setLinearVelocity(Constants.MAX_SPEED, b2body.getLinearVelocity().y);
    }

    public void moveUpLeft() {
        facingRight = false;
        currAState = Constants.ASTATE.RUN_LEFT;
        b2body.setLinearVelocity((float)(-Constants.MAX_SPEED / Math.sqrt(2)), (float)(Constants.MAX_SPEED / Math.sqrt(2)));
    }

    public void moveUpRight() {
        facingRight = true;
        currAState = Constants.ASTATE.RUN_RIGHT;
        b2body.setLinearVelocity((float)(Constants.MAX_SPEED / Math.sqrt(2)), (float)(Constants.MAX_SPEED / Math.sqrt(2)));
    }
    public void moveDownLeft() {
        facingRight = false;
        currAState = Constants.ASTATE.RUN_LEFT;
        b2body.setLinearVelocity((float)(-Constants.MAX_SPEED / Math.sqrt(2)), (float)(-Constants.MAX_SPEED / Math.sqrt(2)));
    }
    public void moveDownRight() {
        facingRight = true;
        currAState = Constants.ASTATE.RUN_RIGHT;
        b2body.setLinearVelocity((float)(Constants.MAX_SPEED / Math.sqrt(2)), (float)(-Constants.MAX_SPEED / Math.sqrt(2)));
    }
    public void stop() {
        switch (currAState) {
            case RUN_UP:
                currAState = Constants.ASTATE.IDLE_UP;
                break;
            case RUN_DOWN:
                currAState = Constants.ASTATE.IDLE_DOWN;
                break;
            case RUN_LEFT:
                currAState = Constants.ASTATE.IDLE_LEFT;
                break;
            case RUN_RIGHT:
                currAState = Constants.ASTATE.IDLE_RIGHT;
                break;
            default:
                break;
        }
        b2body.setLinearVelocity(0, 0);
    }

    public void stopY() {
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
    }

    public void stopX() {
        b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
    }

    public void setCurrAlpha(float alpha) {
        currAlpha = alpha;
    }

    public void addItem(Item item) {
        game.inventory.add(item);
    }

    public LinkedList<Item> getPlayerList() {
        return game.inventory;
    }

    public void setTargetnpc(NPC npc) {
        targetnpc = npc;
    }

    public void setKing(King king) {
        this.king = king;
    }

    public King getKing() {
        return king;
    }

    public void giveItems() {
        king.presentItems(game.inventory);
        game.inventory.clear();
    }

    public NPC getTargetnpc() {
        return targetnpc;
    }

}
