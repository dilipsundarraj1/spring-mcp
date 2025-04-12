package com.llm.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

//@Component
class CustomFilter : Filter {
    companion object {
        private val log: org.slf4j.Logger = LoggerFactory
            .getLogger(CustomFilter::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse?, chain: FilterChain) {
        try {
            val httpRequest = request as HttpServletRequest

            log.info("SSE connection request from: ${request.remoteAddr}, URI: ${httpRequest.requestURI}")

            chain.doFilter(request, response)
        } catch (ex: Exception) {
            log.error("Error in SSE filter: ", ex)
            throw ex;
        }
    } // Optionally override init() and destroy() methods
}
