package net.kaparis.game.supermariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.kaparis.game.supermariobros.Screens.PlayScreen;

public class MarioBrosGame extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;

	/*
	Box2D is an entirely independent of the graphics library used.
	Box2D works in units, LIBGDX works in pixels
	By Default, 1 pixel = 1 unit(meter) which is a bad idea. 10 pixels = 10 meter object, BOX2D will assign more gravity to it.
	Use this number to Convert pixels to units(like meters)
	Suggestion: Work with World Units instead of pixels in codebase.
	*/
	public static final float PPM = 100f;//pixels per meter

	//Box2D Collision Bits - Powers of 2
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));

	}

	@Override
	public void render () {
		super.render();

/*		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/
	}
}
