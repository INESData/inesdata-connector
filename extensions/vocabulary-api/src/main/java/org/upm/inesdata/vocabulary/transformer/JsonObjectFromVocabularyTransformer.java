package org.upm.inesdata.vocabulary.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.EDC_VOCABULARY_TYPE;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_CATEGORY;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_CONNECTOR_ID;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_JSON_SCHEMA;
import static org.upm.inesdata.spi.vocabulary.domain.Vocabulary.PROPERTY_NAME;


/**
 * Creates a JsonObject from a {@link Vocabulary} 
 */
public class JsonObjectFromVocabularyTransformer extends AbstractJsonLdTransformer<Vocabulary, JsonObject> {
    private final ObjectMapper mapper;
    private final JsonBuilderFactory jsonFactory;

    /**
     * A transformer class that converts a {@link Vocabulary} object into a {@link JsonObject}.
     * This class utilizes a {@link JsonBuilderFactory} for creating JSON objects and an {@link ObjectMapper}
     * for handling JSON-LD mapping.
     *
     * @param jsonFactory The factory used to create JSON objects.
     * @param jsonLdMapper The mapper used for JSON-LD serialization and deserialization.
     */
    public JsonObjectFromVocabularyTransformer(JsonBuilderFactory jsonFactory, ObjectMapper jsonLdMapper) {
        super(Vocabulary.class, JsonObject.class);
        this.jsonFactory = jsonFactory;
        this.mapper = jsonLdMapper;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull Vocabulary vocabulary, @NotNull TransformerContext context) {
        var builder = jsonFactory.createObjectBuilder()
                .add(ID, vocabulary.getId())
                .add(TYPE, EDC_VOCABULARY_TYPE)
                .add(PROPERTY_NAME, vocabulary.getName())
                .add(PROPERTY_CONNECTOR_ID, vocabulary.getConnectorId())
                .add(PROPERTY_JSON_SCHEMA, vocabulary.getJsonSchema())
                .add(PROPERTY_CATEGORY, vocabulary.getCategory());

        return builder.build();
    }
}
