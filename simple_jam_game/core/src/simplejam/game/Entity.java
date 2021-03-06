package simplejam.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 5/31/2016.
 */
public class Entity {
    protected Sprite sprite;
    protected Rectangle hitbox;
    protected Strategy strategy;
    protected boolean isDestroyable;

    public Entity(Strategy strategy, Texture texture, int xPos, int yPos, boolean isDestroyable) {
        this.strategy = strategy;
        sprite = new Sprite(texture);
        sprite.setPosition(xPos, yPos);
        hitbox = new Rectangle(xPos, yPos, sprite.getWidth(), sprite.getHeight());
        this.isDestroyable = isDestroyable;
    }

    public Entity(Strategy strategy, Texture texture, int xPos, int yPos) {
        this(strategy, texture, xPos, yPos, false);
    }

    public void update(float delta) {
        Vector2 movement = strategy.getMovement(sprite, delta);
        move(movement);
    }

    protected void move(Vector2 movement) {
        sprite.setPosition(sprite.getX()+movement.x, sprite.getY()+movement.y);
        hitbox.setPosition(sprite.getX(), sprite.getY());
    }

    public void rotateSprite(float degree) {
        sprite.rotate(degree);
    }

    public void scaleSprite(float scale) {
        sprite.scale(scale);
        hitbox = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Strategy getStrategy() { return strategy; }

}
