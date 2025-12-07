plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    war
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.24"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    compileOnly("jakarta.platform:jakarta.jakartaee-web-api:10.0.0")
    compileOnly("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")
    implementation("org.primefaces:primefaces:13.0.3:jakarta") {
        exclude(group = "jakarta.faces") // обязательно, иначе WildFly конфликтует
        exclude(group = "javax.faces")   // обязательно
    }
    implementation("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.3")
    implementation("com.h2database:h2:2.2.224")
}

allOpen {
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.enterprise.context.SessionScoped")
    annotation("jakarta.faces.view.ViewScoped")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("webLab3.war")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kotlin {
    jvmToolchain(17)
}