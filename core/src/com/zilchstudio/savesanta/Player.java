package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;

public class Player extends Actor implements Entity {

    public float x;
    public float y;
    public float tile2px = 1f / 5f;
    public float tileSize = 24f;

    TextureAtlas textures;
    Animation<TextureRegion> idle, run, hit;
    TextureRegion renderTexture;
    Texture weaponTexture;
    TextureRegion weaponRegion = new TextureRegion();

    float stateTime = 0f;

    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    Result result;
    Collision collision;

    float velocityX = 0;
    float velocityY = 0;
    final float GRAVITY = -3f;
    float belowGround = 60f;
    float life = 3;

    boolean takenHit = false;
    boolean hitWall = false;
    boolean hitGround = false;
    boolean flip = false;
    
    String gameOvermsg = "Game Over";

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Solid ) {
                return Response.slide;
            }
            return null;
        }
    };
    
    public Player( float x, float y, World<Entity> world ) {
        this.x = x;
        this.y = y;
        this.world = world;

        textures = new TextureAtlas( Gdx.files.internal( "textures.pack" ) );

        idle = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_idle" ), Animation.PlayMode.LOOP );
        run = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_run" ), Animation.PlayMode.LOOP );
        hit = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_hit" ), Animation.PlayMode.NORMAL );

        item = new Item<Entity>( this );
        world.add( item, x, y, tileSize * tile2px, tileSize * tile2px );

        weaponTexture = new Texture( Gdx.files.internal( "weapons/mp_40.png" ) );
        weaponRegion.setRegion( weaponTexture );
    }

    @Override
    public void act( float delta ) {
        stateTime += delta;

        if( life <= 0 ) {
            life = 0;
            return;
        }

        velocityX = 0;

        if( Gdx.input.isKeyPressed( Keys.A ) ) {
            velocityX = -30 * delta;
            flip = true;
        }
        if( Gdx.input.isKeyPressed( Keys.D ) ) {
            velocityX = 30 * delta;
            flip = false;
        }
        if( Gdx.input.isKeyJustPressed( Keys.W ) && hitGround ) {
            velocityY = 1.5f;
        }

        velocityY += GRAVITY * delta;

        hitWall = false;
        hitGround = false;
        result = world.move( item, x + velocityX, y + velocityY, collisionFilter );
        for( int i = 0; i < result.projectedCollisions.size(); i ++ ) {
            collision = result.projectedCollisions.get( i );
            if( collision.other.userData instanceof Solid ) {
                if( collision.normal.x != 0 ) {
                    hitWall = true;
                }
                if( collision.normal.y != 0 && collision.normal.y == 1 ) {
                    hitGround = true;
                    velocityY = 0;
                }
            }
        }
        rect = world.getRect( item );
        x = rect.x;
        y = rect.y;

        setBounds( x, y, tileSize * tile2px, tileSize * tile2px );

        if( hit.isAnimationFinished( stateTime ) ) {
            takenHit = false;
        }

        if( y <= belowGround && hitGround ) {
            instantDeath();
            gameOvermsg = "You hit the ground too hard.";   
        }

    }

    float centerX, centerY;
    Vector3 touchPoint = new Vector3();
    Vector2 touchPointV2 = new Vector2();
    float angleDeg = 0f;
    public Vector2 sub = new Vector2();

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        
        if( velocityX != 0 ) {
            renderTexture = run.getKeyFrame( stateTime );
        } else if( takenHit ) {
            renderTexture = hit.getKeyFrame( stateTime );
        } else {
            renderTexture = idle.getKeyFrame( stateTime );
        }

        centerX = x + ( tileSize * tile2px ) / 2;
        centerY = y + ( tileSize * tile2px ) / 2;

        touchPoint.set( Gdx.input.getX(), Gdx.input.getY(), 0 );

        touchPoint = getParent().getStage().getCamera().unproject( touchPoint );

        sub = touchPointV2.set( touchPoint.x, touchPoint.y ).sub( centerX, centerY );
        angleDeg = sub.angleDeg();

        batch.draw( renderTexture, sub.x < 0  ? x + 1f + tileSize * tile2px : x - 1f, y - 1f, sub.x < 0  ? - (tileSize * tile2px + 2f) : tileSize * tile2px + 2f, tileSize * tile2px + 2f );

        if( !takenHit )
            batch.draw( weaponRegion, centerX, centerY, 0, - weaponRegion.getRegionHeight() * .1f, weaponRegion.getRegionWidth(), sub.x < 0 ? - weaponRegion.getRegionHeight() : weaponRegion.getRegionHeight(), .15f, .15f, angleDeg );
    }

    public void takeHit() {
        if( takenHit )
            return;
        takenHit = true;
        stateTime = 0;
        life --;
    }

    public void instantDeath() {
        takeHit();
        life = 0;
    }

}
