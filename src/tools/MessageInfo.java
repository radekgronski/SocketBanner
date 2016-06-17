package tools;

public class MessageInfo {
	
	private String message;
	private int milliseconds;
	
	public MessageInfo(String message, int milliseconds) {
		this.message = message;
		this.milliseconds = milliseconds;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getTimeout() {
		return milliseconds;
	}
	public void setTimeout(int milliseconds) {
		this.milliseconds = milliseconds;
	}
	
}
