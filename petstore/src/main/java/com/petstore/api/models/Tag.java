package com.petstore.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a Tag associated with a Pet.
 *
 * Tags are used to label pets with additional metadata (e.g., "friendly", "playful").
 *
 * Lombok annotations:
 * - @Data: generates getters, setters, equals, hashCode, and toString
 * - @Builder: enables fluent object creation using builder pattern
 * - @NoArgsConstructor and @AllArgsConstructor: provide default and full constructors
 *
 * Jackson annotations:
 * - @JsonInclude: skips null fields during JSON serialization
 * - @JsonIgnoreProperties: ignores unknown fields during JSON deserialization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {


    private Long id;

    // Name or label of the tag (e.g., "friendly", "special")
    private String name;
}

