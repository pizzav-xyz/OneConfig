## Why

The fork's config UI only ever renders `MockModules` — a shell with no real `Config` behind it. Upstream OneConfig already ships a real, always-present config that persists via `ConfigManager`: `OneConfigConfig` (`modules/internal/src/main/java/org/polyfrost/oneconfig/internal/OneConfigConfig.java` — `super("oneconfig.json", ..., "OneConfig", Category.QOL)` with real persisted fields such as `enableBackgroundBlur`, `pageOpacity`, `sidebarOpacity`). Wiring that one real module into the fork surface end-to-end proves the registry, rendering, and persistence path instead of another restyle pass.

**Chosen module: OneConfig Preferences (`oneconfig.json` / title `OneConfig`).** Justification in one line: upstream ships `OneConfigConfig` as the `"OneConfig"` config backed by `oneconfig.json`, so it is the only module guaranteed present in every install with real persisted Switch/Slider/Dropdown/Keybind fields and no new persistence machinery to invent.

## What Changes

- Fork surface (`ForkConfigSurface`) loads the **real** `Tree` for `oneconfig.json` from `ConfigRegistry` / `ConfigManager` by name (`findTree("oneconfig.json")`), instead of `MockModules.modules`.
- One real setting is the persistence probe: `enableBackgroundBlur` (Switch) plus `pageOpacity` (Slider) rendered through the existing fork controls, reading/writing the live `Tree` property and calling `save()`.
- `ConfigRegistry.hiddenModCardIds` exemption for the fork scope only: `oneconfig.json` is resolvable by the fork surface even though it stays hidden from the upstream mod-card list.
- clickui E2E gains a `clickui-real-module` path that opens `OneConfigUIScreen`, toggles the real setting, screenshots it, restarts the client, and asserts the value survived in `oneconfig.json`.
- `MockModules` and `ForkTestHooks` synthetic-event path remain for other controls but are no longer the only module source; the real module loads without any test hook.

## Capabilities

### New Capabilities
- `real-module`: fork surface renders the real `oneconfig.json` module loaded by name from the non-mock registry, and a setting changed in the running client persists across a full client restart.

### Modified Capabilities
- None (no existing `openspec/specs/` requirements change; this is the first behavior spec).

## Impact

- Affected code: `modules/internal` fork layout (`ForkSurface`, registry exemption), `minecraft` E2E (`ConfigUIClickTest` / new `clickui-real-module` steps, `run-e2e.sh`).
- APIs/deps: no new dependencies; uses existing `ConfigManager`, `Tree`/`Property`, `ConfigRegistry`, Compose `Fork*` controls.
- Systems: verification requires `./gradlew :minecraft:26.2-fabric:runClient` twice (set + restart-verify) plus `clickui` suite green. No Figma, no audit, no restyle.
