package net.kaparis.game.supermariobros.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.kaparis.game.supermariobros.MarioBrosGame;
import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.Sprites.Mario;

/**
 * Created by David on 11/8/2016.
 */

public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;
    protected float PPM;

    public Item(PlayScreen screen, float x, float y, float PPM)
    {
        this.screen = screen;
        this.world = screen.getWorld();
        toDestroy = false;
        destroyed = false;
        this.PPM = PPM;

        setPosition(x,y);
        setBounds(getX(), getY(), 16 / PPM , 16 / PPM);

        int xCell = (int)(getX() * MarioBrosGame.PPM / 16);
        int yCell = (int)(getY() * MarioBrosGame.PPM / 16);

        Gdx.app.log(this.getClass().getSimpleName(), "Created");
        Gdx.app.log(this.getClass().getSimpleName(), "Cell(X,Y): " + xCell + "," + yCell);
        Gdx.app.log(this.getClass().getSimpleName(), "Location(X,Y): " + this.getX() + "," + this.getY());
        Gdx.app.log(this.getClass().getSimpleName(), "Bounds(W,H): " + this.getWidth() + "," + this.getHeight());

        defineItem();
    }

    public abstract void defineItem();

    public abstract void use(Mario mario);

    public void update(float dt){
        if(toDestroy && !destroyed) {
            world.destroyBody(body);
            // used because box object can't be destroyed in step
            destroyed = true;
        }
    }

    public void draw (Batch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public void reverseVelocity(boolean x, boolean y)
    {
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }

    public void destroy(){
        toDestroy = true;
    }
}
