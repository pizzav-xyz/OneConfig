# Purpose

Two new behavior contracts for the first real (non-mock) module in the fork surface.

## ADDED Requirements

### Requirement: Real module renders from the non-mock registry

The fork surface SHALL resolve the `OneConfig` preferences module by its stable tree id `oneconfig.json` from `ConfigRegistry` / `ConfigManager` (not from `MockModules` or any test hook) and render its live values.

#### Scenario: Module loads by name

- WHEN the client has initialized `ConfigManager` and the fork surface opens
- THEN `ConfigRegistry.findTree("oneconfig.json")` returns non-null and the surface shows a module card titled `OneConfig` with the live `enableBackgroundBlur` and `pageOpacity` values.

#### Scenario: Control writes through to the live property

- WHEN the user toggles `enableBackgroundBlur` in the fork surface
- THEN the underlying `Tree` property value changes (no shadow `toggles` map) and a `ForkTestHooks` event is NOT required for the write to count.

### Requirement: Setting persists across restart

A change made to the real module in the running client SHALL survive a full client restart via the existing file-backed `Config.save()` path.

#### Scenario: File round-trip

- WHEN `enableBackgroundBlur` is set to `false` and saved, the client is fully restarted, and `oneconfig.json` is reloaded
- THEN the reloaded value is still `false` and the fork surface renders the toggle in the off state.
