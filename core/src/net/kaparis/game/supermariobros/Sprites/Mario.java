package net.kaparis.game.supermariobros.Sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by David on 9/14/2016.
 */
public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD};
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;
    private TextureRegion marioStand;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;

    private Animation bigMarioRun;
    private Animation growMario;
    private Animation marioRun;
    private TextureRegion marioJump;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigPlayer;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    private Array<net.kaparis.game.supermariobros.Weapons.FireBall> fireballs;

    private net.kaparis.game.supermariobros.Screens.PlayScreen screen;

    private float PPM;

    public Mario(net.kaparis.game.supermariobros.Screens.PlayScreen screen, float PPM) {
        this.PPM = PPM;
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //Mario Running
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();

        //Mario Grow (rotates between small mario and big mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario = new Animation(0.2f, frames);

        //Mario Jumping
        for (int i = 4; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 11, 16, 16));
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,0,16,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0,16,32);

        //Mario Standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);

        //Mario Dead
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        setBounds(0, 0, 16 / PPM, 16 / PPM);
        setRegion(marioStand);

        defineMario();

        fireballs = new Array<net.kaparis.game.supermariobros.Weapons.FireBall>();

    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / PPM, 32 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = net.kaparis.game.supermariobros.MarioBrosGame.GROUND_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_HEAD_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ITEM_BIT;


        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6/ PPM), new Vector2(2 / PPM, 6  / PPM));

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2Body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario(){
        Vector2 currentPosition = b2Body.getPosition();
        world.destroyBody(b2Body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = net.kaparis.game.supermariobros.MarioBrosGame.GROUND_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_HEAD_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ITEM_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);
        shape.setPosition((new Vector2(0,-14/PPM)));
        b2Body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6/ PPM), new Vector2(2 / PPM, 6  / PPM));

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2Body.createFixture(fdef).setUserData(this);

        timeToDefineBigPlayer = false;
    }

    public void redefineMario(){
        Vector2 position = b2Body.getPosition();
        world.destroyBody(b2Body);


        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2Body = world.createBody(bdef);

        timeToRedefineMario = false;

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / PPM);

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = net.kaparis.game.supermariobros.MarioBrosGame.GROUND_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_HEAD_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ITEM_BIT;


        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / PPM, 6/ PPM), new Vector2(2 / PPM, 6  / PPM));

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2Body.createFixture(fdef).setUserData(this);
    }

    public void update(float dt) {
        // update our sprite to correspond with the position of our Box@D body.
        if(marioIsBig)
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2 - 6 / PPM);
        else
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);

        // update sprite with the correct frame depending on current action
        setRegion(GetFrame(dt));

        if(timeToDefineBigPlayer)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();

        for(net.kaparis.game.supermariobros.Weapons.FireBall ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }
    }

    public TextureRegion GetFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion)growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                        runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion)bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion)marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt: 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(marioIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(b2Body.getLinearVelocity().y > 0 || b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING)
            return State.JUMPING;
        else if(b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigPlayer = true;
        setBounds(getX(), getY(), getWidth(), getHeight()*2);

        AssetManager assetManager = new AssetManager();
        assetManager.load("audio/sounds/powerup.wav",Sound.class);
        assetManager.finishLoading();
        Sound soundEffectCoin = assetManager.get("audio/sounds/powerup.wav", Sound.class);
        soundEffectCoin.play();
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT : Turtle.KICK_RIGHT);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);

                AssetManager assetManager = new AssetManager();
                assetManager.load("audio/sounds/powerdown.wav", Sound.class);
                assetManager.finishLoading();
                assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                screen.stopBackgroundMusic();

                AssetManager assetManager = new AssetManager();
                assetManager.load("audio/sounds/mariodie.wav", Sound.class);
                assetManager.finishLoading();
                assetManager.get("audio/sounds/mariodie.wav", Sound.class).play();

                marioIsDead = true;

                Filter filter = new Filter();
                filter.maskBits = net.kaparis.game.supermariobros.MarioBrosGame.NOTHING_BIT;

                //Update all Mario fixtures
                for (Fixture fixture : b2Body.getFixtureList())
                    fixture.setFilterData(filter);

                b2Body.applyLinearImpulse(new Vector2(0, 4f), b2Body.getWorldCenter(), true);
            }
        }
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void fire(){
        if(marioIsBig)
            fireballs.add(new net.kaparis.game.supermariobros.Weapons.FireBall(screen, PPM, b2Body.getPosition().x, b2Body.getPosition().y, runningRight ? true : false));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(net.kaparis.game.supermariobros.Weapons.FireBall ball : fireballs)
            ball.draw(batch);
    }
}
