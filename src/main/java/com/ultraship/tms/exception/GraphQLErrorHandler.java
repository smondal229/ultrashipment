package com.ultraship.tms.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLErrorHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ShipmentNotFoundException e) {
            return buildError(e.getMessage(), ErrorType.NOT_FOUND);
        }

        if (ex instanceof InvalidShipmentStateException e) {
            return buildError(e.getMessage(), ErrorType.BAD_REQUEST);
        }

        if (ex instanceof BindException e) {
            return buildValidationError(e);
        }

        if (ex instanceof ConstraintViolationException e) {
            return buildConstraintViolationError(e);
        }

        if (ex instanceof AccessDeniedException) {
            return buildError("You are not authorized to perform this action", ErrorType.FORBIDDEN);
        }

        if (ex instanceof DataIntegrityViolationException e) {
            return handleDataIntegrityViolation(e);
        }

        if (ex instanceof UsernameAlreadyPresentException) {
            return buildError(ex.getMessage(), ErrorType.FORBIDDEN);
        }

        if (ex instanceof UnauthorizedException) {
            return buildError(ex.getMessage(), ErrorType.UNAUTHORIZED);
        }

        return buildError(
                ex.getMessage(),
                ErrorType.INTERNAL_ERROR
        );
    }

    private GraphQLError buildError(String message, ErrorType errorType) {
        return GraphqlErrorBuilder.newError()
                .message(message)
                .extensions(Map.of("code", "UNAUTHENTICATED"))
                .errorType(errorType)
                .build();
    }

    private GraphQLError buildValidationError(BindException ex) {
        Map<String, String> validationErrors =
                ex.getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage,
                                (existing, replacement) -> existing
                        ));

        return GraphqlErrorBuilder.newError()
                .message("Validation failed")
                .errorType(ErrorType.BAD_REQUEST)
                .extensions(Map.of(
                        "validationErrors", validationErrors
                ))
                .build();
    }

    private GraphQLError handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        String constraint = extractConstraintName(ex);

        if ("uq_tracking_carrier".equalsIgnoreCase(constraint)) {

            return GraphqlErrorBuilder.newError()
                    .message("Shipment with this tracking number already exists for the carrier")
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(Map.of(
                            "code", "DUPLICATE_TRACKING_NUMBER"
                    ))
                    .build();
        }

        return GraphqlErrorBuilder.newError()
                .message("Database constraint violation")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }

    private String extractConstraintName(Throwable ex) {

        Throwable cause = ex;

        while (cause != null) {

            if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
                return cve.getConstraintName();
            }

            cause = cause.getCause();
        }

        return null;
    }

    private GraphQLError buildConstraintViolationError(
            jakarta.validation.ConstraintViolationException ex) {

        Map<String, String> validationErrors =
                ex.getConstraintViolations()
                        .stream()
                        .collect(Collectors.toMap(
                                violation -> extractFieldName(violation.getPropertyPath().toString()),
                                ConstraintViolation::getMessage,
                                (existing, replacement) -> existing
                        ));

        return GraphqlErrorBuilder.newError()
                .message("Validation failed")
                .errorType(ErrorType.BAD_REQUEST)
                .extensions(Map.of(
                        "validationErrors", validationErrors
                ))
                .build();
    }

    private String extractFieldName(String propertyPath) {

        if (propertyPath == null) return null;

        String[] parts = propertyPath.split("\\.");

        // Find "input" and return everything after it
        for (int i = 0; i < parts.length; i++) {
            if ("input".equals(parts[i]) && i + 1 < parts.length) {
                return String.join(".",
                        java.util.Arrays.copyOfRange(parts, i + 1, parts.length));
            }
        }

        // Fallback: remove only the first segment (method name)
        if (parts.length > 1) {
            return String.join(".",
                    java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }

        return propertyPath;
    }

    GraphQLError buildAccessDeniedError() {
        return GraphqlErrorBuilder.newError()
                        .message("You are not authorized to perform this action")
                        .errorType(ErrorType.FORBIDDEN)
                        .build();
    }
}
