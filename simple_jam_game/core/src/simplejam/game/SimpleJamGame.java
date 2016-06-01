package simplejam.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class SimpleJamGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;

    ArrayList<Entity> entities;
    Entity player;

    InputMultiplexer inputMultiplexer = new InputMultiplexer();

    float speed = 5;
    float rate = 1;
    float deviation = .5f;

    float timeSinceLastSpawn = 0;
    float nextEnemyTime;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("core/assets/badlogic.jpg");

        entities = new ArrayList<Entity>();

        nextEnemyTime = rate + (((float)Math.random() - .5f)  * deviation);

        MouseFollowStrategy mouseFollowStrategy = new MouseFollowStrategy();
        inputMultiplexer.addProcessor(mouseFollowStrategy);
        inputMultiplexer.addProcessor(this);

        player = new Entity(mouseFollowStrategy, img, 0, 0);
        entities.add(player);

        Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {
        for(Entity e : entities) {
            e.update(Gdx.graphics.getDeltaTime());
        }

        updateEnemies();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        for(Entity e : entities) {
            e.getSprite().draw(batch);
        }

		batch.end();
	}

    private void updateEnemies() {
        float timeBit = Gdx.graphics.getDeltaTime();
        timeSinceLastSpawn += timeBit;
        rate *= 1 - timeBit/1000;
        speed += timeBit;

        Texture enemyTexture = new Texture("core/assets/badlogic.jpg");

        int yStart = (int)(Math.random() * (Gdx.graphics.getHeight() - enemyTexture.getHeight()));
        int xStart = Gdx.graphics.getWidth();
        Vector2 velocity = new Vector2(-speed, 0);
        Vector2 acceleration = new Vector2(0, 0);

        if(timeSinceLastSpawn >= nextEnemyTime) {
            entities.add(new Entity(new AcceleratedStrategy(velocity, acceleration), enemyTexture, xStart, yStart));
            nextEnemyTime = rate + (((float)Math.random() - .5f)  * deviation);

            timeSinceLastSpawn = 0;
        }

        for(int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if(e.getSprite().getX() < -e.getSprite().getWidth()) {
                entities.remove(i);

                i--;
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
