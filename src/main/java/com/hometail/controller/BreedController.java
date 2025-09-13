package com.hometail.controller;

import com.hometail.dto.BreedDTO;
import com.hometail.service.BreedService;
import com.hometail.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
public class BreedController {

    private final BreedService breedService;           // should return DTOs for public methods
    private final CategoryRepository categoryRepository;

    /**
     * Public: list active breeds for a category.
     * Required: categoryId (so UI can show dependent dropdown).
     * Optional: q (client-side filter by name, case-insensitive)
     */
    @GetMapping
    public List<BreedDTO> list(@RequestParam Long categoryId,
                               @RequestParam(required = false, name = "q") String query) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        List<BreedDTO> list = breedService.listPublic(categoryId); // active-only DTOs

        if (query != null && !query.isBlank()) {
            String q = URLDecoder.decode(query, StandardCharsets.UTF_8).toLowerCase();
            list = list.stream()
                    .filter(b -> b.getName() != null && b.getName().toLowerCase().contains(q))
                    .toList();
        }
        return list;
    }

    /**
     * Public: get one breed by id.
     */
    @GetMapping("/{id}")
    public BreedDTO get(@PathVariable Long id) {
        return breedService.getById(id);
    }
}
