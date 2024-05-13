plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")

    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
}

application {
    mainClass.set("GUI.CityBuildingGameMain")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
