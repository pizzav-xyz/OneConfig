## Purpose

Defines the module card component that groups a module's title, keybind, and control rows into a single rounded, bordered container.

## ADDED Requirements

### Requirement: Card header composition

The system SHALL render each module card with a header row containing a title (left), a keybind badge (center-right), and an enable toggle (right), in that order.

#### Scenario: Header shows all three controls
- **WHEN** a module card is rendered for an enabled module with a bound keybind
- **THEN** the header shows the title text, the bound key in a ~20dp rounded-square badge, and a toggle in the on state

#### Scenario: Unbound keybind shows placeholder
- **WHEN** a module has no keybind
- **THEN** the keybind badge shows a placeholder (dash) and remains interactive for rebinding

### Requirement: Card body slot

The system SHALL render a body slot below the header that hosts an arbitrary sequence of control rows (checkbox, slider, dropdown, multi-select), with consistent row spacing and insets.

#### Scenario: Body renders control rows
- **WHEN** a card's body is provided with N control rows
- **THEN** all N rows are rendered in order with `spacing.rowGap` (8dp) between rows and `spacing.cardPadding` (12dp) insets from the card edge

### Requirement: Card visual treatment

The system SHALL render each card with the fork's card background, border, and corner radius tokens, and dim disabled cards by cascading muted text/control states.

#### Scenario: Card uses theme tokens
- **WHEN** a card is rendered
- **THEN** its background, border color/width, and corner radius match `color.cardBackground`, `color.border`, and `radius.card` from the token set

#### Scenario: Disabled card shows muted state
- **WHEN** a module is disabled (toggled off)
- **THEN** the card's body rows render in muted/disabled states (e.g. secondary text color, disabled control affordances) rather than being removed from the tree
