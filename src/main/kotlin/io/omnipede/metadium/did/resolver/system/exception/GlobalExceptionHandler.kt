package io.omnipede.metadium.did.resolver.system.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

/**
 * Servlet 내부에서 발생할 수 있는 general 한 exception 들을 처리하고, 클라이언트 단으로
 * 적절한 응답을 전달해주는 클래스
 * @author omnipede
 */
@ControllerAdvice
internal class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.BAD_REQUEST
        val fieldError = e.bindingResult.fieldError
        val message: String = fieldError!!.field + ": " + fieldError.defaultMessage
        return createResponseEntityAndLogError(errorCode, message, e)
    }

    /**
     * Controller 의 path variable 또는 query parameter validation 에러가 발생하는 경우
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.BAD_REQUEST
        return createResponseEntityAndLogError(errorCode, e.message!!, e)
    }

    /**
     * 주로 @RequestParam 이 누락될 경우 발생
     * 필요한 query 파라미터 등이 누락될 경우 발생
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException,
    ): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.BAD_REQUEST
        val message = "Query error: " + e.parameterName + ": " + e.message
        return createResponseEntityAndLogError(errorCode, message, e)
    }

    /**
     * Invalid 한 JSON 을 body 로 전달한 경우
     * {
     * "name": "foo bar",  <-- 마지막에 ',' 로 끝남
     * }
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.BAD_REQUEST
        val message = "Can't read http message ... Please check your request format."
        return createResponseEntityAndLogError(errorCode, message, e)
    }

    /**
     * API end point 가 존재하지 않는 경우
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.NOT_FOUND
        val message = "Maybe you requested to wrong uri"
        return createResponseEntityAndLogError(errorCode, message, e)
    }

    /**
     * 지원하지 않는 http method 요청시 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.NOT_FOUND
        val message = "Maybe you're requesting not supported http method"
        return createResponseEntityAndLogError(errorCode, message, e)
    }

    /**
     * 지원하지 않는 content type 요청시 발생
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.BAD_REQUEST
        val message = e.message
        return createResponseEntityAndLogError(errorCode, message!!, e)
    }

    @ExceptionHandler(SystemException::class)
    fun handleSystemException(e: SystemException): ResponseEntity<RestError?> {
        val errorCode = e.errorCode
        val message = e.message
        return createResponseEntityAndLogError(errorCode, message!!, e)
    }

    /**
     * 그 외 서버 에러 발생 시
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<RestError?> {
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        val message = e.message ?: errorCode.defaultMessage
        return createResponseEntityAndLogError(errorCode, "${e.javaClass.canonicalName}: $message", e)
    }

    /**
     * Http response 객체를 생성하고 에러를 로깅하는 메소드
     * @param errorCode AA 서버에서 정의한 에러 타입
     * @param detailedMessage 상세 에러 메시
     * @param e 에러 객체
     * @return http response
     */
    private fun createResponseEntityAndLogError(
        errorCode: ErrorCode,
        detailedMessage: String,
        e: Throwable
    ): ResponseEntity<RestError?> {
        logError(e, errorCode)
        val restError = RestError(errorCode.status, detailedMessage)
        return ResponseEntity<RestError?>(restError, HttpStatus.valueOf(restError.status))
    }

    /**
     * 에러 로깅 메소드
     * @param e 로깅할 에러
     * @param errorCode 에러 enum
     */
    private fun logError(e: Throwable, errorCode: ErrorCode) {
        when {
            // 서버 에러일경우 => Stack trace 로깅
            errorCode === ErrorCode.INTERNAL_SERVER_ERROR -> logger.error("Stack trace: ", e)
            // 디버그 모드일경우 => Stack trace 로깅
            logger.isDebugEnabled -> logger.error("Stack trace: ", e)
            // 그 외 => 그냥 로깅
            else -> logger.error(e.message)
        }
    }
}
