package kz.app.appcore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Обрабатываем некорректные запросы (400 Bad Request)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, RuntimeException.class})
    public ResponseEntity<Map> handleBadRequest(Exception e) {
        log.error("GlobalExceptionHandler.handleBadRequest", e);
        e.printStackTrace();

        //
        Map body = Map.of(
                "message", e.getMessage(),
                "code", HttpStatus.BAD_REQUEST
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Обрабатываем некорректные запросы (400 Bad Request)
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map> handleRuntimeException(Exception e) {
        log.error("GlobalExceptionHandler.handleRuntimeException", e);
        e.printStackTrace();

        //
        Map body = Map.of(
                "message", e.getMessage(),
                "code", HttpStatus.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
