package spishu.space.engine.lib;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

/**
 * Class representing an opengl buffer, stored on the vram.
 * Used to accelerate graphics.
 * @author SpinyCucumber
 *
 */
public class GLBuffer {
	
	private int id;
	
	public void bind() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
	}
	
	public void delete() {
		GL15.glDeleteBuffers(id);
	}
	
	public GLBuffer(FloatBuffer floats, int usage) {
		id = GL15.glGenBuffers();
		bind();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floats, usage);
		unbind();
	}
	
	public static void unbind() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
}
