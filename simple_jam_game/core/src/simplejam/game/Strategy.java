package simplejam.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 5/31/2016.
 */
public abstract class Strategy {
    public abstract Vector2 getMovement(Sprite sprite, float delta);
}
