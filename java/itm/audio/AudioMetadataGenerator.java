package itm.audio;

/*******************************************************************************
 This file is part of the ITM course 2017
 (c) University of Vienna 2009-2017
 *******************************************************************************/

import itm.model.AudioMedia;
import itm.model.MediaFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * This class reads audio files of various formats and stores some basic audio
 * metadata to text files. It can be called with 3 parameters, an input
 * filename/directory, an output directory and an "overwrite" flag. It will read
 * the input audio file(s), retrieve some metadata and write it to a text file
 * in the output directory. The overwrite flag indicates whether the resulting
 * output file should be overwritten or not.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */
public class AudioMetadataGenerator {

	/**
	 * Constructor.
	 */
	public AudioMetadataGenerator() {
	}

	/**
	 * Processes an audio file directory in a batch process.
	 * 
	 * @param input
	 *            a reference to the audio file directory
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return a list of the created media objects (images)
	 */
	public ArrayList<AudioMedia> batchProcessAudio(File input, File output,
			boolean overwrite) throws IOException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		ArrayList<AudioMedia> ret = new ArrayList<AudioMedia>();

		if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File f : files) {

				String ext = f.getName().substring(
						f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) {
					try {
						AudioMedia result = processAudio(f, output, overwrite);
						System.out.println("created metadata for file " + f
								+ " in " + output);
						ret.add(result);
					} catch (Exception e0) {
						System.err
								.println("Error when creating metadata from file "
										+ input + " : " + e0.toString());
					}

				}

			}
		} else {

			String ext = input.getName().substring(
					input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) {
				try {
					AudioMedia result = processAudio(input, output, overwrite);
					System.out.println("created metadata for file " + input
							+ " in " + output);
					ret.add(result);
				} catch (Exception e0) {
					System.err
							.println("Error when creating metadata from file "
									+ input + " : " + e0.toString());
				}

			}

		}
		return ret;
	}

	/**
	 * Processes the passed input audio file and stores the extracted metadata
	 * to a textfile in the output directory.
	 * 
	 * @param input
	 *            a reference to the input audio file
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return the created image media object
	 */
	protected AudioMedia processAudio(File input, File output, boolean overwrite)
			throws IOException, IllegalArgumentException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		// create outputfilename and check whether thumb already exists. All
		// image metadata files have to start with "aud_" - this is used by the
		// mediafactory!
		File outputFile = new File(output, "aud_" + input.getName() + ".txt");
		if (outputFile.exists())
			if (!overwrite) {
				// load from file
				AudioMedia media = new AudioMedia();
				media.readFromFile(outputFile);
				return media;
			}

		
		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		// create an audio metadata object
		AudioMedia media = (AudioMedia) MediaFactory.createMedia(input);	
	
		// load the input audio file, do not decode
		AudioInputStream audioInputStream = null;
		try {

			audioInputStream = AudioSystem.getAudioInputStream(input);
		
			// read AudioFormat properties
		
			AudioFormat audioFormat = audioInputStream.getFormat();

			// read file-type specific properties
		
			AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(input);
		
			Map<String, Object> properties = audioFileFormat.properties();
		
			media.setDuration( (Long) properties.get("duration"));
			media.setAuthor((String) properties.get("author"));
			media.setTitle((String) properties.get("title"));
			media.setDate((String) properties.get("date"));
			media.setComment((String) properties.get("comment"));
			media.setAlbum((String) properties.get("album"));
			
			// you might have to distinguish what properties are available for what audio format
		
			AudioFileFormat.Type type = audioFileFormat.getType();
		
			if (type.getExtension().equals("mp3")) {

				media.setTrack((String) properties.get("mp3.id3tag.track"));
				media.setComposer((String) properties.get("mp3.id3tag.composer"));
				media.setGenre((String) properties.get("mp3.id3tag.genre"));
				media.setBitrate ((Integer) properties.get("mp3.bitrate.nominal.bps"));

			} else if (type.equals("ogg")|| type.equals("Ogg")) {	

				media.setTrack((String) properties.get("ogg.comment.track"));
				media.setGenre((String) properties.get("ogg.comment.genre"));
				media.setComposer((String) properties.get("ogg.comment.composer"));
				media.setBitrate ((Long) properties.get("ogg.bitrate.nominal.bps"));

			} else if (type.equals("wave")||type.equals("wav")||type.equals("WAVE")) {

			 	media.setTrack((String) properties.get("wave.id3tag.track"));
				media.setGenre((String) properties.get("wave.id3tag.genre"));
				media.setComposer((String) properties.get("wave.id3tag.composer"));
				media.setBitrate ((Integer) properties.get("wave.bitrate.nominal.bps"));
			}
		
			// add a "audio" tag
			media.addTag("audio");
		}

		catch(Exception e) {
			System.out.println("Error:"+ e.getMessage());
		}
		
		// close the audio and write the md file.
		
		media.writeToFile(outputFile);
		audioInputStream.close();
		return media;

	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		//args = new String[] { "./media/audio", "./media/md" };
		
		if (args.length < 2) {
			System.out
					.println("usage: java itm.image.AudioMetadataGenerator <input-image> <output-directory>");
			System.out
					.println("usage: java itm.image.AudioMetadataGenerator <input-directory> <output-directory>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		File fo = new File(args[1]);
		AudioMetadataGenerator audioMd = new AudioMetadataGenerator();
		audioMd.batchProcessAudio(fi, fo, true);
	}
}
