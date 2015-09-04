package spishu.space.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL20;

import spishu.space.engine.gl.GLSLProgram;
import spishu.space.engine.gl.Texture;

/**
 * A global class for loading and retrieving resources from within jar.
 * @author SpinyCucumber
 *
 */
public class Resources {
	
	private static final String EXT_DELIM = Pattern.quote(".");
	
	private static Deque<ResourceLoader> loaders;
	private static Map<String, Object> resources;
	
	public static abstract class ResourceLoader {
	
		private Set<String> extensions;
		
		public abstract Object loadResource(InputStream in) throws IOException;
		
		public ResourceLoader(String...extensions) {
			this.extensions = new HashSet<String>();
			Collections.addAll(this.extensions, extensions);
		}
		
	}
	
	static {
		
		resources = new HashMap<String, Object>();
		loaders = new ArrayDeque<ResourceLoader>();
		
		//Default loaders. Might add a control for them.
		loaders.add(new ResourceLoader("png", "jpg") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				return Texture.fromBufferedImage(ImageIO.read(in));
			}
			
		});
		loaders.add(new ResourceLoader("vs") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				return GLSLProgram.loadShader(in, GL20.GL_VERTEX_SHADER);
			}
			
		});
		loaders.add(new ResourceLoader("fs") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				return GLSLProgram.loadShader(in, GL20.GL_FRAGMENT_SHADER);
			}
			
		});
		loaders.add(new ResourceLoader("c8") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				byte[] bytes = new byte[in.available()];
				in.read(bytes);
				return ByteBuffer.wrap(bytes);
			}
			
		});
		
	}
	
	/**
	 * Iterate over entries in jar and try to load them.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void load() throws IOException, URISyntaxException {
		File src = new File(Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		if(src.isDirectory()) loadRecursive(src, src.toPath());
		else {
			ZipFile zip = new ZipFile(src);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName(), ext = name.split(EXT_DELIM)[1];
				for(ResourceLoader loader : loaders)
					if(loader.extensions.contains(ext)) resources.put(name, loader.loadResource(zip.getInputStream(entry)));
			}
			zip.close();
		}
	}

	private static void loadRecursive(File file, Path dir) throws FileNotFoundException, IOException {
		for(File c : file.listFiles()) {
			if(c.isDirectory()) loadRecursive(c, dir);
			else {
				String relPath = dir.relativize(c.toPath()).normalize().toString(), ext = relPath.split(EXT_DELIM)[1];
				for(ResourceLoader loader : loaders)
					if(loader.extensions.contains(ext)) resources.put(relPath, loader.loadResource(new FileInputStream(c)));
			}
		}
	}
	
	/**
	 * @param name
	 * @return Specified resource, or null if does not exist
	 */
	public static Object getResource(String name) {
		return resources.get(name);
	}
	
}
