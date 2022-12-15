package com.zilchstudio.savesanta;

import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class GameScreen extends ScreenAdapter {
    Stage stage;
    Group background, middle, foreground, playerGroup, foregroundFront;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    StagedTileMapRenderer stagedTileMapRenderer;
    Core parent;

    World<Entity> world;
    Player dino;
    Vector2 dinoPosition = new Vector2();
    Vector2 cameraPosition = new Vector2();

    ParallaxLayer[] layers;
    ParticleEffectActor particleEffectActor;

    final float TILE_SIZE = 24f;

    public GameScreen( Core parent ) {
        this.parent = parent;
        Static.gameOvermsg = "Christmas is in grave danger";
    }

    @Override
    public void show() {
        stage = new Stage( new ExtendViewport( 600, 300 ) );
        stage.addActor( background = new Group() );
        stage.addActor( middle = new Group() );
        stage.addActor( foreground = new Group() );
        stage.addActor( playerGroup = new Group() );
        stage.addActor( foregroundFront = new Group() );

        layers = new ParallaxLayer[4];
        layers[0] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/sky.png" ) ), .1f, true, false );
        layers[1] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/clouds_bg.png" ) ), .2f, true, false );
        layers[2] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/glacial_mountains.png" ) ), .4f, true, false );
        layers[3] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/clouds_mg_2.png" ) ), .5f, true, false );
        //layers[4] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/clouds_mg_2.png" ) ), .8f, true, false );
        //layers[5] = new ParallaxLayer( new Texture( Gdx.files.internal( "bglayers/clouds_mg_1.png" ) ), 1f, true, false );
        for( ParallaxLayer pa_layer : layers ) {
            background.addActor( pa_layer );
        }

        world = new World<Entity>( TILE_SIZE );

        tiledMap = new TmxMapLoader().load( "save-santa.tmx" );
        tiledMapRenderer = new OrthogonalTiledMapRenderer( tiledMap );
        stagedTileMapRenderer = new StagedTileMapRenderer( tiledMapRenderer );
        middle.addActor( stagedTileMapRenderer );
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get( 0 );
        for( int x = 0; x < layer.getWidth(); x ++ ) {
            for( int y = 0; y < layer.getHeight(); y ++ ) {

                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if( cell == null ) continue;

                float trueX = x * TILE_SIZE;
                float trueY = y * TILE_SIZE;

                if( cell.getTile().getProperties().containsKey( "barracks" ) ) {
                    foreground.addActor( new Barracks( trueX, trueY, TILE_SIZE, world ) );
                    continue;
                }

                if( cell.getTile().getProperties().containsKey( "solid" ) ) {
                    background.addActor( new Solid( trueX, trueY, world, TILE_SIZE ) );
                }

                if( cell.getTile().getProperties().containsKey( "dino" ) ) {
                    dino = new Player( trueX , trueY, TILE_SIZE, world );
                    playerGroup.addActor( dino );
                    cell.setTile(null);
                    continue;
                }

                
            }
        }

        // Particle Effect
        foregroundFront.addActor( particleEffectActor = new ParticleEffectActor() );

        //stage.setDebugAll( true );
        time_limit += TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() );
    }

    long time_limit = 60 * 2;

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.BLACK );

        if( TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() ) > time_limit ) {
            dino.instantDeath();
            Static.gameOvermsg = "You've ran out of time.";
        }

        stage.act();
        stage.draw();

        //tiledMapRenderer.setView( (OrthographicCamera) stage.getCamera() );
        //tiledMapRenderer.render();

        if( dino != null )
            dinoPosition.set( dino.getX() + ( dino.sub.x < 0 ? -100f : 100f ), dino.getY());
        
        cameraPosition.set( MathUtils.lerp( stage.getCamera().position.x, dinoPosition.x, Gdx.graphics.getDeltaTime() * 2f), dinoPosition.y );
        stage.getCamera().position.set( cameraPosition.x, cameraPosition.y, 0 );

        if( Rumble.getRumbleTimeLeft() > 0 ) {
            Rumble.tick( Gdx.graphics.getDeltaTime() );
            stage.getCamera().translate( Rumble.getPos() );
        }

        if( dino.life <= 0 ) {
            parent.setScreen( new GameOverScreen( parent ) );
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
    }

    @Override
    public void hide() {
        stage.dispose();
        for( Item item : world.getItems() ) {
            ((Entity)item.userData).dispose();
            //world.remove( item );
        }
        for( ParallaxLayer layer : layers ) {
            layer.dispose();
        }
        particleEffectActor.dispose();
    }
    
}
