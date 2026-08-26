## 1. Scaffolding & Scope Framing

- [x] 1.1.1 Confirm the Fork's build entry point: identify which `ComposeScreen` surface the Fork's `ModuleGrid + Sidebar` composition will attach to, so new files are wired through the existing `SkiaCtx` → `SkiaOffscreenTarget` → `guiGraphics.blit` path rather than a new pipeline
- [x] 1.1.2 Mark the Fork's explicit exclusions in the change log / PR description: list subsystems the Fork leaves out of scope (HUD, notifications, non-mock control types such as color/file/item, `poly-compose` item-icon runtime, Vulkan variants) — file deletions are not a requirement to satisfy any spec; items below are trailing cleanups if the implementer chooses to include them
- [ ] 1.1.3 (Optional trailing cleanup) If pursued: remove deleted paths from `settings.gradle.kts` / imports and confirm `./gradlew build` still resolves module deps; otherwise leave them in-tree and verify the new surface simply doesn't render them
- [ ] 1.1.4 Document the chosen scope posture in the PR (what the Fork renders vs what it deliberately does not exercise), so reviewers audit the additive surface rather than the diff size

## 2. Theme Tokens

- [x] 2.1.1 Create `theme/DarkOrangeTheme.kt` as the Fork's sole rendered `UITheme` instance (dark-orange palette, single entry exposed by the provider for this surface); keep the other themes in `Impls.kt` on disk unless a trailing cleanup opts to remove them — `DarkOrangeTheme` is what the Fork's `Provider` exposes
- [x] 2.1.2 Add/adjust the token table entries called out in `design.md` (colors: `background`, `cardBackground`, `border`, `accent`, `accentDim`, `textPrimary`, `textSecondary`; radii: `card` 12dp, `control` 6dp, `pill` 8dp; spacing: `cardGap` 16dp, `cardPadding` 12dp, `rowGap` 8dp; typography: `title` 13sp semibold, `body` 12sp)
- [ ] 2.1.3 Verify the token values render: screenshot a preview or `runClient` with a single `DarkOrangeTheme` card and visually confirm background/card/border/accent match the Figma estimates within tolerance
- [x] 2.1.4 Implement the glass-vs-opaque fork as a single token swap (`color.background` alpha + presence of `BlurRenderer` backdrop) — include a boolean/feature flag or token variant so toggling the decision point is one change, not a component rewrite

