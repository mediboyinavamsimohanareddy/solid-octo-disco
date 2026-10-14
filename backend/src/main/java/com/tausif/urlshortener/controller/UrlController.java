package com.tausif.urlshortener.controller;

import com.tausif.urlshortener.dto.ShortenRequest;
import com.tausif.urlshortener.dto.UrlResponse;
import com.tausif.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.shorten(request));
    }

    @GetMapping("/api/urls")
    public List<UrlResponse> recent() {
        return service.recent();
    }

    @GetMapping("/api/stats/{code}")
    public UrlResponse stats(@PathVariable String code) {
        return service.stats(code);
    }

    @DeleteMapping("/api/urls/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code:[A-Za-z0-9_-]{3,30}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String target = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(target).toString())
                .build();
    }
}
