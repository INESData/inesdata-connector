package org.upm.inesdata.vocabulary.shared.api.service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;
import org.upm.inesdata.vocabulary.service.VocabularyServiceImpl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link VocabularySharedService} interface
 */
public class VocabularySharedServiceImpl extends VocabularyServiceImpl implements VocabularySharedService {

    /**
     * Constructor
     */
    public VocabularySharedServiceImpl(VocabularyIndex index, TransactionContext transactionContext) {
        super(index, transactionContext);
    }

    @Override
    public ServiceResult<List<Vocabulary>> searchVocabulariesByConnector(ConnectorVocabulary connectorVocabulary) {
        return transactionContext.execute(() -> {
            try (var stream = index.searchVocabulariesByConnector(connectorVocabulary.getConnectorId())) {
                return ServiceResult.success(stream.toList());
            }
        });
    }

    @Override
    public ServiceResult<Void> deleteVocabulariesByConnectorId(String connectorId) {
        return transactionContext.execute(() -> {
            index.deleteByConnectorId(connectorId);
            return ServiceResult.success();
        });
    }

    @Override
    public ServiceResult<String> getJsonSchemaByConnectorIdAndVocabularyId(String participantId, String vocabularyId) {
        Vocabulary vocabulary = index.findByIdAndConnectorId(vocabularyId, participantId);

        String jsonSchema = vocabulary.getJsonSchema();
        return ServiceResult.success(jsonSchema);

    }

    private List<String[]> findRequiredFields(JsonObject jsonObject, String parentKey) {
        List<String[]> requiredFields = new ArrayList<>();

        // Verificar si el JsonObject tiene el campo "required "
        if (jsonObject.containsKey("required")) {
            JsonArray requiredArray = jsonObject.getJsonArray("required");
            for (int i = 0; i < requiredArray.size(); i++) {
                String field = requiredArray.getString(i).trim();
                requiredFields.add(new String[]{field, parentKey.trim()});
            }
        }

        // Iterar sobre las claves del JsonObject
        for (String key : jsonObject.keySet()) {
            if (key.equals("properties") || (key.equals("items") && parentKey.equals("trainedOn"))) {
                String contextKey = parentKey; // Mantener el contexto de "trainedOn" al procesar sus "items"
                JsonObject nestedObject = jsonObject.getJsonObject(key);
                requiredFields.addAll(findRequiredFields(nestedObject, contextKey));
            } else if (jsonObject.get(key) instanceof JsonObject) {
                JsonObject nestedObject = jsonObject.getJsonObject(key);
                requiredFields.addAll(findRequiredFields(nestedObject, key));
            } else if (key.equals("items")) {
                JsonObject itemsObject = jsonObject.getJsonObject("items");
                requiredFields.addAll(findRequiredFields(itemsObject, parentKey));
            }
        }

        return requiredFields;
    }

    private List<String> extractRequiredFields(String jsonSchema) {
        List<String> requiredFields = new ArrayList<>();
        try (JsonReader reader = Json.createReader(new StringReader(jsonSchema))) {
            JsonObject schemaObject = reader.readObject();

            // Recursively extract required fields
            extractRequiredFieldsFromObject(schemaObject, requiredFields);
        }

        return requiredFields;
    }

    private void extractRequiredFieldsFromObject(JsonObject jsonObject, List<String> requiredFields) {
        // Normalize JSON keys (trim spaces) and check for "required"
        JsonObject normalizedObject = normalizeJsonKeys(jsonObject);

        if (normalizedObject.containsKey("required")) {
            JsonArray requiredArray = normalizedObject.getJsonArray("required");
            for (int i = 0; i < requiredArray.size(); i++) {
                requiredFields.add(requiredArray.getString(i));
            }
        }

        if (normalizedObject.containsKey("properties")) {
            JsonObject properties = normalizedObject.getJsonObject("properties");
            for (String key : properties.keySet()) {
                JsonObject propertyObject = properties.getJsonObject(key);
                extractRequiredFieldsFromObject(propertyObject, requiredFields);
            }
        }

        if (normalizedObject.containsKey("items")) {
            JsonObject itemsObject = normalizedObject.getJsonObject("items");
            extractRequiredFieldsFromObject(itemsObject, requiredFields);
        }
    }

    private JsonObject normalizeJsonKeys(JsonObject jsonObject) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        jsonObject.forEach((key, value) -> {
            String normalizedKey = key.trim();
            builder.add(normalizedKey, value);
        });

        return builder.build();
    }
}
