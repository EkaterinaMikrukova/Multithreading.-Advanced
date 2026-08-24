package com.edu.imageconversion.dto;

public record BenchmarkResponse(
        long singleThreadedTimeMs,
        long multiThreadedTimeMs,
        long speedupPercent,
        int filesCount,
        String format
) {
}