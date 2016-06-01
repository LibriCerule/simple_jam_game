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

    public Entity(Strategy strategy, Texture texture, int xPos, int yPos) {
        this.strategy = strategy;
        sprite = new Sprite(texture, xPos, yPos, texture.getWidth(), texture.getHeight());
        hitbox = new Rectangle(xPos, yPos, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta) {
        Vector2 movement = strategy.getMovement(sprite.getX(), sprite.getY(), delta);
        move(movement);
    }

    protected void move(Vector2 movement) {
        sprite.setPosition(sprite.getX()+movement.x, sprite.getY()+movement.y);
        hitbox.setPosition(sprite.getX(), sprite.getY());
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Sprite getSprite() {
        return sprite;
    }

}
