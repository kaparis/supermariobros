package net.kaparis.game.supermariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.kaparis.game.supermariobros.MarioBrosGame;
import net.kaparis.game.supermariobros.Sprites.Enemy;
import net.kaparis.game.supermariobros.Sprites.Mario;

/**
 * Created by David on 9/26/2016.
 */

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        String fixtureA = "";
        String fixtureB = "";

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        //======= DEBUG INFO ========
        if (fixA.getUserData() != null) {
            if (fixA.getUserData().getClass().getSimpleName().equalsIgnoreCase("String") )
                fixtureA = (String)fixA.getUserData();
            else
                fixtureA = fixA.getUserData().getClass().getSimpleName();
        }
        if (fixB.getUserData() != null) {
            if ( fixB.getUserData().getClass().getSimpleName().equalsIgnoreCase("String"))
                fixtureB = (String) fixB.getUserData();
            else
                fixtureB = fixB.getUserData().getClass().getSimpleName();
        }

        Gdx.app.log( this.getClass().getSimpleName(),
                "Start Contact "
                 + "A: " + fixtureA + " P: " +  bodyA.getPosition().toString() + " V: " + bodyA.getLinearVelocity().toString()
                 + " ---- B: " + fixtureB + " P: "+ bodyB.getPosition().toString() + " V: " + bodyB.getLinearVelocity().toString() );
        //======= DEBUG INFO ========


        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MarioBrosGame.MARIO_HEAD_BIT | MarioBrosGame.BRICK_BIT:

            case MarioBrosGame.MARIO_HEAD_BIT | MarioBrosGame.COIN_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.MARIO_HEAD_BIT)
                    ((net.kaparis.game.supermariobros.TileObjects.InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((net.kaparis.game.supermariobros.TileObjects.InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case MarioBrosGame.ENEMY_HEAD_BIT | MarioBrosGame.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else if(fixB.getFilterData().categoryBits == MarioBrosGame.ENEMY_HEAD_BIT)
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case MarioBrosGame.ENEMY_BIT | MarioBrosGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBrosGame.MARIO_BIT | MarioBrosGame.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case MarioBrosGame.ENEMY_BIT | MarioBrosGame.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;
            case MarioBrosGame.ITEM_BIT | MarioBrosGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.ITEM_BIT)
                    ((net.kaparis.game.supermariobros.Items.Item)fixA.getUserData()).reverseVelocity(true,false);
                else
                    ((net.kaparis.game.supermariobros.Items.Item)fixB.getUserData()).reverseVelocity(true,false);
                break;
            case MarioBrosGame.ITEM_BIT | MarioBrosGame.MARIO_BIT:
                if(fixA.getFilterData().categoryBits == MarioBrosGame.ITEM_BIT)
                    ((net.kaparis.game.supermariobros.Items.Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((net.kaparis.game.supermariobros.Items.Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

        String fixtureA = "";
        String fixtureB = "";

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        //======= DEBUG INFO ========
        if (fixA.getUserData() != null) {
            if (fixA.getUserData().getClass().getSimpleName().equalsIgnoreCase("String") )
                fixtureA = (String)fixA.getUserData();
            else
                fixtureA = fixA.getUserData().getClass().getSimpleName();
        }
        if (fixB.getUserData() != null) {
            if ( fixB.getUserData().getClass().getSimpleName().equalsIgnoreCase("String"))
                fixtureB = (String) fixB.getUserData();
            else
                fixtureB = fixB.getUserData().getClass().getSimpleName();
        }

        Gdx.app.log( this.getClass().getSimpleName(),
                "End   Contact "
                        + "A: " + fixtureA + " P: "+  bodyA.getPosition().toString() + " V: " + bodyA.getLinearVelocity().toString()
                        + " ---- B: " + fixtureB + " P: "+ bodyB.getPosition().toString() + " V: " + bodyB.getLinearVelocity().toString() );
        //======= DEBUG INFO ========

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        //once collided, change charterisitics of the collision

        //float x = oldManifold.getLocalPoint().x;
        //float y = oldManifold.getLocalPoint().y;
        //Gdx.app.log(this.getClass().getSimpleName(), "Presolve Location(X,Y): " + x + "," + y);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        //provides results of collision

    }
}
