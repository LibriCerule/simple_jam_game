package simplejam.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 6/1/2016.
 */
public class BulletStrategy extends AcceleratedStrategy {
    public BulletStrategy(Vector2 startPosition, Vector2 clickPosition, float speed) {
        super(new Vector2(-1f * (startPosition.x - clickPosition.x), -1f * (clickPosition.y - (Gdx.graphics.getHeight() - startPosition.y))).nor().scl(speed), new Vector2(0,0));
    }
}
