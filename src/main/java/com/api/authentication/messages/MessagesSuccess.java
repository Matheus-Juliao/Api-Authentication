package com.api.authentication.messages;

import lombok.Getter;

@Getter
public record MessagesSuccess(String message, Integer code) {
}
