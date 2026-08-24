package com.edu.imageconversion.controllers;

import com.edu.imageconversion.dto.BenchmarkResponse;
import com.edu.imageconversion.services.ImageBenchmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageBenchmarkController {

    private final ImageBenchmarkService imageBenchmarkService;

    public ImageBenchmarkController(
            ImageBenchmarkService imageBenchmarkService
    ) {
        this.imageBenchmarkService = imageBenchmarkService;
    }

    @Operation(
            summary = "Compare single-threaded and parallel conversion",
            description = "Measures image conversion performance"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benchmark completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping(
            value = "/convert-benchmark",
            consumes = "multipart/form-data",
            produces = "application/json"
    )
    public ResponseEntity<BenchmarkResponse> benchmark(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("format") String format,
            @RequestParam(value = "quality", defaultValue = "0.8")
            float quality
    ) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!format.equalsIgnoreCase("png")
                && !format.equalsIgnoreCase("svg")) {
            return ResponseEntity.badRequest().build();
        }

        if (quality < 0.0f || quality > 1.0f) {
            return ResponseEntity.badRequest().build();
        }

        try {
            BenchmarkResponse response =
                    imageBenchmarkService.benchmark(
                            files,
                            format,
                            quality
                    );

            return ResponseEntity.ok(response);

        } catch (IOException exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}