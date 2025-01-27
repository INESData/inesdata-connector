plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.api.core)
    api(project(":spi:vocabulary-spi"))
    api(project(":extensions:vocabulary-shared-api"))
    api(project(":extensions:federated-catalog-cache-api"))

    implementation(libs.edc.control.plane.spi)
    implementation(libs.edc.lib.validator)
    implementation(libs.edc.transaction.spi)
}


