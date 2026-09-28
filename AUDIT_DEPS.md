# OneConfig Dependency & Library Audit Report

**Auditor:** Agent 2 — Dependency & Library Audit  
**Date:** 2026-08-27  
**Scope:** `/home/pizzav/Documents/oneconfig-fork/` — all modules, buildSrc, minecraft/  
**Project License:** LGPL-3.0-only + Additional Terms Applicable to OneConfig (Polyfrost)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Dependency Manifest Deep-Dive](#2-dependency-manifest-deep-dive)
3. [Hand-Rolled Code → Library Mapping](#3-hand-rolled-code--library-mapping)
4. [Unused & Missing Dependency Check](#4-unused--missing-dependency-check)
5. [License Compatibility Matrix](#5-license-compatibility-matrix)
6. [Consolidated Recommendation Table](#6-consolidated-recommendation-table)

---

## 1. Executive Summary

OneConfig declares **~55 unique external dependencies** across its multi-version Stonecutter matrix (8 MC versions × 2 loaders). The dependency landscape is shaped by three non-negotiable constraints:

1. **Minecraft's bundled libraries** — Gson 2.2.4, Log4j 2.0-beta9, LWJGL 3.x — must be matched at compile-time to avoid runtime classpath collisions on 1.8.9+.
2. **Compose Multiplatform (Skiko/NanoVG)** — the entire UI layer depends on JetBrains Compose Desktop alpha, which pins Kotlin, coroutines, and lifecycle versions.
3. **Fabric/NeoForge loader APIs** — mod-loader-specific dependencies are resolved via Stonecutter version-conditional catalogues.

**Key Findings:**

| Category | Count | Severity |
|----------|-------|----------|
| Outdated but intentionally pinned | 3 | ⚠️ Low |
| Potentially outdated (safe to upgrade) | 5 | 🔶 Medium |
| Hand-rolled code replaceable by library | 4 | 🔶 Medium |
| Deprecated API usage (Gson `JsonParser()`) | 1 | 🔴 High (security) |
| License compatibility concerns | 0 | ✅ Clean |
| Implicit/unlisted dependencies | 2 | ⚠️ Low |
| Version catalog mismatch (buildSrc vs libs) | 1 | 🔶 Medium |

---

## 2. Dependency Manifest Deep-Dive

### 2.1 Global Dependencies (gradle/libs.versions.toml)

| Library | Declared Version | Latest Stable* | Status | Pin Quality | Notes |
|---------|-----------------|----------------|--------|-------------|-------|
| **kotlin** | 2.3.0 | 2.3.20 | ✅ Active | ⚠️ settings.gradle.kts uses 2.3.20 for plugin resolution, libs pins 2.3.0 | **MISMATCH**: `settings.gradle.kts:14` declares `kotlin("jvm") version ("2.3.20")` while `libs.versions.toml:5` declares `kotlin = "2.3.0"`. The plugin version (2.3.20) differs from the stdlib/compiler version (2.3.0). This is technically valid (Gradle allows plugin vs stdlib version skew) but unusual and risks subtle compiler/runtime mismatches. |
| **kotlinx-coroutines** | 1.10.2 | 1.10.2 | ✅ Current | ✅ Good | — |
| **kotlinx-atomicfu** | 0.29.0 | 0.29.0 | ✅ Current | ✅ Good | — |
| **fabric-language-kotlin** | 1.13.8+kotlin.2.3.0 | 1.13.8+kotlin.2.3.0 | ✅ Current | ✅ Good | Tied to Kotlin version |
| **google-ksp** | 2.3.10 | 2.3.10-1.0.31 | ⚠️ Behind | ✅ Good | Latest is 2.3.10-1.0.31; minor patch |
| **annotations (JetBrains)** | 24.1.0 | 26.0.2 | ⚠️ Behind | ✅ Good | Safe to bump; API-stable annotations |
| **snakeyaml** | 1.31 | 2.3 | 🔴 Behind | ⚠️ **Intentionally pinned** | 1.31 has CVE-2022-1471 (RCE via unsafe deserialization). However, OneConfig only uses `DumperOptions` for YAML output formatting — not for parsing untrusted input. Low actual risk but version is very old. 2.x is a major rewrite with different package structure. |
| **night-config** | 3.6.6 | 3.8.4 | ⚠️ Behind | ✅ Good | Active library, safe semver bump. 3.8.x adds Java 21+ support. |
| **java-objc-bridge** | 1.2 | 1.2 | ✅ Current | ✅ Good | macOS-specific; stable |
| **lwjgl** | 3.3.3 | 3.3.4 | ⚠️ Behind | ✅ Good | Safe patch bump. 3.3.4 fixes macOS rendering issues. |
| **mixin-squared** | 0.3.7-beta.1 | 0.3.7-beta.1 | ✅ Current | ⚠️ Beta | Only option; used for Mixin rebasing |
| **kotlinx-abi** | 0.18.1 | 0.18.1 | ✅ Current | ✅ Good | Gradle plugin only |
| **log4j-api** | 2.0-beta9 | 2.24.3 | 🔴 **Intentionally pinned** | ❌ **Pinned for 1.8.9 compat** | See §2.3 below |
| **log4j-impl (core)** | 2.23.1 | 2.24.3 | ⚠️ Behind | ⚠️ **Mixed versioning** | API is 2.0-beta9, core is 2.23.1 — deliberate mismatch to fix CVEs while maintaining binary compat with 1.8.9 |
| **compose** | 1.12.0-alpha01 | 1.14.x-alpha | ⚠️ Alpha | ⚠️ Alpha pin | JetBrains Compose Desktop; alpha channel expected for this use-case |
| **compose-navigation** | 2.10.0-alpha01 | 2.10.x-alpha | ⚠️ Alpha | ⚠️ Alpha pin | Tied to Compose version |
| **skiko** | 0.999.5 | 0.999.5 | ✅ Current | ✅ Good | Internal JetBrains artifact for Compose rendering |
| **lifecycle** | 2.11.0-beta01 | 2.11.x-beta | ⚠️ Beta | ⚠️ Beta pin | JetBrains Compose lifecycle |
| **viewmodel** | 2.11.0-beta01 | 2.11.x-beta | ⚠️ Beta | ⚠️ Beta pin | Tied to lifecycle |
| **commonmark** | 0.24.0 | 0.25.0 | ⚠️ Behind | ✅ Good | Markdown parser. 0.25.0 released; minor improvements. |
| **junit-bom** | 5.10.2 | 5.12.2 | ⚠️ Behind | ✅ Good | Test-only; safe to bump |
| **adventure** | 4.25.0 | 4.25.0 | ✅ Current | ✅ Good | Kyori text library for Minecraft |
| **adventure-platform** | 4.4.1 | 4.4.1 | ✅ Current | ✅ Good | — |
| **kyori-ansi** | 1.1.1 | 1.1.1 | ✅ Current | ✅ Good | — |
| **kyori-examination** | 1.3.0 | 1.3.0 | ✅ Current | ✅ Good | — |
| **kyori-option** | 1.1.0 | 1.1.0 | ✅ Current | ✅ Good | — |
| **devauth** | 1.2.2 | 1.2.2 | ✅ Current | ✅ Good | Dev authentication; runtime-only |

### 2.2 Fabric-Specific Dependencies (gradle/fabric.versions.toml)

| Library | Declared Version | Latest Stable | Status | Notes |
|---------|-----------------|---------------|--------|-------|
| **loom** | 1.17-SNAPSHOT | 1.17-SNAPSHOT | ✅ Current | Fabric build toolchain; SNAPSHOT by design |
| **fabric-loader** | 0.19.3 | 0.19.3 | ✅ Current | — |
| **clothconfig** | 0 (dynamic) | — | ✅ Dynamic | Version resolved at runtime; placeholder |
| **midnightlib** | 0 (dynamic) | — | ✅ Dynamic | Placeholder |
| **walksylib** | 0 (dynamic) | — | ✅ Dynamic | Placeholder |

### 2.3 Log4j Version Split — Critical Security Note

The project intentionally uses **two different Log4j versions simultaneously**:

- `log4j-api` at **2.0-beta9** — pinned because Minecraft 1.8.9 ships this exact version at runtime. Changing it would break binary compatibility.
- `log4j-core` at **2.23.1** — bumped to fix CVE-2021-44228 (Log4Shell) and subsequent CVEs. Only used in tests (`testImplementation`), not shipped.

**Assessment:** This is a well-known Minecraft modding pattern and is correctly handled. The API module is intentionally compatible; the vulnerable core is never bundled. **No action needed** — but any future dependency on a newer Log4j API feature would require careful version analysis.

### 2.4 Gson Version — Intentional 1.8.9 Pin

`Gson 2.2.4` is declared as `compileOnly` in `modules/utils/build.gradle.kts` because Minecraft 1.8.9 bundles Gson 2.2.4 at runtime. The `@Suppress("VulnerableLibrariesLocal")` annotation confirms this is deliberate.

**Note:** Gson 2.2.4 is ancient (2013) and has known issues, but since OneConfig only uses standard `JsonElement`/`JsonParser` APIs and never processes untrusted Gson input via deserialization, the actual risk is minimal.

### 2.5 Build-Only Dependencies (buildSrc)

| Library | Version | Latest | Status |
|---------|---------|--------|--------|
| **ASM** (org.ow2.asm:asm-commons) | 9.8 | 9.8 | ✅ Current |
| **Guava** (com.google.guava) | 33.0.0-jre | 33.4.8-jre | ⚠️ Behind |
| **kotlin-metadata-jvm** | 2.2.10 | 2.3.20 | ⚠️ Behind — matches buildSrc Kotlin version, not project version |
| **stonecutter** | 0.9 | 0.9 | ✅ Current |
| **mod-publish-plugin** | 1.1.0 | 1.1.0 | ✅ Current |

### 2.6 Relocator Module Dependencies

| Library | Version | Latest | Status |
|---------|---------|--------|--------|
| **KSP API** | 2.0.20-1.0.25 | 2.3.10-1.0.31 | 🔴 **Significantly behind** — uses KSP 2.0 API while project uses KSP 2.3 |
| **KotlinPoet** (me.owdding fork) | 1.0.1 | 1.0.1 | ✅ Current | Fork of square/kotlinpoet; see §6.1 |
| **KotlinPoet KSP** | 1.0.1 | 1.0.1 | ✅ Current | — |

### 2.7 Per-Version Catalogue Dependencies

| Library | Version Range | Used In | Notes |
|---------|--------------|---------|-------|
| **resourcefulconfig** | 3.0.11 → 5.0.0 | All common catalogues | Compatability layer with Resourceful Config mod |
| **moulconfig** | 3.11.0 | Fabric 1.21.1–26.2 | Compatibility with MoulConfig-based mods |
| **yacl** (yet-another-config-lib) | 3.7.1 → 3.9.4 | Fabric 1.21.1–26.2 | Compatability with YACL-based mods |
| **modmenu** | 11.0.3 → 20.0.0-beta.4 | Fabric all versions | Fabric Mod Menu integration |
| **hypixel-mod-api** | 1.0.1 → 1.0.2 | All versions | Version-conditional in setup.gradle.kts |
| **command-api-v2** | 2.2.28 → 3.1.0 | Fabric all versions | Fabric command registration API |

---

## 3. Hand-Rolled Code → Library Mapping

### 3.1 HTTP/Networking — `NetworkUtils.java`

**Current Implementation:** `modules/utils/src/main/java/.../NetworkUtils.java`  
**Capability:** URL content fetching (GET), file downloads, browser launching  
**Lines:** ~150 lines using raw `HttpURLConnection`

**Issues Found:**
- Uses deprecated `new URL(url).openConnection()` pattern
- No retry logic, no redirect handling, no connection pooling
- Hard-coded user agent string from Chrome 114 (now outdated)
- No async/await pattern despite project having kotlinx-coroutines
- `setupConnection()` doesn't close the returned InputStream on error paths

**Library Candidates:**

| Library | Latest | License | Maintenance | Migration Effort | Recommendation |
|---------|--------|---------|-------------|-----------------|----------------|
| **OkHttp** (com.squareup.okhttp3) | 4.12.0 | Apache 2.0 | ✅ Active (Square) | Medium — API surface change | ⚠️ Consider — adds ~4MB dependency |
| **java.net.http.HttpClient** (JDK 11+) | Built-in | N/A | ✅ JDK native | Low — drop-in replacement | ✅ **Recommended** — zero dependency, uses `HttpClient` from java.net.http |

**Recommendation:** Replace with JDK 11+ `HttpClient` — it's built-in, supports async, handles redirects, and eliminates the deprecated `HttpURLConnection` usage. This is zero-cost since the project targets Java 21+.

**Migration Effort:** Low (1-2 hours). Simple API mapping.

### 3.2 SHA-256 Hashing — `IOUtils.java`

**Current Implementation:** `modules/utils/.../IOUtils.java:55-79`  
**Capability:** File checksum computation via hand-rolled `MessageDigest` loop

**Assessment:** This is a ~25-line utility using `java.security.MessageDigest`. It's correct, minimal, and only called internally.

**Library Candidates:**

| Library | Latest | License | Recommendation |
|---------|--------|---------|----------------|
| **Guava `Files.hash()`** | 33.x | Apache 2.0 | ❌ Overkill — Guava is only in buildSrc, not runtime |
| **Apache Commons IO `DigestUtils`** | 2.18.0 | Apache 2.0 | ❌ Overkill — adds dependency for 1 method |

**Recommendation:** **Keep as-is.** The hand-rolled implementation is correct, minimal (~25 lines), uses standard JDK APIs, and doesn't warrant a new dependency. The `HexFormat` usage (Java 17+) is already modern.

### 3.3 Thread Pool Management — `Multithreading.java`

**Current Implementation:** `modules/utils/.../Multithreading.java`  
**Capability:** Cached thread pool, scheduled executor, thread naming

**Issues Found:**
- Lazy initialization without synchronization (thread-safety bug — race condition on first call)
- `Executors.newCachedThreadPool()` without bounds (potential thread explosion)
- `availableProcessors() - 2` for scheduled pool can return ≤0 on single-core systems

**Library Candidates:**

| Library | Latest | License | Migration Effort | Recommendation |
|---------|--------|---------|-----------------|----------------|
| **kotlinx-coroutines** (already a dependency) | 1.10.2 | Apache 2.0 | Medium — API rewrite | ✅ **Recommended** — project already has coroutines; use `CoroutineScope` + `Dispatchers.IO` |
| **Guava `MoreExecutors`** | 33.x | Apache 2.0 | Low | ❌ Adds dependency |

**Recommendation:** Since `kotlinx-coroutines` is already a transitive dependency, the `Multithreading` class should be refactored to delegate to coroutine scopes. However, given this is a **public API** class, a deprecation + new coroutine-based API would be the safe path.

**Migration Effort:** Medium (needs API deprecation strategy). Not urgent.

### 3.4 Image Processing & Imgur Upload — `OneImage.java`

**Current Implementation:** `modules/utils/.../OneImage.java`  
**Capability:** BufferedImage wrapping, crop/scale/rotate, color masking, Imgur upload, clipboard copy

**Issues Found:**
- **Imgur upload** (lines 151-190): Hand-rolled HTTP POST with hardcoded Client-ID `6cfc432a9954f4d`. This is a **security concern** — the Client-ID is committed in source. If Imgur revokes it, the feature silently breaks.
- The class is annotated `@ApiStatus.Experimental`, meaning it's not yet stable.
- Uses `BufferedImage` from AWT, which is fine for server-side processing.

**Recommendation:** 
1. **Imgur upload** should be removed or moved to an optional module — it's an external service integration that doesn't belong in a config library.
2. **Image processing** (crop/scale/rotate) is fine as hand-rolled — it's thin wrappers around standard AWT operations.
3. For clipboard operations, `java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()` is the standard approach.

**Migration Effort:** Low for removing Imgur upload. No library replacement needed for the rest.

### 3.5 Profiler — `SimpleProfiler.java`

**Current Implementation:** `modules/utils/.../SimpleProfiler.java`  
**Capability:** Push/pop timing profiler with HashMap storage

**Assessment:** 83 lines, simple, correct. Uses `System.nanoTime()` and a `HashMap<String, Long>`.

**Library Candidates:**

| Library | Latest | License | Recommendation |
|---------|--------|---------|----------------|
| **Micrometer** | 1.15.x | Apache 2.0 | ❌ Massive overkill |
| **JMH** | 1.37 | GPL 2.0 | ❌ Benchmark harness, not a runtime profiler |

**Recommendation:** **Keep as-is.** This is a lightweight debug profiler — exactly the kind of simple utility that should be hand-rolled. No library adds value here.

---

## 4. Unused & Missing Dependency Check

### 4.1 Dependencies Without Corresponding Imports

| Dependency | Declared In | Import Evidence | Assessment |
|-----------|-------------|-----------------|------------|
| **snakeyaml 1.31** | `modules/utils/build.gradle.kts` (via libs) | `NightConfigSerializer.java:45` — `import org.yaml.snakeyaml.DumperOptions` (1 file) | ✅ Used, but minimal — only `DumperOptions` for YAML output formatting |
| **kotlinx-atomicfu** | `libs.versions.toml` bundle | **No direct imports found** in modules/ | ⚠️ **Potentially unused** — atomicfu is a compiler plugin that transforms `atomic { }` inline functions. It may be used via Kotlin `atomic` syntax rather than explicit imports. Verify with `./gradlew buildEnvironment` |
| **lwjgl-opengl** | `libs.versions.toml` bundle | **No imports found** in modules/ | ⚠️ Possibly used indirectly by Skiko/Compose rendering pipeline; keep if Skiko requires it at runtime |
| **lwjgl-vulkan** | `libs.versions.toml` | Conditionally added in `oneconfig-setup.gradle.kts:291` for VulkanMod compat | ✅ Conditional — only when `vulkanmod` exists in version catalogue |
| **adventure-text-logger-slf4j** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive dependency for adventure platform; may be needed at runtime |
| **adventure-text-serializer-ansi** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ May be needed transitively by adventure-platform-fabric |
| **adventure-text-serializer-gson** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive requirement for adventure-platform |
| **kyori-ansi** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive requirement for adventure |
| **kyori-examination-api** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive requirement for adventure |
| **kyori-examination-string** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive requirement for adventure |
| **kyori-option** | `libs.versions.toml` bundle | **No direct imports found** | ⚠️ Transitive requirement for adventure |
| **kotlin-stdlib-jdk7** | `libs.versions.toml` bundle | No explicit imports (merged into stdlib since Kotlin 1.8) | ⚠️ **Deprecated artifact** — Kotlin 1.8+ merged jdk7/jdk8 into stdlib. Safe to remove. |

### 4.2 Imports Without Declared Dependencies

| Import | Found In | Declared? | Assessment |
|--------|----------|-----------|------------|
| `com.google.gson.*` | `JsonUtils.java`, `ThirdPartyModCategories.kt` | ✅ `compileOnly` in `modules/utils/build.gradle.kts` | ✅ Provided by Minecraft at runtime |
| `com.mojang:brigadier` | `modules/commands/build.gradle.kts` | ✅ `compileOnly("com.mojang:brigadier:1.0.18")` | ✅ Minecraft-provided |
| `net.fabricmc.fabric-api` | `oneconfig-setup.gradle.kts` | ✅ Added conditionally for Fabric | ✅ Fabric-provided at runtime |
| `com.google.devtools.ksp:symbol-processing-api` | `modules/relocator/build.gradle.kts` | ⚠️ **Hardcoded version `2.0.20-1.0.25`** — mismatches libs catalog `2.3.10` | 🔴 **Version mismatch** — relocator uses KSP 2.0 API while project compiles with KSP 2.3. This may cause annotation processing issues. |

### 4.3 Implicit Dependencies (provided at runtime but not declared)

| Dependency | Provided By | Risk |
|-----------|-------------|------|
| **Gson 2.2.4** | Minecraft runtime | ⚠️ Low — well-known in MC modding |
| **Brigadier** | Minecraft runtime | ⚠️ Low — Mojang-provided |
| **Fabric API** | Fabric loader | ⚠️ Low — standard Fabric pattern |

---

## 5. License Compatibility Matrix

Project license: **LGPL-3.0-only + Additional Terms Applicable to OneConfig v1.0**

| Library | License | LGPL-3 Compatible? | Copyleft Risk? | Notes |
|---------|---------|---------------------|----------------|-------|
| **Kotlin** (stdlib, reflect) | Apache 2.0 | ✅ Yes | None | — |
| **kotlinx-coroutines** | Apache 2.0 | ✅ Yes | None | — |
| **kotlinx-atomicfu** | Apache 2.0 | ✅ Yes | None | — |
| **fabric-language-kotlin** | Apache 2.0 | ✅ Yes | None | — |
| **Compose Multiplatform** | Apache 2.0 | ✅ Yes | None | — |
| **Skiko** | Apache 2.0 | ✅ Yes | None | — |
| **Night-Config** | Apache 2.0 | ✅ Yes | None | — |
| **SnakeYAML** | Apache 2.0 | ✅ Yes | None | — |
| **CommonMark** | BSD 2-Clause | ✅ Yes | None | Permissive |
| **LWJGL** | BSD 3-Clause | ✅ Yes | None | Permissive |
| **Adventure API** | MIT | ✅ Yes | None | Permissive |
| **Adventure Platform** | MIT | ✅ Yes | None | Permissive |
| **Kyori (ansi, examination, option)** | MIT | ✅ Yes | None | Permissive |
| **JetBrains Annotations** | Apache 2.0 | ✅ Yes | None | — |
| **Gson** | Apache 2.0 | ✅ Yes | None | compileOnly only |
| **Brigadier** | MPL-2.0 | ✅ Yes | Weak copyleft — file-level | compileOnly only; not distributed |
| **Mixin-Squared** | MIT | ✅ Yes | None | — |
| **Guava** | Apache 2.0 | ✅ Yes | None | buildSrc only |
| **ASM** | BSD 3-Clause | ✅ Yes | None | buildSrc only |
| **JUnit 5** | EPL-2.0 | ✅ Yes | Weak copyleft — file-level | Test-only |
| **Log4j 2** | Apache 2.0 | ✅ Yes | None | API: compileOnly; Core: test-only |
| **Stonecutter** | MIT | ✅ Yes | None | Build tool |
| **mod-publish-plugin** | MIT | ✅ Yes | None | Build tool |
| **KotlinPoet** (me.owdding fork) | Apache 2.0 | ✅ Yes | None | — |
| **KSP** | Apache 2.0 | ✅ Yes | None | — |
| **Hypixel Mod API** | Apache 2.0 | ✅ Yes | None | — |
| **ResourcefulConfig** | MIT | ✅ Yes | None | Compatibility dep |
| **YACL** | CC0-1.0 / MIT | ✅ Yes | None | Compatibility dep |
| **ModMenu** | MIT | ✅ Yes | None | Compatibility dep |
| **MoulConfig** | MIT | ✅ Yes | None | Compatibility dep |
| **DevAuth** | MIT | ✅ Yes | None | Runtime-only dev tool |

**License Assessment: ✅ CLEAN** — All dependencies use permissive licenses (Apache 2.0, MIT, BSD, EPL-2.0). No GPL/AGPL or strong copyleft concerns. The project's own LGPL-3.0 is compatible with all declared dependencies.

**Note:** `kotlin-stdlib-jdk7` and `kotlin-stdlib-jdk8` are deprecated artifacts with no independent license — they're simply part of the Kotlin distribution and can be safely removed.

---

## 6. Consolidated Recommendation Table

### Priority 1: Security / Correctness

| # | Item | Current | Recommended | Effort | Impact |
|---|------|---------|-------------|--------|--------|
| 1a | **SnakeYAML CVE-2022-1471** | 1.31 | Keep if only using `DumperOptions`; audit all YAML code paths | Low | Security — but actual risk is low since only `DumperOptions` is used, not `Yaml()` constructor |
| 1b | **Deprecated `new JsonParser()`** in `JsonUtils.java:48` | `@Deprecated` field using pre-2.8 API | Remove deprecated field; use `JsonParser.parseString()` (static since Gson 2.8.6) | Low | Correctness — old API accepts non-JSON-compliant input |
| 1c | **Hardcoded Imgur Client-ID** in `OneImage.java:161` | `"Client-ID 6cfc432a9954f4d"` committed in source | Remove or make configurable; this is a public API key | Low | Security — API key exposure |
| 1d | **KSP version mismatch** in relocator | KSP 2.0.20-1.0.25 | Align with project KSP 2.3.10-1.0.31 | Medium | Correctness — potential annotation processing issues |

### Priority 2: Version Maintenance

| # | Item | Current | Recommended | Effort | Impact |
|---|------|---------|-------------|--------|--------|
| 2a | **Kotlin stdlib/plugins mismatch** | libs: 2.3.0, settings: 2.3.20 | Align to 2.3.20 everywhere | Low | Consistency — prevents subtle compiler mismatches |
| 2b | **Night-Config** | 3.6.6 | 3.8.4 | Low | Better Java 21+ support, bug fixes |
| 2c | **LWJGL** | 3.3.3 | 3.3.4 | Low | macOS rendering fixes |
| 2d | **JUnit 5** | 5.10.2 | 5.12.2 | Low | Test-only; improved assertions |
| 2e | **JetBrains Annotations** | 24.1.0 | 26.0.2 | Low | New annotations (`@UnmodifiableView` already used) |
| 2f | **Google KSP** | 2.3.10 | 2.3.10-1.0.31 | Low | Minor patch |
| 2g | **Guava** (buildSrc) | 33.0.0-jre | 33.4.8-jre | Low | Build-only |
| 2h | **CommonMark** | 0.24.0 | 0.25.0 | Low | Minor improvements |

### Priority 3: Code Quality / Library Replacement

| # | Item | Current | Recommended | Effort | Impact |
|---|------|---------|-------------|--------|--------|
| 3a | **`NetworkUtils` HTTP client** | Raw `HttpURLConnection` | JDK 11+ `HttpClient` (zero new dependency) | Low | Better redirect handling, async support, modern API |
| 3b | **`Multithreading` thread safety** | Unsynchronized lazy init | Use `@Synchronized` or `AtomicReference` (or migrate to coroutines) | Low | Fixes race condition |
| 3c | **Remove `kotlin-stdlib-jdk7`/`jdk8`** | In libs bundle | Remove from bundle — merged into stdlib since Kotlin 1.8 | Low | Cleanup — reduces JiJ jar size |
| 3d | **`OneImage` Imgur upload** | Hand-rolled HTTP POST | Remove from core library or move to optional module | Low | Reduces surface area, removes hardcoded API key |

### Priority 4: Not Recommended to Change

| # | Item | Reason to Keep |
|---|------|----------------|
| 4a | **SnakeYAML 1.31** | Only `DumperOptions` used — not the vulnerable `Yaml()` constructor. Upgrading to 2.x breaks API and adds unnecessary risk |
| 4b | **Log4j API 2.0-beta9** | Mandatory for Minecraft 1.8.9 binary compatibility |
| 4c | **Gson 2.2.4** | Mandatory compile-only target for 1.8.9 runtime |
| 4d | **SimpleProfiler** | Correct, minimal, no library needed |
| 4e | **IOUtils SHA-256** | Correct, minimal, uses JDK-native `MessageDigest` |
| 4f | **Compose/Skiko alpha versions** | JetBrains Compose Desktop only publishes alpha for JVM — this is normal |
| 4g | **`kotlin-reflect`** | Used by annotation-based config/command factories via reflection |

---

## Appendix A: Dependency Flow Diagram (Simplified)

```
buildSrc (plugins)
  ├── ASM 9.8 (bytecode transforms)
  ├── Guava 33.0 (Essential toolkit compat)
  ├── Stonecutter 0.9 (multi-version)
  ├── Loom / NeoForge ModDev (loader toolchains)
  └── mod-publish-plugin 1.1.0 (Modrinth)

modules/utils (foundation)
  ├── Gson 2.2.4 (compileOnly, MC-provided)
  ├── Brigadier 1.0.18 (compileOnly, MC-provided)
  ├── JetBrains Annotations 24.1
  ├── Hypixel Mod API 1.0.1
  └── java-objc-bridge 1.2 (macOS only)

modules/config → modules/utils
modules/events → modules/utils
modules/commands → modules/utils, Brigadier
modules/notifications → modules/utils, poly-compose, events
modules/poly-compose → compose-runtime (compileOnly), skiko (compileOnly), coroutines (compileOnly)
modules/ui → compose (api), events, notifications, lwjgl (compileOnly)
modules/config-impl → config, night-config (api), poly-compose, ui, compose-runtime
modules/hud → config-impl, ui, poly-compose, events
modules/internal → hud, events, commands, notifications, compose (api), navigation, lifecycle, viewmodel
modules/dependencies → (compat libraries per-version)
modules/relocator → KSP API 2.0.20, KotlinPoet 1.0.1

minecraft/{version}-{loader}
  → MC runtime, Fabric API, mod compatibility libs
  → All of the above

bootstrap/{version}-fabric
  → JiJ of platform + all modules
  → compose-bundle (excluded — standalone mod)
```

---

## Appendix B: All External Import Groups Found in Source

| Import Prefix | Modules Using | Count |
|--------------|---------------|-------|
| `com.google.gson.*` | utils, internal | 2 files |
| `com.electronwill.nightconfig.*` | config-impl | 2 files |
| `org.yaml.snakeyaml.*` | config-impl | 1 file |
| `org.commonmark.*` | internal | 1 file |
| `net.kyori.adventure.*` | utils, events, internal | 7 files |
| `org.jetbrains.annotations.*` | utils, ui, config, events, internal | 22 files |
| `org.jetbrains.compose.*` | (via module API, not direct imports) | 0 direct |
| `kotlinx.coroutines.*` | poly-compose, internal | 11 files |
| `kotlinx.serialization.*` | internal | 7 files |
| `org.lwjgl.*` | ui (1 file) | 1 file |
| `java.security.MessageDigest` | utils | 1 file |
| `java.net.HttpURLConnection` | utils | 2 files |
| `org.apache.logging.log4j.*` | utils, config-impl | 5 files |

---

*End of audit report.*
