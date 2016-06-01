package simplejam.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 5/31/2016.
 */
public abstract class Entity {
    protected Sprite sprite;
    protected Rectangle hitbox;
    //TODO movement strategies

    public Entity(Texture texture, int xPos, int yPos) {
        sprite = new Sprite(texture);
    }

    public void update(float delta) {
    }

    public void move(Vector2 movement) {
        sprite.setPosition(sprite.getX()+movement.x, sprite.getY()+movement.y);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

}
