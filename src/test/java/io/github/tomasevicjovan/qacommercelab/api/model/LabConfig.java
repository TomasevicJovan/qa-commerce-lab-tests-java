package io.github.tomasevicjovan.qacommercelab.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Global lab switches. Only the ones the scenarios change are modelled; the others keep their values. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LabConfig(String paymentMode) {
}
