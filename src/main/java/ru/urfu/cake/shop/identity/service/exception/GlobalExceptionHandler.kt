package ru.urfu.cake.shop.identity.service.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.urfu.cake.shop.identity.service.dto.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse(success = false, message = ex.message))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse(success = false, message = ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Void>> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(separator = "; ") { error -> "${error.field}: ${error.defaultMessage}" }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(success = false, message = message))
    }

    @ExceptionHandler(Exception::class)
    fun handleOtherExceptions(ex: Exception): ResponseEntity<ApiResponse<Void>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(success = false, message = "Произошла внутренняя ошибка"))
    }
}