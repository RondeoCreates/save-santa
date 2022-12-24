package com.zilchstudio.savesanta;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;


/**
 * Free assets
 * https://pixelfrog-assets.itch.io/kings-and-pigs
 * https://elthen.itch.io/2d-pixel-art-santa-sprites
 * https://vnitti.itch.io/glacial-mountains-parallax-background
 * https://blackdragon1727.itch.io/free-trap-platformer
 * https://spirit-warrior.itch.io/cold-valleys-winter-themed-pixel-art-asset-pack
 * https://wenrexa.itch.io/laser2020
 * https://arks.itch.io/dino-characters
 * https://0x42-0x4f-0x49.itch.io/pixel-art-weapons
 * https://ansimuz.itch.io/explosion-animations-pack
 * https://gamedevelopershlok.itch.io/heartpack
 * 
 * https://www.dafont.com/
 * https://github.com/raeleus/Particle-Park
 */
public class Core extends Game {

    Music bgm;
    Preferences preferences;
    
    @Override
    public void create() {
        Pixmap pm = new Pixmap( Gdx.files.internal("crosshair.png") );
        Gdx.graphics.setCursor( Gdx.graphics.newCursor(pm, 16, 16 ) );
        pm.dispose();

        //Gdx.graphics.setFullscreenMode( Gdx.graphics.getDisplayMode() );

        setSound();

        setScreen( new MenuScreen( this ) );

        bgm = Gdx.audio.newMusic( Gdx.files.internal( "bgm.wav" ) );
        bgm.setLooping( true );
        bgm.setVolume( .3f * Static.bgmVol );
        bgm.play();
    }

    public void setSound() {
        preferences = Gdx.app.getPreferences( "save-santa-settings" );
        Static.bgmVol = preferences.getFloat( "bgmVol", .5f );
        Static.sfxVol = preferences.getFloat( "sfxVol", .5f );
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
       super.resize( width, height );
    }

    @Override
    public void dispose() {
        super.dispose();
        bgm.dispose();
    }
}
