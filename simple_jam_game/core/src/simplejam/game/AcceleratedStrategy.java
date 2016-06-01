package simplejam.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 5/31/2016.
 */
public class AcceleratedStrategy extends Strategy {
    Vector2 velocity;
    Vector2 acceleration;

    public AcceleratedStrategy(Vector2 velocity, Vector2 acceleration) {
        this.acceleration = acceleration;
        this.velocity = velocity;
    }

    @Override
    public Vector2 getMovement(Sprite entity, float delta) {
        velocity = velocity.add(acceleration.scl(delta));
        return velocity;
    }
}
