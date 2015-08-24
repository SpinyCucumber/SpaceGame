package spishu.space.engine.gl;

import static org.lwjgl.opengl.GL11.*;

public class DisplayList {  //Simple wrapper class
	
	int id;
	
	public void call() {
		glCallList(id);
	}
	
	public void delete() {
		glDeleteLists(id, 1);
	}
	
	public static void end() {
		glEndList();
	}
	
	public DisplayList() {
		id = glGenLists(1);
		glNewList(id, GL_COMPILE);
	}
	
}
