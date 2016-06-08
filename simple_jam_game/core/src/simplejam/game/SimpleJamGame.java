package simplejam.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SimpleJamGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
    Texture playerTexture, enemyTexture, bulletTexture, gateTexture, starTexture;

    ArrayList<Entity> entities;
    ArrayList<Entity> stars;
    Entity player;

    InputMultiplexer inputMultiplexer = new InputMultiplexer();

    float speed;
    float rate;
    float deviation = .5f;

    float timeSinceLastSpawn;
    float nextEnemyTime;

    float colorTime;
    float starTime;

    float score;

    int volume = 25;

    Music gameMusic;

    BitmapFont font;

    boolean isPaused = false;

    public void initTextures() {
        playerTexture = new Texture("core/assets/player.png");
        enemyTexture = new Texture("core/assets/enemy.png");
        bulletTexture = new Texture("core/assets/player.png");
        gateTexture = new Texture("core/assets/gate.png");
        starTexture = new Texture("core/assets/star.png");
    }
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        font = new BitmapFont();
        initTextures();

        gameMusic = Gdx.audio.newMusic(new FileHandle(new File("core/assets/GameMusicLoop.wav")));
        gameMusic.setVolume(0.25f);
        gameMusic.setLooping(true);

        reset();

        Gdx.input.setInputProcessor(inputMultiplexer);
	}

    public void reset() {
        speed = 2;
        timeSinceLastSpawn = 0;
        rate = 1;

        entities = new ArrayList<Entity>();
        stars = new ArrayList<Entity>();

        gameMusic.play();

        nextEnemyTime = rate + (((float)Math.random() - .5f)  * deviation);

        colorTime = 0;
        starTime = 0;

        score = 0;

        MouseFollowStrategy mouseFollowStrategy = new MouseFollowStrategy();
        inputMultiplexer.addProcessor(mouseFollowStrategy);
        inputMultiplexer.addProcessor(this);

        player = new Entity(mouseFollowStrategy, playerTexture, 0, 0);
        entities.add(player);
    }

	@Override
	public void render () {

        float delta = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        if (!isPaused) {
            for (Entity e : entities) {
                e.update(delta);
            }

            updateEnemies();
            updateBackground(delta);

            for (Entity e : stars) {
                e.getSprite().draw(batch);
            }

            for (Entity e : entities) {
                if (e.getStrategy() instanceof MouseFollowStrategy) {
                    colorTime = colorTime + delta * 2;
                    float red = (float) (Math.sin(colorTime + 0) * 127 + 128) / 255f;
                    float green = (float) (Math.sin(colorTime + 2) * 127 + 128) / 255f;
                    float blue = (float) (Math.sin(colorTime + 4) * 127 + 128) / 255f;
                    e.getSprite().setColor(red, green, blue, 1);
                    e.rotateSprite(90 * delta);
                    e.getSprite().draw(batch);
                } else {
                    e.rotateSprite(30 * delta);
                    e.getSprite().draw(batch);
                }

            }

            font.draw(batch, "Score: " + ((int) (score * 10)) / 10f, 5, Gdx.graphics.getHeight() - 5);
        } else {
            for (Entity e : stars) {
                e.getSprite().setColor(1, 1, 1, 0.2f);
                e.getSprite().draw(batch);
            }

            for (Entity e : entities) {
                e.getSprite().setColor(1, 1, 1, 0.2f);
                e.getSprite().draw(batch);
            }

            font.draw(batch, "You have died. Press X to retry!", Gdx.graphics.getWidth()/2-35, Gdx.graphics.getHeight()/2-8);
            font.draw(batch, "Your final score was: " + ((int) (score * 10)) / 10f, Gdx.graphics.getWidth()/2-35, Gdx.graphics.getHeight()/2-28);
        }

        batch.end();
	}

    private void updateEnemies() {
        float timeBit = Gdx.graphics.getDeltaTime();
        timeSinceLastSpawn += timeBit;
        rate *= 1 - timeBit/850;
        if (speed < 5) {
            speed += timeBit;
        } else if (speed < 10) {
            speed += timeBit/2;
        } else {
            speed += timeBit/4;
        }

        int yStart = (int)(Math.random() * (Gdx.graphics.getHeight() - enemyTexture.getHeight()));
        int xStart = Gdx.graphics.getWidth();
        Vector2 velocity = new Vector2(-speed, 0);
        Vector2 acceleration = new Vector2(0, 0);
        Vector2 secondAccel = new Vector2(0, 0);
        Vector2 secondVelocity = new Vector2(-speed, 0);

        if(timeSinceLastSpawn >= nextEnemyTime) {
            if (Math.random() < 0.5) {
                velocity = new Vector2(-speed, (float)(Math.random() - 1)/5 * speed);
                secondVelocity = new Vector2(-speed, (float)(Math.random() - 1)/5 * speed);
            }

            if (Math.random() < 0.8) {
                acceleration = new Vector2(-(float)(Math.random()) * 5, (float)(Math.random() - .5) * 5);
                secondAccel = new Vector2(-(float)(Math.random()) * 5, (float)(Math.random() - .5) * 5);
            }

            score += 0.1;
            if (Math.random() < 0.25) {
                Entity p1 = new Entity(new AcceleratedStrategy(velocity, acceleration), gateTexture, xStart, yStart);
                yStart = (int) (Math.random() * (Gdx.graphics.getHeight() - enemyTexture.getHeight()));

                Entity p2 = new Entity(new AcceleratedStrategy(secondVelocity, secondAccel), gateTexture, xStart, yStart);
                PentagonGate pgate = new PentagonGate(p1, p2, playerTexture);
                entities.add(pgate);
                entities.add(p1);
                entities.add(p2);
            } else {
                boolean destructible = Math.random() > 0.5;
                Entity e = new Entity(new AcceleratedStrategy(velocity, acceleration), enemyTexture, xStart, yStart, destructible);
                if (destructible) {
                    e.getSprite().setColor(0.4f, 0.30f, 0, 1);
                }
                e.rotateSprite((float) Math.random()*360);
                entities.add(e);
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

                        if(toHit.isDestroyable) {
                            entities.remove(toHit);
                            score += 1;
                        }
                    }
                }
            }

            if(e != player && e.getStrategy() != null && !(e.getStrategy() instanceof BulletStrategy) && e.getHitbox().overlaps(player.getHitbox())) {
                //entities.remove(player);
                isPaused = true;
            }

            if(e.getStrategy() == null && ((PentagonGate)e).getBoundingBox().overlaps(player.getHitbox())){
                if (isCollision(((PentagonGate) e).getCenter(), player.getHitbox())) {
                    entities.remove(((PentagonGate) e).getP1());
                    entities.remove(((PentagonGate) e).getP2());
                    entities.remove(e);
                    score += 5;
                }
            }
        }
    }

    private boolean isCollision(Polygon p, Rectangle r) {
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width, r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        if (Intersector.overlapConvexPolygons(rPoly, p))
            return true;
        return false;
    }

    private void updateBackground(float delta) {
        starTime += delta;
        if (starTime >= nextEnemyTime/3f) {
            Entity e = new Entity(new AcceleratedStrategy(new Vector2((int) (-10 + (Math.random()*4.0 - 2.0)), 0), new Vector2(0,0)), starTexture, Gdx.graphics.getWidth(), (int) (Gdx.graphics.getHeight() - Math.random()*Gdx.graphics.getHeight()));
            e.getSprite().setColor(1, 1, (float) (1-Math.random()*100/255), 1);
            e.getSprite().rotate((float) Math.random()*80-30);
            stars.add(e);
            starTime = 0;
        }
        for (Entity e : stars) {
            e.update(delta);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean mFlag = false;
        if (keycode == Input.Keys.EQUALS) {
            if (volume < 100) {
                volume += 5;
                mFlag = true;
            }
        }
        if (keycode == Input.Keys.MINUS) {
            if (volume > 0) {
                volume -= 5;
                mFlag = true;
            }
        }
        if (mFlag) {
            System.out.println(volume);
            gameMusic.setVolume(volume / 100f);
        }
        if (keycode == Input.Keys.X && isPaused) {
            reset();
            isPaused = false;
        }
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
        if (!isPaused) {
            Vector2 init = new Vector2(player.getSprite().getX() + player.getSprite().getWidth() / 2 - bulletTexture.getWidth() / 2, player.getSprite().getY() + player.getSprite().getHeight() / 2 - bulletTexture.getHeight() / 2);
            Entity bullet = new Entity(new BulletStrategy(init, new Vector2(screenX, screenY), 25), bulletTexture, (int) init.x, (int) init.y, true);
            bullet.scaleSprite(-0.5f);
            entities.add(bullet);
        }
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
