package io.github.prjkmo112.cloudarchitecturedeploydemo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(HttpStatus status) {
        ApiResponse<T> body = new ApiResponse<>(status.value(), "", null);
        return ResponseEntity.status(status).body(body);
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(HttpStatus status, String message) {
        ApiResponse<T> body = new ApiResponse<>(status.value(), message, null);
        return ResponseEntity.status(status).body(body);
    }

    public static <T>ResponseEntity<ApiResponse<T>> of(HttpStatus status, String message, T data) {
        ApiResponse<T> body = new ApiResponse<>(status.value(), message, data);
        return ResponseEntity.status(status).body(body);
    }

    // ---

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return of(HttpStatus.OK, "", data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return of(HttpStatus.OK, message, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return of(HttpStatus.CREATED, message, data);
    }

    public static ResponseEntity<ApiResponse<Void>> badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST, message, null);
    }

}
