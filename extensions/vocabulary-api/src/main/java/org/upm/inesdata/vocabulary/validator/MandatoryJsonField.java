package org.upm.inesdata.vocabulary.validator;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParsingException;
import org.eclipse.edc.validator.jsonobject.JsonLdPath;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;

import java.io.StringReader;
import java.util.Optional;

import static java.lang.String.format;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VALUE;
import static org.eclipse.edc.validator.spi.Violation.violation;

/**
 * Verifies that a @value is present and is a Json.
 */
public class MandatoryJsonField implements Validator<JsonObject> {
    private final JsonLdPath path;


    /**
     * Constructs a MandatoryJsonField with the specified JSON-LD path.
     *
     * @param path the JSON-LD path that represents the mandatory field
     */
    public MandatoryJsonField(JsonLdPath path) {
        this.path = path;
    }

    @Override
    public ValidationResult validate(JsonObject input) {
        return Optional.ofNullable(input.getJsonArray(path.last()))
                .filter(it -> !it.isEmpty())
                .map(it -> it.getJsonObject(0))
                .map(it -> it.getString(VALUE))
                .map(this::validateJson)
                .orElseGet(() -> ValidationResult.failure(violation(format("mandatory value '%s'", path), path.toString())));
    }

    /**
     * Checks whether a string contains a valid json or not
     */
    private ValidationResult validateJson(String value) {
        try {
            try (JsonReader jsonReader = Json.createReader(new StringReader(value))) {
                jsonReader.read();
            }
        } catch (JsonParsingException e) {
            return ValidationResult.failure(violation(format("content '%s' should be a valid Json", path), path.toString()));
        }
        return ValidationResult.success();
    }

}
