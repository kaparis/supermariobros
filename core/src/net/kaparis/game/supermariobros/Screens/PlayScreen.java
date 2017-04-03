package net.kaparis.game.supermariobros.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.kaparis.game.supermariobros.Items.ItemDef;
import net.kaparis.game.supermariobros.Items.Mushroom;
import net.kaparis.game.supermariobros.Scenes.Debug;
import net.kaparis.game.supermariobros.Sprites.Mario;
import net.kaparis.game.supermariobros.Scenes.Hud;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by David on 9/6/2016.
 */
public class PlayScreen implements Screen{

    private net.kaparis.game.supermariobros.MarioBrosGame game;
    private TextureAtlas  atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private Debug debugScene;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    AssetManager assetManager = new AssetManager();
    private Music backGroundMusic;

    private Mario player;

    private Array<net.kaparis.game.supermariobros.Items.Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    net.kaparis.game.supermariobros.Tools.Controller controller;

    //Box 2D
    private World world;
    private Box2DDebugRenderer b2dr;
    private net.kaparis.game.supermariobros.Tools.B2WorldCreator worldCreator;

    public PlayScreen(net.kaparis.game.supermariobros.MarioBrosGame game){
        this.game = game;
        atlas = new TextureAtlas("spritesMain.pack");

        //CAM that will follow mario around
        gameCam = new OrthographicCamera();

        //Aspect Ratio Maintained
        gamePort = new FitViewport(net.kaparis.game.supermariobros.MarioBrosGame.V_WIDTH / net.kaparis.game.supermariobros.MarioBrosGame.PPM, net.kaparis.game.supermariobros.MarioBrosGame.V_HEIGHT / net.kaparis.game.supermariobros.MarioBrosGame.PPM, gameCam);

        // Create Hud for scores/timmers
        hud = new Hud(game.batch);
        debugScene = new Debug(game.batch);

        //Load Game Map
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/ net.kaparis.game.supermariobros.MarioBrosGame.PPM);

        //Move Game Cam to designated place
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //Box2D setup
        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();

        //The Sprites
        player = new Mario(this, net.kaparis.game.supermariobros.MarioBrosGame.PPM);

        //The World
        worldCreator = new net.kaparis.game.supermariobros.Tools.B2WorldCreator(this);

        //Sounds
        playBackgroundMusic();

        //Player Controller
        controller = new net.kaparis.game.supermariobros.Tools.Controller(game.batch);

        //Assigns a listener to the world
        world.setContactListener(new net.kaparis.game.supermariobros.Tools.WorldContactListener());

        // Array of generated items the player can interact with.
        items = new Array<net.kaparis.game.supermariobros.Items.Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    // Helper Method: Add any object that inherits from item that will be spawned later.
    public void spawnItem(ItemDef idef)
    {
        itemsToSpawn.add(idef);
    }

    // Create items to spawn
    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class)
                items.add(new Mushroom(this, idef.position.x, idef.position.y, net.kaparis.game.supermariobros.MarioBrosGame.PPM ));
                Gdx.app.log( this.getClass().getSimpleName(),"idef item added at Location (x,y) " + idef.position.x + "," + idef.position.y);
        }
    }

    public TextureAtlas getAtlas()
    {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt)
    {
        /*
         Temp - Scrolls through the world
        if (Gdx.input.isTouched()) {
            gameCam.position.x += 100 * dt;
        }*/

        if(player.currentState != Mario.State.DEAD) {
            if (controller.isUpPressed() | controller.aPressed()) {
                if ( player.b2Body.getLinearVelocity().y == 0)
                    player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
            }
            if ( controller.isRightPressed() && player.b2Body.getLinearVelocity().x <= 2)
                player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);

            if ( controller.isLeftPressed() && player.b2Body.getLinearVelocity().x >= -2)
                player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);

            if (controller.bPressed()) {
                player.fire();
            }
        }
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1/60f,6,2);

        //Updates Player on Screen
        player.update(dt);

        //Enemy's
        for(net.kaparis.game.supermariobros.Sprites.Enemy enemy: worldCreator.getEnemies()) {
            enemy.update(dt);

            //active enemies as player gets near
            if(enemy.getX() < player.getX() + 224/ net.kaparis.game.supermariobros.MarioBrosGame.PPM)
                enemy.b2Body.setActive(true);
        }

        //Items
        for(net.kaparis.game.supermariobros.Items.Item item : items)
            item.update(dt);

        //Update HUD
        hud.update(dt);

        //DEBUG Player Info
        debugScene.updatePlayerLocation(player.getX(), player.getY());

        int x = (int)(player.b2Body.getPosition().x * net.kaparis.game.supermariobros.MarioBrosGame.PPM / 16);
        int y = (int)(player.b2Body.getPosition().y * net.kaparis.game.supermariobros.MarioBrosGame.PPM / 16);
        debugScene.updatePlayerCell(x, y);

        if(player.currentState != Mario.State.DEAD)
            gameCam.position.x = player.b2Body.getPosition().x;

        // Update CAM View
        gameCam.update();

        // Render only CAM view to screen
        renderer.setView(gameCam);
    }

    public void playBackgroundMusic()
    {
        assetManager.load("audio/music/mario_music.ogg",Music.class);
        assetManager.finishLoading();

        backGroundMusic = assetManager.get("audio/music/mario_music.ogg",Music.class);
        backGroundMusic.setLooping(true);
        backGroundMusic.setVolume(0.05f);

        backGroundMusic.play();
    }

    public void stopBackgroundMusic()
    {
        backGroundMusic.stop();
    }

    // Called continuously
    @Override
    public void render(float delta) {
        // Separate update logic from render logic
        update(delta);

        //Clear Game screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();

        //render virtual controller
        if(Gdx.app.getType() == Application.ApplicationType.Android)
        {}
        controller.draw();

        //Draw on Screen the Score Board
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        game.batch.setProjectionMatrix(debugScene.stage.getCamera().combined);
        debugScene.stage.draw();

        //Box2D Debug Lines (Add before sprite animations)
        b2dr.render(world,gameCam.combined );

        //Sprite render
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        //Mario
        player.draw(game.batch);

        //Enemies
        for(net.kaparis.game.supermariobros.Sprites.Enemy enemy: worldCreator.getEnemies())
            enemy.draw(game.batch);

        //Items
        for(net.kaparis.game.supermariobros.Items.Item item: items)
            item.draw(game.batch);

        game.batch.end();

        if(gameOver()){
            game.setScreen((new net.kaparis.game.supermariobros.Scenes.GameOverScreen(game)));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public boolean gameOver(){
        if(player.currentState  == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
