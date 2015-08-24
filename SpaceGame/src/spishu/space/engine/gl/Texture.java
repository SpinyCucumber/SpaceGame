package spishu.space.engine.gl;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	
	private static final int BYTES_PER_PIXEL = 4;
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private int id, width, height;
	
	public Texture(int width, int height, ByteBuffer buffer) {
		
		this.width = width;
		this.height = height;

        id = glGenTextures();
        bind();

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        unbind();
        
	}
	
	public static Texture fromBufferedImage(BufferedImage image) {
		
		int width = image.getWidth(), height = image.getHeight();
		
		int[] pixels = new int[width * height];
        image.getRGB(0, 0, height, height, pixels, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * BYTES_PER_PIXEL);
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        
        return new Texture(width, height, buffer);
		
	}
	
	public static Texture fromFile(File file) throws IOException {
		return fromBufferedImage(ImageIO.read(file));
	}
	
	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
}
