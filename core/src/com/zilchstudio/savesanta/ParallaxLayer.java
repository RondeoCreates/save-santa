package com.zilchstudio.savesanta;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParallaxLayer extends Actor {

    Texture texture;
    float factor;
    boolean wrapHorizontally;
    boolean wrapVertically;

    public ParallaxLayer( Texture texture, float factor, boolean wrapHorizontally, boolean wrapVertically ) {
        this.texture = texture;
        this.factor = factor;
        this.wrapHorizontally = wrapHorizontally;
        this.wrapVertically = wrapVertically;

        this.texture.setWrap( 
            this.wrapHorizontally ? TextureWrap.Repeat : TextureWrap.ClampToEdge, 
            this.wrapVertically ? TextureWrap.Repeat : TextureWrap.ClampToEdge
        );
    }

    int xOffset;
    int yOffset;
    TextureRegion region = new TextureRegion();

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        xOffset = ( int ) ( getStage().getCamera().position.x * factor );
        yOffset = ( int ) ( getStage().getCamera().position.y * factor );

        setBounds( getStage().getCamera().position.x - getStage().getCamera().viewportWidth / 2, getStage().getCamera().position.y - getStage().getCamera().viewportHeight / 2, getStage().getCamera().viewportWidth, getStage().getCamera().viewportHeight );

        region.setRegion( texture );
        /*region.setRegionX( xOffset % texture.getWidth() );
        region.setRegionY( yOffset % texture.getHeight() );
        region.setRegionWidth( wrapHorizontally ? ( int ) getStage().getCamera().viewportWidth : texture.getWidth() );
        region.setRegionHeight( wrapVertically ? ( int ) getStage().getCamera().viewportHeight : texture.getHeight() );*/

        region.setRegionX( xOffset % texture.getWidth() );
        region.setRegionY( 0 );
        region.setRegionWidth( (int) getWidth() );
        region.setRegionHeight( texture.getHeight() );

        float scale = ( getHeight() / texture.getHeight() );
        
        batch.draw( region, getX(), getY(), getWidth() * scale, getHeight() );
    }
    
}
