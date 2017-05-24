package itm.audio;

/*******************************************************************************
 This file is part of the ITM course 2017
 (c) University of Vienna 2009-2017
 *******************************************************************************/

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays an audio file using the system's default sound output device
 * 
 */
public class AudioPlayer {

	/**
	 * Constructor
	 */
	public AudioPlayer() {

	}

	/**
	 * Plays audio data from a given input file to the system's default sound
	 * output device
	 * 
	 * @param input
	 *            the audio file
	 * @throws IOException
	 *             general error when accessing audio file
	 */
	protected void playAudio(File input) throws IOException {

		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");

		AudioInputStream audio = null;
		try {
			audio = openAudioInputStream(input);
		} catch (UnsupportedAudioFileException e) {
			throw new IOException("could not open audio file " + input
					+ ". Encoding / file format not supported");
		}

		try {
			rawplay(audio);
		} catch (LineUnavailableException e) {
			throw new IOException("Error when playing sound from file "
					+ input.getName() + ". Sound output device unavailable");
		}

		audio.close();

	}

	/**
	 * Decodes an encoded audio file and returns a PCM input stream
	 * 
	 * Supported encodings: MP3, OGG (requires SPIs to be in the classpath)
	 * 
	 * @param input
	 *            a reference to the input audio file
	 * @return a PCM AudioInputStream
	 * @throws UnsupportedAudioFileException
	 *             an audio file's encoding is not supported
	 * @throws IOException
	 *             general error when accessing audio file
	 */
	private AudioInputStream openAudioInputStream(File input)
			throws UnsupportedAudioFileException, IOException {

		AudioInputStream din = null;
		
		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		// open audio stream
		
		din = AudioSystem.getAudioInputStream(input);


		// get format
		AudioFormat audioFormat = din.getFormat();

		// get decoded format
		
		AudioFormat decformat = new AudioFormat(Encoding.PCM_SIGNED, audioFormat.getSampleRate(),audioFormat.getSampleSizeInBits()
				, audioFormat.getChannels(),audioFormat.getFrameSize(),audioFormat.getSampleRate(), false);
		
		// get decoded audio input stream
		
		din = AudioSystem.getAudioInputStream(decformat, din);
		
		return din;
	}

	/**
	 * Writes audio data from an AudioInputStream to a SourceDataline
	 * 
	 * @param audio
	 *            the audio data
	 * @throws IOException
	 *             error when writing audio data to source data line
	 * @throws LineUnavailableException
	 *             system's default source data line is not available
	 */
	private void rawplay(AudioInputStream audio) throws IOException,
			LineUnavailableException {

		
		
		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		
		// get audio format
		
		System.out.println(audio.getFormat());
		AudioFormat audioFormat = audio.getFormat();
		
		// get a source data line
		
		SourceDataLine sdline = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
		sdline.open(audioFormat);
		sdline.start();

		// read samples from audio and write them to the data line

		int bytes = 4096;
		byte[] data = new byte [bytes];
		int count = 0;
		
		while (count != -1) {
			count = audio.read(data, 0 , data.length);
			if(count >= 0) 
				sdline.write(data, 0, count);
		}
		

		// properly close the line!
		
		sdline.drain();
		sdline.stop();
		sdline.close();
	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out
					.println("usage: java itm.audio.AudioPlayer <input-audioFile>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		AudioPlayer player = new AudioPlayer();
		player.playAudio(fi);
		System.exit(0);

	}

}
