package com.github.kotvertolet.youtubeaudioplayer.custom.exceptions;

public class UserFriendlyRuntimeException extends RuntimeException implements UserFriendly {

    private int userErrorMessage;
    private Throwable throwable;

    public UserFriendlyRuntimeException(int userErrorMessage, String message, Throwable throwable) {
        super(message, throwable);
        this.userErrorMessage = userErrorMessage;
        this.throwable = throwable;
    }

    public UserFriendlyRuntimeException(int userErrorMessage, String message) {
        super(message);
        this.userErrorMessage = userErrorMessage;
    }

    @Override
    public int getUserErrorMessage() {
        return userErrorMessage;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
