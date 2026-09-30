# mod-menu-persistence-demo — Tasks

## 1. Fork default theme
- [x] 1.1 Change `ThemeConfig.activeTheme` static default `"PolyGlass Dark"` → `"Dark Orange Fork"` (`modules/internal/.../ThemeConfig.java`).
- [x] 1.2 Change `ThemeRegistry.DEFAULT_THEME_NAME` + init `activeTheme`/`syncNotificationTheme` to `DarkOrangeTheme` (`ThemeRegistry.kt`).
- [x] 1.3 Compile `:modules:internal` + unit tests green (no behavior change beyond the default).

## 2. ModMenu demo path
- [x] 2.1 Add screen-supplier seam to the real-module drive: SET mode obtains `OneConfigUIScreen` via `ModMenuEntrypoint.getModConfigScreenFactory().create(parent)` (production call path, no test-only constructor).
- [x] 2.2 Register `modmenu-demo` (+ verify mode) in `E2ETestRunner`; `./run-e2e.sh modmenu-demo` runs set → screenshot → restart → verify against `oneconfig.json`.
- [x] 2.3 `clickui` + `configui` suites stay green (default change must not regress existing flows).

## 3. Live proof on the real display
- [x] 3.1 `modmenu-demo` SET on `:1`: screenshot shows fork surface opened via Mod Menu factory with flipped probe value.
- [x] 3.2 `modmenu-demo` VERIFY on `:1`: post-restart value reads flipped, `oneconfig.json` on disk matches, screenshot shows persisted state.
- [ ] 3.3 Commit + push + PR with the demo screenshots cited.
