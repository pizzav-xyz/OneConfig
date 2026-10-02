# user-preferences — Spec

## Purpose
A player changes theme, accent colour, and config UI scale from inside the game; the choices persist across restarts.

## ADDED Requirements

### Requirement: Accent colour preference
The Appearance card SHALL offer the accent colour through the upstream picker bound to the live `themes.json` `accentColor` property; committing a pick SHALL re-theme the surface immediately and persist to `themes.json`.

#### Scenario: Pick, see, restart
- WHEN the player picks `#FF9676`-family coral (or any colour) in the picker
- THEN the fork controls re-tint live, `themes.json` `accentColor.value` equals the picked RGBA, and after a full client restart the surface renders with the picked accent.

#### Scenario: Absent key
- WHEN `themes.json` has no `accentColor` key
- THEN the surface uses the active fork theme accent (`#FF9676`), and the picker opens on that colour.

### Requirement: UI scale preference
The Appearance card SHALL offer `UI pixel size` (slider 1–4) plus its `Use custom UI size` enable toggle, bound to the live `oneconfig.json` props; moving the slider SHALL enable custom sizing, save, and take effect at latest on reopen/restart.

#### Scenario: Scale round-trip
- WHEN the player sets the slider to 3.0
- THEN `oneconfig.json` reads `{"useCustomUiSize": true, "uiPixelSize": 3.0}`, and a FRESH client loads `uiPixelSize == 3.0` with custom sizing enabled.

#### Scenario: Absent keys
- WHEN `oneconfig.json` lacks the scale keys
- THEN the slider shows 2.0 with the enable toggle off (statics), and the surface renders at default density.

### Requirement: Theme preference
The Appearance card SHALL list `ThemeRegistry` theme names in a dropdown; selecting one SHALL activate it immediately (visible re-theme) and persist the name to `themes.json`, surviving restart.

#### Scenario: Switch and survive
- WHEN the player selects `Dark Orange Fork` (or any listed theme)
- THEN the surface re-themes at once and a restarted client loads that theme name.

# preference-demo — Spec

## Purpose
Scripted live proof of all three preferences on the real display with fresh-client readback.

## ADDED Requirements

### Requirement: Flip all three live, read back after restart
The demo SHALL set accent to coral, scale to 3.0 (enabled), and theme to Dark Orange Fork in the running client, save, screenshot, restart into a FRESH client, and assert registry values + file JSON + pixels.

#### Scenario: Full round-trip
- WHEN `./run-e2e.sh preference-demo` SET runs
- THEN all three values are live in the registry, both JSON files match the end state in `design.md`, and screenshots show the tinted surface with the scale slider at 3.0.
- WHEN `./run-e2e.sh preference-demo-verify` runs in a fresh client
- THEN the registry reads accent coral + scale 3.0 enabled + theme Dark Orange Fork, the files still match, and the screenshot shows the persisted state; the suite restores prior defaults afterwards so no dirty state leaks.

#### Scenario: Single-command green run
- WHEN `./run-e2e.sh preference-demo` executes against the built client
- THEN the process exits 0 with `[TEST PASS] all` and zero `[TEST FAIL]` lines.
