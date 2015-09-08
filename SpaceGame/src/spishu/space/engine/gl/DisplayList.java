package spishu.space.engine.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * Simple class representing an opengl displaylist object.
 * Used to speed up rendering.
 * @author SpinyCucumber
 *
 */
public class DisplayList {
	
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
