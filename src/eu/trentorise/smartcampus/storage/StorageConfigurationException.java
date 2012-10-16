package eu.trentorise.smartcampus.storage;

public class StorageConfigurationException extends Exception {
	private static final long serialVersionUID = 4598501803681585519L;

	public StorageConfigurationException() {
		super();
	}

	public StorageConfigurationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public StorageConfigurationException(String detailMessage) {
		super(detailMessage);
	}

	public StorageConfigurationException(Throwable throwable) {
		super(throwable);
	}

	
}
