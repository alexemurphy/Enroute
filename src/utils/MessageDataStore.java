package utils;

public class MessageDataStore {

	private String[] messages;
	private Boolean[] sent;
	private int[] usedID;


	public MessageDataStore(String[] messages, Boolean[] sent, int[] usedID) {
		this.messages = messages;
		this.sent = sent;
		this.usedID = usedID;
	}


	public String[] getMessages() {
		return messages;
	}


	public void setMessages(String[] messages) {
		this.messages = messages;
	}


	public Boolean[] getSent() {
		return sent;
	}


	public void setSent(Boolean[] sent) {
		this.sent = sent;
	}


	public int[] getUserID() {
		return usedID;
	}


	public void setUserID(int[] name) {
		this.usedID = usedID;
	}
}
