package com.amazon.mbp.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Response<T> {
    LocalDateTime localDateTime;
    int code;
    String message;
    T data;

    public static int SUCCESS = 200;
    public static int FAIL = -1;


    @Builder
    public Response(int code, String message, T data) {
        this.localDateTime = LocalDateTime.now();
        this.code = code;
        this.message = message;
        this.data = data;

    }

    public static<T> Response<?> success(T data) {

        return Response.builder().code(SUCCESS).message("ok").data(data).build();
    }

    public static<T> Response<?> success(String message) {

        return Response.builder().code(SUCCESS).message(message).data("").build();
    }

}
