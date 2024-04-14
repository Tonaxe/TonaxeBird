package com.mygdx.bird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PowerUp extends Actor {
    private Texture texture;
    private float speed;

    public PowerUp(Texture texture) {
        this.texture = texture;
        setWidth(texture.getWidth());
        setHeight(texture.getHeight());
        speed = 200;
    }

    public void setRandomPosition(float minX, float maxX, float minY, float maxY) {
        // Asegura que los límites estén dentro de los límites de la pantalla
        float adjustedMinX = Math.max(minX, 400); // La mitad de la pantalla por la derecha es 400
        float adjustedMaxX = Math.min(maxX, 800 - getWidth());
        float adjustedMinY = Math.max(minY, 0);
        float adjustedMaxY = Math.min(maxY, 480 - getHeight());

        // Genera una posición aleatoria dentro de los límites ajustados
        float randomX = MathUtils.random(adjustedMinX, adjustedMaxX);
        float randomY = MathUtils.random(adjustedMinY, adjustedMaxY);

        // Establece la posición aleatoria
        setPosition(randomX, randomY);
    }


    @Override
    public void act(float delta) {
        moveBy(-speed * delta, 0);
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
