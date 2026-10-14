package com.tausif.urlshortener.service;

import com.tausif.urlshortener.dto.ShortenRequest;
import com.tausif.urlshortener.dto.UrlResponse;
import com.tausif.urlshortener.exception.ApiException;
import com.tausif.urlshortener.model.UrlMapping;
import com.tausif.urlshortener.repository.UrlMappingRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,30}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlMappingRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UrlResponse shorten(ShortenRequest request) {
        String url = normalise(request.getUrl());
        validateUrl(url);

        String code;
        String alias = request.getCustomAlias();
        if (alias != null && !alias.isBlank()) {
            alias = alias.trim();
            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Alias must be 3-30 characters: letters, digits, '-' or '_'");
            }
            if (repository.existsByShortCode(alias)) {
                throw new ApiException(HttpStatus.CONFLICT, "Alias already in use");
            }
            code = alias;
        } else {
            code = generateUniqueCode();
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(url);
        mapping.setShortCode(code);
        LocalDateTime now = LocalDateTime.now();
        mapping.setCreatedAt(now);
        if (request.getExpiryDays() != null) {
            mapping.setExpiresAt(now.plusDays(request.getExpiryDays()));
        }
        return toResponse(repository.save(mapping));
    }

    @Transactional
    public String resolve(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Short link not found"));
        if (mapping.isExpired()) {
            throw new ApiException(HttpStatus.GONE, "Short link has expired");
        }
        mapping.setClickCount(mapping.getClickCount() + 1);
        mapping.setLastAccessedAt(LocalDateTime.now());
        repository.save(mapping);
        return mapping.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlResponse stats(String code) {
        return repository.findByShortCode(code).map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Short link not found"));
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> recent() {
        return repository.findTop20ByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Short link not found"));
        repository.delete(mapping);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            if (!repository.existsByShortCode(sb.toString())) {
                return sb.toString();
            }
        }
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate a unique code");
    }

    private String normalise(String raw) {
        String url = raw.trim();
        if (!url.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            url = "https://" + url;
        }
        return url;
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            boolean okScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            if (!okScheme || uri.getHost() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Enter a valid http/https URL");
            }
        } catch (URISyntaxException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Enter a valid http/https URL");
        }
    }

    private UrlResponse toResponse(UrlMapping m) {
        return new UrlResponse(m.getShortCode(), baseUrl + "/" + m.getShortCode(),
                m.getOriginalUrl(), m.getClickCount(), m.getCreatedAt(),
                m.getExpiresAt(), m.getLastAccessedAt());
    }
}
