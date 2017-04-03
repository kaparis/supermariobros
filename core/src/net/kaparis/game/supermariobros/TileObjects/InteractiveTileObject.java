package net.kaparis.game.supermariobros.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.kaparis.game.supermariobros.MarioBrosGame;
import net.kaparis.game.supermariobros.Screens.PlayScreen;
import net.kaparis.game.supermariobros.Sprites.Mario;

/**
 * Created by David on 9/16/2016.
 */
public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject mapObject;

    public InteractiveTileObject(PlayScreen screen, MapObject mapObject) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.screen = screen;
        this.mapObject = mapObject;
        this.bounds = ((RectangleMapObject) mapObject).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBrosGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBrosGame.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / MarioBrosGame.PPM, bounds.getHeight() / 2 / MarioBrosGame.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

        int x = (int)(body.getPosition().x * MarioBrosGame.PPM / 16);
        int y = (int)(body.getPosition().y * MarioBrosGame.PPM / 16);

        Gdx.app.debug(this.getClass().getSimpleName(), "Created");
        Gdx.app.debug(this.getClass().getSimpleName(), "    Cell (x,y) " + x + "," + y);
        Gdx.app.debug(this.getClass().getSimpleName(), "Location (x,y) " + body.getPosition().x + "," + body.getPosition().y);
        Gdx.app.debug(this.getClass().getSimpleName(), "  Bounds (w,h) " + bounds.width + "," + bounds.height);
    }

    public abstract void onHeadHit(Mario mario);

    public void setCategoryFilter(short filterBit)
    {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell()
    {
        // Get Game Layer which has our Graphics
        // Our Map is tiled, so we cast it to get additional features
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        int x = (int)(body.getPosition().x * MarioBrosGame.PPM / 16);
        int y = (int)(body.getPosition().y * MarioBrosGame.PPM / 16);

        TiledMapTileLayer.Cell cell = layer.getCell(x,y);

        return cell;

    }
}





























