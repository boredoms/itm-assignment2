package itm.video;

/*******************************************************************************
 This file is part of the ITM course 2017
 (c) University of Vienna 2009-2017
 *******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

import com.xuggle.ferry.JNILibraryLoader;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IPixelFormat.Type;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;


/**
 * 
 * This class creates JPEG thumbnails from from video frames grabbed from the
 * middle of a video stream It can be called with 2 parameters, an input
 * filename/directory and an output directory.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */

public class VideoFrameGrabber {

	/**
	 * Constructor.
	 */
	public VideoFrameGrabber() {
	}

	/**
	 * Processes the passed input video file / video file directory and stores
	 * the processed files in the output directory.
	 * 
	 * @param input
	 *            a reference to the input video file / input directory
	 * @param output
	 *            a reference to the output directory
	 */
	public ArrayList<File> batchProcessVideoFiles(File input, File output) throws IOException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		ArrayList<File> ret = new ArrayList<File>();

		if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File f : files) {
				if (f.isDirectory())
					continue;

				String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv")
						|| ext.equals("mp4")) {
					File result = processVideo(f, output);
					System.out.println("converted " + f + " to " + result);
					ret.add(result);
				}

			}

		} else {
			String ext = input.getName().substring(input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv") || ext.equals("mp4")) {
				File result = processVideo(input, output);
				System.out.println("converted " + input + " to " + result);
				ret.add(result);
			}
		}
		return ret;
	}

	/**
	 * Processes the passed audio file and stores the processed file to the
	 * output directory.
	 * 
	 * @param input
	 *            a reference to the input audio File
	 * @param output
	 *            a reference to the output directory
	 */
	protected File processVideo(File input, File output) throws IOException, IllegalArgumentException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		File outputFile = new File(output, input.getName() + "_thumb.jpg");
		// load the input video file

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		// that helped me: https://github.com/artclarke/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DecodeAndCaptureFrames.java
		try {
			
			IContainer container = IContainer.make();

			int containerExists = container.open(input.getAbsolutePath(), IContainer.Type.READ, null);

			if (containerExists < 0)
				throw new RuntimeException("Failed to open media file");
			
			int i = 0;
			IStreamCoder streamCoder = null;

			int streamnum = container.getNumStreams();
			
			for (; i < streamnum; i++) {
				streamCoder = container.getStream(i).getStreamCoder();

				if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
					break;
			}
			
			streamCoder.open();
			boolean resample = false;
			IVideoResampler videoResampler = null;
			
			if (streamCoder.getPixelType() != IPixelFormat.Type.BGR24) {
				resample = true;
				videoResampler = IVideoResampler.make(streamCoder.getWidth(),
						streamCoder.getHeight(), IPixelFormat.Type.BGR24,
						streamCoder.getWidth(), streamCoder.getHeight(),
						streamCoder.getPixelType());
			}

			IPacket packet = IPacket.make();
			ArrayList<IVideoPicture> savedFrames = new ArrayList<IVideoPicture>();
			

			while (container.readNextPacket(packet) >= 0) {

				if (packet.getStreamIndex() != i)
					continue;

				IVideoPicture pic = IVideoPicture.make(streamCoder.getPixelType(),
						streamCoder.getWidth(), streamCoder.getHeight());

				int offset = 0;

				while (offset < packet.getSize()) {

					offset += streamCoder.decodeVideo(pic, packet, offset);

					if (!pic.isComplete())
						continue;
					
					if (resample) {
							IVideoPicture pic2 = IVideoPicture.make(
									IPixelFormat.Type.BGR24, streamCoder.getWidth(),
									streamCoder.getHeight());
							videoResampler.resample(pic2, pic);
							savedFrames.add(pic2);
					} else {
						savedFrames.add(pic);
					}		

				}
			}

			@SuppressWarnings("deprecation")
			BufferedImage img = com.xuggle.xuggler.Utils.videoPictureToImage(savedFrames.get(savedFrames.size()/2)); // get Frame in the middle of savedFrames
			ImageIO.write(img, "JPEG", outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputFile;

	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		// args = new String[] { "./media/video", "./test" };

		if (args.length < 2) {
			System.out.println("usage: java itm.video.VideoFrameGrabber <input-videoFile> <output-directory>");
			System.out.println("usage: java itm.video.VideoFrameGrabber <input-directory> <output-directory>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		File fo = new File(args[1]);
		VideoFrameGrabber grabber = new VideoFrameGrabber();
		grabber.batchProcessVideoFiles(fi, fo);
	}

}
