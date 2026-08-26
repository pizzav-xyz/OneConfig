## Purpose

Provides the 52px icon-only navigation rail that replaces the upstream 264dp labeled sidebar for category selection.

## ADDED Requirements

### Requirement: Icon-only rail width

The system SHALL render the sidebar as a fixed-width vertical rail of exactly `sidebar.width` (52dp), containing only icons (no text labels).

#### Scenario: Sidebar renders at 52dp
- **WHEN** the config screen is shown
- **THEN** the sidebar occupies 52dp of width on the left edge and shows only icon controls

### Requirement: Sidebar selection state

The system SHALL indicate the selected navigation entry with an accent-background pill or equivalent selected affordance, and distinguish hover/default states.

#### Scenario: Selected entry is highlighted
- **WHEN** a navigation entry is selected
- **THEN** its icon is rendered with the accent-selected treatment (accent background pill), distinct from hover and default

#### Scenario: Hover state is visible
- **WHEN** the pointer hovers an unselected entry
- **THEN** the entry shows a hover treatment distinct from both default and selected

### Requirement: Sidebar retains icon rendering, drops labels and account

The system SHALL keep the upstream icon rendering for navigation entries while removing label text and the `Account()` expansion area from the sidebar.

#### Scenario: No text labels or account area in sidebar
- **WHEN** the sidebar is rendered
- **THEN** no per-entry text labels and no account/expansion area are shown in the rail
