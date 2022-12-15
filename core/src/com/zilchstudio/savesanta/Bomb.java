package com.zilchstudio.savesanta;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;

public class Bomb extends Actor implements Entity {

    Vector2 from, to;
    Vector2 tempVector = new Vector2();
    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    float velocityX, velocityY;
    final float GRAVITY = -13f;
    Result result;
    Collision collision;
    Random random = new Random();
    TextureAtlas textures;
    Animation<TextureRegion> explosion;
    float stateTime;
    ArrayList<Item> items = new ArrayList<>();

    Texture texture;
    TextureRegion renderTexture;

    boolean takenHit = false;
    boolean hitWall = false;
    boolean hitGround = false;
    boolean isDead = false;
    boolean explode = false;
    boolean canHit = true;
    boolean soundPlaying = false;

    Sound whistle, explodeSound;

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Solid ) {
                explode = true;
                
                whistle.stop();
                if( !soundPlaying ) {
                    explodeSound.play( .5f, 1f, 0f );
                    soundPlaying = true;
                }
                
                return Response.slide;
            }
            return null;
        }
    };
    CollisionFilter playerSensor = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( item.userData instanceof Player && canHit  ) {
                if( ((Player) item.userData).takenHit )
                    return null;
                tempVector.set( ((Player) item.userData).getX() + ((Player) item.userData).getWidth()/2, ((Player) item.userData).getY() + ((Player) item.userData).getHeight()/2 );
                tempVector.sub( getX() + getWidth()/2, getY() + getHeight()/2 ).nor().scl( 2f );
                ((Player) item.userData).knockback.set( tempVector.x, 1f );
                ((Player) item.userData).takeHit();
                Rumble.rumble( 4f, .5f );
                canHit = false;
                return Response.slide;
            }
            return null;
        };
    };

    public Bomb( Vector2 from, Vector2 to, float size, World<Entity> world ) {
        this.from = from;
        this.to = to;
        this.world = world;

        item = new Item<Entity>( this );
        setBounds( from.x, from.y, size, size );
        world.add( item, getX(), getY(), getWidth(), getHeight() );

        texture = new Texture( Gdx.files.internal( "bomb.png" ) );

        velocityY = bounciness;

        textures = new TextureAtlas( Gdx.files.internal( "textures.pack" ) );
        explosion = new Animation<TextureRegion>( .1f, textures.findRegions( "explosion" ), Animation.PlayMode.NORMAL );

        whistle = Gdx.audio.newSound( Gdx.files.internal( "whistle.wav" ) );
        whistle.play( .4f, 1f, 0f );

        explodeSound = Gdx.audio.newSound( Gdx.files.internal( "explosion.wav" ) );
    }

    float bounciness = 7f;

    @Override
    public void act(float delta) {
        if( disposed )
            return;
        
        if( explode && explosion.isAnimationFinished( stateTime ) ) {
            isDead = true;

            world.remove( item );
            remove();
            textures.dispose();
            texture.dispose();

            return;
        }

        if( explode )
            stateTime += delta;

        velocityX = 0;
        from.lerp( to, delta );

        velocityY += GRAVITY * delta;

        hitWall = false;
        hitGround = false;
        result = world.move( item, explode ? getX() : from.x, getY() + velocityY, collisionFilter );
        for( int i = 0; i < result.projectedCollisions.size(); i ++ ) {
            collision = result.projectedCollisions.get( i );
            if( collision.other.userData instanceof Solid ) {
                if( collision.normal.x != 0 ) {
                    hitWall = true;
                }
                if( collision.normal.y != 0 && collision.normal.y == 1 ) {
                    hitGround = true;
                    velocityY = 0; //!explode ? collision.normal.y * (bounciness-- * random.nextFloat() ) : 0;
                }
            }
        }
        rect = world.getRect( item );
        setPosition( rect.x, rect.y );

        if( explode ) {
            world.queryRect( getX() - 30f, getY(), getWidth() + 60f, getHeight() + 20f, playerSensor, items );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if( disposed )
            return;

        if( isDead )
            return;

        if( explode ) {
            renderTexture = explosion.getKeyFrame( stateTime );
            batch.draw( renderTexture, getX() - 30f, getY(), getWidth() + 60f, getHeight() + 60f );
            return;
        }
            batch.draw( texture, getX(), getY(), getWidth(), getHeight() );
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        
        texture.dispose();
        textures.dispose();

        whistle.dispose();
    }
    
}
