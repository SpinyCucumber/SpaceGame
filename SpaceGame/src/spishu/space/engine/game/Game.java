package spishu.space.engine.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * A global class for accessing components such as resources and logging utilities.
 * @author SpinyCucumber
 *
 */
public final class Game {
	
	/**
	 * Implementation of ResourceSource designed to recursively load from a directory.
	 * @author SpinyCucumber
	 *
	 */
	private static class DirectorySource implements ResourceSource {
		
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
	private static class ZipSource implements ResourceSource {
		
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
	
	public static void addLoader(ResourceLoader loader) {
		loaders.add(loader);
	}
	
	public static List<ResourceLoader> getLoaders() {
		return loaders;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * Formats string than passes to logger.
	 */
	public static void log(Level level, String message, Object...args) {
		logger.log(level, String.format(message, args));
	}
	
	public static void info(String message, Object...args) {
		log(Level.INFO, message, args);
	}
	
	/**
	 * @param name
	 * @return Specified resource, or null if does not exist
	 */
	public static Object getResource(String name) {
		return resources.get(name.replace("\\\\", File.separator));
	}
	/**
	 * @return Object representing code source
	 */
	public static ResourceSource getSource() {
		return source;
	}
	
	/**
	 * Iterate over entries in jar and try to load them.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void loadResources() throws IOException {
		
		info("Loading resources...");
		Collections.sort(loaders);
		info("Using %d resource loaders (Sorted based on priority)", loaders.size());

		Collection<String> entries = source.getEntries();
		info("Found %d possible resources", entries.size());
		for(String entry : entries) log(Level.FINE, "Entry: %s", entry);
		
		for(ResourceLoader loader : loaders) {
			for(String entry : entries) {
				String extension = entry.split(EXT_DELIM)[1];
				if(!loader.getExtensions().contains(extension)) continue;
				try {
					InputStream in = source.getStream(entry);
					Object obj = loader.loadResource(in);
					resources.put(entry, obj);
					info("Loaded resource (%s) %s from %s", obj.getClass(), obj, entry);
				} catch(IOException e) {
					throw new IOException(String.format("Error loading from %s:", entry), e);
				}
			}
		}
		
		info("Loaded %d total resources out of %d possible resources", resources.size(), entries.size());
		
	}
	
	/**
	 * Use resource loaders defined in Game class.
	 */
	public static void useDefaultLoaders() {
		info("Using default loaders");
		loaders.addAll(defaultLoaders);
	}

	private static final String EXT_DELIM = Pattern.quote(".");
	
	private static final Formatter loggerFormatter = new Formatter() {
		
		private long start;
		
		{
			start = System.currentTimeMillis();
		}
		
		@Override
		public String format(LogRecord arg0) {
			StringBuilder builder = new StringBuilder();
			builder.append(String.format("%06d:%s - %s%n", arg0.getMillis() - start, arg0.getLevel(), arg0.getMessage()));
			Throwable thrown = arg0.getThrown();
			if(thrown != null) {
				StringWriter str = new StringWriter();
				thrown.printStackTrace(new PrintWriter(str));
				builder.append(str.toString());
			}
			return builder.toString();
		}
		
	};
	
	public static void setSource(File src) throws IOException {
		if(src.isDirectory()) source = new DirectorySource(src);
		else source = new ZipSource(src);
		info("Using %s as source", source);
	}
	
	public static void setSource(CodeSource src) throws IOException, URISyntaxException {
		setSource(new File(src.getLocation().toURI()));
	}
	
	public static void setSource(Class<?> src) throws IOException, URISyntaxException {
		setSource(src.getProtectionDomain().getCodeSource());
	}
	
	private static void setUpDefaultLoaders() {
		defaultLoaders = new ArrayList<ResourceLoader>();
		defaultLoaders.add(ResourceLoader.ANIM_LOADER);
		defaultLoaders.add(ResourceLoader.TEX_LOADER);
		defaultLoaders.add(ResourceLoader.RAW_LOADER);
		defaultLoaders.add(ResourceLoader.GLSL_LOADER);
		defaultLoaders.add(ResourceLoader.FS_LOADER);
		defaultLoaders.add(ResourceLoader.VS_LOADER);
		defaultLoaders.add(ResourceLoader.YAML_LOADER);
		defaultLoaders.add(ResourceLoader.OGG_LOADER);
	}
	
	private static ResourceSource source;
	
	private static Logger logger;
	
	private static List<ResourceLoader> loaders, defaultLoaders;
	
	private static Map<String, Object> resources;
	
	static {
		
		logger = Logger.getLogger("SpaceGame");
		resources = new HashMap<String, Object>();
		loaders = new ArrayList<ResourceLoader>();

		logger.setUseParentHandlers(false);
		Handler handler = new StreamHandler(System.out, loggerFormatter);
		logger.addHandler(handler);
		
		setUpDefaultLoaders();
		
	}
	
}
