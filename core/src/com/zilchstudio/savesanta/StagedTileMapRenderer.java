package com.zilchstudio.savesanta;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class StagedTileMapRenderer extends Actor{
    TiledMapRenderer tiledMapRenderer;

    public StagedTileMapRenderer( TiledMapRenderer renderer ) {
        tiledMapRenderer = renderer;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        tiledMapRenderer.setView( (OrthographicCamera) getStage().getCamera() );
        tiledMapRenderer.render();
    }
    
}
