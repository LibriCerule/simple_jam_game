package simplejam.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class SimpleJamGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
    Texture playerTexture, enemyTexture, bulletTexture, gateTexture, starTexture;

    ArrayList<Entity> entities;
    ArrayList<Entity> stars;
    Entity player;

    InputMultiplexer inputMultiplexer = new InputMultiplexer();

    float speed = 1;
    float rate = 1;
    float deviation = .5f;

    float timeSinceLastSpawn = 0;
    float nextEnemyTime;

    float colorTime;
    float starTime;

    float score;

    BitmapFont font;

    public void initTextures() {
        playerTexture = new Texture("core/assets/player.png");
        enemyTexture = new Texture("core/assets/enemy.png");
        bulletTexture = new Texture("core/assets/bullet.png");
        gateTexture = new Texture("core/assets/gate.png");
        starTexture = new Texture("core/assets/star.png");
    }
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        font = new BitmapFont();
        initTextures();

        entities = new ArrayList<Entity>();
        stars = new ArrayList<Entity>();

        nextEnemyTime = rate + (((float)Math.random() - .5f)  * deviation);

        colorTime = 0;
        starTime = 0;

        score = 0;

        MouseFollowStrategy mouseFollowStrategy = new MouseFollowStrategy();
        inputMultiplexer.addProcessor(mouseFollowStrategy);
        inputMultiplexer.addProcessor(this);

        player = new Entity(mouseFollowStrategy, playerTexture, 0, 0);
        entities.add(player);

        Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {
        float delta = Gdx.graphics.getDeltaTime();

        for(Entity e : entities) {
            e.update(delta);
        }

        updateEnemies();
        updateBackground(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        drawBackground();

        for(Entity e : entities) {
            if (e.getStrategy() instanceof MouseFollowStrategy) {
                colorTime = colorTime + delta * 2;
                float red = (float) (Math.sin(colorTime + 0) * 127 + 128) / 255f;
                float green = (float) (Math.sin(colorTime + 2) * 127 + 128) / 255f;
                float blue = (float) (Math.sin(colorTime + 4) * 127 + 128) / 255f;
                e.getSprite().setColor(red, green, blue, 1);
                e.getSprite().draw(batch);
            } else {
                if (e.isDestroyable && e.getStrategy() instanceof AcceleratedStrategy) {
                    e.getSprite().setColor(0.5f, 1, 0, 1);
                }
                e.getSprite().draw(batch);
            }

        }

        font.draw(batch, "Score: " + ((int) (score * 10)) / 10f, 5, Gdx.graphics.getHeight()-5);

		batch.end();
	}

    private void updateEnemies() {
        float timeBit = Gdx.graphics.getDeltaTime();
        timeSinceLastSpawn += timeBit;
        rate *= 1 - timeBit/1000;
        speed += timeBit;

        int yStart = (int)(Math.random() * (Gdx.graphics.getHeight() - enemyTexture.getHeight()));
        int xStart = Gdx.graphics.getWidth();
        Vector2 velocity = new Vector2(-speed, 0);
        Vector2 acceleration = new Vector2(0, 0);

        if(timeSinceLastSpawn >= nextEnemyTime) {
            score += 0.1;
            if (Math.random() < 0.2) {
                Entity p1 = new Entity(new AcceleratedStrategy(velocity, acceleration), gateTexture, xStart, yStart);
                yStart = (int) (Math.random() * (Gdx.graphics.getHeight() - enemyTexture.getHeight()));
                Entity p2 = new Entity(new AcceleratedStrategy(velocity, acceleration), gateTexture, xStart, yStart);
                PentagonGate pgate = new PentagonGate(p1, p2, playerTexture);
                entities.add(pgate);
                entities.add(p1);
                entities.add(p2);
            } else {
                entities.add(new Entity(new AcceleratedStrategy(velocity, acceleration), enemyTexture, xStart, yStart, (Math.random() > 0.5)));
            }
            nextEnemyTime = rate + (((float)Math.random() - .5f)  * deviation);

            timeSinceLastSpawn = 0;
        }

        for(int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if(e.getSprite().getX() < -e.getSprite().getWidth() || e.getSprite().getX() > Gdx.graphics.getWidth() || e.getSprite().getY() < -e.getSprite().getHeight() || e.getSprite().getHeight() > Gdx.graphics.getHeight()) {
                entities.remove(i);

                i--;
            }

            if(e.isDestroyable) {
                for(int j = 0; j < entities.size(); j++) {
                    Entity toHit = entities.get(j);

                    if(i != j && toHit != player && e.getStrategy() != null && toHit.getStrategy() != null && !e.getStrategy().getClass().equals(toHit.getStrategy().getClass()) && toHit.getHitbox().overlaps(e.getHitbox())) {
                        entities.remove(e);

                        if(toHit.isDestroyable)
                            score += 1;
                            entities.remove(toHit);
                    }
                }
            }

            if(e != player && e.getStrategy() != null && !(e.getStrategy() instanceof BulletStrategy) && e.getHitbox().overlaps(player.getHitbox())) {
                entities.remove(player);
            }

            if(e.getStrategy() == null && ((PentagonGate)e).getBoundingBox().overlaps(player.getHitbox())){
                entities.remove(((PentagonGate) e).getP1());
                entities.remove(((PentagonGate) e).getP2());
                entities.remove(e);
                score += 5;
            }
        }
    }

    private void drawBackground() {
        for (Entity e : stars) {
            e.getSprite().setColor(1, 1, (float) (1-Math.random()*100/255), 1);
            e.getSprite().draw(batch);
        }
    }

    private void updateBackground(float delta) {
        starTime += delta;
        if (starTime >= nextEnemyTime/5f) {
            stars.add(new Entity(new AcceleratedStrategy(new Vector2((int) (-20 + (Math.random()*8.0 - 4.0)), 0), new Vector2(0,0)), starTexture, Gdx.graphics.getWidth(), (int) (Gdx.graphics.getHeight() - Math.random()*Gdx.graphics.getHeight())));
            starTime = 0;
        }
        for (Entity e : stars) {
            e.update(delta);
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
        Vector2 init = new Vector2(player.getSprite().getX() + player.getSprite().getWidth()/2 - bulletTexture.getWidth()/2, player.getSprite().getY() + player.getSprite().getHeight()/2 - bulletTexture.getHeight()/2);
        Entity bullet = new Entity(new BulletStrategy(init, new Vector2(screenX, screenY), 25), bulletTexture, (int)init.x, (int)init.y, true);

        entities.add(bullet);
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
