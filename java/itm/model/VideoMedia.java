package itm.model;

/*******************************************************************************
 This file is part of the ITM course 2017
 (c) University of Vienna 2009-2017
 *******************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.xuggle.xuggler.IRational;

public class VideoMedia extends AbstractMedia {

	// ***************************************************************
	// Fill in your code here!
	// ***************************************************************

	/* video format metadata */

    //videoFrameRate
    IRational videoFrameRate;
    
    //videoLength [seconds]
    int videoLength;
    
    //videoHeight [pixel], videoWidth [pixel]
    int videoHeigth;
    int videoWidth;

    //videoCodec and codecID
    String videoCodec;


	/* audio format metadata */

	//audioCodec and codecID
	String audioCodec;
	String audioCodecID;
	
	//audioChannels (number of channels)
	int	audioChannels;
	
	//audioSampleRate [Hz]
	int audioSampleRate;
	
	//audioBitRate [kb/s]
	int audioBitRate;


	/**
	 * Constructor.
	 */
	public VideoMedia() {
		super();
	}

	/**
	 * Constructor.
	 */
	public VideoMedia(File instance) {
		super(instance);
	}

	/* GET / SET methods */

	/* GET / SET methods */

   	public String getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	public String getVideoCodecID() {
		return videoCodecID;
	}

	public void setVideoCodecID(String videoCodecID) {
		this.videoCodecID = videoCodecID;
	}

	public IRational getVideoFrameRate() {
		return videoFrameRate;
	}

	public void setVideoFrameRate(IRational videoFrameRate) {
		this.videoFrameRate = videoFrameRate;
	}

	public int getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(int videoLength) {
		this.videoLength = videoLength;
	}

	public int getVideoHeigth() {
		return videoHeigth;
	}

	public void setVideoHeigth(int videoHeigth) {
		this.videoHeigth = videoHeigth;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public String getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public String getAudioCodecID() {
		return audioCodecID;
	}

	public void setAudioCodecID(String audioCodecID) {
		this.audioCodecID = audioCodecID;
	}

	public int getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(int audioChannels) {
		this.audioChannels = audioChannels;
	}

	public int getAudioSampleRate() {
		return audioSampleRate;
	}

	public void setAudioSampleRate(int audioSampleRate) {
		this.audioSampleRate = audioSampleRate;
	}

	public int getAudioBitRate() {
		return audioBitRate;
	}

	public void setAudioBitRate(int audioBitRate) {
		this.audioBitRate = audioBitRate;
	}

	String videoCodecID;

	/* (de-)serialization */

	/**
	 * Serializes this object to the passed file.
	 * 
	 */
	@Override
	public StringBuffer serializeObject() throws IOException {
		StringWriter data = new StringWriter();
		PrintWriter out = new PrintWriter(data);
		out.println("type: video");
		StringBuffer sup = super.serializeObject();
		out.print(sup);

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		/* video fields */
		out.println("videoCodec: " + getVideoCodec() );
		out.println("codecID: " + getVideoCodecID());
		out.println("videoFrameRate: " + getVideoFrameRate());
		out.println("videoLength: " + getVideoLength());
		out.println("videoHeight: " + getVideoHeigth());
		out.println("videoWidth: " + getVideoWidth());
		
		/* audio fields */
		out.println("audioCodec: " + getAudioCodec());
		out.println("codecID: " + getAudioCodecID());
		out.println("audioChannels: " + getAudioChannels());
		out.println("audioSampleRate: "+ getAudioSampleRate());
		out.println("audioBitRate: " + getAudioBitRate());
		
		return data.getBuffer();
	}

	/**
	 * Deserializes this object from the passed string buffer.
	 */
	@Override
	public void deserializeObject(String data) throws IOException {
		super.deserializeObject(data);

		StringReader sr = new StringReader(data);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		while ((line = br.readLine()) != null) {

			// ***************************************************************
			// Fill in your code here!
			// ***************************************************************

			/* video fields */
			if (line.startsWith("videoCodec: "))
				setVideoCodec((line.substring("videoCodec:".length())));
			else if (line.startsWith("codecID: "))
				setVideoCodecID(line.substring("codecID: ".length()));
			else if (line.startsWith("videoFrameRate: "))
				setVideoFrameRate(IRational.make(Integer.parseInt((line.substring("videoFrameRate: ".length())))));
			else if (line.startsWith("videoLength: "))
				setVideoLength(Integer.parseInt(line.substring("videoLength: ".length())));
			else if (line.startsWith("videoHeight: "))
				setVideoHeigth(Integer.parseInt(line.substring("videoHeight: ".length())));
			else if (line.startsWith("videoWidth: "))
				setVideoWidth(Integer.parseInt(line.substring("videoWidth: ".length())));
			
			/* audio fields */
			else if (line.startsWith("audioCodec: "))
				setAudioCodec((line.substring("audioCodec: ".length())));
			else if (line.startsWith("codecID: "))
				setAudioCodecID((line.substring("codecID: ".length())));
			else if (line.startsWith("audioChannels: "))
				setAudioChannels(Integer.parseInt(line.substring("audioChannels: ".length())));
			else if (line.startsWith("audioSampleRate: "))
				setAudioSampleRate(Integer.parseInt(line.substring("audioSampleRate: ".length())));
			else if (line.startsWith("audioBitRate: "))
				setAudioBitRate(Integer.parseInt(line.substring("audioBitRate: ".length())));
		}
	}

}
