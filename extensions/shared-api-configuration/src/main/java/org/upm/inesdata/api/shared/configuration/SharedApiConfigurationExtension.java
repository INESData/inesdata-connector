package org.upm.inesdata.api.shared.configuration;

import org.eclipse.edc.api.auth.spi.AuthenticationRequestFilter;
import org.eclipse.edc.api.auth.spi.registry.ApiAuthenticationRegistry;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.system.ExecutorInstrumentation;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.jersey.providers.jsonld.JerseyJsonLdInterceptor;
import org.eclipse.edc.web.jersey.providers.jsonld.ObjectMapperProvider;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.eclipse.edc.web.spi.configuration.PortMapping;
import org.eclipse.edc.web.spi.configuration.PortMappingRegistry;

import java.net.URI;

import static java.lang.String.format;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VOCAB;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_PREFIX;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_SCHEMA;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * This extension provides generic endpoints which are open to all connectors.
 */
@Provides(SharedApiUrl.class)
@Extension(value = SharedApiConfigurationExtension.NAME)
public class SharedApiConfigurationExtension implements ServiceExtension {

    public static final String NAME = "Shared Public API";
    private static final int DEFAULT_SHARED_PORT = 8186;
    private static final String SHARED_CONTEXT_PATH = "/api/v1/shared";
    private static final String SHARED_API_CONTEXT = "shared";
    @Setting(value = "Base url of the shared API endpoint without the trailing slash. This should correspond to the values configured " +
            "in '" + DEFAULT_SHARED_PORT + "' and '" + SHARED_CONTEXT_PATH + "'.", defaultValue = "http://<HOST>:" + DEFAULT_SHARED_PORT + SHARED_CONTEXT_PATH)
    private static final String SHARED_ENDPOINT = "edc.shared.endpoint";
    private static final String SHARED_SCOPE = "SHARED_API";
    @Configuration
    private SharedApiConfiguration apiConfiguration;

    @Inject
    private WebServer webServer;
    @Inject
    private ApiAuthenticationRegistry authenticationRegistry;
    @Inject
    private WebService webService;
    @Inject
    private ExecutorInstrumentation executorInstrumentation;
    @Inject
    private Hostname hostname;
    @Inject
    private IdentityService identityService;
    @Inject
    private JsonLd jsonLd;
    @Inject
    private TypeManager typeManager;
    @Inject
    private PortMappingRegistry portMappingRegistry;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var portMapping = new PortMapping(SHARED_API_CONTEXT, apiConfiguration.port(), apiConfiguration.path());
        portMappingRegistry.register(portMapping);

        context.registerService(SharedApiUrl.class, sharedApiUrl(context, portMapping));

        var authenticationFilter = new AuthenticationRequestFilter(authenticationRegistry, "shared-api");
        webService.registerResource("shared", authenticationFilter);

        jsonLd.registerNamespace(ODRL_PREFIX, ODRL_SCHEMA, SHARED_SCOPE);
        jsonLd.registerNamespace(VOCAB, EDC_NAMESPACE, SHARED_SCOPE);
        webService.registerResource("shared", new ObjectMapperProvider(typeManager , JSON_LD));
        webService.registerResource("shared", new JerseyJsonLdInterceptor(jsonLd, typeManager, JSON_LD, SHARED_SCOPE));
    }

    private SharedApiUrl sharedApiUrl(ServiceExtensionContext context, PortMapping config) {
        var callbackAddress = context.getSetting(SHARED_ENDPOINT, format("http://%s:%s%s", hostname.get(), config.port(), config.path()));
        try {
            var url = URI.create(callbackAddress);
            return () -> url;
        } catch (IllegalArgumentException e) {
            context.getMonitor().severe("Error creating shared endpoint url", e);
            throw new EdcException(e);
        }
    }

    @Settings
    record SharedApiConfiguration(
            @Setting(key = "web.http." + SHARED_API_CONTEXT + ".port", description = "Port for " + SHARED_API_CONTEXT + " api context", defaultValue = DEFAULT_SHARED_PORT + "")
            int port,
            @Setting(key = "web.http." + SHARED_API_CONTEXT + ".path", description = "Path for " + SHARED_API_CONTEXT + " api context", defaultValue = SHARED_CONTEXT_PATH)
            String path
    ) {

    }
}
