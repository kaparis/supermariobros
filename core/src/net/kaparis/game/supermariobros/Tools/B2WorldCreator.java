package net.kaparis.game.supermariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.TileObjects.Brick;
import net.kaparis.game.supermariobros.TileObjects.Coin;
import net.kaparis.game.supermariobros.Sprites.Goomba;

/**
 * Created by David on 9/16/2016.
 */
public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<net.kaparis.game.supermariobros.Sprites.Turtle> turtles;

    public B2WorldCreator(PlayScreen screen)
    {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        //temp
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM, (rect.getY() + rect.getHeight() /2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth()/ 2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM, (rect.getHeight() /2)/ net.kaparis.game.supermariobros.MarioBrosGame.PPM);
            fdef.shape = shape;

            body.createFixture(fdef).setUserData("ground");
        }

        //create pipes
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM, (rect.getY() + rect.getHeight() /2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth()/ 2) / net.kaparis.game.supermariobros.MarioBrosGame.PPM, (rect.getHeight() /2)/ net.kaparis.game.supermariobros.MarioBrosGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT;
            body.createFixture(fdef).setUserData("pipe");
        }

        //create bricks
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class))
        {
            //Rectangle rect = ((RectangleMapObject)object).getRectangle();
            new Brick(screen, object);
        }

        //create coins
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class))
        {
            //Rectangle rect = ((RectangleMapObject)object).getRectangle();
            new Coin(screen, object);
        }

        //create goombas
        goombas = new Array<Goomba>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            goombas.add(new Goomba(screen, this, rect.getX() / net.kaparis.game.supermariobros.MarioBrosGame.PPM , rect.getY() / net.kaparis.game.supermariobros.MarioBrosGame.PPM, net.kaparis.game.supermariobros.MarioBrosGame.PPM));
        }

        //create turtles
        turtles  = new Array<net.kaparis.game.supermariobros.Sprites.Turtle>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class))
        {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            turtles.add(new net.kaparis.game.supermariobros.Sprites.Turtle(screen, this, rect.getX() / net.kaparis.game.supermariobros.MarioBrosGame.PPM , rect.getY() / net.kaparis.game.supermariobros.MarioBrosGame.PPM, net.kaparis.game.supermariobros.MarioBrosGame.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<net.kaparis.game.supermariobros.Sprites.Enemy> getEnemies() {
        Array<net.kaparis.game.supermariobros.Sprites.Enemy> enemies = new Array<net.kaparis.game.supermariobros.Sprites.Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);

        return enemies;
    }

    public void removeTurtle(net.kaparis.game.supermariobros.Sprites.Turtle turtle){
        // removes from memory
        turtles.removeValue(turtle, true);
    }

    public void removeGoomba(Goomba goomba){
        // removes from memory
        goombas.removeValue(goomba, true);
    }
}
