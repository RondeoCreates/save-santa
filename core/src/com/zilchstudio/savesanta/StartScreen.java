package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class StartScreen extends ScreenAdapter {
    Table table;
    Stage stage;
    Skin skin;
    Core parent;

    public StartScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new ExtendViewport( 600, 300 ) );
        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.BLACK );
        
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
