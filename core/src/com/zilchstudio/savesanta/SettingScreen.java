package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SettingScreen extends ScreenAdapter {
    Stage stage;
    Table table;
    Skin skin;
    Core parent;

    Slider bgm, sfx;

    public SettingScreen( Core parent ) {
        this.parent = parent;
    }

    Preferences preferences;

    @Override
    public void show() {
        stage = new Stage( new FitViewport( 800, 400 ) );
        skin = new Skin( Gdx.files.internal( "default_skin.json" ) );
        
        table = new Table( skin );
        table.setFillParent( true );
        stage.addActor( table );

        LabelStyle style = new LabelStyle( skin.getFont( "caviardreams" ), Color.WHITE );

        Label bgmLabel = new Label( "BGM ", style );
        bgm = new Slider( .2f, 1f, .01f, false, skin );
        bgm.setValue( Static.bgmVol );

        table.add( bgmLabel );
        table.add( bgm );

        table.row();

        Label sfxLabel = new Label( "SFX ", style );
        sfx = new Slider( .2f, 1f, .01f, false, skin );
        sfx.setValue( Static.sfxVol );

        table.add( sfxLabel );
        table.add( sfx );

        Gdx.input.setInputProcessor( stage );

        preferences = Gdx.app.getPreferences( "save-santa-settings" );

        bgm.addListener( new ChangeListener() {
            public void changed( ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor ) {
                preferences.putFloat( "bgmVol", bgm.getValue() );
                preferences.flush();
                Static.bgmVol = bgm.getValue();
                parent.bgm.setVolume( .3f * bgm.getValue() );
            };
        } );

        sfx.addListener( new ChangeListener() {
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                preferences.putFloat( "sfxVol", sfx.getValue() );
                preferences.flush();
                Static.sfxVol = sfx.getValue();
            };
        } );

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
