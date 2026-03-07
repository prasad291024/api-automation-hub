package com.petstore.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class representing a Pet in the Petstore
 * This class is used to map request and response payloads for pet-related operations.
 * It includes fields like ID, name, category, photo URLs, tags, and status.
 *
 * Lombok annotations simplify boilerplate code:
 * - @Data: generates getters, setters, toString, equals, and hashCode
 * - @Builder: enables fluent object creation using builder pattern
 * - @NoArgsConstructor and @AllArgsConstructor: provide default and full constructors
 *
 * Jackson annotations help with JSON serialization/deserialization:
 * - @JsonInclude: skips null fields when converting to JSON
 * - @JsonIgnoreProperties: ignores unknown fields during deserialization
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {

    private Long id;                // Unique identifier for the pet
    private Category category;      // Category object (e.g., Dogs, Cats)
    private String name;            // Name of the pet
    private List<String> photoUrls; // List of photo URLs for the pet
    private List<Tag> tags;         // List of tags associated with the pet
    private String status;          // Status of the pet (available, pending, sold)

}