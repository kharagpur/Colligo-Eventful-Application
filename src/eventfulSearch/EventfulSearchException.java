package eventfulSearch;

public class EventfulSearchException extends RuntimeException {
	public EventfulSearchException(String msg) {
		super(msg);
	}
	public EventfulSearchException(String msg, Exception e) {
		super(msg, e);
	}
}
