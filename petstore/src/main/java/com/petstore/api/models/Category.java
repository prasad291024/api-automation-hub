package com.petstore.api.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing Pet Category
 * * Lombok annotations are used to reduce boilerplate:
 *  * - @Data: generates getters, setters, toString, equals, and hashCode
 *  * - @Builder: enables fluent object creation using builder pattern
 *  * - @NoArgsConstructor and @AllArgsConstructor: provide default and full constructors
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private Long id; // Unique identifier for the category

    private String name; // Name of the category (e.g., Dogs, Cats)

}