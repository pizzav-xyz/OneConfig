## Why

The fork ships a dark-orange config surface and a real persistence path (`oneconfig.json` via the live registry, proven by the `first-real-module` change), but the in-game front door does not lead to it: `ThemeRegistry` and `ThemeConfig` default to `PolyGlass Dark`, so the Mod Menu entry (`ModMenuEntrypoint` → `OneConfigUIScreen()`) renders the upstream UI, not the fork surface. A user opening OneConfig from Mod Menu never sees the fork, and no end-to-end path proves Mod Menu → change → restart → persisted.

## What Changes

- Fork product default theme becomes `Dark Orange Fork`: `ThemeRegistry` initial `activeTheme` + `DEFAULT_THEME_NAME`, and `ThemeConfig.activeTheme` static default, so every entry point (Mod Menu, command, fresh install with no `themes.json`) renders the fork surface. A persisted `themes.json` choice still wins via `loadFromConfig()`.
- No Mod Menu code changes: `ModMenuEntrypoint` factories already construct `OneConfigUIScreen()`; with the fork default they open the fork surface automatically. Verified, not rewritten.
- New `modmenu-demo` E2E path: obtains the screen through the exact factory call Mod Menu uses (`getModConfigScreenFactory().create(parent)`), displays it, flips the real `enableBackgroundBlur` probe, saves, screenshots, restarts the client, and asserts the value survived in `oneconfig.json` — the live round-trip demo, runnable with `./run-e2e.sh modmenu-demo`.
- Out of scope: restyle, audit, new controls, upstream UI removal. No Figma.

## Capabilities

### New Capabilities
- `mod-menu-default`: fork surface is what Mod Menu opens, by product default, while a persisted user theme choice keeps working.
- `modmenu-demo`: scripted live demo proving Mod Menu entry → visible fork surface → changed value → restart → persisted value, with screenshots.

### Modified Capabilities
- None (no existing `openspec/specs/` requirements change; defaults change, behavior contracts don't).

## Impact

- Affected code: `modules/internal` theme defaults (`ThemeRegistry`, `ThemeConfig`), `minecraft` E2E (new demo path reusing the real-module drive).
- APIs/deps: no new dependencies; uses existing `ModMenuEntrypoint`, `ConfigManager`, `Tree save/load`.
- Systems: verification is two `runClient` launches on the real display (set + restart-verify) plus the existing suites staying green.
