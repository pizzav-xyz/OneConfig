## Purpose

Closes the gap between the dark-orange config surface (verified in
`configui-test_20260909_160249.png`, E2E `[TEST PASS] all`) and the Figma
reference (`figma-gui-complete`), merging TWO review passes: the 34-point
mismatch list and the 38-section structural review ("SaaS dashboard → Minecraft
overlay"). The structural review's key order is law: **Geometry → Density →
Navigation → Components → Icons → Typography → Colors → Polish**.

Already landed: warm re-tint (`#2B1F2A`/`#FF9676`), header killed, search killed,
chips killed, stripe killed, All-view killed, theme-aware tokens, slider hover.

## Triage — 34-point list

**DONE (5):** #4 header, #5 search, #18 chips, #22 alignment, #23 glass.
**OBSOLETE (2):** #8 solid rail, #25 orange outline — describe an older render.
**OUT-OF-SCOPE as literally stated (3):** #1/#2/#6 (a floating *OS* window is
impossible) — SUPERSEDED by the floating-panel requirement below, which achieves
the same composition *inside* the fullscreen screen.
**ACCEPTED (14):** prior parity requirements (sub-rail, avatar, glow, grid density,
labelless toggles, muted secondary, ambient light, sheen, key boxes, collapse,
inline enums, checkbox dot).
**REVERSED (3):** #28/#30/#31 were "decided keep" under the old token spec; the
structural review explicitly demands tiny toggles/sliders and the stakeholder
pasted it as direction — now ACCEPTED (§5 below).
**KEPT (2):** #11 icon assets (repo set only), #20 Poppins titles (brand face).

## Triage — 38-section structural review (§1–§38)

| Section | Status | Disposition |
|---|---|---|
| §1 compact floating window, §25 floating-panel feel, §28 intrinsic height, §29 panel scroll | ACCEPTED | §1 Shell requirements |
| §2 header, §22 search, §23 branding, §33 count label | DONE | already killed; search stays dead (no hidden-search revival) |
| §3 sidebar concept, §4 icon categories, §10 badges | ACCEPTED (badges DONE) | §2 Navigation requirements |
| §5 card size, §15 density, §16 columns, §17 collapse, §34 content-sized modules | ACCEPTED | §2 + §4 requirements |
| §6 toggles, §7 keybinds, §18 sliders, §19 dropdowns | ACCEPTED (tokens shrink) | §5 Component requirements |
| §8 icons, §21 semantic icons | KEPT | closest existing repo icons; no new asset pipeline in this change |
| §9 typography scale-down | ACCEPTED | §6 Type requirements |
| §11 borders, §12 radius hierarchy | ACCEPTED | §7 Border/Radius requirements |
| §13 purple-black treatment | DONE via re-tint; warmth verified by hex sample | — |
| §14 surface layering | ACCEPTED | §7 Surface requirements |
| §24 background dim + game visible | ACCEPTED | §1 scrim requirement |
| §26 hover states | ACCEPTED (slider done; rest open) | §8 State requirements |
| §27 enabled/disabled distinction | DONE (title-80 + toggle + sheen) | — |
| §30 spacing scale, §31 dimension tokens | ACCEPTED | §7 Token requirements |
| §32 hierarchy, §35 design language, §36 checklist | ACCEPTED as acceptance criteria | §9 Acceptance |
| §37 priority order (Shell→Polish) | ADOPTED | task phases below |
| §38 15-row before→after table | ACCEPTED as exit criteria | §9 Acceptance |

## ADDED Requirements

### §0 Carried-over requirements (34-point list, intact — nothing dropped)

#### Requirement: Inner Sub-Rail (#7 + #9)
The system SHALL render a second narrow icon rail inside the main rail when the
selected category has sub-groups, matching the reference dual-bar navigation.

#### Scenario: Category with sub-groups shows inner rail
- **WHEN** the selected rail entry declares children
- **THEN** a second 44dp rail appears adjacent with the child icons, and grid filters to the selected child

