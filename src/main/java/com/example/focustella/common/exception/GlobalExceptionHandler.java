package com.example.focustella.common.exception;

import com.example.focustella.common.api.ApiError;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.common.api.FieldErrorDetail;
import com.example.focustella.common.exception.code.CommonErrorCode;
import com.example.focustella.common.exception.code.ErrorCodeSpec;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        ErrorCodeSpec errorCode = exception.getErrorCode();
        return buildErrorResponse(
                errorCode.getStatus(),
                new ApiError(
                        errorCode.getCode(),
                        exception.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        CommonErrorCode.INVALID_INPUT.getMessage(),
                        request.getRequestURI(),
                        exception.getBindingResult().getFieldErrors().stream()
                                .map(this::toFieldErrorDetail)
                                .toList()
                )
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(
            BindException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        CommonErrorCode.INVALID_INPUT.getMessage(),
                        request.getRequestURI(),
                        exception.getBindingResult().getFieldErrors().stream()
                                .map(this::toFieldErrorDetail)
                                .toList()
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<FieldErrorDetail> details = exception.getConstraintViolations().stream()
                .map(violation -> new FieldErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .toList();

        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        CommonErrorCode.INVALID_INPUT.getMessage(),
                        request.getRequestURI(),
                        details
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(CommonErrorCode.MESSAGE_NOT_READABLE, request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(CommonErrorCode.METHOD_NOT_ALLOWED, request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        CommonErrorCode.INVALID_INPUT.getMessage(),
                        request.getRequestURI(),
                        List.of(new FieldErrorDetail(
                                exception.getParameterName(),
                                null,
                                "필수 요청 파라미터가 누락되었습니다."
                        ))
                )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        CommonErrorCode.INVALID_INPUT.getMessage(),
                        request.getRequestURI(),
                        List.of(new FieldErrorDetail(
                                exception.getName(),
                                exception.getValue(),
                                "요청 파라미터 타입이 올바르지 않습니다."
                        ))
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                CommonErrorCode.INVALID_INPUT.getStatus(),
                new ApiError(
                        CommonErrorCode.INVALID_INPUT.getCode(),
                        exception.getMessage(),
                        request.getRequestURI(),
                        List.of()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return buildErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, ApiError error) {
        return ResponseEntity.status(status).body(ApiResponse.failure(error));
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCodeSpec errorCode, String path) {
        return buildErrorResponse(errorCode.getStatus(), errorCode.toApiError(path));
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
        );
    }
}
