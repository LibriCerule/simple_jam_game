package simplejam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import simplejam.game.SimpleJamGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = true;
		config.title = "Title";
		config.useGL30 = true;
        config.width = 1024;
		config.height = 768;

		new LwjglApplication(new SimpleJamGame(), config);
	}
}
