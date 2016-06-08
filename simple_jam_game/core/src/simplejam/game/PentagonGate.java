package simplejam.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by libri on 6/1/16.
 */
public class PentagonGate extends Entity{

    private Entity p1;
    private Entity p2;
    private Polygon center;

    public PentagonGate(Entity p1, Entity p2, Texture texture) {
        super(null, texture, -63, -63);
        this.p1 = p1;
        this.p2 = p2;
        float[] vertices = new float[]{
                p1.getSprite().getX() + p1.getSprite().getWidth() / 2 - 1, p1.getSprite().getY() + p1.getSprite().getHeight() / 2,
                p1.getSprite().getX() + p1.getSprite().getWidth() / 2 + 1, p1.getSprite().getY() + p1.getSprite().getHeight() / 2,
                p2.getSprite().getX() + p2.getSprite().getWidth() / 2 - 1, p2.getSprite().getY() + p2.getSprite().getHeight() / 2,
                p2.getSprite().getX() + p2.getSprite().getWidth() / 2 + 1, p2.getSprite().getY() + p2.getSprite().getHeight() / 2
        };
        center = new Polygon(vertices);

        this.sprite.setColor(0, 0, 0, 0);
    }

    public void update(float delta) {
        float[] vertices = new float[]{
                p1.getSprite().getX() + p1.getSprite().getWidth() / 2 - 1, p1.getSprite().getY() + p1.getSprite().getHeight() / 2,
                p1.getSprite().getX() + p1.getSprite().getWidth() / 2 + 1, p1.getSprite().getY() + p1.getSprite().getHeight() / 2,
                p2.getSprite().getX() + p2.getSprite().getWidth() / 2 - 1, p2.getSprite().getY() + p2.getSprite().getHeight() / 2,
                p2.getSprite().getX() + p2.getSprite().getWidth() / 2 + 1, p2.getSprite().getY() + p2.getSprite().getHeight() / 2
        };
        center = new Polygon(vertices);
    }

    public Rectangle getBoundingBox() { return center == null? null : center.getBoundingRectangle(); }
    public Entity getP1() { return p1; }
    public Entity getP2() { return p2; }

    public Polygon getCenter() {
        return center;
    }

}
