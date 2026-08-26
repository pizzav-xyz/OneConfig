## Context

OneConfig's UI already runs on the full Jetpack Compose for Desktop stack (`CanvasLayersComposeScene` via `ComposeScreen`) that backs `OneConfigUIScreen` and every reference control in `oneconfig-ui-repomix.xml` (`SwitchControl`, `Sidebar`, `DropdownOption`, etc.). Alongside it, a self-contained mini-runtime `poly-compose` (`PolyNode`/`PolyApplier`/`PolyLayoutEngine`/`RenderContext` → Skia) is used today only by `PolyItemVisuals` for item-icon thumbnails, and the theme layer ships 4 `UITheme` instances in `Impls.kt`. The intended glass-panel look pairs alpha-baked panel colors (e.g. `Color(0xC411171C)`) with `BlurRenderer` sampling the live world FBO. See `proposal.md` for motivation — this doc covers the implementation approach.

A deliberate choice in this change is to keep the plan **additive**: the five capabilities in the proposal are what the Fork builds; anything not exercised by the Figma target is left in-tree and out of scope, not required to be deleted. Concrete scoping (which screens, controls, and variants the Fork actually renders) is the mechanism — not a wholesale teardown. File removals, if the implementer opts to include them, are a trailing cleanup handled in tasks, not a specced contract.

## Goals / Non-Goals

**Goals:**
- Render the Figma's module-card grid (icon sidebar + 2-col card grid + 6 control types) through the existing Compose Desktop + GL blit path.
- Introduce one dark-orange theme and a Figma-derived token table as the Fork's sole rendered theme within its own scope.
- Make the glass-vs-opaque decision a token swap, not a control rewrite.
- Verify AGENTS.md compliance (split files, shared tokens, real launch).

**Non-Goals:**
- Upstream feature parity (HUD, notifications, profiles, search, marketplace, color/file/item controls).
- Deleting poly-compose, HUD, notification, or other subsystems as a requirement — their absence from the Fork's rendered surface is sufficient; removal is optional trailing cleanup.
- Pixel-exact parity before an opaque Figma export is available.
- A generic, multi-theme design system — one dark-orange theme.

## Decisions

### 1. Compose Desktop is the Fork's rendered path; poly-compose is left out of scope

**Decision:** The Fork's UI surface is built exclusively on Compose Desktop (`CanvasLayersComposeScene` via `ComposeScreen`). The `poly-compose` mini-runtime is not part of the Fork's scope and is left in-tree, unrendered, rather than being deleted as a requirement.

**Rationale:** Every file the Figma target needs (`SwitchControl`, `SliderControl`, `DropdownOption`, `Sidebar`, theme structs) already runs on `CanvasLayersComposeScene`. `poly-compose` has its own `PolyLayoutEngine`/`Modifier`/`Node` stack, lacks `Popup`/`AnimatedVisibility`/hover `InteractionSource`, and would need to be taught every Compose interaction primitive just to match what already exists. Its only real consumer today (`PolyItemVisuals` — item icons not in the mock) is not part of the Fork's surface, so pulling a whole extra runtime into the Fork's build for nothing is the opposite of simplicity.

**Alternative considered:** keep poly-compose trimmed as a second runtime. Rejected: two mental models, two build modules, no consumer in the target design.

### 2. GL is the Fork's rendered backend; Vulkan variants are out of scope

**Decision:** The Fork renders through `SkiaCtx` → `SkiaOffscreenTarget` → GL texture → `guiGraphics.blit` as already present. Vulkan `VariantService` paths are simply not exercised by the Fork, not required to be removed — keeping them on disk but absent from the Fork's surface satisfies the requirement.

**Rationale:** The GL blit path is proven, the mock shows no Vulkan-specific rendering, and carrying two rendered backends would violate "one implementation" for the Fork's surface (AGENTS.md rule 1). `BlurRenderer` stays available — it already works on the GL path by sampling the world `RenderTarget`'s FBO if glass is confirmed.

