package com.lab.jpa.gestaoufc.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MM-yyyy'T'HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        Object message,
        String path
) {
}
