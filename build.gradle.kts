plugins {
    java
    application
}

group = "info.kunalsheth"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.google.protobuf", "protobuf-java", "3.8.0")
    implementation("org.fusesource.jansi", "jansi", "1.18")
    implementation("com.sparkjava", "spark-core", "2.8.0")
    implementation("org.slf4j", "slf4j-nop", "1.7.21")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "info.kunalsheth.grpcvisualizer.GrpcVisualizer"
}