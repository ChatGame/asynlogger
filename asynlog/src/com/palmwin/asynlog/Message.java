package com.palmwin.asynlog;

public class Message {

	private String content;
	private long timestamp;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public Message(){
		
	}
	public Message(String content, long timestamp) {
		super();
		this.content = content;
		this.timestamp = timestamp;
	}
	
}