- [x] 2.2.1 Wire the new `components/` / `layout/` surface to read tokens exclusively from `Provider`/`LocalTheme` (no direct `Color(0x…)` or `dp` literals in the Fork's `components/` / `layout/` additions)
- [x] 2.2.2 Grep audit over the **Fork's changed files**: `rg -n "Color\(0x|#[0-9a-fA-F]{6}|\b\d+\.dp\b" <fork-components> <fork-layout>` (or equivalent) returns no hits outside `theme/` except transient locals that delegate to tokens — attach the grep output to the PR
- [ ] 2.2.3 Fix any violations found in 2.2.2 by moving the literal into the token table and importing it

- [ ] 2.3.1 Mark the glass-vs-opaque token (`color.background` alpha + `BlurRenderer` usage) as held/pending in code (TODO comment + issue link referencing the required opaque Figma re-export) so the branch is still reviewable

## 3. Control Set

- [x] 3.1.1 Add `Toggle` to the Fork's surface, scoped from `SwitchControl` (42x21dp track, thumb animation, on/off label) — keep `animateColorAsState`/`animateDpAsState` behavior
- [ ] 3.1.2 Verify toggle visual states: on, off, hover (border fade-in), pressed, disabled (alpha 0.4); add a Compose Preview / screenshot showing all four and attach it to the PR

- [x] 3.2.1 Add `Checkbox` to the Fork's surface, scoped from `CheckboxIndicator` (24dp → ~16dp, centered filled-dot when checked instead of `Icon("tick")`)
- [ ] 3.2.2 Verify checkbox states: unchecked, checked (dot visible), hover, disabled; the dot must be centered within the 16dp box and distinct from the border color

- [x] 3.3.1 Add `Slider` to the Fork's surface from `SliderControl` (thin track, round thumb, `pointerInput(awaitEachGesture)` drag, min/max/step)
- [ ] 3.3.2 Verify label-with-value rows (e.g. "Lifespan (ticks) 20") render the value next to the label and keep the slider track full-width, not collapsed
- [x] 3.3.3 Add unit tests for slider snapping: clamp to [min, max], round to nearest `step`, and handle edge values; tests must pass on CI

- [x] 3.4.1 Add `Dropdown` to the Fork's surface, scoped from `DropdownOption` — keep `Popup` + pill trigger + `PlainListItem` selection-index logic; restyle the selected row to a **trailing accent dot** (remove full-row accent highlight)
- [ ] 3.4.2 Verify dropdown: open/close, selection updates the bound value and trigger text, popup closes on selection; the selected row shows a trailing dot and no other row does — attach a screenshot with the popup open

- [x] 3.5.1 Add `MultiSelectDropdown` to the Fork's surface, scoped from `MultiSelectDropdownOption` — restyle `CheckableListItem` to trailing dot (drop the leading 24dp checkbox), keep checkable/count-label/flag-set logic
- [ ] 3.5.2 Verify the trigger shows a count summary (e.g. `2/3`) when 2 of 3 options are selected, not option names; toggling an option flips its flag and the dot appears/disappears accordingly — attach a screenshot
- [ ] 3.5.3 Add unit tests for multi-select flag set: toggle single, toggle all, trigger count string formatting

- [x] 3.6.1 Add `KeybindBadge` to the Fork's surface, scoped from `KeybindOption` (~20dp rounded-square, bound-key display + click-to-record)
- [ ] 3.6.2 Verify `KeybindBadge` states: unbound (`-` placeholder) vs bound (letter) vs recording (awaiting keypress); pressing a key stores it and exits recording
- [ ] 3.6.3 Verify Escape/dismissal during recording retains the previous value and exits recording state

- [ ] 3.7.1 Confirm `Slider` covers the visible numeric rows (`20`, `2.6`) and no bordered text-field-with-spinner is rendered on the Fork's surface; mark `NumberField` as held in the PR until a plain numeric row is confirmed outside the 8 visible modules (do not add speculative `NumberField` work)

## 4. Layout & Shell

- [x] 4.1.1 Build `ModuleCard` (`Column` with header `Row`: title left, `KeybindBadge` center-right, `Toggle` right; body slot: `Column` of control rows)
- [ ] 4.1.2 Apply token insets: `spacing.cardPadding` on header and body, `spacing.rowGap` (8dp) between body rows; card surface uses `color.cardBackground`, `color.border`, `radius.card` — verify in preview that the header divider / border matches the Figma's 1px low-alpha border

- [x] 4.2.1 Build `ModuleGrid` (2-col grid host for `ModuleCard`); gutters on both axes use `spacing.cardGap` (16dp)
- [x] 4.2.2 Handle odd card count: the last row's single card is left-aligned with no placeholder in the second column
- [x] 4.2.3 Make the grid vertically scrollable when the total content height exceeds the viewport; confirm the `Sidebar` remains fixed while the grid scrolls

- [x] 4.3.1 Create `ModuleGrid` mock dataset: 8 modules matching the Figma (titles, initial control states) as static data in a preview entry point
- [x] 4.3.2 Build `IconSidebar` (or wire the Fork's `Sidebar`) as a 52dp **icon-only vertical rail**; reuse the icon rendering for `NavigationEntries` within the Fork's surface, scoped to the new rail (label text and `Account()` expansion are not part of the Fork's surface)
- [ ] 4.3.3 Verify sidebar states per `specs/icon-sidebar`: default, hover (distinct), selected → accent-background pill; attach a screenshot at 52dp width showing hover and selected together
- [ ] 4.3.4 Verify the `IconSidebar` does not clip icons at 52dp and still handles overflow for longer-than-8 module lists (scroll or collapsed overflow — document which and why)

## 5. Rendering & Wiring

- [x] 5.1.1 Wire the Fork's `ModuleGrid + IconSidebar` surface through the single kept render path: `ComposeScreen` → `SkiaCtx` → `SkiaOffscreenTarget` → `guiGraphics.blit`, without adding a second backend
- [x] 5.1.2 Confirm no additional render-path work is required for the Fork beyond the existing GL pipeline — unused Vulkan variants are not part of the Fork's surface (trailing removal, if pursued at all, is an isolated follow-up commit)

## 6. Verification & Compliance (self-check)

- [ ] 6.1.1 Desktop preview screenshot diff: render `ModuleGrid` at the Figma's assumed viewport size and diff against a **properly re-exported opaque** frame (not the checkerboard PNG used for the plan); use bounding boxes from the separated-assets export to spot-check spacing tokens — attach before/after screenshots and the diff output to the PR
- [ ] 6.1.2 In-game `./gradlew runClient` smoke: open the screen, confirm it blits without flicker or Z-order issues

- [ ] 6.2.1 HiDPI verification: run the in-game screen at GUI scales 1x, 2x, 3x and visually confirm text stays crisp and not blurry; if `pixelGridScale`/`surfaceRatio` handling was adjusted for the Fork, verify it does not regress and fix before sign-off
- [x] 6.2.2 AGENTS.md split-files audit: every **new** component, card, grid, and theme entry the Fork introduces lives in its own file grouped by concern (e.g. `theme/`, `components/settings/`, `layout/`); grep shows no monofile containing unrelated concerns

- [ ] 6.3.1 Real-instance click-through on `./gradlew runClient`: manually exercise every new control (toggle, checkbox, slider drag + step, dropdown single, multi-select count, keybind rebind + Escape cancel) and confirm no mocked rendering is involved
- [x] 6.3.2 Add unit tests for pure logic only: slider snap/step clamping, multi-select flag / count-string logic, dropdown selection-index update — attach CI run showing they pass
- [ ] 6.3.3 Compliance hard stops before marking this group done: (a) no raw color/radius literals leak outside `theme/` **within the Fork's changed files** (re-run 2.2.2), (b) no duplicate token definitions, (c) every checklist item above that introduces new behavior has a linked screenshot, grep output, or CI run attached

## 7. Mandatory Brutal Review (subagent, diff-only, unbiased)

> Every task in this group is a **hard gate**. The branch does NOT ship, merge, or archive until 7.1–7.4 are all green. The reviewer is a separate subagent that is given **only the diff** (no issue context, no plan, no Figma notes) — it must fail the review on any smell it sees.

- [ ] 7.1.1 Launch a dedicated review subagent whose **sole input is `git diff` (plus `git diff --stat`)** against the base branch — no proposal, design, or task context, no author commentary, no screenshots. The subagent must produce a structured report: `PASS` or `FAIL` per new component / modification, with line-referenced findings
- [ ] 7.1.2 Subagent checks **scope hygiene**: no dead imports or orphaned resources introduced by the Fork's additions; the diff does not silently re-introduce a hard-coded palette outside `theme/` or a deleted-style control

- [ ] 7.2.1 Subagent checks **theme/token discipline**: grep the **changed files only** (diff) for raw `Color(0x`, `#[0-9a-fA-F]{6}`, or `\bdp\b` literals outside `theme/`; flag any hard-coded palette; verify `DarkOrangeTheme` is the Fork's rendered theme and it derives from shared tokens
- [ ] 7.2.2 Subagent checks **no monofile / no duplication**: flag any file touching unrelated concerns (e.g. card layout + control logic in one file, or duplicated color/radius constants across new components); duplicated blocks must be extracted into a shared module, not left as copy-paste

- [ ] 7.3.1 Subagent checks **visual contract from code alone**: without seeing the Figma notes, infer the intended look purely from token values and the new component anatomy in the diff and call out any inconsistency (e.g. toggle track 42x21dp vs sidebar 52dp mismatch, `Checkbox` still 24dp, trailing dot missing on `Dropdown`/`MultiSelectDropdown`)
- [ ] 7.3.2 Subagent checks **verification completeness**: diff must include or reference (in test files or CI steps) the pure-logic unit tests from 6.3.2; the reviewer must fail if test coverage, CI attachment, or the HiDPI/split-files audits from §6 are absent

- [ ] 7.4.1 Author fixes **every finding** the subagent raises, then **re-runs the same diff-only subagent on the updated diff** — at least one full retry cycle is required even if the first pass looked clean (prevents single-pass rubber-stamping)
- [ ] 7.4.2 Record the subagent's final `PASS` report (including the exact commit SHA reviewed) in the PR / change log; a `FAIL` at any sub-step blocks archiving — the spec-driven `validate` gate alone is not sufficient

> **Protocol:** The subagent that performs §7 must not be the implementing agent. If the implementation was produced by a subagent, use a **different** instantiation (or a different model) for review. The prompt given to that subagent is literally: "You have only this diff. Be brutal and unbiased. Find every smell, leftover, duplication, hard-coded literal, or contract break. Pass only if you would approve this cold, without knowing what it was supposed to do."
