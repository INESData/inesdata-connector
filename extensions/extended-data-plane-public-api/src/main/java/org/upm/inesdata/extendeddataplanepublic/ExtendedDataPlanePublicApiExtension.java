/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.upm.inesdata.extendeddataplanepublic;

import org.eclipse.edc.connector.dataplane.api.controller.DataPlanePublicApiV2Controller;
import org.eclipse.edc.connector.dataplane.spi.Endpoint;
import org.eclipse.edc.connector.dataplane.spi.iam.DataPlaneAuthorizationService;
import org.eclipse.edc.connector.dataplane.spi.iam.PublicEndpointGeneratorService;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.SettingContext;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.system.ExecutorInstrumentation;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.eclipse.edc.web.spi.configuration.PortMapping;
import org.eclipse.edc.web.spi.configuration.PortMappingRegistry;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurer;
import org.eclipse.edc.web.spi.configuration.WebServiceSettings;

import java.util.concurrent.Executors;

/**
 * This extension provides generic endpoints which are open to public participants of the Dataspace to execute
 * requests on the actual data source.
 */
@Extension(value = ExtendedDataPlanePublicApiExtension.NAME)
public class ExtendedDataPlanePublicApiExtension implements ServiceExtension {
    public static final String NAME = "Data Plane Public API";

    private static final int DEFAULT_PUBLIC_PORT = 8185;
    private static final String DEFAULT_PUBLIC_PATH = "/api/v2/public";

    @Setting(value = "Base url of the public API endpoint without the trailing slash. This should correspond to the values configured " +
            "in '" + DEFAULT_PUBLIC_PORT + "' and '" + DEFAULT_PUBLIC_PATH + "'.", defaultValue = "http://<HOST>:" + DEFAULT_PUBLIC_PORT + DEFAULT_PUBLIC_PATH)
    private static final String PUBLIC_ENDPOINT = "edc.dataplane.api.public.baseurl";

    private static final int DEFAULT_THREAD_POOL = 10;

    @Configuration
    private PublicApiConfiguration apiConfiguration;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private WebService webService;

    @Inject
    private ExecutorInstrumentation executorInstrumentation;

    @Inject
    private DataPlaneAuthorizationService authorizationService;

    @Inject
    private PublicEndpointGeneratorService generatorService;

    @Inject
    private Hostname hostname;

    @Inject
    private PortMappingRegistry portMappingRegistry;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var portMapping = new PortMapping(ApiContext.PUBLIC, apiConfiguration.port(), apiConfiguration.path());
        portMappingRegistry.register(portMapping);

        var executorService = executorInstrumentation.instrument(
                Executors.newFixedThreadPool(DEFAULT_THREAD_POOL),
                "Data plane proxy transfers"
        );

        var publicEndpoint = context.getSetting(PUBLIC_ENDPOINT, null);
        if (publicEndpoint == null) {
            publicEndpoint = "http://%s:%d%s".formatted(hostname.get(), apiConfiguration.port(), apiConfiguration.path());
            context.getMonitor().warning("Config property '%s' was not specified, the default '%s' will be used.".formatted(PUBLIC_ENDPOINT, publicEndpoint));
        }
        var endpoint = Endpoint.url(publicEndpoint);
        generatorService.addGeneratorFunction("HttpData", dataAddress -> endpoint);

        var publicApiController = new DataPlanePublicApiV2Controller(pipelineService, executorService, authorizationService);
        webService.registerResource(ApiContext.PUBLIC, publicApiController);
    }

    @Settings
    record PublicApiConfiguration(
            @Setting(key = "web.http." + ApiContext.PUBLIC + ".port", description = "Port for " + ApiContext.PUBLIC + " api context", defaultValue = DEFAULT_PUBLIC_PORT + "")
            int port,
            @Setting(key = "web.http." + ApiContext.PUBLIC + ".path", description = "Path for " + ApiContext.PUBLIC + " api context", defaultValue = DEFAULT_PUBLIC_PATH)
            String path
    ) {
    }
}
