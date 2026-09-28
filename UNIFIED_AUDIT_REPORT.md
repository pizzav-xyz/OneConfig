# Unified Audit Report — OneConfig Fork
**Project:** `/home/pizzav/Documents/oneconfig-fork` | **Date:** 2026-08-28 Europe/Brussels | **Method:** 5 parallel subagents (4 active + 1 static visual report)
**Source reports:** `AUDIT_DEPS.md` (Agent 2), Agent 4 completeness/security (inline), `VISUAL_AUDIT_REPORT.md` (Agent 5 static), partial Agent1/Agent3 (resumed)

---

## 1. Summary Table — Agent | Findings | Critical

| Agent | Scope | Findings | Critical | Status |
|-------|-------|----------|----------|--------|
| 1 Code Simplifier | Fallback chains, complexity, monoliths | ~7 chains flagged (partial) — NetworkUtils HTTP retries, MHUtils Unsafe fallback, catalog cascades, McUiSound silent swallow | 1 (silent catch-all) | ⏳ resumed, partial |
| 2 Dependency & Library | Manifests, hand-rolled vs library, licenses, CVEs | 13 findings (5 actionable) | 2 (hardcoded Imgur ID, deprecated Gson API) | ✅ completed |
| 3 TODO & Dead Work | TODO/FIXME, dead code, commented blocks | ~10 TODOs + silent catches (partial, timed out) | 1 (ForkSurface println stubs) | ⏳ resumed, partial |
| 4 Completeness & Security | Module stubs, hardcoded values, security, fallbacks | 10 action items, 8 fallback chains audited | 3 (empty Bootstrap, empty Mixin_TabOpenedEvent, hardcoded Imgur key) | ✅ completed |
| 5 Visual — PNG micro-audit | 11 PNGs at 1x → 32 crops at 4x/8x | 1 major, 9 minor, 16 micro, 4 info | 1 (Figma orange ↔ code blue palette mismatch) | ✅ static report |

---

## 2. Deduplicated Findings — Merged & Most Detailed Kept

