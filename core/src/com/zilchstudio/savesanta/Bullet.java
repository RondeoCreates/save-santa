package com.zilchstudio.savesanta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;

public class Bullet extends Actor implements Entity {
    
    Vector2 direction = new Vector2();
    Rectangle rectangle = new Rectangle();
    Texture texture;
    TextureRegion textureRegion = new TextureRegion();

    Item<Entity> item;
    World<Entity> world;
    Rect rect;
    
    boolean dead = false;

    CollisionFilter filter = new CollisionFilter() {

        @Override
        public Response filter(Item item, Item other) {
            if( dead )
                return null;
                
            if( other.userData instanceof Barracks ) {
                if( ((Barracks) other.userData ).isDead )
                    return null;
                ((Barracks) other.userData ).takeHit();
                Rumble.rumble( 1f, .5f );
            }
            if( other.userData instanceof Enemy ) {
                ((Enemy) other.userData ).takeHit();
                Rumble.rumble( 1f, .5f );
            }

            if( !(other.userData instanceof Player) && !(other.userData instanceof Bomb) )
                dead = true;

            return null;
        }
        
    };

    public Bullet( Vector2 pointA, Vector2 pointB, World<Entity> world ) {
        this.world = world;
        
        setBounds( pointA.x, pointA.y, 5, 5);
        item = new Item<Entity>( this );
        world.add( item, getX(), getY(), getWidth(), getHeight() );

        direction.set( pointB ).nor();

        texture = new Texture( Gdx.files.internal( "bullet.png" ) );

        textureRegion.setRegion( texture );
    }

    @Override
    public void act(float delta) {
        if( disposed )
            return;

        if( dead ) {
            world.remove( item );
            remove();
            texture.dispose();
        }

        if( dead ) {
            return;
        }

        world.move( item, getX() + ( direction.x * 1200f ) * delta, getY() + ( direction.y * 1200f ) * delta, filter );
        rect = world.getRect( item );
        setPosition( rect.x, rect.y );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if( disposed )
            return;

        if( dead )
            return;
        //batch.draw( texture, getX() - 10f, getY() - 10f, getWidth() + 20f, getHeight() + 20f );
        batch.draw( textureRegion, getX() - 1f, getY() - 1f, (getWidth() + 2f) / 2, (getHeight() + 2f) / 2, getWidth() + 2f, getHeight() + 2f, 1f, 1f, direction.angleDeg() );
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        
        texture.dispose();
    }

}
