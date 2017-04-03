package net.kaparis.game.supermariobros.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

import net.kaparis.game.supermariobros.Items.ItemDef;
import net.kaparis.game.supermariobros.Items.Mushroom;
import net.kaparis.game.supermariobros.Sprites.Mario;

/**
 * Created by David on 9/16/2016.
 */
public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 27+1;

    public Coin(net.kaparis.game.supermariobros.Screens.PlayScreen play, MapObject object) {
        super(play, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log(this.getClass().getSimpleName(), "on head hit");

        AssetManager assetManager = new AssetManager();
        assetManager.load("audio/sounds/coin.wav",Sound.class);
        assetManager.load("audio/sounds/bump.wav",Sound.class);
        assetManager.load("audio/sounds/powerup_spawn.wav",Sound.class);
        assetManager.finishLoading();

        TiledMapTileLayer.Cell cell = getCell();
        TiledMapTile tile = cell.getTile();
        int id = tile.getId();

        if(id == BLANK_COIN) {
            Sound soundEffectBump = assetManager.get("audio/sounds/bump.wav",Sound.class);
            soundEffectBump.play();
        }
        else {
            if(mapObject.getProperties().containsKey("mushroom")) {
                Vector2 vector2 = new Vector2(body.getPosition().x, body.getPosition().y + (16 / net.kaparis.game.supermariobros.MarioBrosGame.PPM));
                ItemDef itemDef = new ItemDef(vector2, Mushroom.class);
                screen.spawnItem(itemDef);

                Sound soundEffectPowerUp = assetManager.get("audio/sounds/powerup_spawn.wav", Sound.class);
                soundEffectPowerUp.play();
            } else
            {
                Sound soundEffectCoin = assetManager.get("audio/sounds/coin.wav", Sound.class);
                soundEffectCoin.play();
            }
        }

        // Update cell with new image
        cell.setTile(tileSet.getTile(BLANK_COIN));

        net.kaparis.game.supermariobros.Scenes.Hud.addScore(250);
    }
}
