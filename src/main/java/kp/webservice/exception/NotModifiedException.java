package kp.webservice.exception;

public class NotModifiedException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotModifiedException() {
        super();
    }

    public NotModifiedException(String message) {
        super(message);
    }

    public NotModifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotModifiedException(Throwable cause) {
        super(cause);
    }

    protected NotModifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
