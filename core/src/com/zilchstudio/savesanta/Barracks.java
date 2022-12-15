package com.zilchstudio.savesanta;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class Barracks extends Actor implements Entity {
    Array<Enemy> enemies = new Array<>();
    World<Entity> world;
    Item<Entity> item;

    Random random = new Random();

    int life = 5;
    boolean isDead = false;
    boolean takeHit = false;

    Texture healthy, hit, dead;
    TextureRegion region = new TextureRegion();

    public Barracks( float x, float y, float size, World<Entity> world ) {
        setBounds( x, y, size, size );
        item = new Item<Entity>( this );
        world.add( item, x, y, size, size );
        this.world = world;

        healthy = new Texture( Gdx.files.internal( "cave_entrance_healthy.png" ) );
        hit = new Texture( Gdx.files.internal( "cave_entrance_hit.png" ) );
        dead = new Texture( Gdx.files.internal( "cave_entrance_dead.png" ) );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if( disposed )
            return;

        if( isDead ) {
            region.setRegion( dead );
        } else if( takeHit ) {
            region.setRegion( hit );
            takeHit = false;
        } else {
            region.setRegion( healthy );
        }

        batch.draw( region, getX(), getY(), getWidth(), getHeight() );
    }


    @Override
    public void act(float delta) {
        if( disposed )
            return;

        if( life <= 0 )
            isDead = true;
        
        if( life > 0 && TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() ) % 10 == 0 ) {
            //Gdx.app.log( "DEBUG", "spawning" );
            if( enemies.size < 4 ) {
                enemies.add( new Enemy( getX() + ( random.nextBoolean() ? - random.nextInt( 30 ) : random.nextInt( 30 ) ), getY(), getWidth(), world ) );
                getStage().addActor( enemies.get( enemies.size - 1 ) );
            }
        }

        for ( int i = 0; i < enemies.size; i ++ ) {
            if( enemies.get( i ).isDead ) {
                 enemies.removeIndex( i );
            }
        }

    }

    public void takeHit() {
        life --;
        takeHit = true;
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        
        healthy.dispose();
        hit.dispose();
        dead.dispose();
    }
    
}
