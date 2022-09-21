package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class MainMenuScreen implements Screen {


    final Drop game;
    TextureRegion backGround;
    OrthographicCamera camera;

    public MainMenuScreen(final Drop gam) {

        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        backGround = new TextureRegion(new Texture("back.jpg"));
        game.batch.begin();
        game.font.draw(game.batch, backGround.toString(),0,0);
        game.font.draw(game.batch, "Hello Tube to start playing do 30 push-ups!", 315, 260);
        game.font.draw(game.batch, "Now click on the screen to play", 315, 240);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
