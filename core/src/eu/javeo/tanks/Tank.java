package eu.javeo.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Tank extends GameObject {
    private static final float SPEED = 4f;

    private final Sprite sprite;
    private final Texture tankTexture;
    private final ControlType controlType;
    private SpriteBatch batch;
    enum ControlType {HUMAN, COMPUTER};
    private Explosion explosion;
    private Animation explosionAnimation;
    private float timeCounter = 0;

    public Tank(Texture tankTexture, SpriteBatch batch, ControlType controlType, Animation explosionAnimation, GameObjectsManager gameObjectsManager) {
        this.gameObjectsManager = gameObjectsManager;
		this.explosionAnimation = explosionAnimation;
        this.tankTexture = tankTexture;
        this.controlType = controlType;
        sprite = new Sprite(tankTexture);

        switch (controlType) {
            case COMPUTER:
                sprite.setY(200);
                sprite.setX(400);
                break;
            case HUMAN:
                sprite.setY(0);
                sprite.setX(0);
                break;
        }

        this.batch = batch;
    }

    public void init() {

    }

    public void draw() {
        sprite.draw(batch);
    }

    public void update(float touchpadX, float touchpadY) {
        switch (controlType) {
            case HUMAN:
                updatePosition(touchpadX, touchpadY);
                break;
            case COMPUTER:
                updateComputer();
                break;
        }
    }

    private void updateComputer() {
        float touchpadX = 1.0f;
        float touchpadY = 0.0f;
        updatePosition(touchpadX, touchpadY);
    }

    private void updatePosition(float touchpadX, float touchpadY) {
        if (Math.abs(touchpadX) > Math.abs(touchpadY)) {
            touchpadY = 0.0f;
        } else {
            touchpadX = 0.0f;
        }
        move(touchpadX, touchpadY);
        rotate(touchpadX, touchpadY);
        timeCounter += Gdx.graphics.getDeltaTime();
    }

    private void move(float touchpadX, float touchpadY) {
        if (!gameObjectsManager.collides(this)) {
            float x = (sprite.getX() + touchpadX * SPEED + sprite.getBoundingRectangle().getWidth() / 2) % TanksGame.SCREEN_WIDTH;
            float y = (sprite.getY() + touchpadY * SPEED + sprite.getBoundingRectangle().getHeight() / 2) % TanksGame.SCREEN_HEIGHT;
            if (x < 0) x += TanksGame.SCREEN_WIDTH;
            if (y < 0) y += TanksGame.SCREEN_HEIGHT;
            sprite.setX(x - sprite.getBoundingRectangle().getWidth() / 2);
            sprite.setY(y - sprite.getBoundingRectangle().getHeight() / 2);
        }
    }


    private void rotate(float touchpadX, float touchpadY) {
        float rotation = (float) Math.atan2(touchpadX, touchpadY) * MathUtils.radiansToDegrees;
        sprite.setRotation(-rotation);
    }

    public void destroy() {
        explosion = new Explosion(explosionAnimation, sprite.getX(), sprite.getY());
    }

    public Vector2 getPosition() { return sprite.getBoundingRectangle().getCenter(new Vector2(sprite.getX(), sprite.getY())); }

    public Vector2 getDirection() {
        Vector2 direction = new Vector2(1, 1);
        direction.setAngle(sprite.getRotation() + 90);
        direction.nor();
        return direction;
    }

    public boolean canFire() {
        if (timeCounter > 0.3) {
            timeCounter = 0;
            return true;
        }
        return false;
    }

    public boolean isHuman() {
        return controlType == ControlType.HUMAN;
    }
}
