package com.llm.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


/**
 * The OncePerRequestFilter provides several important advantages over a basic Filter implementation:
 * Single Execution Guarantee:
    *  It ensures the filter is executed exactly once per request, even in cases of forward or include requests within the same request.
    *  Your custom filter might execute multiple times.
 * HTTP-Specific:
    * It automatically casts ServletRequest and ServletResponse to their HTTP variants and ensures only HTTP requests are processed, providing cleaner code through doFilterInternal.
 *
 * Async Support:
    *  Built-in handling of async requests through shouldNotFilterAsyncDispatch(), which your custom filter doesn't handle.
 *
 * Error Dispatch Handling:
    * Proper handling of error dispatches through shouldNotFilterErrorDispatch().
 */
@Component
class HTTPFilter : OncePerRequestFilter() {

    companion object {
        private val log: org.slf4j.Logger = LoggerFactory
            .getLogger(HTTPFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            log.info("SSE connection request from: ${request.remoteAddr}, URI: ${request.requestURI}")
            request.headerNames.asSequence()
                .iterator()
                .forEach { headerName ->
                    log.info("Header: $headerName = ${request.getHeader(headerName)}")
                }
//            response.setHeader("Cache-Control", "no-cache")
//            response.setHeader("X-Accel-Buffering", "no")
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            log.error("Error in SSE filter: ", ex)
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }
}