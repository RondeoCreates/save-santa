package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameOverScreen extends ScreenAdapter {
    Table table;
    Stage stage;
    Skin skin;
    Core parent;
    Label gameOverLabel;
    Label gameOverMsg;
    Label startAgainLabel;

    public GameOverScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new ExtendViewport( 600, 300 ) );
        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );

        gameOverLabel = new Label( "GAME OVER", new LabelStyle( skin.getFont( "F04b" ), Color.WHITE ) );
        LabelStyle style = new LabelStyle( skin.getFont( "caviardreams" ), Color.WHITE );
        gameOverMsg = new Label( Static.gameOvermsg, style );
        startAgainLabel = new Label( "(Click anywhere to start again)", style );
        startAgainLabel.setFontScale( .5f );

        table.row();
        table.add( gameOverLabel );
        
        table.row();
        table.add( gameOverMsg );

        table.row();
        table.add().height( 40f );

        table.row();
        table.add( startAgainLabel );

        table.setFillParent( true );

        stage.addActor( table );
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.valueOf( "#319ede" ) );
        
        stage.act();
        stage.draw();

        if( Gdx.input.isButtonJustPressed( Buttons.LEFT ) ) {
            parent.setScreen( new GameScreen( parent ) );
        }
    }

    @Override
    public void hide() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
    }

}
