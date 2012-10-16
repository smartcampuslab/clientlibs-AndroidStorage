package eu.trentorise.smartcampus.storage;

public class DataException extends Exception {
	private static final long serialVersionUID = -3560954301528483885L;

	public DataException() {
		super();
	}

	public DataException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DataException(String detailMessage) {
		super(detailMessage);
	}

	public DataException(Throwable throwable) {
		super(throwable);
	}

	
}
