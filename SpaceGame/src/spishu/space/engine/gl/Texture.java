package spishu.space.engine.gl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import spishu.space.engine.math.Vec2;

/**
 * Class representing an OpenGL texture object. Contains methods for loading from files and etc.
 * Game class is meant for loading and handling of resources.
 * @author SpinyCucumber
 *
 */
public class Texture {
	
	/**
	 * Creates opengl texture from java awt image.
	 * @param image
	 * @return
	 */
	public static Texture fromBufferedImage(BufferedImage image) {
		
		int width = image.getWidth(), height = image.getHeight();
		
		int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

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
	
	private static final int BYTES_PER_PIXEL = 4;
	
	private int id, width, height;
	
	public Texture(int width, int height) {
		this(width, height, null);
	}
	
	/**
	 * Inititializes texture and sets data.
	 * @param width
	 * @param height
	 * @param buffer
	 */
	public Texture(int width, int height, ByteBuffer buffer) {
		
		this.width = width;
		this.height = height;

        id = GL11.glGenTextures();
        bind();

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	public Vec2 getDim() {
		return new Vec2(width, height);
	}
	
	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}
	
	/**
	 * Calls opengl 
	 */
	public void destroy() {
		GL11.glDeleteTextures(id);
	}

	public int getWidth() {
		return width;
	}
	
	@Override
	public String toString() {
		return "Texture [id=" + id + ", width=" + width + ", height=" + height
				+ "]";
	}
	
}
