# user-configurable-preferences — Design

## End state (exact)

After the demo SET phase, on disk:

`minecraft/run/config/themes.json`
```json
{
  "activeTheme": "Dark Orange Fork",
  "accentColor": { "value": [255, 150, 118, 255], "class": "org.polyfrost.compose.render.PolyColor" },
  "animations": true
}
```

`minecraft/run/config/oneconfig.json` (relevant keys)
```json
{ "useCustomUiSize": true, "uiPixelSize": 3.0, ... }
```

Missing-key defaults (key absent from file): accent → active fork theme accent (`#FF9676`, never the unused blue static); scale → statics (`useCustomUiSize=false`, `uiPixelSize=2f`); theme → fork default (`Dark Orange Fork`). Registry-miss fallback mirrors `RealOneConfigModule` (static read, UI stays up).

## Decisions

1. **Reuse the upstream picker, don't hand-roll.** `ColorOption(ColorOptionData(prop))` takes the live `themes.json` `accentColor` Tree property and ships a full popup picker. Libraries over hand-rolled code; the fork only provides the row slot and the apply step.
2. **Accent apply = theme copy + statics + save.** On picker commit: set Tree prop and `ThemeConfig.accentColor`, activate `activeTheme.copy(accent = picked)` via `ThemeRegistry.activate` (persists the name), call `updateAccent()`, save `themes.json`. Restart readback: fork surface init resolves `themes.json`, and if the persisted accent differs from the stock theme accent, re-applies the copy before first composition — all in fork-owned code (`ForkAppearance`), upstream load semantics untouched.
3. **Scale honest about liveness.** `chosenEmPx` reads statics outside composition observation, so a moved slider takes effect on reopen/restart, not mid-frame. The slider writes live + saves; the demo asserts persisted value and fresh-client readback, and screenshots the slider position — never claims mid-frame rescale.
4. **Theme dropdown = registry names → activate().** Immediate apply + persisted name; no new machinery.
5. **Demo extends the proven drive.** Same set → screenshot → restart → verify shape as `modmenu-demo` (which stays green untouched); new probe values asserted from registry props, file JSON, and pixels. `clickui`/`configui` stay green.

## Consequences

- A third preference later = a Tree path + a row + a fallback line — no new plumbing.
- `ThemeConfig.accentColor` static becomes genuinely live (it is currently write-only); nothing else reads it, so blast radius is the fork surface.
- Upstream Themes screen keeps working; selecting another theme there still wins until the user re-picks.
