package com.zilchstudio.savesanta;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;

public class Barracks extends Actor implements Entity {
    Array<Enemy> enemies = new Array<>();
    World<Entity> world;
    Item<Entity> item;

    Random random = new Random();

    int life = 5;
    boolean isDead = false;
    boolean takeHit = false;
    boolean isStarted = true;

    Texture healthy, hit;
    TextureRegion region = new TextureRegion();
    TextureAtlas textures;
    Animation<TextureRegion> explosion;
    float stateTime;
    long clockTime = 0;

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( item.userData instanceof Enemy ) {
                  ((Enemy) item.userData).takeHit();
                return Response.slide;
            }
            return null;
        };
    };

    CollisionFilter playerSensor = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( item.userData instanceof Player )
                return Response.slide;
            return null;
        };
    };
    
    ArrayList<Item> items = new ArrayList<>();

    public Barracks( float x, float y, float size, World<Entity> world ) {

        setBounds( x, y, size, size );
        item = new Item<Entity>( this );
        world.add( item, x, y, size, size );
        this.world = world;

        healthy = new Texture( Gdx.files.internal( "cave_entrance_healthy.png" ) );
        hit = new Texture( Gdx.files.internal( "cave_entrance_hit.png" ) );

        textures = new TextureAtlas( Gdx.files.internal( "textures.pack" ) );
        explosion = new Animation<TextureRegion>( .1f, textures.findRegions( "explosion_barracks" ), Animation.PlayMode.NORMAL );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if( disposed )
            return;

        if( isDead ) {
            region.setRegion( explosion.getKeyFrame( stateTime ) );
        } else if( takeHit ) {
            region.setRegion( hit );
            takeHit = false;
        } else {
            region.setRegion( healthy );
        }

        if( isDead && explosion.isAnimationFinished( stateTime ) )
            return;

        if( isDead && !explosion.isAnimationFinished( stateTime ) ) {
            world.queryRect( getX() + (isDead? -50f : 0f ), getY(), getWidth() + ( isDead? 100f : 0f ), getHeight() + ( isDead? 100f : 0 ), collisionFilter, items );
        }

        batch.draw( region, getX() + (isDead? -50f : 0f ), getY(), getWidth() + ( isDead? 100f : 0f ), getHeight() + ( isDead? 100f : 0 ) );
    }


    @Override
    public void act(float delta) {
        //if( enemies.size <= Static.difficulties[Static.difficulty] - 1 )
            clockTime ++;

        if( disposed )
            return;

        stateTime += delta;

        if( life <= 0 && !isDead ) {
            isDead = true;
            stateTime = 0;
            Rumble.rumble( 10f, .8f );
        }
        
        if( life > 0 && clockTime % 150f == 0 || isStarted ) {
            isStarted = !isStarted;
            world.queryRect( getX() + (getWidth()/2) - 500f, getY() + (getHeight()/2) - 500f, 1000f, 1000f, playerSensor, items );
            if( items.size() > 0 )
                if( enemies.size < Static.difficulties[Static.difficulty] ) {
                    addPigs();
                }
        }

        for ( int i = 0; i < enemies.size; i ++ ) {
            if( enemies.get( i ).isDead ) {
                 enemies.removeIndex( i );
            }
        }

    }

    void addPigs() {
        enemies.add( new Enemy( getX() + ( random.nextBoolean() ? - random.nextInt( 30 ) : random.nextInt( 30 ) ), getY(), getWidth(), world ) );
        getStage().addActor( enemies.get( enemies.size - 1 ) );
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
    }
    
}