| # | Category | Finding (merged) | Evidence | Severity | Agents |
|---|----------|------------------|----------|----------|--------|
| **F1** | Security — Hardcoded credential | Imgur Client-ID `6cfc432a9954f4d` in `OneImage.java:160` authorization header | `modules/utils/.../OneImage.java:160` | **CRITICAL** | 2,4 |
| **F2** | Security — Silent swallowing | `McUiSoundService.kt:38,100,107,186,220` — 5× `catch (_: Throwable) {}` with zero logging; `UpdateChannel.java:19`, `ModMenuApi.java:19`, `ModMenuApiCompat.java:57-106` (5 blocks) | `McUiSoundService.kt:33-39`, `UpdateChannel.java:19` | **HIGH** | 4 |
| **F3** | Completeness — Empty production code | `OneConfigBootstrap.java` empty class body; `Bootstrap.java:30-32` unused imports + empty `init()`; `Mixin_TabOpenedEvent.java` entirely commented out | `bootstrap/.../OneConfigBootstrap.java:3-4`, `minecraft/.../Bootstrap.java:30-32`, `Mixin_TabOpenedEvent.java:3` | **HIGH** | 4 |
| **F4** | Completeness — Stubbed fork wiring | `ForkSurface.kt:47-52` keybind/toggle callbacks only `println()` — not wired to ConfigRegistry; `MockModules.kt:26-30` explicit mock in prod code | `ForkSurface.kt:47-52`, `MockModules.kt:26-30` | **MEDIUM** | 4,3 |
| **F5** | Visual — Palette mismatch (major) | Figma warm orange `#FF8A65/#FF7A50` vs E2E code cool blue `#2D5AFF/#3A5BFF` — accent token diverges across entire UI; slider fill, toggle track, chip selected, dropdown active all affected | `figma-gui-complete.png` vs `screenshot-orange:full`; `Theme.kt:accent` | **MAJOR** | 5 |
| **F6** | Visual — 1px seams & AA stair | Card footer dark seam `#1E2430` 1px; toggle knob 1px left offset + 4-step stair at 8x; card corner 2px stair → NanoVG integer quantization; `header.png` raster blockiness | `.audit-crops/*toggle-closeup*`, `*mod-card*`, `*header*`; `Visual report P1-1, P1-3` | **MINOR** | 5 |
| **F7** | Dependency — Version mismatches | Kotlin `2.3.20` (settings) vs `2.3.0` (libs.versions.toml); KSP `2.0.20` in relocator vs `2.3.10` project compile | `settings.gradle.kts`, `gradle/libs.versions.toml`, `modules/relocator` | **MEDIUM** | 2 |
| **F8** | Dependency — Hardcoded / outdated values | User-Agent `Chrome/114` (2023) in `NetworkUtils.java:52`; network timeout `5000` hardcoded ×3; Imgur key (F1); polyfrost data URL `data-v2.polyfrost.org/...` not configurable; `mc-heads.net` avatar URL; `both()` in `settings.gradle.kts:50-52` says both but only adds Fabric | `NetworkUtils.java:52,90,122,136`, `ThirdPartyModCategories.kt:14`, `PlayerHeadLoader.kt:88`, `settings.gradle.kts:50-52` | **MEDIUM** | 4,2 |
| **F9** | Dependency — Vulnerable / deprecated libs | Gson `2.2.4` (`@Suppress VulnerableLibrariesLocal`), log4j-api `2.0-beta9` (mitigated by impl `2.23.1`), SnakeYAML `1.31` CVE-2022-1471 (only DumperOptions used), `kotlin-stdlib-jdk7/jdk8` deprecated since 1.8, Night-Config `3.6.6→3.8.4`, LWJGL `3.3.3→3.3.4` | `gradle/libs.versions.toml:28`, `modules/utils/build.gradle.kts:29` | **LOW-MEDIUM** | 2,4 |
| **F10** | Code Simplifier — Fallback chains | 8 fallback chains inventoried (see §4); worst is McUiSound silent catch-all + MHUtils degradation + NetworkUtils hand-rolled HTTP retry | `root.kt:21-25,37,47-50`, `LoaderPlatformImpl.java:41-50`, `MHUtils.kt:107-114`, `McUiSoundService.kt:33-39`, `PlayerHeadLoader.kt:86-92` | **MEDIUM** | 1,4 |
| **F11** | TODOs — Scattered tech debt | `ConfigDSL.kt:95` keybind migration, `ColorOption.kt:178` design accuracy, `FileBackendTest.java:37` zero-test class, `LogScanner.java:161` mixin transformers, `ForkSurface.kt` TODO persist, `config-impl` empty test | `ConfigDSL.kt:95`, `ColorOption.kt:178`, `FileBackendTest.java:37` | **LOW** | 3,4 |
| **F12** | Visual — Low-res / tight assets | `header.png` 325×111 blocky 1px halo, `polyfrost.png` 149×25 tight crop no padding, `hue.png` 8-bit banding, `alpha.png` low contrast ΔL7% clipped, `oneconfig-icon.png` double halo + 3-step hexagon stair | `VISUAL_AUDIT_REPORT.md P1-4…P3` | **MINOR** | 5 |
| **F13** | Build — Missing catalog & sparse | No `gradle/neoforge/26.2.versions.toml` but `26.2` listed as `both()`; common catalogs sparse (only `rconfig`); Stella GitHub-raw workaround comment | `gradle/neoforge`, `gradle/common/1.21.1`, `gradle/fabric/26.2:18-19` | **LOW** | 2,4 |
| **F14** | Threading — Lazy init race | `Multithreading.java` lazy init not synchronized; `McUiSoundService.kt:178,223` magic sleeps `500L/25L` not constants | `Multithreading.java`, `McUiSoundService.kt:178,223` | **MEDIUM** | 2 |

---

## 3. Priority Ranking — Security > Runtime Breaks > Code Quality > Style

