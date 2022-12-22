package com.zilchstudio.savesanta;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

public class Enemy extends Actor implements Entity {

    TextureAtlas textures;
    Animation<TextureRegion> idle, run, hit, attack, dead;
    TextureRegion renderTexture;

    float stateTime = 0f;
    long time = 0;

    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    Result result;
    Collision collision;
    Random random = new Random();

    float velocityX = 0;
    float velocityY = 0;
    final float GRAVITY = -15f;
    float belowGround = 60f;
    float life = Static.diffLife[Static.difficulty];

    boolean takenHit = false;
    boolean hitWall = false;
    boolean hitGround = false;
    boolean flip = false;
    boolean isDead = false;
    boolean isAttacking = false;

    Sound pigSound, dieSound, hitSound;

    CollisionFilter collisionFilter = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Solid ) {
                return Response.slide;
            }
            return null;
        }
    };

    CollisionFilter playerSensor = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( item.userData instanceof Player )
                return Response.slide;
            return null;
        };
    };

    CollisionFilter fallSensor = new CollisionFilter() {
        public Response filter(Item item, Item other) {
            if( item.userData instanceof Solid )
                return Response.slide;
            return null;
        };
    };
    ArrayList<Item> items = new ArrayList<>();
    Player tempPlayer;
    Vector2 playerPosition = new Vector2();

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

        pigSound = Gdx.audio.newSound( Gdx.files.internal( "pig.wav" ) );
        hitSound = Gdx.audio.newSound( Gdx.files.internal( "pig_hit.wav" ) );
        dieSound = Gdx.audio.newSound( Gdx.files.internal( "pig_die.wav" ) );

        flip = random.nextBoolean();
    }

    Vector2 knockback = new Vector2();

    @Override
    public void act(float delta) {
        if( disposed )
            return;

        if( Static.end  )
            return;

        stateTime += delta;
        time ++;

        velocityX = 0;

        if( life > 0 ) {
            world.queryRect( getX() + (flip ? -24f : 24f), getY() - 24f, getWidth(), getHeight(), fallSensor, items );
            if( items.size() <= 0 || hitWall ) {
                flip = !flip;
            }

            if( flip )
                velocityX = -1f;
            else
                velocityX = 1f;
        }
        

        if( life <= 0 ) {
            if( !isDead ) {
                hitSound.stop();
                dieSound.play( .2f * Static.sfxVol, 1f, 0f );
            }
            life = 0;
            isDead = true;

            if( dead.isAnimationFinished( stateTime ) ) {
                world.remove( item );
                remove();
                textures.dispose();
                return;
            } else {
                velocityX = knockback.x;
            }

            
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

        if( hit.isAnimationFinished( stateTime ) ) {
            takenHit = false;
        }

        if( time % 100 == 0 && !isAttacking ) {
            world.queryRect( getX() + (getWidth()/2) - 250f, getY() + (getHeight()/2) - 250f, 500f, 500f, playerSensor, items );
            if( items.size() > 0 && !isAttacking ) {

                if( Static.difficulty > 1 )
                    throwBomb = random.nextBoolean();

                if( throwBomb ) {
                    isAttacking = true;
                    stateTime = 0;
                    tempPlayer = (Player) items.get( 0 ).userData;
                    playerPosition.set( tempPlayer.getX(), tempPlayer.getY() );
                    getStage().addActor( new Bomb( new Vector2( getX() + getWidth()/2, getY() + getHeight()/2 ), playerPosition, 24f, world ) );
                }
            }
            
        }

        if( attack.isAnimationFinished( stateTime ) ) {
            isAttacking = false;
        }

    }

    boolean throwBomb = true;

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( disposed )
            return;
        
        if( isDead ) {
            renderTexture = dead.getKeyFrame( stateTime );
        } else if( takenHit ) {
            renderTexture = hit.getKeyFrame( stateTime );
        } else if( isAttacking ) {
            renderTexture = attack.getKeyFrame( stateTime );
        } else if( velocityX != 0 ) {
            renderTexture = run.getKeyFrame( stateTime );
        } else {
            renderTexture = idle.getKeyFrame( stateTime );
        }

        batch.draw( renderTexture, !flip ? getX() + 10f + getWidth() : getX() - 10f, getY(), !flip ? - (getWidth() + 20f) : getWidth() + 20f, getHeight() + 20f );
    }

    public void takeHit() {
        if( takenHit )
            return;
        
        hitSound.stop();
        hitSound.play( 1f * Static.sfxVol );
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
        pigSound.dispose();
        hitSound.dispose();
        dieSound.dispose();
    }
}