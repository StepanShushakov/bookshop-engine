package com.example.mybookshopapp.controllers.exception;

import com.example.mybookshopapp.aop.annotation.ExceptionLogging;
import com.example.mybookshopapp.data.ApiResponse;
import com.example.mybookshopapp.data.ApiTypicalResponse;
import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.errs.*;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author karl
 */

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    @ExceptionLogging
    public String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError",e);
        return "redirect:/";
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    @ExceptionLogging
    public ResponseEntity<ApiTypicalResponse> handleTooLargeFileException(Exception exception) {
        return typicalErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    @ExceptionLogging
    public ResponseEntity<ApiTypicalResponse> handleBadRequestException(Exception exception) {
        return typicalErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ExceptionLogging
    public ResponseEntity<ApiResponse<BookDto>> handleMissingServletRequestParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST,
                "Missing required parameters",
                exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    @ExceptionLogging
    public ResponseEntity<ApiResponse<BookDto>> handleBookstoreApiWrongParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST,
                "Bad parameter value...",
                exception), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotPermissionException.class)
    @ExceptionLogging
    public ResponseEntity<ApiTypicalResponse> handleUserNotPermissionException(Exception exception) {
        return typicalErrorResponse(exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserExistsException.class)
    @ExceptionLogging
    public ResponseEntity<ApiTypicalResponse> handleUserExistsException(Exception exception) {
        return typicalErrorResponse(exception, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(BlacklistTokenException.class)
    @ExceptionLogging
    public ResponseEntity<ApiTypicalResponse> handleBlacklistTokenException(Exception exception) {
        return typicalErrorResponse(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmptyPasswordException.class)
    @ExceptionLogging
    public String handleEmptyPasswordException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("profileError", exception);
        return "redirect:/profile#basic";
    }

    @ExceptionHandler(PasswordReplyException.class)
    @ExceptionLogging
    public String handlePasswordReplyException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("profileError", exception);
        return "redirect:/profile#basic";
    }

    @ExceptionHandler(NotFoundProfileTokenException.class)
    @ExceptionLogging
    public String handleNotFoundProfileTokenException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("profileError", e);
        return "redirect:/profile#basic";
    }

    private ResponseEntity<ApiTypicalResponse> typicalErrorResponse(Exception e, HttpStatus status) {
        return new ResponseEntity<>(new ApiTypicalResponse(false, e.getLocalizedMessage()), status);
    }
}
