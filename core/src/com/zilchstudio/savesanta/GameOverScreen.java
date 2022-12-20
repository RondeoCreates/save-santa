package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen extends ScreenAdapter {
    Table table;
    Stage stage;
    Skin skin;
    Core parent;
    Label gameOverLabel;
    Label gameOverMsg;
    TextButton easy, normal, hard;

    public GameOverScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new FitViewport( 800, 400 ) );
        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );

        gameOverLabel = new Label( "GAME OVER", new LabelStyle( skin.getFont( "F04b" ), Color.WHITE ) );
        LabelStyle style = new LabelStyle( skin.getFont( "caviardreams" ), Color.WHITE );
        gameOverMsg = new Label( Static.gameOvermsg, style );
        
        HorizontalGroup diffHorizontalGroup = new HorizontalGroup();
        diffHorizontalGroup.space( 5f );
        easy = new TextButton( "Easy", skin );
        easy.addListener( new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Static.difficulty = 0;
                parent.setScreen( new GameScreen( parent ) );
            };
        } );
        easy.pad( 5f, 20f, 5f, 20f );
        easy.getLabel().setFontScale( .4f );
        diffHorizontalGroup.addActor( easy );
        
        normal = new TextButton( "Normal", skin );
        normal.addListener( new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Static.difficulty = 1;
                parent.setScreen( new GameScreen( parent ) );
            };
        } );
        normal.pad( 5f, 20f, 5f, 20f );
        normal.getLabel().setFontScale( .4f );
        diffHorizontalGroup.addActor( normal );

        hard = new TextButton( "Hard" , skin );
        hard.addListener( new ClickListener() {
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Static.difficulty = 2;
                parent.setScreen( new GameScreen( parent ) );
            };
        } );
        hard.pad( 5f, 20f, 5f, 20f );
        hard.getLabel().setFontScale( .4f );
        diffHorizontalGroup.addActor( hard );

        table.row();
        table.add( gameOverLabel ).pad( 3f );
        
        table.row();
        table.add( gameOverMsg ).pad( 3f );

        table.row();
        table.add().height( 40f );

        table.row();
        table.add( diffHorizontalGroup );

        table.setFillParent( true );

        stage.addActor( table );

        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.valueOf( "#319ede" ) );
        
        stage.act();
        stage.draw();
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
