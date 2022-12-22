package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen extends ScreenAdapter {
    Stage stage;
    Table table;
    Skin skin;
    Core parent;

    public MenuScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new FitViewport( 800, 400 ) );
        skin = new Skin( Gdx.files.internal( "default_skin.json" ) );
        
        table = new Table( skin );
        table.setFillParent( true );
        stage.addActor( table );

        TextButton startGame = new TextButton( "Start", skin );
        startGame.pad( 5f, 20f, 5f, 20f );

        TextButton settings = new TextButton( "Settings", skin );
        settings.pad( 5f, 20f, 5f, 20f );

        TextButton exit = new TextButton( "Quit", skin );
        exit.pad( 5f, 20f, 5f, 20f );

        VerticalGroup vGroup = new VerticalGroup();
        vGroup.space( 5f );


        table.row();
        table.add( vGroup );

        startGame.addListener( new ClickListener(){
            @Override
            public void clicked( InputEvent event, float x, float y ) {
                parent.setScreen( new StartScreen( parent ) );
            }
        } );
        startGame.getLabel().setFontScale( .4f );

        settings.addListener( new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                parent.setScreen( new SettingScreen( parent ) );
            };
        } );
        settings.getLabel().setFontScale( .4f );

        exit.addListener( new ClickListener() {
            public void clicked( InputEvent event, float x, float y ) {
                Gdx.app.exit();
            };
        } );
        exit.getLabel().setFontScale( .4f );

        vGroup.addActor( startGame );
        vGroup.addActor( settings );
        vGroup.addActor( exit );

        table.add();

        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.valueOf( "#319ede" ) );
        
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height );
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
