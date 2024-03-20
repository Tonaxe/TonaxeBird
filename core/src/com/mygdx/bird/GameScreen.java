package com.mygdx.bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Bird game;

    OrthographicCamera camera;

    Stage stage;
    Player player;
    boolean dead;


    public GameScreen(Bird game) {
        this.game = game;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.setManager(game.manager);
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.addActor(player);

        dead = false;


    }

    @Override
    public void render(float delta) {

        //LOGICA ========================================================
        stage.act();
        // tell the camera to update its matrices.
        camera.update();
        // process user input
        if (Gdx.input.justTouched()) {
            player.impulso();
        }

        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over
        if (player.getBounds().y > 480 - player.getHeight())
            player.setY( 480 - player.getHeight() );
        if (player.getBounds().y < 0 - player.getHeight()) {
            dead = true;
        }



        //RENDER ========================================================
        // clear the screen with a color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class),0, 0);
        game.batch.end();

        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

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
