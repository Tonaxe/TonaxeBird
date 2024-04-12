package com.mygdx.bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Bird game;

    int puntuacion;

    OrthographicCamera camera;

    Array<Pipe> obstacles;
    long lastObstacleTime;


    Stage stage;
    Player player;
    boolean dead;
    boolean tuberiaCount;
    int tuberiasPasadas;
    float initialPipeSpeed = 200;
    float currentPipeSpeed;

    private Texture powerUpTexture;
    private PowerUp powerUp;
    private long lastPowerUpSpawnTime;

    boolean firstPipePassed;



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

        // create the obstacles array and spawn the first obstacle
        obstacles = new Array<Pipe>();
        spawnObstacle();

        dead = false;
        puntuacion = 0;
        tuberiaCount = false;
        tuberiasPasadas = 0;
        currentPipeSpeed = initialPipeSpeed;

        firstPipePassed = false;

    }

    private void spawnObstacle() {

        // Calcula la alçada de l'obstacle aleatòriament
        float holey = MathUtils.random(50, 230);

        // Crea dos obstacles: Una tubería superior i una inferior
        Pipe pipe1 = new Pipe();
        pipe1.setSpeed(currentPipeSpeed);
        pipe1.setX(800);
        pipe1.setY(holey - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);

        Pipe pipe2 = new Pipe();
        pipe2.setSpeed(currentPipeSpeed);
        pipe2.setX(800);
        pipe2.setY(holey + 200);
        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);
        lastObstacleTime = TimeUtils.nanoTime();

    }

    @Override
    public void render(float delta) {
        //LOGICA ========================================================
        stage.act();
        // tell the camera to update its matrices.
        camera.update();
        // process user input
        if (Gdx.input.justTouched()) {
            game.manager.get("flap.wav", Sound.class).play();
            player.impulso();
        }
        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over
        if (player.getBounds().y > 480 - player.getHeight())
            player.setY(480 - player.getHeight());
        if (player.getBounds().y < 0 - player.getHeight()) {
            dead = true;
        }


        // Comprova si cal generar un obstacle nou
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000)
            spawnObstacle();
        // Comprova si les tuberies colisionen amb el jugador
        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }
        // Treure de l'array les tuberies que estan fora de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            } else if (pipe.getX() + pipe.getWidth() < player.getX() && !pipe.isScored()) {
                // El pájaro ha pasado esta tubería
                pipe.setScored(true);
                if (!tuberiaCount) {
                    tuberiaCount = true; // Marca que el pájaro ha pasado una tubería en el par actual
                    firstPipePassed = true;
                } else {
                    // El pájaro ha pasado ambas tuberías en el par actual
                    tuberiaCount = false; // Restablece el contador de tuberías
                    puntuacion++; // Incrementa la puntuación
                    aumentarVelocidad();
                    // Reproduce aquí cualquier sonido de puntuación si lo deseas
                }
            }
        }


        // Treure de l'array les tuberies que estan fora de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }


        // Generar power-up cada 5 segundos si no hay uno presente
        if (powerUp == null && TimeUtils.nanoTime() - lastPowerUpSpawnTime > 5000000000L) {
            spawnPowerUp();
            lastPowerUpSpawnTime = TimeUtils.nanoTime();
        } else if (powerUp != null && TimeUtils.nanoTime() - lastPowerUpSpawnTime > 5000000000L) {
            // Si hay un power-up presente y ha pasado más de 5 segundos
            powerUp.remove(); // Eliminar el power-up actual
            powerUp = null;
            spawnPowerUp(); // Generar uno nuevo
            lastPowerUpSpawnTime = TimeUtils.nanoTime();
        }


        // Verificar colisión entre el jugador y el power-up si hay uno presente
        if (powerUp != null && player.getBounds() != null && player.getBounds().overlaps(powerUp.getBounds())) {
            // El jugador colisionó con el power-up
            powerUp.remove(); // Elimina el power-up de la pantalla
            powerUp = null; // Establece el power-up a null para indicar que ya no está presente
        }

        if (dead) {
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int) puntuacion;
            if (game.lastScore > game.topScore)
                game.topScore = game.lastScore;

            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        //RENDER ========================================================
        // clear the screen with a color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0, 0);

        // Renderizar power-up
        if (powerUp != null) {
            game.batch.draw(powerUp.getTexture(), powerUp.getX(), powerUp.getY());
        }

        game.batch.end();

        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        game.batch.begin();
        game.smallFont.draw(game.batch, "Score: " + (int) puntuacion, 10, 470);
        game.batch.end();

    }

    private void aumentarVelocidad() {
        // Aumenta la velocidad actual de las tuberías
        currentPipeSpeed += 20; // Aumenta la velocidad actual de las tuberías en 50 unidades
    }

    private void spawnPowerUp() {
        if (firstPipePassed && powerUp == null) {
            powerUp = new PowerUp(powerUpTexture);
            // Establecer la posición aleatoria del power-up evitando las tuberías
            powerUp.setRandomPosition(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
            powerUp.setSpeed(currentPipeSpeed); // Establecer la misma velocidad que las tuberías
            stage.addActor(powerUp);
            lastPowerUpSpawnTime = TimeUtils.nanoTime();
        }
    }



    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
        powerUpTexture = new Texture("pastilla.png");
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
        powerUpTexture.dispose();
    }
}