1. **P0 SECURITY** — F1 hardcoded Imgur key — rotate key, move to properties/env, purge git history if ever pushed.
2. **P0 VISUAL/PRODUCT** — F5 palette mismatch — decide single `--accent-primary` (`peach 500 #FF7A45` vs `blue 600 #2D5AFF`) and propagate to Figma + `DarkOrangeTheme.kt` + toggles/sliders/chips. Blocks ship (1 major).
3. **P1 RUNTIME** — F2 silent McUiSound catches — add `LOGGER.debug`/`warn` to 5 blocks; users currently get zero feedback on sound failure.
4. **P1 COMPLETENESS** — F3 empty Bootstrap/Mixin_TabOpenedEvent — confirm intentionally empty via Stonecutter or implement/remove; empty prod class is ship-blocker for loader path.
5. **P1 CODE** — F4 ForkSurface println stubs — wire to `ConfigRegistry` instead of `println`; mock in prod code must be behind `if (configRegistry == null)` or removed.
6. **P1 CODE** — F6 1px seam/toggle stair — apply `.audit-crops` fixes: `footer margin-top:-1px` + `Theme` wrapper already done, but toggle `inset 2px` + `antialias true` still needed per visual audit P1-2.
7. **P2 DEPS** — F7 Kotlin/KSP version alignment — single source of truth (`2.3.20`).
8. **P2 DEPS** — F8 User-Agent + timeout + `both()` naming — make UA configurable + timeout constant + rename `both()` to `fabricOnly()` or fix to actually add both loaders.
9. **P2 VISUAL** — F6/F12 remaining minors — header SVG, icon MSAA, hue dither, rail padding — all 9 minors from visual report.
10. **P3 POLISH** — F9 lib upgrades (Night-Config, LWJGL, remove jdk7/8), F11 TODOs, F13 catalog gaps, F14 threading.

---

## 4. Fallback Chain Inventory — Every Multi-Path Found

| Location | Current Paths (N) | Recommended Single Clean Path | Lines Saved |
|----------|-------------------|-------------------------------|-------------|
| `buildSrc/root.kt:21-25` ForwardingVersionCatalog `first()` cascade | 4 (catalog[0..3] → throw) | Keep — this IS the clean path (intentional version cascade). No change; document as design pattern. | 0 |
| `buildSrc/root.kt:47-50` `getOrFallback(name)` | 2 (primary → fallback name) | Keep — alias fallback is intentional catalog indirection. No change. | 0 |
| `buildSrc/root.kt:37` `has()` try→boolean | 2 (try get → false) | Keep — idiomatic `runCatching` existence check. No change. | 0 |
| `minecraft/.../LoaderPlatformImpl.java:41-50` `addToClasspath()` | 3 (addToClassPath → propose → throw) | Keep — Fabric API version branching; clean is already 2-fallback + loud fail. No change. | 0 |
| `modules/utils/.../MHUtils.kt:107-114` trustedLookup | 2 (Unsafe IMPL_LOOKUP → MethodHandles.lookup degraded) | **FIX:** Remove silent degradation — fail loud if trusted lookup required: `error("IMPL_LOOKUP unavailable on this JDK; require --add-opens")` instead of returning degraded lookup that fails later. | -3 |
| `minecraft/.../PlayerHeadLoader.kt:86-92` `loadFromWeb()` `runCatching → null` | 2 (network → null) | Keep but add `LOGGER.debug("head fetch failed {}", e)` — currently silent. | +1 |
| `minecraft/.../McUiSoundService.kt:33-39` `play()` catch-all | 2 (playModern/minecraft → silent {}) | **FIX:** Replace `catch (_: Throwable) {}` with `catch (e: Throwable) { LOGGER.debug("ui sound failed", e) }` on all 5 blocks. Single path: log + swallow (cosmetic). | +5 |
| `minecraft/.../UpdateChannel.java:8-22` + `ModMenuApi.java:19` reflection | 2 (reflect → RELEASE default) | Keep but add `LOGGER.trace("ModMenu shim fallback", ignored)` — not silent. | +1 |
| `modules/utils/.../NetworkUtils.java:90-136` hand-rolled `HttpURLConnection` | 3 (setupConnection → readBytes → runCatching) | **REPLACE:** Single `java.net.http.HttpClient` (JDK 11+) with `HttpRequest` + `BodyHandlers.ofByteArray()` + `Duration.ofSeconds(5)` timeout — removes UA spoof + manual buffer copy. | -40 |
| `minecraft/.../ScreenshotHelper.java:60-68` getRenderTarget reflection | 2 (getMainRenderTarget → gameRenderer.mainRenderTarget) | **ALREADY FIXED** to single clean path via field check + keep fallback only for version matrix. Current 2-path is minimal needed for 1.21.1→26.2 matrix; keep but document why 2 paths exist. | 0 |
| `minecraft/.../ConfigUITest.java:19-46` nested Threads + execute | 3 nested Threads + sleeps `2000/500/1000` | **SIMPLIFY:** Single `E2ETestRunner.delayTicks` helper already exists — remove hand-rolled `new Thread → sleep → execute` nesting that was added as workaround; fix root cause (tick suspension) once instead of polling file on background thread. | -20 |

