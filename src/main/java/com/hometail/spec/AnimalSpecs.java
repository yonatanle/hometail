package com.hometail.spec;

import com.hometail.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing static methods to create JPA Specifications for querying Animal entities.
 * These specifications can be used with Spring Data JPA repositories to build dynamic queries.
 *
 * <p>This class follows the Specification pattern and provides type-safe query construction
 * for filtering animals based on various criteria such as category, breed, gender, size, etc.</p>
 *
 * @see org.springframework.data.jpa.domain.Specification
 * @since 1.0
 */
public final class AnimalSpecs {

    /**
     * Creates a specification to filter animals by category ID.
     *
     * @param categoryId the ID of the category to filter by (can be null, which means no filtering by category)
     * @return a Specification that filters animals by the given category ID, or null if categoryId is null
     */
    public static Specification<Animal> hasCategoryId(Long categoryId) {
        return (root, q, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    /**
     * Creates a specification to filter animals by breed ID.
     *
     * @param breedId the ID of the breed to filter by (can be null, which means no filtering by breed)
     * @return a Specification that filters animals by the given breed ID, or null if breedId is null
     */
    public static Specification<Animal> hasBreedId(Long breedId) {
        return (root, q, cb) ->
                breedId == null ? null : cb.equal(root.get("breed").get("id"), breedId);
    }

    /**
     * Creates a specification to filter animals by gender.
     *
     * @param gender the gender to filter by (can be null, which means no filtering by gender)
     * @return a Specification that filters animals by the given gender, or null if gender is null
     */
    public static Specification<Animal> hasGender(Gender gender) {
        return (root, q, cb) ->
                gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    /**
     * Creates a specification to filter animals by size.
     *
     * @param size the size to filter by (can be null, which means no filtering by size)
     * @return a Specification that filters animals by the given size, or null if size is null
     */
    public static Specification<Animal> hasSize(Size size) {
        return (root, q, cb) ->
                size == null ? null : cb.equal(root.get("size"), size);
    }



    /**
     * Creates a specification to filter animals by adoption status.
     *
     * @param adopted the adoption status to filter by (true for adopted, false for not adopted)
     *               (can be null, which means no filtering by adoption status)
     * @return a Specification that filters animals by adoption status, or null if adopted is null
     */
    public static Specification<Animal> isAdopted(Boolean adopted) {
        return (root, q, cb) ->
                adopted == null ? null : cb.equal(root.get("isAdopted"), adopted);
    }

    /**
     * Creates a specification to filter animals by age group.
     * Converts the age group to a date range based on the current date.
     *
     * @param ageGroup the age group to filter by (can be null, which means no filtering by age group)
     * @return a Specification that filters animals within the specified age group's date range,
     *         or null if ageGroup is null or not recognized
     *
     * @implNote The age groups are defined as follows:
     * <ul>
     *   <li>BABY: 0-6 months old</li>
     *   <li>YOUNG: 6-24 months old</li>
     *   <li>ADULT: 2-7 years old</li>
     *   <li>SENIOR: 7+ years old</li>
     * </ul>
     */
    public static Specification<Animal> inAgeGroup(AgeGroup ageGroup) {
        if (ageGroup == null) return null;

        LocalDate today = LocalDate.now();
        LocalDate min; // inclusive
        LocalDate max; // exclusive upper bound is fine; we'll use <= for inclusive

        switch (ageGroup) {
            case BABY -> { min = today.minusMonths(6);  max = today.plusDays(1); }
            case YOUNG        -> { min = today.minusMonths(24); max = today.minusMonths(6); }
            case ADULT        -> { min = today.minusMonths(84); max = today.minusMonths(24); }
            case SENIOR       -> { min = LocalDate.of(1900,1,1); max = today.minusMonths(84); }
            default           -> { return null; }
        }

        LocalDate minDate = min;
        LocalDate maxDate = max;
        return (root, q, cb) -> cb.between(root.get("birthday"), minDate, maxDate);
    }

    /**
     * Escapes special characters in a string to be used in a SQL LIKE clause.
     * Escapes: %, _, and the escape character (\).
     *
     * @param s the string to escape
     * @return the escaped string, or null if the input is null
     */
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * Creates a specification for a full-text search across multiple animal fields.
     * The search is case-insensitive and token-based, where all tokens must match
     * in any of the searched fields.
     *
     * <p>Searches in the following fields:
     * <ul>
     *   <li>Animal name</li>
     *   <li>Animal short description</li>
     *   <li>Category name</li>
     *   <li>Breed name</li>
     * </ul>
     *
     * @param raw the raw search query string (whitespace-separated tokens)
     * @return a Specification that performs the text search, or null if the input is blank
     */
    public static Specification<Animal> textSearch(String raw) {
        final String q = raw.trim().toLowerCase();
        if (q.isBlank()) return null;

        // Split by whitespace; require all tokens to match somewhere
        final String[] tokens = q.split("\\s+");

        return (root, query, cb) -> {
            // LEFT joins so animals without category/breed still match by other fields
            Join<Animal, Category> cat = root.join("category", JoinType.LEFT);
            Join<Animal, Breed>    br  = root.join("breed", JoinType.LEFT);

            List<Predicate> andPerToken = new ArrayList<>();
            for (String t : tokens) {
                String like = "%" + escapeLike(t) + "%";

                Predicate anyFieldMatchesThisToken = cb.or(
                        cb.like(cb.lower(root.get("name")),              like, '\\'),
                        cb.like(cb.lower(root.get("shortDescription")), like, '\\'),
                        cb.like(cb.lower(cat.get("name")),               like, '\\'),
                        cb.like(cb.lower(br.get("name")),                like, '\\')
                );
                andPerToken.add(anyFieldMatchesThisToken);
            }
            return cb.and(andPerToken.toArray(new Predicate[0]));
        };
    }

}
