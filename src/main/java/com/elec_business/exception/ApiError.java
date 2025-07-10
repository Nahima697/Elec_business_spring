package com.elec_business.exception;

public record ApiError(
        String message,
        String path,
        int status
) {}

