import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.getByName

plugins {
    id("oneconfig-setup")
}

dependencies {
    "minecraft"("com.mojang:minecraft:${versionedCatalog.versions["minecraft"]}")

    val libsCatalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    // libs.bundles.test-core would pull in log4j-core which collides with the log4j
    // loom already puts on the test classpath
    "testImplementation"(platform(libsCatalog.findLibrary("junit-bom").get()))
    "testImplementation"(libsCatalog.findLibrary("junit").get())
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "testRuntimeOnly"(versionedCatalog["fabric-loader-junit"])
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    workingDir = layout.buildDirectory.dir("test-run").get().asFile
    doFirst { workingDir.mkdirs() }

    maxHeapSize = "2G"
    systemProperty("java.awt.headless", "true")
    systemProperty("mixin.debug.countInjections", "true")
    systemProperty("org.apache.logging.log4j.level", "INFO")

    onlyIf { !project.hasProperty("skipMixinAudit") }

    testLogging {
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

val loom = extensions.getByName<LoomGradleExtensionAPI>("loom")
loom.apply {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        // -Pdevauth=false launches offline for runs that do not need a real account
        property("devauth.enabled", (project.findProperty("devauth") ?: "true").toString())
        property("oneconfig.test", "true")
        // E2E screenshot harness: pass -Pe2eTest=configui or -Doneconfig.e2e.test=configui
        val e2eTest = project.findProperty("e2eTest") ?: project.findProperty("oneconfig.e2e.test") ?: System.getProperty("oneconfig.e2e.test")
        e2eTest?.let { property("oneconfig.e2e.test", it.toString()) }
        val e2eWorld = project.findProperty("e2eTest.world") ?: project.findProperty("oneconfig.e2e.test.world") ?: System.getProperty("oneconfig.e2e.test.world")
        e2eWorld?.let { property("oneconfig.e2e.test.world", it.toString()) }
        if (e2eTest != null) {
            val world = e2eWorld?.toString() ?: "New World"
            programArgs("--quickPlaySingleplayer", world)
        }
    }
}
