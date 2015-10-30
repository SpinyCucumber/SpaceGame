package spishu.space.engine.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.lwjgl.opengl.GL20;

import spishu.space.engine.assets.SingleTexture;
import spishu.space.engine.assets.TextureAtlas;
import spishu.space.engine.assets.TextureLineup;
import spishu.space.engine.lib.GLSLProgram;
import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Vec2d;

/**
 * Loads objects from inputstreams created from resources with the specifies extension.
 * @author SpinyCucumber
 *
 */
public abstract class ResourceLoader implements Comparable<ResourceLoader> {
	
	//Default resource loaders.
	public static final ResourceLoader ANIM_LOADER = new ResourceLoader(1, "anim") {

		@Override
		public Object loadResource(InputStream in) throws IOException {
			try {
				
				Element root = xmlBuilder.build(in).getRootElement();
				String type = root.getAttributeValue("type");
				
				switch(type) {
					case "lineup" : {
						List<Texture> textures = new ArrayList<Texture>();
						for(Element child : root.getChildren()) {
							String location = child.getAttributeValue("location").replace("\\\\", File.separator);
							textures.add((Texture) Game.getResource(location));
						}
						float speed = Float.parseFloat(root.getAttributeValue("speed"));
						return new TextureLineup(speed, textures);
					}
					case "atlas" : {
						Vec2d dim = Vec2d.fromXML(root.getChild("dim"));
						float speed = Float.parseFloat(root.getAttributeValue("speed"));
						String location = root.getAttributeValue("location").replace("\\\\", File.separator);
						Texture texture = (Texture) Game.getResource(location);
						return new TextureAtlas(speed, texture, dim);
					}
					case "single" : {
						String location = root.getAttributeValue("location").replace("\\\\", File.separator);
						Texture texture = (Texture) Game.getResource(location);
						return new SingleTexture(texture);
					}
					default : return null;
				}
				
			} catch (JDOMException e) {
				throw new RuntimeException(e);
			}
		}
		
	}, TEX_LOADER = new ResourceLoader(0, "png", "jpg") {

		@Override
		public Object loadResource(InputStream in) throws IOException {
			return Texture.fromBufferedImage(ImageIO.read(in));
		}
		
	}, RAW_LOADER = new ResourceLoader(0, "c8") {

		@Override
		public Object loadResource(InputStream in) throws IOException {
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			return buffer;
		}
		
	}, GLSL_LOADER = new ResourceLoader(1, "glsl") {
		
		Map<String, Integer> shaderTypes;
		
		{
			shaderTypes = new HashMap<String, Integer>();
			shaderTypes.put("vertex", GL20.GL_VERTEX_SHADER);
			shaderTypes.put("fragment", GL20.GL_FRAGMENT_SHADER);
		}
		
		@Override
		public Object loadResource(InputStream in) throws IOException {
			try {
				
				Element root = xmlBuilder.build(in).getRootElement();
				List<Integer> shaders = new ArrayList<Integer>();
				
				for(Element child : root.getChildren()) {
					String location = child.getAttributeValue("location").replace("\\\\", File.separator);
					shaders.add((Integer) Game.getResource(location));
				}
				return new GLSLProgram(shaders.toArray(new Integer[shaders.size()]));
			} catch (JDOMException e) {
				throw new RuntimeException(e);
			}
		}
		
	}, FS_LOADER = new ResourceLoader(0, "fs") {

		@Override
		public Object loadResource(InputStream in) throws IOException {
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			String src = new String(bytes);
			return GLSLProgram.buildShader(src, GL20.GL_FRAGMENT_SHADER);
		}
		
	}, VS_LOADER = new ResourceLoader(0, "vs") {

		@Override
		public Object loadResource(InputStream in) throws IOException {
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			String src = new String(bytes);
			return GLSLProgram.buildShader(src, GL20.GL_VERTEX_SHADER);
		}
		
	};
	
	private static final SAXBuilder xmlBuilder = new SAXBuilder();
	
	private Set<String> extensions;
	private int priority;
	
	public ResourceLoader(int priority, String...extensions) {
		this.priority = priority;
		this.extensions = new HashSet<String>();
		Collections.addAll(this.extensions, extensions);
	}
	
	public int compareTo(ResourceLoader other) {
		return priority-other.priority;
	}

	public Set<String> getExtensions() {
		return extensions;
	}
	
	public int getPriority() {
		return priority;
	}

	/**
	 * Loads a resource from an inputstream.
	 * @param in InputStream from source
	 * @return Loaded resource to be cached
	 * @throws IOException
	 */
	public abstract Object loadResource(InputStream in) throws IOException;
	
}