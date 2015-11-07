package spishu.space.engine.lib;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

public class ALSource {
	
	private int handle, alBuffer;
	
	public ALSource(ByteBuffer buffer, int format, int frequency) {
		alBuffer = AL10.alGenBuffers();
		handle = AL10.alGenSources();
		AL10.alBufferData(alBuffer, format, buffer, frequency); //Give buffer meta-data
		AL10.alSourcei(handle, AL10.AL_BUFFER, alBuffer); //Assign buffer to source
	}
	
	public static ALSource fromVorbis(ByteBuffer vorbis) {
		ByteBuffer infoBuffer = STBVorbisInfo.malloc();
		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if ( decoder == MemoryUtil.NULL )
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));

		STBVorbis.stb_vorbis_get_info(decoder, infoBuffer);
		STBVorbisInfo info = new STBVorbisInfo(infoBuffer);

		int channels = info.getChannels();

		int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

		ByteBuffer pcm = BufferUtils.createByteBuffer(lengthSamples * 2);

		STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm, lengthSamples);
		STBVorbis.stb_vorbis_close(decoder);

		return new ALSource(pcm, AL10.AL_FORMAT_MONO16, info.getSampleRate());
	}
	
	public void destroy() {
		AL10.alDeleteSources(handle);
		AL10.alDeleteBuffers(alBuffer);
	}
	
	public void play() {
		AL10.alSourcePlay(handle);
	}
	
	public void stop() {
		AL10.alSourceStop(handle);
	}

	@Override
	public String toString() {
		return "ALSource [handle=" + handle + ", alBuffer=" + alBuffer + "]";
	}
	
}
