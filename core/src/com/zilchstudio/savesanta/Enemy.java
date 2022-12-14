package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;

public class Enemy extends Actor implements Entity {

    TextureAtlas textures;
    Animation<TextureRegion> idle, run, hit, attack, dead;
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
    final float GRAVITY = -15f;
    float belowGround = 60f;
    float life = 3;

    boolean takenHit = false;
    boolean hitWall = false;
    boolean hitGround = false;
    boolean flip = false;
    boolean isDead = false;

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Solid ) {
                return Response.slide;
            }
            return null;
        }
    };

    public Enemy( float x, float y, float size, World<Entity> world ) {
        this.world = world;
        
        setBounds( x, y, size, size );
        textures = new TextureAtlas( Gdx.files.internal( "textures.pack" ) );

        idle = new Animation<TextureRegion>( .1f, textures.findRegions( "pig_idle" ), Animation.PlayMode.LOOP );
        run = new Animation<TextureRegion>( .1f, textures.findRegions( "pig_run" ), Animation.PlayMode.LOOP );
        hit = new Animation<TextureRegion>( .1f, textures.findRegions( "pig_hit" ), Animation.PlayMode.NORMAL );
        attack = new Animation<TextureRegion>( .1f, textures.findRegions( "pig_attack" ), Animation.PlayMode.NORMAL );
        dead = new Animation<TextureRegion>( .1f, textures.findRegions( "pig_dead" ), Animation.PlayMode.NORMAL );

        item = new Item<Entity>( this );
        world.add( item, x, y, size, size );

        weaponTexture = new Texture( Gdx.files.internal( "weapons/mp_40.png" ) );
        weaponRegion.setRegion( weaponTexture );
    }

    @Override
    public void act(float delta) {
        stateTime += delta;

        if( life <= 0 ) {
            life = 0;
            isDead = true;

            if( dead.isAnimationFinished( stateTime ) ) {
                world.remove( item );
                remove();
                textures.dispose();
            }

            return;
        }

        velocityX = 0;

        /*if( Gdx.input.isKeyPressed( Keys.A ) ) {
            velocityX = -120 * delta;
            flip = true;
        }
        if( Gdx.input.isKeyPressed( Keys.D ) ) {
            velocityX = 120 * delta;
            flip = false;
        }
        if( Gdx.input.isKeyJustPressed( Keys.W ) && hitGround ) {
            velocityY = 8f;
        }*/

        velocityY += GRAVITY * delta;

        hitWall = false;
        hitGround = false;
        result = world.move( item, getX() + velocityX, getY() + velocityY, collisionFilter );
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
        setPosition( rect.x, rect.y );

        if( hit.isAnimationFinished( stateTime ) ) {
            takenHit = false;
        }

    }

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        
        if( isDead ) {
            renderTexture = dead.getKeyFrame( stateTime );
        } else if( velocityX != 0 ) {
            renderTexture = run.getKeyFrame( stateTime );
        } else if( takenHit ) {
            renderTexture = hit.getKeyFrame( stateTime );
        } else {
            renderTexture = idle.getKeyFrame( stateTime );
        }

        batch.draw( renderTexture, !flip ? getX() + 10f + getWidth() : getX() - 10f, getY(), !flip ? - (getWidth() + 20f) : getWidth() + 20f, getHeight() + 20f );
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