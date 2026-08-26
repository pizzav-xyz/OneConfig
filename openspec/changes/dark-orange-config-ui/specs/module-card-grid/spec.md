## Purpose

Lays out the 2-column responsive grid that hosts the fork's module cards, providing consistent gutters, card placement, and overflow behavior for the config surface.

## ADDED Requirements

### Requirement: Two-column card grid

The system SHALL render the config surface as a 2-column grid of module cards with a consistent gutter between cards and between the grid and the sidebar/content edge.

#### Scenario: Grid shows two columns at normal width
- **WHEN** the viewport is at least the minimum width for two `ModuleCard` columns plus gutters and the icon sidebar
- **THEN** cards are arranged in 2 columns with `spacing.cardGap` (16dp) as both row and column gutter

#### Scenario: Card count not a multiple of two
- **WHEN** the number of modules is odd
- **THEN** the last row shows a single card left-aligned with no empty placeholder occupying the second column

### Requirement: Grid adapts to content height

The system SHALL allow cards of unequal height (content-driven) and arrange them without clipping or overlapping, growing the scrollable content area as needed.

#### Scenario: Cards of different heights
- **WHEN** cards contain different numbers or types of control rows
- **THEN** each card's height is determined by its own content and the grid's row placement does not force a uniform row height

### Requirement: Grid replaces routed per-mod screens

The system SHALL not use navigation-routed per-mod screens for the config surface covered by this fork; the grid is the sole host for module configuration rows.

#### Scenario: Navigation does not create per-mod pages
- **WHEN** the user navigates the config UI via the sidebar
- **THEN** the selected category filters which module cards are visible in the same grid (or shows an empty state), rather than pushing a separate per-mod screen

### Requirement: Grid overflow is scrollable

The system SHALL make the grid vertically scrollable when the total content height exceeds the viewport.

#### Scenario: Overflow scrolls
- **WHEN** the combined height of all visible cards exceeds the viewport height
- **THEN** the grid is scrollable vertically with no horizontal scroll, and the icon sidebar remains fixed