**Alternative considered:** delete every Vulkan variant file as a specced step. Rejected per **C**: scoping them out of the Fork's behavior is the requirement; deletion is an optional follow-up, not a contract.

### 3. Control mapping — Alternatives: repurpose RadioButtonOption into the dot-list

**Decision:** Do not reuse `RadioButtonOption.kt`'s horizontal segmented-pill API for the Figma's vertical dot-list. Instead, base the Fork's list rows on the existing `DropdownOption`/`MultiSelectDropdownOption` stack, restyling only the row visuals (leading checkbox/full-row highlight → trailing accent dot).

**Rationale:** Pills and dot-lists share nothing visually; the list rows in `DropdownOption`/`MultiSelectDropdownOption` already implement the needed popup, selection-index, and count-label logic correctly. A visual-only row restyle is smaller and less risky than forcing a pill component to behave like a list.

### 4. NumberField held back — Alternatives: build it speculatively

**Decision:** Do not introduce `NumberField` (trim of `NumberOption`/`NumberSpinner`) until a plain numeric text-field row is confirmed outside the 8 visible Figma modules. Numeric values in the mock (`20`, `2.6`) ride on full-width slider tracks — structurally `SliderControl`, not a bordered text field with spinner arrows.

**Rationale:** Building an unused control violates AGENTS.md rule 1. Holding it avoids speculative work that may be thrown away.

### 5. Single theme instance within the Fork's scope — Alternatives: theme switcher with dark-orange as default

**Decision:** Introduce one `DarkOrangeTheme` `UITheme` as the Fork's rendered theme; keep the `UITheme`/`UITypography` shape from `Structs.kt`. The other 3 themes in `Impls.kt` are simply not rendered by the Fork — whether they are deleted from disk is a trailing choice, not a specced condition.

**Rationale:** The Figma shows one dark-orange palette, not a theme picker. Rendering a switcher would be dead surface.

## Risks / Trade-offs

- **Checkerboard transparency is unresolved** → Mitigation: keep `BlurRenderer` available and make the decision a single token swap (`color.background` alpha); confirm via an opaque Figma export or source `.fig` file before locking hex values.
- **HiDPI blurriness if Provider pixel-grid logic is over-trimmed** → Mitigation: keep `pixelGridScale`/`surfaceRatio` from `Provider.kt` as rendered by the Fork until verified; verify at GUI scales 1x/2x/3x in-game, not just in desktop preview.
- **Font mismatch (Poppins vs mock's font)** → Mitigation: use Poppins (already bundled); call out kerning/x-height differences as acceptable until the mock's font is confirmed.
- **Clipping + scrolling inside rounded cards** → Mitigation: flag if real module row counts exceed the mock's fixed counts; test `clip(RoundedCornerShape)` + scroll seams in a tall card.
- **Only 8 of unknown total module count visible** → Mitigation: confirm the full module list before treating `NumberField` as needed; that is why it is held.
- **Desktop preview vs in-game divergence** → Mitigation: require `./gradlew runClient` + GL blit check for final sign-off; desktop `Compose Preview` alone is insufficient.

## Migration Plan

- Additive migration within the Fork: introduce `DarkOrangeTheme`, `ModuleCard`/`ModuleGrid`, `KeybindBadge`, and `IconSidebar` alongside existing code; switch the Fork's entry point (`ComposeScreen` surface) to the new `ModuleGrid + Sidebar` composition. Verify `./gradlew build` after the additions, then run `./gradlew runClient` for visual/HiDPI checks. Optional trailing cleanups (removing unused files or modules that the Fork no longer renders) are deferred, isolated commits, and must not be required to satisfy any spec. No data migration or rollback beyond reverting the branch.

## Open Questions

- Glass/no-glass: needs opaque Figma re-export or source file (Risk 1) — blocks final token values, not the approach.
- Full module roster: confirms whether `NumberField` is needed.
- Demo target: in-game (`runClient`) required, or Compose Desktop preview sufficient? Affects whether `SkiaOffscreenTarget` can be simplified.
