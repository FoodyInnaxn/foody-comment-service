package com.foody.commentservice.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CommentNotFoundException extends ResponseStatusException {
    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND, "INVALID_COMMENT_ID");
    }
}
