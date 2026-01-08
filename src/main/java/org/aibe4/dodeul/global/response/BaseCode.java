package org.aibe4.dodeul.global.response;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();

    String getMessage();
}
