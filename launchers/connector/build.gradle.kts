
plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
    alias(libs.plugins.shadow)
}

dependencies {
    // Librerias base
    implementation(libs.edc.boot)
    implementation(libs.edc.connector.core) 

    // Common libs
    implementation(libs.edc.web.spi)
    implementation(libs.edc.dsp)
    implementation(libs.edc.management.api)
    
    // Temporary libs
    implementation(libs.edc.vault.filesystem)
    implementation(libs.edc.configuration.filesystem)
    
    // Control Plane
    implementation(libs.edc.control.plane.api.client)
    implementation(libs.edc.control.plane.api)
    implementation(libs.edc.control.plane.core)

    // Data Plane
    implementation(libs.edc.data.plane.control.api)
    implementation(libs.edc.data.plane.public.api)
    implementation(libs.edc.data.plane.core)
    implementation(libs.edc.data.plane.http)
    implementation(libs.edc.data.plane.selector.api)
    implementation(libs.edc.data.plane.selector.core)

    // Transferencia
    implementation(libs.edc.transfer.pull.http.receiver)
    implementation(libs.edc.transfer.data.plane)

    // Vocabularios
    implementation(project(":extensions:vocabulary-api"))

    // Policies
    implementation(project(":extensions:policy-always-true"))
    implementation(project(":extensions:policy-time-interval"))

    // Persistencia comun
    implementation(libs.edc.sql.core)
    implementation(libs.edc.sql.edr)
    implementation(libs.edc.sql.lease)
    implementation(libs.edc.sql.pool)
    // Persistencia control plane
    implementation(libs.edc.sql.asset.index)
    implementation(libs.edc.sql.contract.definition.store)
    implementation(libs.edc.sql.contract.negotiation.store)
    implementation(libs.edc.sql.policy.definition.store)
    implementation(libs.edc.sql.transfer.process.store)
    implementation(project(":extensions:vocabulary-index-sql"))
    implementation(project(":extensions:count-elements-sql"))
    // Persistencia data plane
    implementation(libs.edc.sql.data.plane.store)

    // Persistencia de objetos
    implementation(libs.edc.aws.s3.core)
    implementation(libs.edc.data.plane.aws.s3)

    // IAM Identity and authorization
    implementation(libs.edc.iam.oauth2.service)
    implementation(project(":extensions:auth-oauth2-jwt"))

    // Observability
    implementation(libs.edc.observability.api)

    // Federated Catalog
    implementation(project(":extensions:participants-from-configuration"))
    implementation(libs.edc.federated.catalog.spi)
    implementation(libs.edc.federated.catalog.core)
    implementation(libs.edc.federated.catalog.api)
    implementation(project(":extensions:federated-catalog-cache-sql"))
    implementation(project(":extensions:federated-catalog-cache-api"))

    // Storage assets
    implementation(project(":extensions:store-asset-api"))

    // Count elements
    implementation(project(":extensions:count-elements-api"))
    
    runtimeOnly(libs.edc.transaction.local)
    runtimeOnly(libs.postgres)

}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("connector-app.jar")
}
