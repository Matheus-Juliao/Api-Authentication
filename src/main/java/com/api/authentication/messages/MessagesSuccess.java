package com.api.authentication.messages;

import lombok.Getter;

@Getter
public class MessagesSuccess {
    private String message;
    private Integer code;

    public MessagesSuccess(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
