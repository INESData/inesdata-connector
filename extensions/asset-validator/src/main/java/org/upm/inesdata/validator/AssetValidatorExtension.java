package org.upm.inesdata.validator;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.upm.inesdata.spi.vocabulary.VocabularyIndex;
import org.upm.inesdata.spi.vocabulary.VocabularySharedService;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.upm.inesdata.vocabulary.shared.api.service.VocabularySharedServiceImpl;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;

/**
 * Service extension for asset validation.
 */
@Provides(AssetValidatorExtension.class)
@Extension(value = AssetValidatorExtension.NAME)
public class AssetValidatorExtension implements ServiceExtension {

    public static final String NAME = "Asset Validator";

    @Inject
    private JsonObjectValidatorRegistry validator;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private VocabularyIndex vocabularyIndex;

    private String participantId;

    /**
     * Provides a default vocabularyService implementation
     */
    @Provider(isDefault = true)
    public VocabularySharedService vocabularySharedService() {
        return new VocabularySharedServiceImpl(vocabularyIndex, transactionContext);
    }


    /**
     * Returns the name of the extension.
     *
     * @return the name of the extension
     */
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        participantId = context.getParticipantId();
    }

    @Override
    public void prepare() {
        validator.register(EDC_ASSET_TYPE, InesdataAssetValidator.instance(vocabularySharedService(), participantId));
    }
}
