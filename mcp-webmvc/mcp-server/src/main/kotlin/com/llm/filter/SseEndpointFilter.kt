package com.llm.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class SseEndpointFilter : OncePerRequestFilter() {

    companion object {
        private val log: org.slf4j.Logger = LoggerFactory
            .getLogger(SseEndpointFilter::class.java)
    }

//    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
//        return !request.requestURI.endsWith("/sse")
//    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            log.info("SSE connection request from: ${request.remoteAddr}, URI: ${request.requestURI}")
//            response.setHeader("Cache-Control", "no-cache")
//            response.setHeader("X-Accel-Buffering", "no")
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            log.error("Error in SSE filter: ", ex)
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }
}