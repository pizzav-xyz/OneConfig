## Context

Fork surface today: `ForkConfigSurface` (`modules/internal/src/main/kotlin/org/polyfrost/oneconfig/internal/ui/layout/fork/ForkSurface.kt`) renders `MockModules.modules` with local `toggles` map and records synthetic events in `ForkTestHooks`. Real registry: `ConfigRegistry` (`modules/internal/src/main/kotlin/org/polyfrost/oneconfig/internal/ui/api/ConfigRegistry.kt`) loads all `ConfigManager` trees but hides `oneconfig.json` / `themes.json` from mod cards via `hiddenModCardIds`. Real config: `OneConfigConfig` (`modules/internal/src/main/java/org/polyfrost/oneconfig/internal/OneConfigConfig.java`, `super("oneconfig.json", ..., "OneConfig", Category.QOL)`, `INSTANCE.save()` persists). E2E: `ConfigUIClickTest` (`minecraft/src/main/java/org/polyfrost/oneconfig/test/e2e/ConfigUIClickTest.java`, 453 lines, 55 assertions) drives mock hooks via synthetic scene input; `run-e2e.sh` launches `:minecraft:26.2-fabric:runClient`.

## Goals / Non-Goals

Goals: render `oneconfig.json` by name from the real registry in the fork surface; toggle `enableBackgroundBlur` + drag `pageOpacity` through live `Tree` properties; persist via `save()`; prove across restart; cover with clickui.
Non-Goals: restyle, new controls, theme work, second module, HUD/notifications, deleting mocks, Figma parity.

## Decisions

1. **Load by tree id `oneconfig.json`, not by title.** Rationale: id is stable (`Config` constructor arg); title is translated/render text. `ConfigRegistry.findTree("oneconfig.json")` after `ConfigManager` init; fail fast with log if absent.
2. **Fork-scoped registry exemption, not global unhide.** Rationale: upstream intentionally hides preferences from mod cards (`hiddenModCardIds`, `dedicatedScreenConfigIds`); fork surface resolves the tree directly instead of changing `shouldShowModCard` globally.
3. **Live `Property` binding, no shadow state.** Rationale: `MockModules` keeps `toggles` map + `dropdownSelection` maps that diverge from disk; real module reads `tree.getProperty(...).getAs(...)` and writes via `setAs(...)` + `INSTANCE.save()` so the file is the source of truth.
4. **Probe settings: `enableBackgroundBlur` (Switch) + `pageOpacity` (Slider).** Rationale: both exist upstream today, cover boolean + float paths, and `pageOpacity` visibly affects the shell (`ShellState.setPageOpacity` callback already wired), so a wrong binding is observable.
5. **E2E extends clickui, new `clickui-real-module` steps — not a separate harness.** Rationale: `run-e2e.sh` + `ScreenshotHelper` + bounds/event infra already proven (55 assertions green); adding steps keeps one suite and one pass/fail gate.

## Risks / Trade-offs

- `oneconfig.json` hidden from `modCardConfigs` → Mitigation: direct `findTree` bypass, no change to upstream filtering.
- Keybind properties need GLFW codes; E2E only asserts Switch/Slider persistence → Mitigation: keybind rows render read-only in v1.
- Restart test doubles client launch time (~2×300s timeout) → Mitigation: reuse `run-e2e.sh` timeout/early-pass logic; second launch only asserts file value + screenshot.
