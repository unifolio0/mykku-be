package com.example.mykku.docs

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import org.springframework.restdocs.cookies.CookieDescriptor
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestPartDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.restdocs.snippet.Snippet

class RestDocumentationRequest {

    private val resourceBuilder = ResourceSnippetParametersBuilder()
    private val snippets = mutableListOf<Snippet>()

    fun tag(tag: Tag): RestDocumentationRequest {
        resourceBuilder.tag(tag.displayName)
        return this
    }

    fun summary(summary: String): RestDocumentationRequest {
        resourceBuilder.summary(summary)
        return this
    }

    fun description(description: String): RestDocumentationRequest {
        resourceBuilder.description(description)
        return this
    }

    fun pathParameter(vararg descriptors: ParameterDescriptor): RestDocumentationRequest {
        snippets.add(pathParameters(*descriptors))
        return this
    }

    fun queryParameter(vararg descriptors: ParameterDescriptor): RestDocumentationRequest {
        snippets.add(queryParameters(*descriptors))
        return this
    }

    fun requestHeader(vararg descriptors: HeaderDescriptor): RestDocumentationRequest {
        snippets.add(requestHeaders(*descriptors))
        resourceBuilder.requestHeaders(*descriptors)
        return this
    }

    fun requestCookie(vararg descriptors: CookieDescriptor): RestDocumentationRequest {
        snippets.add(requestCookies(*descriptors))
        return this
    }

    fun requestBodyField(vararg descriptors: FieldDescriptor): RestDocumentationRequest {
        snippets.add(requestFields(*descriptors))
        return this
    }

    fun requestPart(vararg descriptors: RequestPartDescriptor): RestDocumentationRequest {
        snippets.add(requestParts(*descriptors))
        return this
    }

    fun requestPartField(partName: String, vararg descriptors: FieldDescriptor): RestDocumentationRequest {
        snippets.add(requestPartFields(partName, *descriptors))
        return this
    }

    fun getResourceBuilder(): ResourceSnippetParametersBuilder = resourceBuilder

    fun getSnippets(): List<Snippet> = snippets.toList()
}
