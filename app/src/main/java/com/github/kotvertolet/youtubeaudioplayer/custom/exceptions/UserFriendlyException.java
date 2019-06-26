package com.github.kotvertolet.youtubeaudioplayer.custom.exceptions;

public class UserFriendlyException extends Exception implements UserFriendly {

    private int userErrorMessage;
    private Throwable throwable;

    public UserFriendlyException(int userErrorMessage, String message, Throwable throwable) {
        super(message, throwable);
        this.userErrorMessage = userErrorMessage;
        this.throwable = throwable;
    }

    public UserFriendlyException(int userErrorMessage, String message) {
        super(message);
        this.userErrorMessage = userErrorMessage;
    }

    @Override
    public int getUserErrorMessage() {
        return userErrorMessage;
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
