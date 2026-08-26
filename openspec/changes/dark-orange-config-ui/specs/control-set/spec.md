## Purpose

Specifies the trimmed control set that backs the module-card body rows — the only interactive primitives rendered inside cards, each mapped to the Figma's row anatomy.

## ADDED Requirements

### Requirement: Control set is limited to the Figma's rows

The system SHALL expose only the following controls for use inside module cards: toggle, checkbox, slider, dropdown (single-select), multi-select dropdown, and keybind badge.

#### Scenario: Only allowed controls are used in cards
- **WHEN** a module card's body is rendered
- **THEN** every row is one of the six allowed controls; no color picker, file picker, item list, or other upstream control types are rendered in cards

### Requirement: Toggle control

The system SHALL provide a toggle (on/off switch) that reflects a boolean value, shows the current value label ("On"/"Off"), and toggles on activation, with hover/pressed/disabled states.

#### Scenario: Toggle reflects and toggles state
- **WHEN** the toggle is in the on state and the user activates it
- **THEN** the bound value becomes false and the visual state transitions to off

#### Scenario: Toggle disabled state
- **WHEN** a toggle is disabled
- **THEN** it is non-interactive and rendered at reduced opacity

### Requirement: Checkbox control

The system SHALL provide a checkbox (~16dp) that shows a filled-dot (not tick icon) when checked, with hover and disabled states.

#### Scenario: Checkbox shows checked state
- **WHEN** a checkbox's value is true
- **THEN** it renders the centered filled-dot affordance distinct from the unchecked state

### Requirement: Slider control

The system SHALL provide a slider with a thin track, round thumb, and optional label-with-value text (e.g. "Lifespan (ticks) 20"), supporting min/max/step, drag interaction, and value snapping.

#### Scenario: Slider renders label-with-value
- **WHEN** a slider row includes a label and a current value
- **THEN** the row shows the label and the numeric value alongside the track (layout as per the Figma's Lifespan/Shield-Hold rows)

#### Scenario: Slider snaps to step
- **WHEN** the user drags the thumb and releases at an in-between position
- **THEN** the resulting value is snapped to the nearest step and clamped to [min, max]

### Requirement: Dropdown (single-select)

The system SHALL provide a dropdown with a pill trigger, a popup list shown on open, and a trailing accent dot on the selected row (no leading checkbox, no full-row highlight).

#### Scenario: Dropdown selection changes value
- **WHEN** the user opens the dropdown and selects an option
- **THEN** the bound value updates to the selected option, the trigger text reflects the selection, and the popup closes

#### Scenario: Selected row shows trailing dot
- **WHEN** the dropdown popup is open
- **THEN** the currently selected row shows a trailing accent dot and no other row does

### Requirement: Multi-select dropdown

The system SHALL provide a multi-select dropdown whose trigger shows a count summary (e.g. "2/3"), whose popup supports toggling individual options, and whose rows use a trailing dot for selected items (no leading checkbox).

#### Scenario: Multi-select toggles an option
- **WHEN** the user toggles an option in the multi-select popup
- **THEN** the bound flag set for that option flips, the trigger count label updates, and the row's trailing dot appears or disappears accordingly

#### Scenario: Trigger shows selection count
- **WHEN** the multi-select has 2 of 3 options selected
- **THEN** the trigger displays a summary reflecting the count (e.g. "2/3") rather than listing option names

### Requirement: Keybind badge

The system SHALL provide a keybind badge (~20dp rounded-square) that displays the bound key or a placeholder, and enters a recording state on activation to capture the next keypress.

#### Scenario: Rebinding captures a key
- **WHEN** the user activates the keybind badge and presses a key
- **THEN** the badge stores that key as the bound value and exits recording state

#### Scenario: Escape or dismissal cancels recording
- **WHEN** the badge is in recording state and the user dismisses it (e.g. pressing Escape)
- **THEN** the previous bound value is retained and recording state is exited

### Requirement: Control states and accessibility

The system SHALL render each control with distinguishable default, hover, pressed, and disabled visual states, and each control SHALL remain reachable via pointer and keyboard.

#### Scenario: Disabled controls are non-interactive
- **WHEN** a control is disabled
- **THEN** pointer and keyboard interactions do not change its value

### Requirement: Numeric text-field input is out of scope

The system SHALL NOT provide a standalone bordered numeric text-field-with-spinner as part of the control set until a plain numeric-input row is confirmed present outside the 8 visible modules in the Figma.

#### Scenario: Numeric values in the mock are rendered as sliders
- **WHEN** a numeric value like "20" or "2.6" from the Figma's visible cards is rendered
- **THEN** it is rendered via the slider control (label + value alongside a track), not via a separate text-field-with-spinner control
