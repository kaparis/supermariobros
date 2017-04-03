package net.kaparis.game.supermariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.Tools.B2WorldCreator;

/**
 * Created by David on 9/29/2016.
 */

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2Body;
    protected float PPM;
    public Vector2 velocity;
    protected B2WorldCreator b2WorldCreator;

    public Enemy(PlayScreen screen, B2WorldCreator b2WorldCreator, float x, float y, float PPM){
        this.world = screen.getWorld();
        this.screen = screen;
        this.PPM = PPM;
        this.b2WorldCreator = b2WorldCreator;

        setPosition(x,y);
        defineEnemy();

        velocity = new Vector2(1,0);

        // Put Body to sleep
        b2Body.setActive(false);
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead(Mario mario);
    public abstract void update(float dt);
    public abstract void onEnemyHit(Enemy enemy);

    public void reverseVelocity(boolean x, boolean y)
    {
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
