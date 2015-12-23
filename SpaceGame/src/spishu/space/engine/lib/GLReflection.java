package spishu.space.engine.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import spishu.space.engine.game.Game;

public final class GLReflection {
	
	private static Map<String, Integer> keys;
	
	public static void generateKeymap() throws IllegalAccessException {
		Game.info("Generating keymap...");
		keys = new HashMap<String, Integer>();
		for(Field field : GLFW.class.getFields()) {
			if(!Modifier.isStatic(field.getModifiers())
					|| !field.getName().startsWith("GLFW_KEY")) continue;
			String name = field.getName().substring(9);
			keys.put(name, field.getInt(null));
		}
		Game.info("Mapped %d key numbers to their names", keys.size());
	}

	public static Map<String, Integer> getKeymap() {
		return keys;
	}	
	
}
