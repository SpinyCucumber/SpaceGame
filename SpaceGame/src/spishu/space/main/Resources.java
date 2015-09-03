package spishu.space.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import spishu.space.engine.gl.Texture;

/**
 * A global class for loading and retrieving resources from within jar.
 * @author SpinyCucumber
 *
 */
public class Resources {
	
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
		
		loaders = new ArrayDeque<ResourceLoader>();
		loaders.add(new ResourceLoader("png", "jpg") {

			@Override
			public Object loadResource(InputStream in) throws IOException {
				return Texture.fromBufferedImage(ImageIO.read(in));
			}
			
		});
		
	}
	
	public static void load() throws IOException, URISyntaxException {
		ZipFile zip = new ZipFile(Paths.get(Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile());
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName(), ext = name.split(".")[1];
			for(ResourceLoader loader : loaders)
				if(loader.extensions.contains(ext)) resources.put(name, loader.loadResource(zip.getInputStream(entry)));
		}
		zip.close();
	}
	
	public static Texture getTexture(String name) {
		return (Texture) resources.get(name);
	}
	
}
