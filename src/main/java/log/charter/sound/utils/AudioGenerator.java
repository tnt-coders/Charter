package log.charter.sound.utils;

import static java.lang.Math.ceil;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import javax.sound.sampled.AudioFormat;

import log.charter.sound.data.AudioData;
import log.charter.sound.data.AudioUtils;

public class AudioGenerator {
	public static AudioData generateSilence(final double lengthSeconds, final float sampleRate, final int channels,
			final int sampleSize) {
		final int frames = (int) ceil(lengthSeconds * sampleRate);
		final int frameSize = channels * sampleSize;
		final int length = frames * frameSize;
		return new AudioData(new byte[length], sampleRate, sampleSize, channels);
	}

	public static AudioData generateSilence(final double lengthSeconds, final AudioFormat format) {
		return generateSilence(lengthSeconds, format.getSampleRate(), format.getChannels(),
				format.getSampleSizeInBits() / 8);
	}

	public static AudioData generateEmpty(final double lengthSeconds) {
		return generateSilence(lengthSeconds, AudioUtils.DEF_RATE, 1, 2);
	}

	public static AudioData generateSound(final double pitchHz, final double lengthSeconds, final double loudness,
			final float sampleRate) {
		final int[] data = new int[(int) (lengthSeconds * sampleRate)];
		for (int i = 0; i < data.length; i++) {
			data[i] = (int) (pow(sin((pitchHz * Math.PI * i) / sampleRate), 2) * loudness * AudioData.getMax(2));
		}

		return new AudioData(new int[][] { data }, sampleRate, 2);
	}

	public static AudioData generateSound(final double pitchHz, final double lengthSeconds, final double loudness) {
		return generateSound(pitchHz, lengthSeconds, loudness, AudioUtils.DEF_RATE);
	}
}
