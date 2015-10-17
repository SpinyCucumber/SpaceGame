package spishu.space.engine.assets;

import org.lwjgl.opengl.GL11;
import static org.lwjgl.glfw.GLFW.*;

import spishu.space.engine.lib.GLWindow;
import spishu.space.engine.math.Vec2;

/**
 * Simple scrolling camera.
 * Using double values for speed, etc to increase precision (there will not be multiple cameras!)
 * @author SpinyCucumber
 *
 */
public class Camera2d {
	
	private Vec2 position;
	private double zoom, moveSpeed, zoomSpeed;
	private GLWindow window;
	
	public void transform() {
		GL11.glScaled(zoom, zoom, 0);
		position.negate().glTranslate();
	}
	
	public void update(double delta) {
		if(window.isKeyPressed(GLFW_KEY_UP)) position.y += moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_DOWN)) position.y -= moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_LEFT)) position.x -= moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_RIGHT)) position.x += moveSpeed * delta;
		if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) zoom /= zoomSpeed;
		if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) zoom *= zoomSpeed;
	}

	public Camera2d(Vec2 position, double zoom, double moveSpeed, double zoomSpeed,
			GLWindow window) {
		this.position = position;
		this.zoom = zoom;
		this.moveSpeed = moveSpeed;
		this.zoomSpeed = zoomSpeed;
		this.window = window;
	}

	@Override
	public String toString() {
		return "Camera [position=" + position + ", zoom=" + zoom
				+ ", moveSpeed=" + moveSpeed + ", zoomSpeed=" + zoomSpeed + "]";
	}
	
}
