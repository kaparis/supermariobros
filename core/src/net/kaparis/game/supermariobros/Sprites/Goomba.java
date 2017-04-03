package net.kaparis.game.supermariobros.Sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

/**
 * Created by David on 9/29/2016.
 */

public class Goomba extends Enemy  {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private TextureAtlas.AtlasRegion spriteRegion;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(net.kaparis.game.supermariobros.Screens.PlayScreen screen, net.kaparis.game.supermariobros.Tools.B2WorldCreator b2WorldCreator, float x, float y, float PPM) {
        super(screen, b2WorldCreator,  x, y, PPM);

        spriteRegion = screen.getAtlas().findRegion("goomba");

        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++)
        {
            frames.add(new TextureRegion(spriteRegion, i * 16, 0, 16, 16));
        }

        stateTime = 0;
        walkAnimation = new Animation(0.4f, frames);

        setBounds(getX(), getY(), 16 / PPM, 16 / PPM);

        setToDestroy = false;
        destroyed = false;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle)enemy).currentState == Turtle.State.MOVING_SHELL){
            setToDestroy = true;
            b2WorldCreator.removeGoomba(this);
        } else
        {
            reverseVelocity(true, false);
        }
    }

    @Override
    protected void defineEnemy(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius( 6 / PPM);

        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_BIT;
        fdef.filter.maskBits =
                net.kaparis.game.supermariobros.MarioBrosGame.GROUND_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.MARIO_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_BIT ;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        //Create Head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5,8).scl(1/PPM);
        vertice[1] = new Vector2(5,8).scl(1/PPM);
        vertice[2] = new Vector2(-3,3).scl(1/PPM);
        vertice[3] = new Vector2(3,3).scl(1/PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f; // bounce
        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.ENEMY_HEAD_BIT;
        b2Body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;

        AssetManager assetManager = new AssetManager();
        assetManager.load("audio/sounds/stomp.wav",Sound.class);
        assetManager.finishLoading();
        assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    // Class Specific Methods

    public void draw(Batch batch){
        // only draw if not destroyed and more than 1 second since destrution.
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }

    public void update(float dt){
        stateTime += dt;

        // Destroy body, else update body location
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2Body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"),32,0, 16,16));

            //resets timer. use to know how long the enemy is dead
            stateTime = 0;
        } else if(!destroyed) {
            b2Body.setLinearVelocity(velocity);

            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion)walkAnimation.getKeyFrame(stateTime, true));
        }
    }
}