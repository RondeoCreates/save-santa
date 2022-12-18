package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class EndScreen extends ScreenAdapter {
    Table table;
    Stage stage;
    Skin skin;
    Core parent;
    Label gameOverLabel;
    Label gameOverMsg;
    TextButton easy, normal, hard;

    public EndScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new ExtendViewport( 600, 300 ) );
        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );

        gameOverLabel = new Label( "GOOD JOB!", new LabelStyle( skin.getFont( "F04b" ), Color.WHITE ) );
        LabelStyle style = new LabelStyle( skin.getFont( "caviardreams" ), Color.WHITE );
        String diff = "Easy";
        switch( Static.difficulty ) {
            case 1:
                diff = "Normal";
                break;
            case 2:
                diff = "Hard";
                break;
            default:
                diff = "Easy";    
        }
        gameOverMsg = new Label( "TimeLeft: " + Static.timeLeft + "\nDifficulty: " + diff + "\nKidnap Santa again?", style );
        gameOverMsg.setAlignment( Align.center );

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
