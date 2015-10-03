package spishu.space.engine.gl;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;

/**
 * A class representing a single GLSL program.
 * @author SpinyCucumber
 *
 */
public class GLSLProgram {
	
	/**
	 * Generate shader with specified type.
	 * @param src Shader source code
	 * @param type Shader type. Common ones are GL_VERTEX_SHADER and GL_FRAGMENT_SHADER
	 * @return Shader id
	 * @throws IOException
	 */
	public static int buildShader(String src, int type) throws IOException {
		int id = glCreateShader(type);
        glShaderSource(id, src);
        glCompileShader(id);
        if(glGetShaderi(id, GL_COMPILE_STATUS) == GL11.GL_FALSE) throw new IOException(glGetShaderInfoLog(id, 1024));
        return id;
	}

	public static void useNoProgram() {
		glUseProgram(0);
	}
	
	private Map<String, Integer> uniforms = new HashMap<String, Integer>();
	
	int id;
	
	/**
	 * Generate program from specified shader ids.
	 * @param integer
	 */
	public GLSLProgram(Integer...integer) {
		id = glCreateProgram();
		for(int shader : integer) glAttachShader(id, shader);
		glLinkProgram(id);
	}
	
	public void delete() {
		glDeleteProgram(id);
	}
	
	private int getUniformLoc(String name) {
		Integer loc = uniforms.get(name);
		if(loc == null) {
			loc = glGetUniformLocation(id, name);
			uniforms.put(name, loc);
		}
		return loc;
	}
	
	public void setUniform(String name, float value) {
		glUniform1f(getUniformLoc(name), value);
	}
	
	public void setUniform(String name, float x, float y) {
		glUniform2f(getUniformLoc(name), x, y);
	}
	
	public void setUniform(String name, Vec2 vec) {
		setUniform(name, vec.x, vec.y);
	}
	
	public void use() {
		glUseProgram(id);
	}

	@Override
	public String toString() {
		return "GLSLProgram [uniforms=" + uniforms + ", id=" + id + "]";
	}
	
}
