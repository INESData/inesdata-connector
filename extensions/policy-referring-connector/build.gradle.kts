plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.auth.spi)
    api(libs.edc.policy.engine.spi)
    api(libs.edc.contract.spi)
    api(libs.edc.catalog.spi)
    api(libs.edc.participant.spi)
    testImplementation(libs.edc.core.junit)
}
