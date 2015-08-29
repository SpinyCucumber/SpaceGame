package spishu.space.engine.gl;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;

public class Camera2 {
	
	private Vec2 position;
	private float zoom;
	
	public void transform() {
		position.glTranslate();
		GL11.glScalef(zoom, zoom, 0);
	}
	
	public Camera2() {
		
	}
	
}
