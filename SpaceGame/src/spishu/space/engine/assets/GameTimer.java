package spishu.space.engine.assets;

import org.lwjgl.glfw.GLFW;

/**
 * Utility class for glfw timing methods. Using double internally for precision.
 * @author SpinyCucumber
 *
 */
public class GameTimer {
	
	private double time, lastTime, timeScale;
	private long frames;
	
	public double getTime() {
		return time;
	}

	public long getFrames() {
		return frames;
	}

	/**
	 * Updates time, returns delta and increments frames.
	 * @return Time in seconds between current frame and last frame.
	 */
    public double update() {
	    time = GLFW.glfwGetTime() * timeScale;
	    double delta = time - lastTime;
    	lastTime = time;
    	frames++;
    	return delta;
    }
	
	public GameTimer(double timeScale) {
		this.timeScale = timeScale;
	}

	@Override
	public String toString() {
		return "GLTimer [timeScale=" + timeScale + "]";
	}
	
}
