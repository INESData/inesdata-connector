package org.upm.inesdata.spi.vocabulary;

import org.eclipse.edc.spi.result.ServiceResult;
import org.upm.inesdata.spi.vocabulary.domain.ConnectorVocabulary;
import org.upm.inesdata.spi.vocabulary.domain.Vocabulary;

import java.util.HashMap;
import java.util.List;

public interface VocabularySharedService extends VocabularyService {

    /**
     * Get Vocabularies from a connector
     *
     * @param connectorVocabulary connector id
     * @return the collection of vocabularies stored from a connector
     */
    ServiceResult<List<Vocabulary>> searchVocabulariesByConnector(ConnectorVocabulary connectorVocabulary);

    /**
     * Delete vocabularies by connector Id
     *
     * @param connectorId connector id
     */
    ServiceResult<Void> deleteVocabulariesByConnectorId(String connectorId);

    /**
     * Get the mandatory fields from a vocabulary of a connector
     *
     * @param connectorId connector id
     * @param vocabularyId vocabulary id
     * @return the list of mandatory fields
     */
    ServiceResult<String> getJsonSchemaByConnectorIdAndVocabularyId(String connectorId, String vocabularyId);
}
