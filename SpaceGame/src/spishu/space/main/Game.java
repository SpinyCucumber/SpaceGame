package spishu.space.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.lwjgl.opengl.GL20;

import spishu.space.engine.gl.GLSLProgram;
import spishu.space.engine.gl.Texture;

/**
 * A global class for accessing components such as resources and logging utilities.
 * @author SpinyCucumber
 *
 */
public class Game {
	
	/**
	 * Implementation of ResourceSource designed to recursively load from a directory.
	 * @author SpinyCucumber
	 *
	 */
	public static class DirectorySource implements ResourceSource {
		
		private File dir;
		private Path dirPath;
		
		public DirectorySource(File dir) {
			this.dir = dir;
			dirPath = dir.toPath();
		}
		
		@Override
		public Collection<String> getEntries() {
			return walkThrough(dir);
		}

		@Override
		public InputStream getStream(String resource) throws IOException {
			return new FileInputStream(new File(dir, resource));
		}

		@Override
		public String toString() {
			return "DirectorySource [dir=" + dir + "]";
		}

		private Collection<String> walkThrough(File f) {
			Deque<String> resources = new ArrayDeque<String>();
			for(File c : f.listFiles()) {
				if(c.isDirectory()) resources.addAll(walkThrough(c));
				else resources.add(dirPath.relativize(c.toPath()).toString());
			}
			return resources;
		}
		
	}
	
	/**
	 * Loads objects from inputstreams created from resources with the specifies extension.
	 * @author SpinyCucumber
	 *
	 */
	public static abstract class ResourceLoader {
	
		private Set<String> extensions;
		
		public ResourceLoader(String...extensions) {
			this.extensions = new HashSet<String>();
			Collections.addAll(this.extensions, extensions);
		}
		
		public abstract Object loadResource(InputStream in) throws IOException;
		
	}
	/**
	 * Interface for gathering resources.
	 * @author SpinyCucumber
	 *
	 */
	public interface ResourceSource {
		
		Collection<String> getEntries();
		InputStream getStream(String resource) throws IOException;
		
	}
	/**
	 * Implementation of ResourceSource designed to load from zip archives.
	 * @author SpinyCucumber
	 *
	 */
	public static class ZipSource implements ResourceSource {
		
		private ZipFile zip;
		
		public ZipSource(File src) throws ZipException, IOException {
			zip = new ZipFile(src);
		}

		@Override
		public Collection<String> getEntries() {
			Deque<String> resources = new ArrayDeque<String>();
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements()) resources.add(entries.nextElement().getName());
			return resources;
		}
		
		@Override
		public InputStream getStream(String resource) throws IOException {
			return zip.getInputStream(zip.getEntry(resource));
		}

		@Override
		public String toString() {
			return "ZipSource [zip=" + zip + "]";
		}
		
	}
	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * @param name
	 * @return Specified resource, or null if does not exist
	 */
	public static Object getResource(String name) {
		return resources.get(name);
	}
	
	public static Deque<ResourceLoader> getResourceLoaders() {
		return loaders;
	}
	
	/**
	 * Iterate over entries in jar and try to load them.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void loadResources() throws IOException {
		Collection<String> entries = source.getEntries();
		logger.info(String.format("Found %d possible resources", entries.size()));
		for(String entry : entries) {
			String ext = entry.split(EXT_DELIM)[1];
			for(ResourceLoader loader : loaders)
				if(loader.extensions.contains(ext)) {
					Object cache = loader.loadResource(source.getStream(entry));
					logger.info(String.format("Loaded resource %s from %s", cache, entry));
					resources.put(entry, cache);
				}
		}
		logger.info(String.format("Loaded %d resources", resources.size()));
	}

	private static final String EXT_DELIM = Pattern.quote(".");
	
	private static ResourceSource source;
	
	private static Logger logger;
	
	private static Deque<ResourceLoader> loaders;
	
	private static Map<String, Object> resources;
	
	static {
		
		logger = Logger.getLogger("Hi.");
		resources = new HashMap<String, Object>();
		loaders = new ArrayDeque<ResourceLoader>();

		logger.setUseParentHandlers(false);
		final long start = System.currentTimeMillis();
		Handler handler = new StreamHandler(System.out, new Formatter() {

			@Override
			public String format(LogRecord arg0) {
				return String.format("%06d:%s - %s%n", arg0.getMillis() - start, arg0.getLevel(), arg0.getMessage());
			}
			
		});
		logger.setLevel(Level.ALL);
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.info(String.format("Logger has handlers: %s", Arrays.toString(logger.getHandlers())));
		
		try {
			File src = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(src.isDirectory()) source = new DirectorySource(src);
			else source = new ZipSource(src);
			logger.info(String.format("Using %s as source", source));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		
		//Default loaders. Might add a control for them.
		loaders.add(new ResourceLoader("png", "jpg") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				return Texture.fromBufferedImage(ImageIO.read(in));
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
		
		final SAXBuilder jdomBuilder = new SAXBuilder();
		final Map<String, Integer> types = new HashMap<String, Integer>();
		types.put("vertex", GL20.GL_VERTEX_SHADER);
		types.put("fragment", GL20.GL_FRAGMENT_SHADER);
		loaders.add(new ResourceLoader("glsl") {
			
			@Override
			public Object loadResource(InputStream in) throws IOException {
				try {
					
					Element root = jdomBuilder.build(in).getRootElement();
					List<Element> shaderElems = root.getChildren();
					int[] shaders = new int[shaderElems.size()];
					
					for(int i = 0; i < shaderElems.size(); i++) {
						
						Element shaderElem = shaderElems.get(i);
						
						InputStream stream = source.getStream(shaderElem.getAttributeValue("location"));
						byte[] bytes = new byte[stream.available()];
						stream.read(bytes);
						String src = new String(bytes);
						
						int type = types.get(shaderElem.getAttributeValue("type"));
						shaders[i] = GLSLProgram.buildShader(src, type);
						
					}
					return new GLSLProgram(shaders);
				} catch (JDOMException e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		
	}
	
}
