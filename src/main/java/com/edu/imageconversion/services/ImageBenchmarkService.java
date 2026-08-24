package com.edu.imageconversion.services;

import com.edu.imageconversion.dto.BenchmarkResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageBenchmarkService {

    private final ImageProcessingService imageProcessingService;
    private final ZipService zipService;

    public ImageBenchmarkService(
            ImageProcessingService imageProcessingService,
            ZipService zipService
    ) {
        this.imageProcessingService = imageProcessingService;
        this.zipService = zipService;
    }

    public BenchmarkResponse benchmark(
            MultipartFile[] files,
            String format,
            float quality
    ) throws IOException {

        long singleThreadedStart = System.nanoTime();

        byte[][] singleThreadedResult =
                imageProcessingService.convertImages(
                        files,
                        format,
                        quality
                );

        zipService.zipFiles(singleThreadedResult, format);

        long singleThreadedTime =
                elapsedMilliseconds(singleThreadedStart);

        long multiThreadedStart = System.nanoTime();

        byte[][] multiThreadedResult =
                imageProcessingService.convertImagesParallel(
                        files,
                        format,
                        quality
                );

        zipService.zipFiles(multiThreadedResult, format);

        long multiThreadedTime =
                elapsedMilliseconds(multiThreadedStart);

        long speedupPercent = calculateSpeedup(
                singleThreadedTime,
                multiThreadedTime
        );

        return new BenchmarkResponse(
                singleThreadedTime,
                multiThreadedTime,
                speedupPercent,
                files.length,
                format
        );
    }

    private long elapsedMilliseconds(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    private long calculateSpeedup(
            long singleThreadedTime,
            long multiThreadedTime
    ) {
        if (singleThreadedTime == 0) {
            return 0;
        }

        return Math.round(
                ((double) singleThreadedTime - multiThreadedTime)
                        / singleThreadedTime
                        * 100
        );
    }
}