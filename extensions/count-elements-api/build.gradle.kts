plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
    id("com.gmv.inesdata.edc-swagger")
}

dependencies {
    api(project(":spi:count-elements-spi"))
    api(libs.edc.spi.core)
    implementation(libs.edc.spi.transform)
    implementation(libs.edc.web.spi)

    implementation(libs.edc.connector.core)
    implementation(libs.edc.api.core)
    implementation(libs.edc.api.lib)
    implementation(libs.edc.lib.util)
    implementation(libs.edc.lib.transform)
    implementation(libs.edc.dsp.api.configuration)
    implementation(libs.edc.api.management.config)
    implementation(libs.swagger.annotations.jakarta)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.lib.validator)
    implementation(libs.edc.validator.spi)
    implementation(libs.swagger.annotations.jakarta)
    runtimeOnly(libs.edc.spi.jsonld)
    runtimeOnly(libs.edc.json.ld.lib)
}

edcBuild {
    swagger {
        apiGroup.set("management-api")
    }
}