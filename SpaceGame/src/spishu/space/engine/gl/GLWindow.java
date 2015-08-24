package spishu.space.engine.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import spishu.space.engine.math.Vec2;


public class GLWindow {
	
	private int width, height;
	private long handle;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getHandle() {
		return handle;
	}
	
	public void destroy() {
		glfwDestroyWindow(handle);
	}
	
	public void makeContext() {
		glfwMakeContextCurrent(handle);
	}
	
	public void setPosition(Vec2 pos) {
		glfwSetWindowPos(handle, (int) pos.x, (int) pos.y);
	}
	
	public void swapBuffers() {
		glfwSwapBuffers(handle);
	}
	
	public void setTitle(String title) {
		glfwSetWindowTitle(handle, title);
	}

	public GLWindow(int width, int height) {
		this.width = width;
		this.height = height;
		handle = glfwCreateWindow(width, height, "", NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
	}
	
	
	
}
