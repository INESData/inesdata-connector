plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.lib.sql)
    api(libs.edc.sql.core)
    api(libs.edc.sql.lib)
    implementation(libs.edc.web.spi)
}


