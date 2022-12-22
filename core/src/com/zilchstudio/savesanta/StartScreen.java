package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StartScreen extends ScreenAdapter {
    Table table;
    Stage stage;
    Skin skin;
    Core parent;
    Label titleLabel;
    Label message;
    Label mechanics;
    Label tut_1_lbl, tut_2_lbl, tut_3_lbl, tut_4_lbl;
    Texture tut_1, tut_2, tut_3, tut_4;
    HorizontalGroup tut_1_g, tut_2_g, tut_3_g, tut_4_g;

    TextButton easy, normal, hard;

    public StartScreen( Core parent ) {
        this.parent = parent;
    }

    @Override
    public void show() {
        stage = new Stage( new FitViewport( 800, 500 ) );

        tut_1 = new Texture( Gdx.files.internal( "heart_live.png" ) );
        tut_2 = new Texture( Gdx.files.internal( "pig.png" ) );
        tut_3 = new Texture( Gdx.files.internal( "cave_entrance.png" ) );
        tut_4 = new Texture( Gdx.files.internal( "key.png" ) );
        
        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );

        titleLabel = new Label( "SAVE SANTA", new LabelStyle( skin.getFont( "F04b" ), Color.WHITE ) );
        String head =   "Hello Agent Dino. Santa is in need of help. He's been kidnapped by the piggy orcs on the piggy mountain. Without him, Christmas won't be as happy as it seems." +
                        " Please save and free Santa from the hands of the piggy orcs and Christmas will be happy again.";
        LabelStyle style = new LabelStyle( skin.getFont( "caviardreams" ), Color.WHITE );
        
        message = new Label( head, style );
        message.setFontScale( .5f );
        message.setWrap( true );
        message.setWidth( 550f );
        message.setAlignment( Align.center );
        
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

        mechanics = new Label( "Press A and D to move. Press W/SPACE to jump. LMB to Shoot.", style );
        mechanics.setFontScale( .5f );
        mechanics.setWrap( true );
        mechanics.setWidth( 550f );
        mechanics.setAlignment( Align.center );

        table.row();
        table.add( titleLabel );
        
        table.row();
        table.add( message ).width( 550f ).pad( 20f );

        table.row();
        tut_1_g = new HorizontalGroup();
        tut_1_g.pad( 3f );
        tut_1_g.addActor( new Image( tut_1 ) );
        tut_1_lbl = new Label( " You will be given [n] lives and [n] minutes to save Santa." , style );
        tut_1_lbl.setFontScale( .4f );
        tut_1_g.addActor( tut_1_lbl );
        table.add( tut_1_g );

        table.row();
        tut_2_g = new HorizontalGroup();
        tut_2_g.pad( 3f );
        tut_2_g.addActor( new Image( tut_2 ) );
        tut_2_lbl = new Label( " Piggy orcs are bad, kill them all." , style );
        tut_2_lbl.setFontScale( .4f );
        tut_2_g.addActor( tut_2_lbl );
        table.add( tut_2_g );

        table.row();
        tut_3_g = new HorizontalGroup();
        tut_3_g.pad( 3f );
        tut_3_g.addActor( new Image( tut_3 ) );
        tut_3_lbl = new Label( " Destroy their barracks to disable them from spawning over time." , style );
        tut_3_lbl.setFontScale( .4f );
        tut_3_g.addActor( tut_3_lbl );
        table.add( tut_3_g );

        table.row();
        tut_4_g = new HorizontalGroup();
        tut_4_g.pad( 3f );
        tut_4_g.addActor( new Image( tut_4 ) );
        tut_4_lbl = new Label( " Destroy all the barracks to obtain the key to open Santa's cage." , style );
        tut_4_lbl.setFontScale( .4f );
        tut_4_g.addActor( tut_4_lbl );
        table.add( tut_4_g );

        if( !Static.mobileDevice ) {
            table.row();
            table.add( mechanics ).width( 550f ).pad( 20f );
        }

        table.row();
        table.add( diffHorizontalGroup );

        table.row();
        table.add().height( 40f );

        table.row();
        TextButton back = new TextButton( "Back", skin );
        table.add( back ).colspan( 2 );
        back.addListener( new ClickListener() {
            public void clicked( com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y ) {
                parent.setScreen( new MenuScreen( parent ) );
            };
        } );
        back.pad( 5f, 20f, 5f, 20f );
        back.getLabel().setFontScale( .4f );

        table.setFillParent( true );

        stage.addActor( table );

        //stage.setDebugAll( true );

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
        tut_1.dispose();
        tut_2.dispose();
        tut_3.dispose();
        tut_4.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
    }

}