#### Scenario: Category without sub-groups shows no inner rail
- **WHEN** the selected entry has no children
- **THEN** no second rail is rendered and grid filters to the category as today

#### Requirement: Rail Avatar (#10)
The system SHALL render a circular avatar badge pinned to the bottom of the icon rail.

#### Scenario: Avatar visible
- **WHEN** the config surface is open
- **THEN** a 28dp circle (player head or fallback glyph) sits at the rail bottom with 8dp inset

#### Requirement: Selected-Pill Glow (#12)
The system SHALL render the selected rail entry with a soft accent outer glow in addition to the flat fill.

#### Scenario: Selected entry glows
- **WHEN** an entry is selected
- **THEN** the pill draws accent @35% blurred 7dp behind the solid pill (same technique as `IconWithIndicator`)

#### Requirement: Labelless Toggles (#19)
The system SHALL NOT render "On"/"Off" text beside toggles; toggle state reads from the switch alone.

#### Scenario: No toggle labels
- **WHEN** any card header renders
- **THEN** no `Text("On"/"Off")` node exists in the header row

#### Requirement: Muted Secondary Text (#21)
The system SHALL render secondary/value text muted toward lavender-grey `#8A8C9E`
(current warm `#9A8B84` is close; verify with hex sample and adjust if ΔE is visible).

#### Scenario: Secondary text sampled
- **WHEN** a value caption is hex-sampled from E2E output
- **THEN** it matches `#8A8C9E` within ±8 per channel

#### Requirement: Ambient Card Lighting (#24)
The system SHALL render a faint warm radial glow behind each enabled card (accent @8%,
24dp blur bleed), matching the reference ambient coral lighting.

#### Scenario: Enabled card glows
- **WHEN** a card is enabled
- **THEN** a blurred accent halo extends ~12dp beyond the card bounds; disabled cards render none

#### Requirement: Enabled-Card Header Sheen (#26)
The system SHALL render a 1px top-inner highlight gradient (`accent @28%` → transparent)
on enabled cards — the reference-style sheen (NOT the removed 2dp stripe).

#### Scenario: Sheen present only when enabled
- **WHEN** a card is enabled vs disabled
- **THEN** the enabled card shows the top sheen line; the disabled card shows none

#### Requirement: Tiny Keycaps (#27, extended by §7)
Min width 44dp, 11sp SemiBold label, no keyboard glyph, 4dp radius, near-black fill.

#### Scenario: Key box metrics
- **WHEN** `ForkTokens.Size.keybindMinWidth` is read
- **THEN** it is 44dp, and no `Icon("keyboard")` node exists in the badge

#### Requirement: Card Collapse Control (#29, §17, §34)
The system SHALL render a subtle chevron affordance in each card header that collapses
the card body; collapsed modules are header-only ≈44dp rows; state remembered per session.

#### Scenario: Collapse toggles body
- **WHEN** the collapse affordance is clicked
- **THEN** the card body hides and the affordance flips state; collapsed state is remembered per session

#### Requirement: Inline Enum Options (#32, with #34 dots)
The system SHALL render single-select enum settings as an inline vertical option list
with coral active-option dots (like the reference AntiBot dropdown) for ≤4 options;
larger enums keep the closed dropdown.

#### Scenario: Enum renders inline options
- **WHEN** a setting is an enum with ≤4 options
- **THEN** options render as rows with a coral dot on the active one; larger enums keep the closed dropdown

#### Requirement: Checkbox Glow Dot (#33, after verifying current `ForkCheckbox`)
The system SHALL render checkboxes as small squares with a glowing accent dot when checked.

#### Scenario: Checked checkbox glows
- **WHEN** a checkbox is checked
- **THEN** a 6dp accent dot with 7dp @35% blur halo centers in the box (mirrors `IconWithIndicator`)

#### Requirement: Dense Grid Gutters (#14/#15/#16, reconciled with §2)
Cards lay out 2-up inside the 640dp panel (the panel width, not the viewport, governs
columns — the old "3-col ≥1400px viewport" rule is superseded); gutters 8dp (was 12dp).

