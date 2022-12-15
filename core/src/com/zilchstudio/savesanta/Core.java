package com.zilchstudio.savesanta;


import com.badlogic.gdx.Game;


/**
 * Free assets
 * https://elthen.itch.io/2d-pixel-art-santa-sprites
 * https://vnitti.itch.io/glacial-mountains-parallax-background
 * https://blackdragon1727.itch.io/free-trap-platformer
 * https://spirit-warrior.itch.io/cold-valleys-winter-themed-pixel-art-asset-pack
 * https://wenrexa.itch.io/laser2020
 * https://arks.itch.io/dino-characters
 * https://0x42-0x4f-0x49.itch.io/pixel-art-weapons
 * https://ansimuz.itch.io/explosion-animations-pack
 * https://gamedevelopershlok.itch.io/heartpack
 * 
 * https://www.dafont.com/
 * https://github.com/raeleus/Particle-Park
 */
public class Core extends Game {
    
    @Override
    public void create() {
        setScreen( new StartScreen( this ) );
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
       
    }
}
