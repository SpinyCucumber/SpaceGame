package spishu.space.engine.gl;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;

public class GLSLProgram {
	
	public static GLSLProgram fromVertexFragmentPair(String vertexLocation, String fragmentLocation) throws IOException {
		return new GLSLProgram(loadShader(new FileInputStream(vertexLocation), GL_VERTEX_SHADER),
				loadShader(new FileInputStream(fragmentLocation), GL_FRAGMENT_SHADER));
	}
	
	public static int loadShader(InputStream in, int type) throws IOException {
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		String src = new String(bytes);
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
	
	public GLSLProgram(int...shaders) {
		id = glCreateProgram();
		for(int shader : shaders) glAttachShader(id, shader);
		glLinkProgram(id);
	}
	
	public void delete() {
		glDeleteProgram(id);
	}
	
	private int getUniformLocation(String name) {
		Integer loc = uniforms.get(name);
		if(loc == null) {
			loc = glGetUniformLocation(id, name);
			uniforms.put(name, loc);
		}
		return loc;
	}
	
	public void setUniform(String name, float value) {
		glUniform1f(getUniformLocation(name), value);
	}
	
	public void setUniform(String name, float x, float y) {
		glUniform2f(getUniformLocation(name), x, y);
	}
	
	public void setUniform(String name, Vec2 vec) {
		setUniform(name, vec.x, vec.y);
	}
	
	public void use() {
		glUseProgram(id);
	}
	
}
