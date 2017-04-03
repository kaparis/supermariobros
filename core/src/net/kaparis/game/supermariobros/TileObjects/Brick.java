package net.kaparis.game.supermariobros.TileObjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;

import net.kaparis.game.supermariobros.Sprites.Mario;

/**
 * Created by David on 9/16/2016.
 */
public class Brick extends InteractiveTileObject {
    public Brick (net.kaparis.game.supermariobros.Screens.PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            setCategoryFilter(net.kaparis.game.supermariobros.MarioBrosGame.DESTROYED_BIT);
            getCell().setTile(null);
            net.kaparis.game.supermariobros.Scenes.Hud.addScore(200);

            AssetManager assetManager = new AssetManager();
            assetManager.load("audio/sounds/breakblock.wav", Sound.class);
            assetManager.finishLoading();
            Sound soundEffect = assetManager.get("audio/sounds/breakblock.wav", Sound.class);
            soundEffect.play();
            assetManager.load("", Sound.class);
        } else {
            AssetManager assetManager = new AssetManager();
            assetManager.load("audio/sounds/bump.wav", Sound.class);
            assetManager.finishLoading();
            Sound soundEffect = assetManager.get("audio/sounds/bump.wav", Sound.class);
            soundEffect.play();
            assetManager.load("", Sound.class);
        }

    }
}
