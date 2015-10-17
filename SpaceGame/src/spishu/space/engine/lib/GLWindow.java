package spishu.space.engine.lib;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2d;

/**
 * A class representing a glfw window object.
 * @author SpinyCucumber
 *
 */
public class GLWindow {
	
	private int width, height;
	private long handle;
	
	public Vec2d getDim() {
		return new Vec2d(width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void destroy() {
		glfwDestroyWindow(handle);
	}
	
	public void makeContext() {
		glfwMakeContextCurrent(handle);
	}
	
	public void setPosition(Vec2d pos) {
		glfwSetWindowPos(handle, (int) pos.x, (int) pos.y);
	}
	
	public void swapBuffers() {
		glfwSwapBuffers(handle);
	}
	
	public void setTitle(String title) {
		glfwSetWindowTitle(handle, title);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(handle) == GL11.GL_TRUE;
	}
	
	public boolean isKeyPressed(int key) {
		return glfwGetKey(handle, key) == GL11.GL_TRUE;
	}
	
	public Vec2d getMousePos() {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1),
				y = BufferUtils.createDoubleBuffer(1);

	    glfwGetCursorPos(handle, x, y);
	    x.rewind();
	    y.rewind();
	    return new Vec2d((float) x.get(), (float) y.get());
	}
	
	public GLWindow(int width, int height) {
		this.width = width;
		this.height = height;
		handle = glfwCreateWindow(width, height, "", NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
	}
	
	public static Vec2d getScreenDimensions() {
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		return new Vec2d(GLFWvidmode.width(vidmode), GLFWvidmode.height(vidmode));
	}

	@Override
	public String toString() {
		return "GLWindow [width=" + width + ", height=" + height + ", handle="
				+ handle + "]";
	}
	
}