**#1 thing to find:** McUiSound silent catch-all + NetworkUtils hand-rolled HTTP are the two fallback chains where a single clean path (logged swallow; HttpClient) replaces the chain. All other cascades are intentional version/config indirection and should be kept but documented.

---

## 5. Action Plan — Ordered Fix List with Finding Cross-Refs

1. **F1 + F2** — Purge `OneImage.java:160` key; add `LOGGER.debug` to 5 `McUiSoundService` catches (Agents 2,4).
2. **F5** — Align accent token Figma↔code (Agent 5 P0). Single decision, touches ~4 files.
3. **F3 + F4** — Fill or delete empty `OneConfigBootstrap`, `Mixin_TabOpenedEvent`; wire `ForkSurface` println stubs to registry (Agent 4 items 2,5).
4. **F6 (P1-2)** — Toggle/rail/seam fixes from `.audit-crops` crops: `track 36×18 inset 2px`, `rail 56px pad 8 gap 28`, `footer margin-top:-1px` (Agent 5 P1-2, P1-3, P2-7).
5. **F7** — Align Kotlin `2.3.20` + KSP `2.3.10` single versions (Agent 2).
6. **F8 + F9** — UA/timeout constants + Night-Config/LWJGL bumps + remove `jdk7/8` (Agents 2,4).
7. **F10 MHUtils + NetworkUtils** — Fail-loud trustedLookup; replace `HttpURLConnection` with `HttpClient` (Agent 1/4).
8. **F12** — Vectorize `header.png`, pad `polyfrost.png`, dither `hue.png`, fix `alpha` contrast (Agent 5 P1-4…P3).
9. **F11 + F13 + F14** — Address remaining TODOs, catalog gaps, Multithreading sync (Agents 2,3,4).

---

## 6. Pass / Fail — Does This Codebase Ship?

**NO with blockers — but close.**

- **Blockers (must fix before ship):** F1 (credential in source), F3 (empty loader class), F5 (major palette divergence breaks design contract), F4 (fork println stubs in prod path that E2E currently exercises).
- **Soft blockers (fix before 1.0, not before internal dogfood):** F2 silent catches, F6 seam/stair, F7 version skew.
- **Non-blockers (backlog):** F8-F14, F11 TODOs, F12 asset polish, banding/low-contrast micro-issues.

**Caveated YES:** If F1/F3/F4/F5 are addressed and E2E `[TEST PASS] all` with dark-orange fork screenshot is archived as proof, the fork surface (8-module grid + icon rail) is internally consistent, Java-window pinned screenshots work, and the remaining 9 minor + 16 micro findings are NanoVG quantization / 1px AA artifacts typical of integer-scaled Minecraft GUI — shippable with the P1 polish backlog filed.

---
*Generated 2026-08-28 Europe/Brussels — sources: `AUDIT_DEPS.md` (Agent 2, 420 lines), Agent 4 inline report (~600 lines), `VISUAL_AUDIT_REPORT.md` (187 lines, 32 crops, 35 files, 1.7 MB), partial Agent1/Agent3 (resumed, pending), E2E `configui-test_20260827_204055.png` (230 KB, 949×1028, `[TEST PASS] all`).*
