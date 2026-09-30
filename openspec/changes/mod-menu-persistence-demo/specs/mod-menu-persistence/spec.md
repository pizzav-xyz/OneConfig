# mod-menu-default — Spec

## Purpose
Every Mod Menu entry for OneConfig opens the fork (dark-orange) config surface out of the box, while a user-persisted theme choice keeps working.

## ADDED Requirements

### Requirement: Fork surface by default
The product SHALL render `ForkConfigSurface` from a plain `OneConfigUIScreen()` on a fresh install (no `themes.json`), WHEN `ThemeConfig.activeTheme` has its fork default AND `ThemeRegistry` initializes to the fork theme.

#### Scenario: Fresh install opens fork surface
- GIVEN no persisted theme choice
- WHEN the Mod Menu factory creates `OneConfigUIScreen()` (the exact call Mod Menu makes)
- THEN the composed surface is `ForkConfigSurface` (dark-orange), not the upstream interface.

### Requirement: Persisted choice wins
The product SHALL honor a persisted `themes.json` selection over the fork default.

#### Scenario: User switches away and back
- GIVEN `themes.json` persists `activeTheme = "PolyGlass Dark"`
- WHEN the client starts and `loadFromConfig()` runs
- THEN the active theme is PolyGlass Dark (upstream UI), until the user re-selects the fork theme.

### Requirement: No Mod Menu rewrite
The change SHALL NOT modify `ModMenuEntrypoint` or `ModMenuCompat`; the existing factories route to the fork surface via the default alone.

#### Scenario: Factory untouched
- GIVEN the demo opens the screen only through `ModMenuEntrypoint.getModConfigScreenFactory()`
- THEN the fork surface renders with zero changes to Mod Menu wiring files.

# modmenu-demo — Spec

## Purpose
A runnable live demo proving the full round-trip a user performs: open from Mod Menu → see fork surface → change a value → restart → value persists.

## ADDED Requirements

### Requirement: Demo enters through the Mod Menu factory
The demo SHALL obtain its screen exclusively via the Mod Menu entry point, never by constructing the screen directly.

#### Scenario: Same call Mod Menu makes
- GIVEN a running client with Mod Menu loaded
- WHEN the demo requests `ModMenuEntrypoint.getModConfigScreenFactory().create(parent)`
- THEN a non-null `OneConfigUIScreen` is displayed and the real `oneconfig.json` tree resolves in the registry.

### Requirement: Change → save → restart → verify
The demo SHALL flip the `enableBackgroundBlur` probe, persist it, restart the client, and assert the flipped value is live after restart.

#### Scenario: Round-trip
- GIVEN the demo screen showing the real OneConfig module
- WHEN the probe is set to false, saved, screenshotted, and the client restarts
- THEN the post-restart registry value reads false AND `oneconfig.json` on disk contains false AND a screenshot shows the persisted state.

### Requirement: Runnable in one command
The demo SHALL run via `./run-e2e.sh modmenu-demo` on the real display and exit 0 with `[TEST PASS] all` on success.

#### Scenario: Single-command green run
- WHEN `./run-e2e.sh modmenu-demo` executes against the built client
- THEN the process exits 0, the log contains `[TEST PASS] all` with zero `[TEST FAIL]` lines, and fresh `modmenu-demo-*.png` screenshots exist for the set and verify phases.
