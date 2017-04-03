package net.kaparis.game.supermariobros.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by David on 11/8/2016.
 */

public class Mushroom extends Item {

    public Mushroom(net.kaparis.game.supermariobros.Screens.PlayScreen screen, float x, float y, float PPM) {
        super(screen, x, y, PPM);

        //Setup region of where image is
        setRegion(screen.getAtlas().findRegion("mushroom"),0,0,16,16);

        this.velocity = new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius( 6 / PPM);

        //This fixture is
        fdef.filter.categoryBits = net.kaparis.game.supermariobros.MarioBrosGame.ITEM_BIT;
        // This fixture can collide with
        fdef.filter.maskBits = net.kaparis.game.supermariobros.MarioBrosGame.MARIO_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.OBJECT_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.GROUND_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.COIN_BIT |
                net.kaparis.game.supermariobros.MarioBrosGame.BRICK_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(net.kaparis.game.supermariobros.Sprites.Mario mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if(!toDestroy) {
            float x = body.getPosition().x - getWidth() / 2;
            float y = body.getPosition().y - getHeight() / 2;
            setPosition(x,y);

            velocity.y = body.getLinearVelocity().y;
            body.setLinearVelocity(velocity);
        }
    }
}
