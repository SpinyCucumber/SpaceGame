package spishu.space.engine.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;


public class GLWindow {
	
	private int width, height;
	private long handle;
	
	public Vec2 getDimensions() {
		return new Vec2(width, height);
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
	
	public void setPosition(Vec2 pos) {
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
	
	public void fullscreenQuad() {
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(1, 0);
		glVertex2f(width, 0);
		glTexCoord2f(1, 1);
		glVertex2f(width, height);
		glTexCoord2f(0, 1);
		glVertex2f(0, height);
		glEnd();
	}
	
	public GLWindow(int width, int height) {
		this.width = width;
		this.height = height;
		handle = glfwCreateWindow(width, height, "", NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
	}
	
	public static Vec2 getScreenDimensions() {
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		return new Vec2(GLFWvidmode.width(vidmode), GLFWvidmode.height(vidmode));
	}
	
}
