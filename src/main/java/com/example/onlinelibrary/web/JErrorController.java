package com.example.onlinelibrary.web;

import com.example.onlinelibrary.exception.AuthenticationException;
import com.example.onlinelibrary.security.RestError;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Additional layer for catching exceptions.
 */
@RestController
public class JErrorController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<RestError> handleError(final HttpServletRequest request,
                                                 final HttpServletResponse response)
            throws Throwable {
        Object attribute = request.getAttribute("javax.servlet.error.exception");
        if (Objects.nonNull(attribute)) {
            // Not valid token
            throw (AuthenticationException) attribute;
        }
        // Forbidden for this role
        return new ResponseEntity<>(new RestError("FORBIDDEN",
                "You don't have permission to access this resource"), HttpStatus.FORBIDDEN);
    }

    public String getErrorPath() {
        return PATH;
    }
}
