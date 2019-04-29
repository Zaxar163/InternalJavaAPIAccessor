package ru.zaxar163.unsafe.xlevel;

public class NativeAccessError extends Error {
	private static final long serialVersionUID = 6634706822016426099L;

	public NativeAccessError() {
	}

	public NativeAccessError(final String message) {
		super(message);
	}

	public NativeAccessError(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NativeAccessError(final Throwable cause) {
		super(cause);
	}
}
