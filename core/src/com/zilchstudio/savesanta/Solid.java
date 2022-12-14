package com.zilchstudio.savesanta;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class Solid extends Actor implements Entity {
    Item<Entity> item;
    
    public Solid( float x, float y, World<Entity> world, float tileSize ) {

        item = new Item<Entity>( this );
        world.add( item, x, y, tileSize, tileSize );

        setBounds( x, y, tileSize, tileSize );

        //System.out.println( x + "," + y + ":" + tileSize +","+tileSize );
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        
    }

}
