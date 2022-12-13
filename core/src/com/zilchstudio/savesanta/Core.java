package com.zilchstudio.savesanta;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dongbat.jbump.World;

public class Core extends ApplicationAdapter {
    ExtendViewport viewport;
    Stage stage;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    World<Entity> world;
    Player dino;
    Vector2 dinoPosition = new Vector2();
    Vector2 cameraPosition = new Vector2();

    @Override
    public void create () {
        viewport = new ExtendViewport( 100, 50 );
        stage = new Stage( viewport );

        float tileSize = 24f;
        float tile2px = 1f / 4.8f;

        world = new World<Entity>( 5f );

        tiledMap = new TmxMapLoader().load("save-santa.tmx");

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get( 0 );
        for( int x = 0; x < layer.getWidth(); x ++ ) {
            for( int y = 0; y < layer.getHeight(); y ++ ) {

                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if( cell == null ) continue;

                float trueX = x * (tileSize * tile2px);
                float trueY = y * (tileSize * tile2px);

                if( cell.getTile().getProperties().containsKey( "solid" ) ) {
                    stage.addActor( new Solid( trueX, trueY, world, tileSize * tile2px ) );
                }

                if( cell.getTile().getProperties().containsKey( "dino" ) ) {
                    dino = new Player( trueX , trueY, world );
                    dino.tile2px = tile2px;
                    dino.tileSize = tileSize;

                    stage.addActor( dino );
                    cell.setTile(null);
                }

                
            }
        }
        
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, tile2px );

        //stage.setDebugAll( true );
    }

    @Override
    public void render () {
        ScreenUtils.clear( Color.BLACK );

        
        tiledMapRenderer.setView( (OrthographicCamera) stage.getCamera() );
        tiledMapRenderer.render();

        stage.act();
        stage.draw();

        if( dino != null )
            dinoPosition.set( dino.x + ( dino.sub.x < 0 ? -10f : 10f ), dino.y );
        
        cameraPosition.set( MathUtils.lerp( stage.getCamera().position.x, dinoPosition.x, Gdx.graphics.getDeltaTime() * 2f), dinoPosition.y );
        stage.getCamera().position.set( cameraPosition.x, cameraPosition.y, 0 );

    }

   @Override
   public void resize(int width, int height) {
       viewport.update( width, height, true );
   }

    @Override
    public void dispose () {
    }

    public void log( String str ) {
        Gdx.app.log( "DEBUG", str );
    }

}
