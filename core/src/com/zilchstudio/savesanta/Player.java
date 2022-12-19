package com.zilchstudio.savesanta;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;

public class Player extends Actor implements Entity {

    TextureAtlas textures;
    Animation<TextureRegion> idle, run, hit;
    TextureRegion renderTexture;
    Texture weaponTexture;
    TextureRegion weaponRegion = new TextureRegion();
    Random random = new Random();

    float stateTime = 0f;
    float hitSafe = 3f;

    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    Result result;
    Collision collision;

    float velocityX = 0;
    float velocityY = 0;
    final float GRAVITY = -15f;
    float belowGround = 60f;
    float life = Static.diffPLife[Static.difficulty];

    boolean takenHit = false;
    boolean hitWall = false;
    boolean hitGround = false;
    boolean flip = false;
    boolean fastVelocity = false;
    boolean fire = false;
    boolean end = false;

    Touchpad moveTouchpad, fireTouchpad;
    
    Sound gunSound;

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Solid ) {
                return Response.slide;
            }
            return null;
        }
    };

    public Player( float x, float y, float size, World<Entity> world, Touchpad moveTouchpad, Touchpad fireTouchpad ) {
        this.world = world;
        this.moveTouchpad = moveTouchpad;
        this.fireTouchpad = fireTouchpad;
        
        setBounds( x, y, size, size );
        textures = new TextureAtlas( Gdx.files.internal( "textures.pack" ) );

        idle = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_idle" ), Animation.PlayMode.LOOP );
        run = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_run" ), Animation.PlayMode.LOOP );
        hit = new Animation<TextureRegion>( .1f, textures.findRegions( "dino_hit" ), Animation.PlayMode.NORMAL );

        item = new Item<Entity>( this );
        world.add( item, x, y, size, size );

        weaponTexture = new Texture( Gdx.files.internal( "weapons/ak47.png" ) );
        weaponRegion.setRegion( weaponTexture );

        gunSound = Gdx.audio.newSound( Gdx.files.internal( "cg1.wav" ) );
    }

    @Override
    public void act(float delta) {
        if( disposed )
            return;

        stateTime += delta;
        time ++;

        if( life <= 0 ) {
            life = 0;
            return;
        }

        if( end ) {
            velocityX = 0;
            velocityY = 0;
            return;
        }

        velocityX = 0;
        if( !takenHit ) {
            if( Static.mobileDevice ) {
                if( moveTouchpad.getKnobPercentY() > .5f && hitGround ) {
                    velocityY = 8f;
                }
                if( moveTouchpad.getKnobPercentX() < -.5f ) {
                    velocityX = -120 * delta;
                    flip = true;
                }
                if( moveTouchpad.getKnobPercentX() > .5f ) {
                    velocityX = 120 * delta;
                    flip = false;
                }
            } else {
                if( Gdx.input.isKeyPressed( Keys.A ) ) {
                    velocityX = -120 * delta;
                    flip = true;
                }
                if( Gdx.input.isKeyPressed( Keys.D ) ) {
                    velocityX = 120 * delta;
                    flip = false;
                }
                if( (Gdx.input.isKeyJustPressed( Keys.W ) || Gdx.input.isKeyJustPressed( Keys.SPACE ) ) && hitGround ) {
                    velocityY = 8f;
                }
            }
            
        } else {
            velocityX = knockback.x;
            velocityY = knockback.y;
        }

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
        Static.x = getX();
        Static.y = getY();

        if( hit.isAnimationFinished( stateTime ) ) {
            takenHit = false;
        }
        
        if( /*velocityY < -12f*/ getY() < 300f ) {
            fastVelocity = true;
        }

        if( fastVelocity && hitGround ) {
            instantDeath();
            Static.gameOvermsg = "You hit the ground too hard.";   
        }
    }

    float centerX, centerY;
    Vector3 touchPoint = new Vector3();
    Vector2 touchPointV2 = new Vector2();
    float angleDeg = 0f;
    public Vector2 sub = new Vector2();
    Bullet tempBullet;
    float recoil = 0f;
    long time = 0;
    long trigger_time = 0;
    Vector2 knockback = new Vector2();

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( disposed )
            return;
        
        if( life < 0 ) {
            renderTexture = hit.getKeyFrame( stateTime );
        } else if( takenHit ) {
            renderTexture = hit.getKeyFrame( stateTime );
        } else if( velocityX != 0 ) {
            renderTexture = run.getKeyFrame( stateTime );
        } else {
            renderTexture = idle.getKeyFrame( stateTime );
        }

        centerX = getX() + getWidth() / 2;
        centerY = getY() + getHeight() / 2;
        if( Static.mobileDevice ) {
            if( fireTouchpad.getKnobPercentX() != 0 || fireTouchpad.getKnobPercentY() != 0 ) {
                touchPoint.set( fireTouchpad.getKnobPercentX(), fireTouchpad.getKnobPercentY(), 0 );
                touchPoint.scl( 10f );
                sub = touchPointV2.set( touchPoint.x, touchPoint.y );
                angleDeg = sub.angleDeg();
            }
        } else {
            touchPoint.set( Gdx.input.getX(), Gdx.input.getY(), 0 );
            touchPoint = getParent().getStage().getCamera().unproject( touchPoint );
            sub = touchPointV2.set( touchPoint.x, touchPoint.y ).sub( centerX, centerY );
            angleDeg = sub.angleDeg();
        }

        batch.draw( renderTexture, sub.x < 0  ? getX() + 5f + getWidth() : getX() - 5f, getY() - 5f, sub.x < 0  ? - (getWidth() + 10f) : getWidth() + 10f, getHeight() + 10f );

        if( !takenHit ) {
            recoil = fire ? random.nextFloat() * 4f : 0;
            batch.draw( weaponRegion, centerX + MathUtils.lerp( 0, recoil, Gdx.graphics.getDeltaTime() * 100f ), centerY - 11f, 0, 0, weaponRegion.getRegionWidth(), sub.x < 0 ? - weaponRegion.getRegionHeight() : weaponRegion.getRegionHeight(), .4f, .4f, angleDeg );
        }

        if( life <= 0 || end )
            return;

        fire = false;
        if( Static.mobileDevice ) {
            if( fireTouchpad.getKnobPercentX() != 0 || fireTouchpad.getKnobPercentY() != 0 ) {
                if( time <= trigger_time + 6f )
                    return;
                shoot();
            }
        } else {
            if( Gdx.input.isButtonJustPressed( Buttons.LEFT ) ) {
                shoot();
            } else if( Gdx.input.isButtonPressed( Buttons.LEFT ) && !takenHit ) {
                if( time <= trigger_time + 6f )
                    return;
                shoot();
            }
        }
        
    }

    void shoot() {
        trigger_time = time;
            gunSound.play( .3f, random.nextFloat() * 1f + .5f, 0f );
            tempBullet = new Bullet( new Vector2( centerX, centerY ).add( new Vector2( sub ).nor().scl( 30f ) ), touchPointV2, world );
            getStage().addActor( tempBullet );
            fire = true;
    }

    public void takeHit() {
        if( takenHit || hitSafe >= stateTime )
            return;
        takenHit = true;
        stateTime = 0;
        life --;
    }

    public void instantDeath() {
        takeHit();
        life = 0;
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        
        textures.dispose();
        weaponTexture.dispose();

        gunSound.dispose();
    }
}
