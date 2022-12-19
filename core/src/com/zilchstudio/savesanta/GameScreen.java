package com.zilchstudio.savesanta;

import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class GameScreen extends ScreenAdapter {
    Stage stage;
    Stage hud;
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
    
    Table table;
    Skin skin;
    Texture heartImage, keyImage, timeImage;
    Touchpad moveTouchpad, fireTouchpad;
    Label lifeLabel, keyLabel, timeLabel;

    Texture santaTexture, dinoTexture;
    Label dialogLabel;
    Table dialog;
    Image dinoImage, santaImage;

    Array<Barracks> barracks = new Array<>();
    Barracks tempBarracks;
    Rectangle santaCage = new Rectangle();
    BloodOverlay bloodOverlay;

    Music scaryMusic;

    public GameScreen( Core parent ) {
        this.parent = parent;
        Static.gameOvermsg = "Christmas is in grave danger";

        hud = new Stage( new ExtendViewport( 800, 400 ) );

        table = new Table( skin = new Skin( Gdx.files.internal( "default_skin.json" ) ) );
        table.setFillParent( true );
        table.pad( 20f );

        heartImage = new Texture( Gdx.files.internal( "heart_live.png" ) );
        keyImage = new Texture( Gdx.files.internal( "key.png" ) );
        timeImage = new Texture( Gdx.files.internal( "time.png" ) );

        LabelStyle style = new LabelStyle( skin.getFont( "F04b" ), Color.WHITE );

        timeLabel = new Label( "00", style );
        timeLabel.setFontScale( .5f );
        lifeLabel = new Label( "x 5", style );
        lifeLabel.setFontScale( .5f );
        keyLabel = new Label( "x 0", style );
        keyLabel.setFontScale( .5f );

        table.row();
        HorizontalGroup timeGroup = new HorizontalGroup();
        timeGroup.addActor( new Image( timeImage ) );
        timeGroup.addActor( timeLabel );
        table.add( timeGroup ).align( Align.left ).fillX();
        table.add().expandX();
        
        table.row();
        HorizontalGroup heartGroup = new HorizontalGroup();
        heartGroup.addActor( new Image( heartImage ) );
        heartGroup.addActor( lifeLabel );
        table.add( heartGroup ).align( Align.left ).fillX();
        table.add().expandX();

        table.row();
        HorizontalGroup keyGroup = new HorizontalGroup();
        keyGroup.addActor( new Image( keyImage ) );
        keyGroup.addActor( keyLabel );
        table.add( keyGroup ).align( Align.left ).fillX();
        table.add().expandX();

        table.row();
        table.add().expandY();

        table.row();

        hud.addActor( table );

        santaTexture = new Texture( Gdx.files.internal( "santa.png" ) );
        dinoTexture = new Texture( Gdx.files.internal( "dino.png" ) );

        LabelStyle style2 = new LabelStyle( skin.getFont( "caviardreams" ), Color.BLACK );
        dialogLabel = new Label( "default", style2 );
        dialogLabel.setFontScale( .6f );

        dialog = new Table( skin );
        dialog.setBackground( skin.getDrawable( "window" ) );
        dialog.pad( 3f );

        dinoImage = new Image( dinoTexture );
        santaImage = new Image( santaTexture );

        dialog.add( dinoImage ).size( 24f * 3f );
        dialog.add( dialogLabel ).expandX();
        dialog.add( santaImage ).size( 24f * 3f );
        dialog.setVisible( false );

        table.add( dialog ).colspan( 2 ).fillX();

        Static.end = false;

        if( Static.mobileDevice ) {
            createTouchpad();
        }
        
        //hud.setDebugAll( true );
    }

    public void createTouchpad() {

        moveTouchpad = new Touchpad( 0, skin.get( "default", TouchpadStyle.class ) );
        fireTouchpad = new Touchpad( 0, skin.get( "fire", TouchpadStyle.class ) );
        
        moveTouchpad.setPosition( 50, 50 );
        fireTouchpad.setPosition( hud.getWidth() - fireTouchpad.getWidth() - 50, 50 );
        
        hud.addActor( moveTouchpad );
        hud.addActor( fireTouchpad );

        hud.addListener( new InputListener() {

            Vector2 p = new Vector2();
            Rectangle b = new Rectangle();

            Vector2 p2 = new Vector2();
            Rectangle b2 = new Rectangle();

            @Override
            public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
                if( x < hud.getWidth() / 2 ) {
                    if( event.getTarget() != moveTouchpad ) {
                        // If we didn't actually touch the touchpad, set position to our touch point
                        b.set( moveTouchpad.getX(), moveTouchpad.getY(), moveTouchpad.getWidth(), moveTouchpad.getHeight() );
                        b.setCenter( x, y );
                        moveTouchpad.setBounds( b.x, b.y, b.width, b.height );
                        // Let the touchpad know to start tracking touch
                        moveTouchpad.fire( event );
                    }
                }

                if( x > hud.getWidth() / 2 ) {
                    if( event.getTarget() != fireTouchpad ) {
                        // If we didn't actually touch the touchpad, set position to our touch point
                        b2.set( fireTouchpad.getX(), fireTouchpad.getY(), fireTouchpad.getWidth(), fireTouchpad.getHeight() );
                        b2.setCenter( x, y );
                        fireTouchpad.setBounds( b2.x, b2.y, b2.width, b2.height );
                        // Let the touchpad know to start tracking touch
                        fireTouchpad.fire( event );
                    }
                }
                return true;
            }

            @Override
            public void touchDragged( InputEvent event, float x, float y, int pointer ) {
                /*if( x < hud.getWidth() / 2 ) {
                    moveTouchpad.stageToLocalCoordinates( p.set(x, y) );
                    if( moveTouchpad.hit( p.x, p.y, true ) == null ) {
                        // If we moved outside of the touchpad, have it follow our touch position;
                        // but we want to keep the direction of the knob, so shift to the edge of the
                        // touchpad's radius with a small amount of smoothing, so it looks nice.
                        p.set( -moveTouchpad.getKnobPercentX(), -moveTouchpad.getKnobPercentY() ).nor()
                                .scl( Math.min( moveTouchpad.getWidth(), moveTouchpad.getHeight() ) * 0.5f )
                                .add( x, y );
                                moveTouchpad.addAction( Actions.moveToAligned( p.x, p.y, Align.center, 0.15f ) );
                    }
                }
                
                if( x > hud.getWidth() / 2 ) {
                    fireTouchpad.stageToLocalCoordinates( p2.set(x, y) );
                    if( fireTouchpad.hit( p2.x, p2.y, true ) == null ) {
                        // If we moved outside of the touchpad, have it follow our touch position;
                        // but we want to keep the direction of the knob, so shift to the edge of the
                        // touchpad's radius with a small amount of smoothing, so it looks nice.
                        p2.set( -fireTouchpad.getKnobPercentX(), -fireTouchpad.getKnobPercentY() ).nor()
                                .scl( Math.min( fireTouchpad.getWidth(), fireTouchpad.getHeight() ) * 0.5f )
                                .add( x, y );
                                fireTouchpad.addAction( Actions.moveToAligned( p2.x, p2.y, Align.center, 0.15f ) );
                    }
                }*/
            }

            @Override
            public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
                if( x < hud.getWidth() / 2 ) {
                    // Put the touchpad back to its original position
                    moveTouchpad.clearActions();
                    moveTouchpad.addAction( Actions.moveTo( 50, 50, 0.15f ) );
                }
                
                if( x > hud.getWidth() / 2 ) {
                    fireTouchpad.clearActions();
                    fireTouchpad.addAction( Actions.moveTo( hud.getWidth() - fireTouchpad.getWidth() - 50, 50, 0.15f ) );
                }
            }
        } );

        Gdx.input.setInputProcessor( hud );
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
                    barracks.add( tempBarracks = new Barracks( trueX, trueY, TILE_SIZE, world ) );
                    foreground.addActor( tempBarracks );
                    continue;
                }

                if( cell.getTile().getProperties().containsKey( "solid" ) ) {
                    background.addActor( new Solid( trueX, trueY, world, TILE_SIZE ) );
                }

                if( cell.getTile().getProperties().containsKey( "dino" ) ) {
                    dino = new Player( trueX , trueY, TILE_SIZE, world, moveTouchpad, fireTouchpad );
                    playerGroup.addActor( dino );
                    cell.setTile(null);
                    continue;
                }

                if( cell.getTile().getProperties().containsKey( "santa" ) ) {
                    santaCage.set( trueX, trueY, TILE_SIZE, TILE_SIZE );
                    continue;
                }
                
            }
        }

        // Particle Effect
        foregroundFront.addActor( particleEffectActor = new ParticleEffectActor() );

        // Blood Overlay
        foregroundFront.addActor( bloodOverlay = new BloodOverlay() );

        //stage.setDebugAll( true );
        time_limit += TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() );

        scaryMusic = Gdx.audio.newMusic( Gdx.files.internal( "scary.wav" ) );
        scaryMusic.setLooping( true );
    }

    long time_limit = Static.diffTime[Static.difficulty];
    int deadBarracks = 0;
    int key = 0;

    String[] endTexts = { 
        "Hi Santa! I'm here to save you.", 
        "Oh ho ho ho. Please, don't torture me!",
        "What are you talking about?",
        "I'm here to release you.",
        "Oh ho ho ho. You're the one who kidnapped me.",
        "You will put me back again to this cell to torture me.",
        "What?!!! Santa? I don't understand.",
        "You are the Piggy orcs leader that put me here in this cage.",
        "Please I beg you. Chistmas needs me. Release me please.",
        "*Suddenly, you've realized that you put Santa",
        "to the cage to play with him* Noooooooo!",
        "*You found an amnesia pill on your pocket.*",
        "*You lock Santa to his cage and takes the pill.*"
    };
    int[] endIndex = { 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0 };
    int endCount = 0;

    @Override
    public void render(float delta) {
        ScreenUtils.clear( Color.BLACK );

        if( !dino.end ) {
            Static.timeLeft = String.valueOf( time_limit - TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() ) );
            timeLabel.setText( " " + String.valueOf( time_limit - TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() ) ));
            lifeLabel.setText( " x " + (int) dino.life );
            keyLabel.setText( " x " + key );
        }

        if( key >= 1 ) {
            if( santaCage.contains( dino.getX() + dino.getWidth()/2, dino.getY() + dino.getHeight() / 2 ) ) {
                // EndScreen
                Static.end = true;
                //parent.setScreen( new EndScreen( parent ) );\
                if( !scaryMusic.isPlaying() ) {
                    scaryMusic.play();
                    parent.bgm.setVolume( .09f );
                }
                dino.end = true;
                if( endCount < endTexts.length ) {
                    dialog.setVisible( true );
                    if( endIndex[endCount] == 0 ) {
                        dinoImage.setVisible( true );
                        santaImage.setVisible( false );
                        dialogLabel.setColor( Color.BLACK );
                    } else {
                        dinoImage.setVisible( false );
                        santaImage.setVisible( true );
                        dialogLabel.setColor( Color.RED );
                    }
                    dialogLabel.setText( endTexts[endCount] );
                    if( Gdx.input.isButtonJustPressed( Buttons.LEFT ) ) {
                        endCount ++;
                    }
                } else {
                    parent.bgm.setVolume( .3f );
                    parent.setScreen( new EndScreen( parent ) );
                }
                
            }
        }

        {
            deadBarracks = 0;
            for( Barracks barrack : barracks ) {
                if( barrack.isDead )
                    deadBarracks ++;
            }
            if( deadBarracks >= barracks.size && key == 0 ) {
                // Obtain key
                key = 1;
            }
        }

        if( TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() ) > time_limit && !dino.end ) {
            dino.instantDeath();
            Static.gameOvermsg = "You've ran out of time.";
        }

        bloodOverlay.takenHit = dino.takenHit;

        stage.act();
        stage.draw();

        hud.act();
        hud.draw();

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

        if( dino.life <= 0 && dino.hit.isAnimationFinished( dino.stateTime )) {
            parent.setScreen( new GameOverScreen( parent ) );
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
        hud.getViewport().update( width, height, true );
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
        skin.dispose();
        heartImage.dispose();
        keyImage.dispose();
        timeImage.dispose();
        bloodOverlay.dispose();
        santaTexture.dispose();
        dinoTexture.dispose();
        scaryMusic.dispose();
    }
    
}
