package com.mykino.controller;

import com.mykino.entity.ExploreCategory;
import com.mykino.entity.OttPlatform;
import com.mykino.repository.ExploreCategoryRepository;
import com.mykino.repository.OttPlatformRepository;
import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/tmdb")
@RequiredArgsConstructor
public class TmdbApiController {

    private final TmdbService tmdbService;
    private final ExploreCategoryRepository exploreCategoryRepository;
    private final OttPlatformRepository ottPlatformRepository;

    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> popular(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tmdbService.getPopular(page));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> topRated(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tmdbService.getTopRated(page));
    }

    @GetMapping("/now-playing")
    public ResponseEntity<Map<String, Object>> nowPlaying(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tmdbService.getNowPlaying(page));
    }

    @GetMapping("/discover")
    public ResponseEntity<Map<String, Object>> discover(
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "all") String mediaType,
            @RequestParam(defaultValue = "popularity.desc") String sort,
            @RequestParam(required = false) String provider,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tmdbService.discover(genre, mediaType, sort, provider, page));
    }

    @GetMapping("/genres")
    public ResponseEntity<List<Map<String, Object>>> genres() {
        return ResponseEntity.ok(tmdbService.getTmdbGenres());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> categories() {
        List<ExploreCategory> categories = exploreCategoryRepository.findAllByOrderBySortOrder();
        List<Map<String, Object>> result = categories.stream().map(cat -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("key", cat.getCategoryKey());
            map.put("label", cat.getLabel());
            map.put("mediaType", cat.getMediaType());
            map.put("baseGenre", cat.getBaseGenre());
            List<Map<String, Object>> subGenres = cat.getSubGenres().stream().map(sg -> {
                Map<String, Object> sgMap = new LinkedHashMap<>();
                sgMap.put("id", sg.getTmdbGenreId());
                sgMap.put("name", sg.getGenreName());
                return sgMap;
            }).collect(Collectors.toList());
            map.put("subGenres", subGenres);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ott-providers")
    public ResponseEntity<List<Map<String, Object>>> ottProviders() {
        List<OttPlatform> providers = ottPlatformRepository.findByTmdbProviderIdIsNotNullOrderById();
        Map<Integer, String> tmdbLogos = tmdbService.getWatchProviderLogos();

        List<Map<String, Object>> result = providers.stream().map(p -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getTmdbProviderId());
            map.put("name", p.getName());
            String tmdbLogo = tmdbLogos.get(p.getTmdbProviderId());
            map.put("logoUrl", tmdbLogo != null ? tmdbLogo : p.getLogoUrl());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
