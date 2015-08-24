package spishu.space.engine.gl;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class Framebuffer2 {
	
	private int id, colorBufferId;
	
	public void bind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
	}
	
	public void bindColorTexture() {
		glBindTexture(GL_TEXTURE_2D, colorBufferId);
	}
	
	public Framebuffer2(int width, int height) {
		
		//Generate textures.
        colorBufferId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, colorBufferId);
        
        //Set texture wrapping. GL_REPEAT is default, but I like keeping the code.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        //Set the protocol for when the texture is being scaled onto an area smaller than itself. GL_NEAREST selects nearest pixel.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //Protocol for enlarging the texture, like above.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //Allocate memory for the texture.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);;
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        //Allocate framebuffers.
        id = glGenFramebuffersEXT();
        
        bind();
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorBufferId, 0);   
        unbind();
        
	}
	
	public void delete() {
		glDeleteFramebuffersEXT(id);
	}
	
	public static void unbind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
}
