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

public class AudioMedia extends AbstractMedia {

	// ***************************************************************
	// Fill in your code here!
	// ***************************************************************

	private String encoding;
	private long duration;
	private String author;
	private String title;
	private String date;
	private String comment;
	private String album;
	private String track;
	private String composer;
	private String genre;
	private String frequency;
	private double bitrate;
	private int channels;

	/**
	 * Constructor.
	 */
	public AudioMedia() {
		super();
	}

	/**
	 * Constructor.
	 */
	public AudioMedia(File instance) {
		super(instance);
	}

	/* GET / SET methods */

	// ***************************************************************
	// Fill in your code here!
	// ***************************************************************

	public String getEncoding(){
		
		return encoding;
	}
	
	public long getDuration(){
		
		return duration;
	}
	
	public String getAuthor(){
		
		return author;
	}
	
	public String getTitle(){
		
		return title;
	}
	
	public String getDate(){
		
		return date;
	}
	
	public String getComment(){
		
		return comment;
	}
	
	public String getAlbum(){
		
		return album;
	}
	
	public String getTrack(){
		
		return track;
	}
	
	public String getComposer(){
		
		return composer;
	}
	
	public String getGenre(){
		
		return genre;
	}
	
	public String getFrequency(){
		
		return frequency;
	}
	
	public double getBitrate(){
		
		return bitrate;
	}
	
	public int getChannels(){
		
		return channels;
	}

	
	
	public void setEncoding(String encoding){
		
		this.encoding = encoding;
	}
	
	public void setDuration(long duration){
		
		this.duration = duration;
	}
	
	public void setAuthor(String author){
		
		this.author = author;
	}
	
	public void setTitle(String title){
		
		this.title = title;
	}
	
	public void setDate(String date){
		
		this.date = date;
	}
	
	public void setComment(String comment){
		
		this.comment = comment;
	}
	
	public void setAlbum(String album){
		
		this.album = album;
	}
	
	public void setTrack(String track){
		
		this.track = track;
	}
	
	public void setComposer(String composer){
		
		this.composer = composer;
	}
	
	public void setGenre(String genre){
		
		this.genre = genre;
	}
	
	public void setFrequency(String frequency){
		
		this.frequency = frequency;
	}
	
	public void setBitrate(long bitrate){
		
		this.bitrate = bitrate;
	}
	
	public void setChannels(int channels){
		
		this.channels = channels;
	}

	/* (de-)serialization */

	/**
	 * Serializes this object to the passed file.
	 * 
	 */
	@Override
	public StringBuffer serializeObject() throws IOException {
		StringWriter data = new StringWriter();
		PrintWriter out = new PrintWriter(data);
		out.println("type: audio");
		StringBuffer sup = super.serializeObject();
		out.print(sup);

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		out.println("Encoding: " + this.getEncoding());
		out.println("Duration: " + this.getDuration());
		out.println("Author: " + this.getAuthor());
		out.println("Title: " + this.getTitle());
		out.println("Date: " + this.getDate());
		out.println("Comment: " + this.getComment());
		out.println("Album: " + this.getAlbum());
		out.println("Track: " + this.getTrack());
		out.println("Composer: " + this.getComposer());
		out.println("Genre: " + this.getGenre());
		out.println("Frequency: "+ this.getFrequency());
		out.println("Bitrate: " + this.getBitrate());
		out.println("Channels: "+ this.getChannels());

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

			if(line.startsWith("Encoding: ")){
				this.setEncoding(line.substring("Encoding: ".length()));
			}
			
			else if(line.startsWith("Duration: ")){
				this.duration = (Integer.parseInt (line.substring("Duration: ".length())));
			}
			
			else if(line.startsWith("Author: ")){
				this.setAuthor(line.substring("Author: ".length()));
			}
			
			else if(line.startsWith("Title: ")){
				this.setTitle(line.substring("Title: ".length()));
			}
			
			else if(line.startsWith("Date: ")){
				this.setDate(line.substring("Date: ".length()));
			}
			
			else if(line.startsWith("Comment: ")){
				this.setComment(line.substring("Comment: ".length()));
			}
			
			else if(line.startsWith("Album: ")){
				this.setAlbum(line.substring("Album: ".length()));
			}
			
			else if(line.startsWith("Track: ")){
				this.setTrack(line.substring("Track: ".length()));
			}
			
			else if(line.startsWith("Composer: ")){
				this.composer = (line.substring("Composer: ".length()));
			}
			
			else if(line.startsWith("Genre: ")){
				this.genre = line.substring("Genre: ".length());
			}
			
			else if(line.startsWith("Frequency: ")){
				this.setFrequency(line.substring("Frequency ".length()));
			}
			
			else if(line.startsWith("bitrate: ")){
				this.setBitrate(Integer.parseInt(line.substring("bitrate: ".length())));
				
			}
			
			else if(line.startsWith("Channels: ")){
				this.setChannels(Integer.parseInt(line.substring("Channels: ".length())));
			}

		}
	}

}
