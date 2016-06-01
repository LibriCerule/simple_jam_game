package simplejam.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class SimpleJamGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

    ArrayList<Entity> entities;
    Entity player;

    float speed = 1;
    float rate = 1;
    float deviation = .5f;

    float timeSinceLastSpawn = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("core/assets/badlogic.jpg");

        entities = new ArrayList<Entity>();

        player = new Entity("core/assets/badlogic.jpg", 0, 0);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
        for(Entity e : entities) {
            e.getSprite().draw(batch);
        }
		batch.end();
	}

    private void createEnemies() {
        float timeBit = Gdx.graphics.getDeltaTime();
        rate += timeBit / 100;
        speed += timeBit / 100;
    }
}
