## Why

The fork surface is a demo screen: one hardcoded Switch (`enableBackgroundBlur`) and one hardcoded Slider (`pageOpacity`) bound to two fixed `oneconfig.json` paths. A player cannot change how OneConfig looks — theme, accent colour, UI scale — from inside the game. The persistence loop itself is proven (`first-real-module`, `modmenu-demo`); what is missing is the generalization: fork-owned preferences that ride the same registry → file → restart path, so a new preference is a declaration, not a code change.

## What Changes

- New **Appearance** card on the fork surface (Misc scope, next to the OneConfig card) with three live preferences:
  1. **Accent colour** (colour): upstream `ColorOption` picker bound to the `themes.json` `accentColor` prop; applying writes the Tree prop + static, activates a fork-theme copy carrying the accent, and saves `themes.json`.
  2. **UI scale** (scale): `ForkSliderRow` 1–4 over `OneConfigConfig.uiPixelSize` (+ `useCustomUiSize` toggle row); applies on reopen/restart via the existing `chosenEmPx` path, persists to `oneconfig.json`.
  3. **Theme** (dropdown): `ForkDropdown` over `ThemeRegistry` names → `ThemeRegistry.activate()` (persists name, applies immediately).
- Missing-key defaults: accent falls back to the active fork theme accent (`#FF9676`); scale falls back to statics (`useCustomUiSize=false`, `uiPixelSize=2f`); theme falls back to the fork default (`Dark Orange Fork`). Same static-fallback pattern as `RealOneConfigModule`.
- New `modmenu-demo`-style E2E coverage: flip all three live on the real display, save, screenshot, restart in a FRESH client, read back from registry + files + pixels.
- Out of scope: new picker art (reuse upstream `ColorOption`), restyle, audit, HUD, upstream UI removal. No Figma.

## Capabilities

### New Capabilities
- `user-preferences`: Appearance card rendering three registry-backed fork preferences (accent colour, UI scale + enable, theme) with live apply, file persistence, and restart readback.
- `preference-demo`: scripted proof flipping all three on a real display and reading them back from a fresh client restart.

### Modified Capabilities
- None (no existing `openspec/specs/` requirements change; additive surface + additive tests).

## Impact

- Affected code: `modules/internal` fork layout (new `ForkAppearance*` card/module + helpers), `minecraft` E2E (extended demo drive).
- APIs/deps: no new dependencies — reuses `ColorOption`/`ColorOptionData`, `ForkSliderRow`, `ForkDropdown`, `ThemeRegistry`, `ConfigManager`.
- Systems: two `runClient` launches on the real display (set + restart-verify); both existing suites stay green.
