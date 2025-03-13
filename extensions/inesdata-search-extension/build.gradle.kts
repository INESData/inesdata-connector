plugins {
    `java-library`
    id("com.gmv.inesdata.edc-application")
}

dependencies {
    api(libs.edc.lib.sql)
    implementation(libs.edc.web.spi)
}


