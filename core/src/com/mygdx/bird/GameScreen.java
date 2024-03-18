package com.mygdx.bird;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Bird game;

    OrthographicCamera camera;

    public GameScreen(Bird game) {
        this.game = game;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

    }

    @Override
    public void render(float delta) {

        // clear the screen with a color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class),0, 0);
        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
    }
}
