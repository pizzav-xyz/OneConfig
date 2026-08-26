## Purpose

Defines the single dark-orange design-token set and `UITheme` that backs the fork's rendering — the sole source of truth for colors, radii, spacing, and typography.

## ADDED Requirements

### Requirement: Single dark-orange theme instance

The system SHALL expose exactly one `UITheme` instance (dark-orange) as the active theme, replacing the upstream 4-theme set in `themes/Impls.kt`.

#### Scenario: Theme is the only selectable option
- **WHEN** the theme provider is queried for available themes
- **THEN** it returns a single dark-orange theme and no theme-switch UI is shown

#### Scenario: Theme shape is preserved
- **WHEN** the theme is constructed
- **THEN** it conforms to the existing `UITheme`/`UITypography`/`UIBranding` shape from `Structs.kt`/`Provider.kt` (fields not redefined, only values changed)

### Requirement: Token values derived from Figma

The system SHALL define tokens for background, card, border, accent, text, radii, spacing, and typography whose values are sampled or estimated from the Figma mocks, with estimates marked pending opaque re-export.

#### Scenario: Tokens resolve to Figma-estimated values
- **WHEN** a component reads a token (e.g. `color.background`, `radius.card`, `spacing.cardGap`)
- **THEN** the value matches the Token Spec in `design.md` (e.g. `~#0F0F13` for background, `12.dp` for card radius) within the documented tolerance until re-export confirms the hex

### Requirement: Glass vs opaque background is token-level

The system SHALL represent the page background token such that a glass (alpha + blurred world) vs solid (opaque) decision can be made without changing control code.

#### Scenario: Background token supports glass intent
- **WHEN** the transparency-intent decision for the page background is resolved
- **THEN** only the token value (alpha) and the presence of a `BlurRenderer` backdrop change; no control component requires code changes

### Requirement: No hard-coded literals outside theme

The system SHALL NOT contain hard-coded color, radius, or spacing literals outside the theme token layer, except for transient local variables that are immediately assigned from a token.

#### Scenario: Literal audit passes
- **WHEN** the codebase is grepped for raw color/radius/spacing literals outside `theme/`
- **THEN** no such literals are found other than transient locals that delegate to tokens
