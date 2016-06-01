package simplejam.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Declan on 5/31/2016.
 */
public class MouseFollowStrategy extends Strategy implements InputProcessor {
    //Store last checked mouse position
    float mouseX = 0;
    float mouseY = 0;
    Vector2 kick = new Vector2(0,0);
    Vector2 pos = new Vector2(0,0);

    //Unused
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    //Unused
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    //Unused
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    //Unused
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        kick = new Vector2((pos.x - screenX) * -1, ((Gdx.graphics.getHeight() - pos.y) - screenY)).nor().scl(15f);
        return false;
    }

    //Unused
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    //Unused
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseX = screenX;
        mouseY = screenY;
        return false;
    }

    //Unused
    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public Vector2 getMovement(Sprite entity, float delta) {
        //Calculate movement vector, half the distance to the cursor every second
        //Adjusting for 0,0 being top left instead of bottom left with mouse input
        Vector2 movement = new Vector2((entity.getX() - mouseX + entity.getWidth()/2) * -1.5f * delta - kick.x,(mouseY - (Gdx.graphics.getHeight() - entity.getY()) + + entity.getHeight()/2) * -1.5f * delta - kick.y);
        kick = new Vector2(0,0);
        pos = new Vector2(entity.getX() + entity.getWidth()/2, entity.getY() + entity.getHeight()/2);
        return movement;
    }
}
