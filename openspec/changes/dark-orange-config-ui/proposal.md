## Why

> **ABANDONED 2026-09-30:** pixel-parity against an opaque Figma export that was never provided cannot converge — 68 audit items closed no-change; the surface ships as-is and further work moved to behavior changes.

OneConfig already ships a Compose-based in-game config UI, but the Fork's target is narrower: the Figma's module-card grid (dark-orange palette, glass-panel semantics, sidebar + 2-col cards + a fixed set of row controls). Rather than stripping the codebase down, the change builds the new surface **on top of** the existing stack — new theme, new layout, and a focused control set — and scopes out what the Fork chooses not to exercise.

## What Changes

- Introduce a **single dark-orange `UITheme`** and a Figma-derived design-token table (colors, radii, spacing, typography) with the glass-vs-opaque background left as a single token fork pending re-export.
- Introduce a **2-column `ModuleGrid`** of `ModuleCard` components as the new config surface, replacing navigation-routed per-mod screens within this Fork's scope.
- Introduce an **icon-only `Sidebar` rail** (52px) that reuses the upstream icon rendering and drops label text plus the `Account()` expansion within this Fork's scope.
- Introduce a **focused control set** for card body rows: toggle, checkbox (filled-dot), slider with label+value, dropdown (single), multi-select dropdown, and a keybind badge — each trimmed and restyled to the Figma's row anatomy and trailing-dot selection treatment.
- Clarify **scope exclusions**: subsystems not exercised by the Figma target (HUD, notifications, non-mock control types such as color/file/item, the `poly-compose` mini-runtime used today only for item-icon thumbnails, and unused `VulkanService` variants) are left in-tree but out of the Fork's rendered surface. Concrete removals, if any, are an implementation consequence handled in `design.md`/`tasks.md`, not a specced requirement.
- **Verified by:** Compose Desktop preview screenshot diff against a properly re-exported opaque Figma frame + `runClient` in-game blit check at GUI scales 1x/2x/3x; AGENTS.md compliance via no-monofile split, dedup grep on literals outside `theme/`, and a real launch with no mocked rendering.

## Capabilities

### New Capabilities
- `theme-tokens`: Single dark-orange `UITheme` and design-token table (colors, radii, spacing, typography) derived from the Figma; glass-vs-opaque background is a token-level fork pending re-export.
- `module-card-grid`: 2-col responsive grid of `ModuleCard` components hosting per-module control rows.
- `module-card`: Card component (header + body slot) with keybind-badge + toggle integration.
- `icon-sidebar`: 52px icon-only navigation rail replacing the upstream labeled sidebar.
- `control-set`: Focused control set (toggle, checkbox, slider, dropdown, multi-select dropdown, keybind badge) mapped to the Figma's row anatomy and selection-dot treatment.

### Modified Capabilities
- No existing `openspec/specs/` specs exist in this repo to modify — this is the first spec-driven change, so all entries are net-new.

## Impact

- Affected code: `modules/internal` (new theme, components, layout additions), `minecraft` (kept `SkiaCtx`/`SkiaOffscreenTarget`/`ComposeScreen` render path); other modules left intact.
- APIs/deps: No required Gradle module deletions; no new runtime dependencies expected beyond existing Compose Desktop + Skia/Skiko.
- Systems: Build via `./gradlew build`; verification requires `./gradlew runClient` for in-game GL blit + HiDPI checks. Desktop-only `Compose Preview` is insufficient for final sign-off.

### Non-Goals

- Full upstream OneConfig feature parity, Minecraft version matrix, HUD/notification subsystems, profiles/search/keybinds/marketplace screens — none are in the Figma and all are explicitly out of scope for this change.
- Pixel-exact Figma parity before a proper opaque export is available — hex values in the token spec remain estimates until re-export resolves the checkerboard-transparency question.
- Deleting or restructuring subsystems that the Fork simply doesn't render — anything beyond the five capabilities above is out-of-scope absence, not a requirement to demolish.
