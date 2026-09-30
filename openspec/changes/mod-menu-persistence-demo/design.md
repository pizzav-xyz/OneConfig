# mod-menu-persistence-demo — Design

## Decisions

1. **Default, not rewrite.** The Mod Menu factories (`ModMenuEntrypoint`) already construct `OneConfigUIScreen()`; `OneConfigUIScreen.compose()` already branches to `ForkConfigSurface()` when the active theme starts with `"Dark Orange"`. The only missing link is the default: `ThemeConfig.activeTheme = "PolyGlass Dark"` and `ThemeRegistry` init/fallback to `PolyGlassDark`. Changing two defaults routes every entry point to the fork with zero wiring changes and zero new branches.
2. **Persisted choice wins, unchanged mechanics.** `loadFromConfig()` already prefers the persisted name with a default fallback — only the fallback constant changes. Users who switch themes keep working behavior; only fresh installs change surface.
3. **Demo reuses the proven drive.** `ConfigUIRealModuleTest` already implements set → screenshot → restart → verify against the live registry. The demo adds a screen-supplier seam (Mod Menu factory instead of direct constructor) rather than a parallel implementation. E2E registration follows the existing `E2ETestRunner` map + `run-e2e.sh` argument pattern.
4. **No new persistence machinery.** Probe stays `enableBackgroundBlur` in `oneconfig.json`; save/load stays `ConfigManager`/`Tree`. The change proves the path, it doesn't rebuild it.

## Consequences

- Fresh installs (and the E2E run dir after its `themes.json` is cleared) open the fork surface everywhere, including Mod Menu. This is the fork product's intent.
- Upstream UI remains reachable by selecting another theme — nothing is deleted.
- Risk: any test asserting the PolyGlass default surface breaks — mitigated by running both suites green after the change.
