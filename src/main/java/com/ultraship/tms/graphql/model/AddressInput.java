package com.ultraship.tms.graphql.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressInput {
        @NotBlank(message = "City is required")
        String city;

        @NotBlank(message = "Postal code is required")
        String postalCode;

        String state;
        String country;
        String street;

        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Invalid contact number")
        String contactNumber;
}