### §1 Shell — Floating Panel (highest priority)

#### Requirement: Centered compact panel with dim scrim

The system SHALL render the config UI as a centered panel (max width 640dp,
max height 82% of viewport, 12dp outer radius, 16dp shadow) over a dim scrim
(black @55%) through which the blurred game remains faintly visible — never as
a full-bleed dashboard. Content taller than the panel scrolls *inside* the panel;
the rail stays fixed. Panel height is intrinsic up to the max (no 500px voids).

#### Scenario: Panel centers with scrim
- **WHEN** the surface opens at any window size ≥800×600
- **THEN** a scrim covers the fullscreen, the panel centers with the specified
  max metrics, and E2E pixels outside the panel show dimmed game, not UI chrome

#### Scenario: Small content packs tight
- **WHEN** filtered modules fit in less than max height
- **THEN** the panel shrinks to content height (no empty region)

#### Scenario: Overflow scrolls internally
- **WHEN** content exceeds max height
- **THEN** only the module column scrolls; rail and panel frame stay fixed

### §2 Navigation & Layout

#### Requirement: Integrated compact rail (prior parity spec carries over: sub-rail, avatar, glow, 16dp rhythm)

#### Requirement: Dense content grid
The system SHALL lay out cards 2-up inside the 640dp panel with 8dp gutters
(was 12dp); cards are content-sized (collapsed header-only ≈44dp tall).

#### Scenario: Collapsed module is one row
- **WHEN** a module is collapsed (or has no visible body)
- **THEN** its card is header-only, ≈44dp tall, with zero body padding

### §4 Collapse (carries over: Card Collapse Control)

### §5 Components — Small Client-Style Controls (REVERSED tokens)

#### Requirement: Tiny toggles
`ForkTokens.Size`: track 28×16dp, knob 11dp, inset 2dp (was 38×20/14).

#### Scenario: Toggle metrics
- **WHEN** any toggle renders
- **THEN** track measures 28×16dp and knob 11dp in a 4x crop

#### Requirement: Tiny keycaps
Min width 44dp (already specced), 11sp label, no glyph, 4dp radius, near-black fill.

#### Requirement: Compact sliders
Track 4dp (was 6), thumb 10dp (was 14), value on the label row right-aligned:
`Clicks per second            12` with the track beneath full-width.

#### Scenario: Slider metrics
- **WHEN** any slider renders
- **THEN** track measures 4dp and thumb 10dp in a 3x crop

#### Requirement: Compact dropdowns
Trigger height 32→28dp, 11sp text, background matched to card fill (not a contrasting pill).

### §6 Typography Scale-Down
Module title 15→13sp Bold; setting label 12→11sp Medium; value/caption 11→10sp;
uppercase tracking removed everywhere except rail tooltips.

### §7 Borders, Radii, Surfaces, Spacing Tokens
- Radii: card 12→8dp, control 6→4dp, pill 8→6dp, panel (new) 12dp.
- Borders: card 1dp `borderColor` @50%; inner controls borderless except keycap/dropdown 1dp.
- Surfaces (warm plum steps, no new `UITheme` fields — alpha steps of card fill):
  panel = card @100%, card = theme fill, control row = fill @55% + 4dp radius,
  hover = text @8%.
- Spacing: card padding 12→8dp, row gap 6→4dp, control gap 4→2dp, rail gap 28→16dp.

### §8 States
Hover on rail items (done), toggles (track brighten), sliders (done), dropdowns (done),
keycaps (border brighten), cards (border → full alpha). Recording/focus states unchanged.
Missing-state grep (`hoverable` absent) must return only `ForkTokens.kt` + non-interactive files.

### §9 Acceptance (exit criteria)
The §38 15-row table flips to Target on every row in E2E pixels; hierarchy §32
holds (category glow > module title > setting > value > metadata); checklist §36
passes except dedicated-icon-set (deferred) and search-revival (rejected).
