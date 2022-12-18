package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

public class BloodOverlay extends Actor implements Disposable {
    boolean takenHit = false;
    Texture bloodTexture;
    
    public BloodOverlay() {
        bloodTexture = new Texture( Gdx.files.internal( "blood_overlay.png" ) );
    }

    @Override
    public void act(float delta) {
        if( !takenHit )
            return;
        
        setX( getStage().getCamera().position.x - getStage().getCamera().viewportWidth/2 );
        setY( getStage().getCamera().position.y - getStage().getCamera().viewportHeight/2 );
        setWidth( getStage().getCamera().viewportWidth );
        setHeight( getStage().getCamera().viewportHeight );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if( !takenHit )
            return;
        batch.draw( bloodTexture, getX(), getY(), getWidth(), getHeight() );
    }

    @Override
    public void dispose() {
        bloodTexture.dispose();
    }

}
