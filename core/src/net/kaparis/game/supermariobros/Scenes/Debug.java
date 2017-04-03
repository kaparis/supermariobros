package net.kaparis.game.supermariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by David on 12/25/2016.
 */
public class Debug implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private float playerX;
    private float playerY;

    Label playerLocation;
    Label playerCell;

    public Debug (SpriteBatch sb )
    {
        viewport = new FitViewport(net.kaparis.game.supermariobros.MarioBrosGame.V_WIDTH, net.kaparis.game.supermariobros.MarioBrosGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.left();
        table.top();
        table.setFillParent(true);
        table.debug();

        // Player Coordinates
        Label playerLocationTitle = new Label("Player Location", new Label.LabelStyle(new BitmapFont(), Color.WHITE) );
        playerLocationTitle.setFontScale(.5f);

        playerLocation = new Label("{X,Y}", new Label.LabelStyle(new BitmapFont(), Color.WHITE) );
        playerLocation.setFontScale(.5f);

        table.add(playerLocationTitle).expandX();
        table.add(playerLocation).expandX();

        // Cell Coordinates
        Label playerCellTitle = new Label("Cell Coordinates", new Label.LabelStyle(new BitmapFont(), Color.WHITE) );
        playerCellTitle.setFontScale(.5f);

        playerCell = new Label("{X,Y}", new Label.LabelStyle(new BitmapFont(), Color.WHITE) );
        playerCell.setFontScale(.5f);

        table.add(playerCellTitle).expandX();
        table.add(playerCell).expandX();

        //Add table to stage
        stage.addActor(table);
    }

    public void updatePlayerLocation(float x, float y){
        playerX = x;
        playerY = y;

        playerLocation.setText("X: " + x + " | Y:" + y);
    }

    public void updatePlayerCell(int x, int y)
    {
        playerCell.setText("X: " + x + " | Y:" + y);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
