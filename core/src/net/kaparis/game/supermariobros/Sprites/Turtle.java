package net.kaparis.game.supermariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import net.kaparis.game.supermariobros.MarioBrosGame;
import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.Tools.B2WorldCreator;

/**
 * Created by David on 3/13/2017.
 */

public class Turtle extends net.kaparis.game.supermariobros.Sprites.Enemy {
    public static final int KICK_LEFT = -2;
    public static final int KICK_RIGHT = 2;
    public enum State {WALKING, MOVING_SHELL, STANDING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private float deadRotationDegrees;
    private boolean setToDestroy;
    private boolean destroyed;

    public Turtle(PlayScreen screen, B2WorldCreator b2WorldCreator,  float x, float y, float PPM) {
        super(screen, b2WorldCreator,  x, y, PPM);

        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

        setBounds(getX(),getY(),16 / PPM, 24 / PPM );
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

        fdef.filter.categoryBits = MarioBrosGame.ENEMY_BIT;
        fdef.filter.maskBits =
                MarioBrosGame.GROUND_BIT |
                        MarioBrosGame.COIN_BIT |
                        MarioBrosGame.BRICK_BIT |
                        MarioBrosGame.OBJECT_BIT |
                        MarioBrosGame.MARIO_BIT |
                        MarioBrosGame.ENEMY_BIT ;

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
        fdef.restitution = 1.5f; // bounce
        fdef.filter.categoryBits = MarioBrosGame.ENEMY_HEAD_BIT;
        b2Body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else{
            if(mario.b2Body.getPosition().x > b2Body.getPosition().x)
                velocity.x = KICK_LEFT;
            else
                velocity.x = KICK_RIGHT;

            currentState = State.MOVING_SHELL;
        }
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - 8 /PPM);

        if(currentState == State.DEAD){
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(stateTime > 5 && !destroyed){
                world.destroyBody(b2Body);
                destroyed = true;

                b2WorldCreator.removeTurtle(this);
            }
        } else {
            // when dead, don't want body to move.
            b2Body.setLinearVelocity(velocity);
        }
        b2Body.setLinearVelocity(velocity);
    }

    @Override
    public void onEnemyHit(net.kaparis.game.supermariobros.Sprites.Enemy enemy) {
        if(enemy instanceof Turtle) {
            if(((Turtle)enemy).currentState == State.MOVING_SHELL && currentState !=  State.MOVING_SHELL){
                killed();
            }
            else if (currentState == State.MOVING_SHELL && ((Turtle)enemy).currentState == State.WALKING )
                return;
            else
                    reverseVelocity(true, false);
        } else if(currentState != State.MOVING_SHELL){
            reverseVelocity(true, false);
        }
    }


    // Class Specific Methods

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState(){
        return currentState;
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState){
            case MOVING_SHELL:
            case STANDING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = (TextureRegion)walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        if(velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBrosGame.NOTHING_BIT;

        for(Fixture fixture : b2Body.getFixtureList())
        {
            fixture.setFilterData(filter);
        }

        b2Body.applyLinearImpulse(new Vector2(0,5f), b2Body.getWorldCenter(), true);
    }

//    public void draw (Batch batch){
//        // temp fix, hides destroyed turtle.
//        // TODO in World Created class to destroy.
//        if(!destroyed)
//                super.draw(batch);
//    }
}
