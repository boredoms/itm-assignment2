package itm.video;

/*******************************************************************************
 This file is part of the ITM course 2017
 (c) University of Vienna 2009-2017
 *******************************************************************************/

import itm.util.ImageCompare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.xuggle.xuggler.*;
import com.xuggle.mediatool.*;
import com.xuggle.xuggler.video.*;
import java.awt.image.BufferedImage;

import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

/**
 * This class reads video files, extracts metadata for both the audio and the
 * video track, and writes these metadata to a file.
 * 
 * It can be called with 3 parameters, an input filename/directory, an output
 * directory and an "overwrite" flag. It will read the input video file(s),
 * retrieve the metadata and write it to a text file in the output directory.
 * The overwrite flag indicates whether the resulting output file should be
 * overwritten or not.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */
public class VideoThumbnailGenerator {

	/**
	 * Constructor.
	 */
	public VideoThumbnailGenerator() {
	}

	/**
	 * Processes a video file directory in a batch process.
	 * 
	 * @param input
	 *            a reference to the video file directory
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing output files should be overwritten
	 *            or not
	 * @return a list of the created media objects (videos)
	 */
	public ArrayList<File> batchProcessVideoFiles(File input, File output, boolean overwrite, int timespan) throws IOException {
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

				String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv")
						|| ext.equals("mp4"))
					try {
						File result = processVideo(f, output, overwrite, timespan);
						System.out.println("processed file " + f + " to " + output);
						ret.add(result);
					} catch (Exception e0) {
						System.err.println("Error processing file " + input + " : " + e0.toString());
					}
			}
		} else {

			String ext = input.getName().substring(input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv") || ext.equals("mp4"))
				try {
					File result = processVideo(input, output, overwrite, timespan);
					System.out.println("processed " + input + " to " + result);
					ret.add(result);
				} catch (Exception e0) {
					System.err.println("Error when creating processing file " + input + " : " + e0.toString());
				}

		}
		return ret;
	}

	/**
	 * Processes the passed input video file and stores a thumbnail of it to the
	 * output directory.
	 * 
	 * @param input
	 *            a reference to the input video file
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing files should be overwritten or not
	 * @return the created video media object
	 */
	protected File processVideo(File input, File output, boolean overwrite, int timespan) throws Exception {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		// create output file and check whether it already exists.
		File outputFile = new File(output, input.getName() + "_thumb.avi");

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************
        long timespan_us = timespan * 1000000;

        IContainer container = IContainer.make();
        if (container.open(input.toString(), IContainer.Type.READ, null) < 0)
            throw new IOException("Could not open file: " + input);

        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }

        if (videoStreamId == -1)
            throw new RuntimeException("could not find video stream in " + input);

        if (videoCoder.open() < 0)
            throw new RuntimeException("could not open video decoder");

        IVideoResampler resampler = null;

        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
            resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                                             videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());

            if (resampler == null)
                throw new RuntimeException("could not find resampler");
        }

        long last_write = Global.NO_PTS;
        IPacket packet = IPacket.make();
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();

        while (container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() != videoStreamId)
                continue;

            IVideoPicture pic = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
            int offset = 0;

            while (offset < packet.getSize()) {
                offset += videoCoder.decodeVideo(pic, packet, offset);

                if (!pic.isComplete())
                    continue;

                if (timespan > 0 && pic.getPts() - last_write <= timespan_us) {
                    break;
                } else {
                    last_write = pic.getPts();
                    if (resampler != null) {
                        IVideoPicture pic2 = IVideoPicture.make(IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight());
                        resampler.resample(pic2, pic);
                        frames.add(Utils.videoPictureToImage(pic2));
                    } else {
                        frames.add(Utils.videoPictureToImage(pic));
                    }
                }
            }
        }
        videoCoder.close();
        container.close();

		// extract frames from input video
		File watermark_file = new File("./chen_watermark.png");
        BufferedImage watermark = ImageIO.read(watermark_file);

        double thumb_scale_factor = (double)frames.get(0).getHeight() / watermark.getHeight();
        AffineTransform scale = AffineTransform.getScaleInstance(thumb_scale_factor, thumb_scale_factor);
        AffineTransformOp op = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);
        watermark = op.filter(watermark, null);

        for (BufferedImage img : frames) {
            Graphics2D g2d = (Graphics2D) img.getGraphics();
            g2d.drawImage(watermark, 0, 0, null);
        }

		// add a watermark of your choice and paste it to the image
        // e.g. text or a graphic
        IContainer writer = IContainer.make();
        if (writer.open(outputFile.toString(), IContainer.Type.WRITE, null) < 0)
            throw new RuntimeException("failed to open write container");
		// create a video writer

        ICodec outCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
        IStream outStream = writer.addNewStream(outCodec);
        IStreamCoder outCoder = outStream.getStreamCoder();

        IRational frameRate = IRational.make(1, 1);

        outCoder.setHeight(frames.get(0).getHeight());
        outCoder.setWidth(frames.get(0).getWidth());
        outCoder.setFrameRate(frameRate);
        outCoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
        outCoder.setPixelType(IPixelFormat.Type.YUV420P);

        outCoder.open(null, null);
        writer.writeHeader();

		// add a stream with the proper width, height and frame rate
        
		if (timespan == 0) {
            BufferedImage last = null;
            ArrayList<BufferedImage> temp = new ArrayList<BufferedImage>();
            for (BufferedImage img : frames) {
                if (last == null) {
                    last = img;
                    continue;
                }

                ImageCompare comparator = new ImageCompare(last, img);

                comparator.compare();

                if (!comparator.match()) {
                    last = img;
                    temp.add(img);
                }
            }

            frames = temp;
        }
		// if timespan is set to zero, compare the frames to use and add 
		// only frames with significant changes to the final video
        long timestamp = 0;
        for (BufferedImage img : frames) {
            BufferedImage outputImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            outputImg.getGraphics().drawImage(img, 0, 0, null);

            IPacket outPacket = IPacket.make();

            IConverter converter = ConverterFactory.createConverter(outputImg, outCoder.getPixelType());

            IVideoPicture vpic = converter.toPicture(outputImg, timestamp);



            if (outCoder.encodeVideo(outPacket, vpic, 0) < 0) {
                throw new RuntimeException("could not encode video");
            }

            if (outPacket.isComplete()) {
                if (writer.writePacket(outPacket) < 0)
                    throw new RuntimeException("could not write to output");
            }
            timestamp += 1000000;
        }
		// loop: get the frame image, encode the image to the video stream
        writer.writeTrailer();

		outCoder.close();
        writer.close();
		// Close the writer

		return outputFile;
	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
            System.out.println("usage: java itm.video.VideoThumbnailGenerator <input-video> <output-directory> <timespan>");
            System.out.println("usage: java itm.video.VideoThumbnailGenerator <input-directory> <output-directory> <timespan>");
            System.exit(1);
        }
        File fi = new File(args[0]);
        File fo = new File(args[1]);
        int timespan = 5;
        if(args.length == 3)
            timespan = Integer.parseInt(args[2]);
        
        VideoThumbnailGenerator videoMd = new VideoThumbnailGenerator();
        videoMd.batchProcessVideoFiles(fi, fo, true, timespan);
	}
}
