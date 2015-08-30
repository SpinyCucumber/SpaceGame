package spishu.space.engine.gl;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.glfw.GLFW.*;

import spishu.space.engine.math.Vec2;

public class Camera {
	
	private Vec2 position;
	private float zoom, moveSpeed, zoomSpeed;
	private GLWindow window;
	
	public void transform() {
		position.negate().glTranslate();
		GL11.glScalef(zoom, zoom, 0);
	}
	
	public void update(double delta) {
		if(window.isKeyPressed(GLFW_KEY_UP)) position.y -= moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_DOWN)) position.y += moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_LEFT)) position.x -= moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_RIGHT)) position.x += moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_KP_0)) zoom /= zoomSpeed;
		if(window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) zoom *= zoomSpeed;
	}

	public Camera(Vec2 position, float zoom, float moveSpeed, float zoomSpeed,
			GLWindow window) {
		this.position = position;
		this.zoom = zoom;
		this.moveSpeed = moveSpeed;
		this.zoomSpeed = zoomSpeed;
		this.window = window;
	}
	
}
