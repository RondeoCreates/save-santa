package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
    ParticleEffect snowEffect;
    public ParticleEffectActor() {

        snowEffect = new ParticleEffect();
        snowEffect.load( Gdx.files.internal("snow_effect.pe"), Gdx.files.internal("") );
        snowEffect.start();
    }

    Vector2 cameraPosition = new Vector2();
    Vector2 cameraSize = new Vector2();

    @Override
    public void act(float delta) {
        cameraPosition.set( getStage().getCamera().position.x, getStage().getCamera().position.y );
        cameraSize.set( getStage().getCamera().viewportWidth, getStage().getCamera().viewportHeight );
        
        snowEffect.getEmitters().first().setPosition( cameraPosition.x - cameraSize.x / 2, cameraPosition.y + cameraSize.y / 2 );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        snowEffect.draw( batch, Gdx.graphics.getDeltaTime() );
    }

    public void dispose() {
        snowEffect.dispose();
    }
    
}
