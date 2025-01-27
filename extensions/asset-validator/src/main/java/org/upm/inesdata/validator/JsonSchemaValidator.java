package org.upm.inesdata.validator;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryValue;

import java.util.List;
import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

public class JsonSchemaValidator {

    public static JsonObjectValidator.Builder fromJsonSchema(JsonObject schema, String vocabulary) {
        var builder = JsonObjectValidator.newValidator();

        var requiredProperties = schema.containsKey("required") && schema.getJsonArray("required") != null
                ? schema.getJsonArray("required")
                .stream()
                .map(item -> ((JsonString) item).getString())
                .toList()
                : List.of();

        builder.verifyObject(vocabulary, vocabularyBuilder -> {
            for (var propertyName : requiredProperties) {
                vocabularyBuilder.verify(getFullyQualifiedPropertyName(schema, (String) propertyName), MandatoryValue::new);
            }

            if (schema.containsKey("properties") && schema.getJsonObject("properties") != null) {
                var properties = schema.getJsonObject("properties");
                for (Map.Entry<String, jakarta.json.JsonValue> entry : properties.entrySet()) {
                    var propertyName = getFullyQualifiedPropertyName(schema, entry.getKey());
                    var propertySchema = entry.getValue().asJsonObject();

                    if (propertySchema.containsKey("type")) {
                        var propertyType = propertySchema.getString("type");

                        if ("object".equals(propertyType)) {
                            var objectRequiredProperties = propertySchema.containsKey("required") && propertySchema.getJsonArray("required") != null
                                    ? propertySchema.getJsonArray("required")
                                    .stream()
                                    .map(item -> ((JsonString) item).getString())
                                    .toList()
                                    : List.of();

                            if (!objectRequiredProperties.isEmpty()) {
                                vocabularyBuilder.verifyObject(propertyName, objectBuilder -> {
                                    for (var objectPropertyName : objectRequiredProperties) {
                                        objectBuilder.verify(getFullyQualifiedPropertyName(schema, (String) objectPropertyName), MandatoryValue::new);
                                    }
                                    return objectBuilder;
                                });
                            }
                        } else if ("array".equals(propertyType)) {
                            if (propertySchema.containsKey("items") && propertySchema.getJsonObject("items") != null) {
                                var itemsSchema = propertySchema.getJsonObject("items");

                                if (itemsSchema.containsKey("type") && "object".equals(itemsSchema.getString("type"))) {
                                    var itemsRequiredProperties = itemsSchema.containsKey("required") && itemsSchema.getJsonArray("required") != null
                                            ? itemsSchema.getJsonArray("required")
                                            .stream()
                                            .map(item -> ((JsonString) item).getString())
                                            .toList()
                                            : List.of();

                                    if (!itemsRequiredProperties.isEmpty()) {
                                        vocabularyBuilder.verifyObject(propertyName, itemBuilder -> {
                                            for (var itemPropertyName : itemsRequiredProperties) {
                                                itemBuilder.verify(getFullyQualifiedPropertyName(schema, (String) itemPropertyName), MandatoryValue::new);
                                            }
                                            return itemBuilder;
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return vocabularyBuilder;
        });

        return builder;
    }

    private static boolean hasNamespace(String key) {
        return key.contains(":") || key.contains("#") || key.contains("/");
    }

    private static String getFullyQualifiedPropertyName(JsonObject schema, String propertyName) {
        if (hasNamespace(propertyName)) {
            String[] parts = propertyName.split(":", 2);
            String prefix = parts[0];
            String localPart = parts[1];
            JsonObject context = schema.getJsonObject("@context");
            if (context != null && context.containsKey(prefix)) {
                return context.getString(prefix) + localPart;
            }
        }
        return EDC_NAMESPACE + propertyName;
    }
}