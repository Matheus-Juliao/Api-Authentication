package com.api.authentication.messages;

import lombok.Getter;

@Getter
public class MessagesSuccess {
    private final String message;
    private final Integer code;

    public MessagesSuccess(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
