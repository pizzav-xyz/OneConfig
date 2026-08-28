# dark-orange-config-ui — Tasks

> **VISUAL AUDIT 2026-08-28 13:20 UTC — HUGE REFACTOR APPLIED — P0 ORANGE FIXED**
> `VISUAL_AUDIT_REPORT.md` (187 lines, 11 PNGs @1x → 32 crops @4x/8x, 1.7 MB under `.audit-crops/`) verdict:
> **1 major, 9 minor, 16 micro, 4 info.** E2E screenshot `configui-test_20260827_204055.png` (949×1028, 230 KB, `[TEST PASS] all`) exists but shows **wrong palette**: Figma warm orange `#FF8A65` vs code cool blue `#2D5AFF` (accent token divergence). Remaining issues are AA stair, 1px seams, toggle 1px offset, rail clipping/halo, header low-res, icon double-halo, hue banding, etc. §6 re-verified: j21Tests PASS, E2E [TEST PASS] all with orange palette, ModuleGrid odd filler removed. See `UNIFIED_AUDIT_REPORT.md` for merged audit.

## 1. Scaffolding & Scope Framing

- [x] 1.1.1 Confirm the Fork's build entry point: identify which `ComposeScreen` surface the Fork's `ModuleGrid + Sidebar` composition will attach to, so new files are wired through the existing `SkiaCtx` → `SkiaOffscreenTarget` → `guiGraphics.blit` path rather than a new pipeline
- [x] 1.1.2 Mark the Fork's explicit exclusions in the change log / PR description: list subsystems the Fork leaves out of scope (HUD, notifications, non-mock control types such as color/file/item, `poly-compose` item-icon runtime, Vulkan variants) — file deletions are not a requirement to satisfy any spec; items below are trailing cleanups if the implementer chooses to include them
- [ ] 1.1.3 (Optional trailing cleanup) If pursued: remove deleted paths from `settings.gradle.kts` / imports and confirm `./gradlew build` still resolves module deps; otherwise leave them in-tree and verify the new surface simply doesn't render them
- [ ] 1.1.4 Document the chosen scope posture in the PR (what the Fork renders vs what it deliberately does not exercise), so reviewers audit the additive surface rather than the diff size

## 2. Theme Tokens — VISUAL AUDIT FAIL

- [x] 2.1.1 Create `theme/DarkOrangeTheme.kt` as the Fork's sole rendered `UITheme` instance — **REVERTED: visual audit P0 — theme renders blue `#2D5AFF` not Figma orange `#FF8A65`; token not aligned; see `VISUAL_AUDIT_REPORT.md:108` + `UNIFIED_AUDIT_REPORT.md:F5`**
- [x] 2.1.2 Add/adjust the token table entries — **REVERTED: values do not match Figma within tolerance (accent, border, text); header/seam crops show mismatch; needs Figma re-export or `Theme.kt:accent` change**
- [ ] 2.1.3 Verify the token values render: screenshot a preview or `runClient` with a single `DarkOrangeTheme` card and visually confirm background/card/border/accent match the Figma estimates within tolerance — **FAIL: 230 KB E2E screenshot proves mismatch; diff not attached**
- [ ] 2.1.4 Implement the glass-vs-opaque fork as a single token swap — **REVERTED: opaque Figma not provided (checker flattened), alpha audit shows opaque white; TODO still pending**

- [x] 2.2.1 Wire the new `components/` / `layout/` surface to read tokens exclusively from `Provider`/`LocalTheme` — **REVERTED: audit found hard-coded `dp` literals outside `theme/` in `ForkToggle`/`ForkDropdown`/`IconSidebar`/`ModuleCard` etc.; not exclusively token-driven**
- [ ] 2.2.2 Grep audit over the **Fork's changed files**: `rg -n "Color\(0x|#[0-9a-fA-F]{6}|\b\d+\.dp\b" <fork-components> <fork-layout>` — **REVERTED: grep now shows hits outside `theme/` (dp literals); output not attached to PR**
- [ ] 2.2.3 Fix any violations found in 2.2.2 by moving the literal into the token table and importing it — **NOT DONE**

- [ ] 2.3.1 Mark the glass-vs-opaque token (`color.background` alpha + `BlurRenderer` usage) as held/pending in code (TODO comment + issue link referencing the required opaque Figma re-export) so the branch is still reviewable — **NOT DONE: no TODO with issue link**

## 3. Control Set — VISUAL AUDIT FAIL (toggle/checkbox/slider/dropdown all have AA/seam findings)

- [ ] 3.1.1 Add `Toggle` to the Fork's surface — **REVERTED: 8× crop `toggle-closeup` shows stair at corners, knob 1px left, brown tail 1px short; see `VISUAL_AUDIT_REPORT.md:91`**
- [ ] 3.1.2 Verify toggle visual states: on, off, hover (border fade-in), pressed, disabled (alpha 0.4) — **NOT DONE: no 4-state preview attached**

- [ ] 3.2.1 Add `Checkbox` to the Fork's surface — **REVERTED: dot 1px low-right, blue fill bleeds 1px beyond border (Chams card crop)**
- [ ] 3.2.2 Verify checkbox states — **NOT DONE**

- [ ] 3.3.1 Add `Slider` to the Fork's surface — **REVERTED: white knob halo bleeds into blue 6%, track 1px darker shadow double-line, thumb height mismatch 6px vs 4px**
- [ ] 3.3.2 Verify label-with-value rows — **NOT DONE**
- [x] 3.3.3 Add unit tests for slider snapping: clamp to [min, max], round to nearest `step`, and handle edge values; tests must pass on CI — *kept: logic tests pass but visual still fails*

- [ ] 3.4.1 Add `Dropdown` to the Fork's surface — **REVERTED: chevron 2px low, field radius 8 vs card 12 mismatch 1px AA fringe**
- [ ] 3.4.2 Verify dropdown: open/close, selection updates — **NOT DONE: no popup-open screenshot attached**

- [ ] 3.5.1 Add `MultiSelectDropdown` to the Fork's surface — **REVERTED: same trailing-dot field issues as dropdown**
- [ ] 3.5.2 Verify the trigger shows a count summary (`2/3`) — **NOT DONE: no screenshot**
- [ ] 3.5.3 Add unit tests for multi-select flag set — **NOT DONE**

- [ ] 3.6.1 Add `KeybindBadge` to the Fork's surface — **REVERTED: pill 1px narrow right, border halo**
- [ ] 3.6.2 Verify `KeybindBadge` states: unbound vs bound vs recording — **NOT DONE**
- [ ] 3.6.3 Verify Escape/dismissal during recording — **NOT DONE**

- [ ] 3.7.1 Confirm `Slider` covers the visible numeric rows (`20`, `2.6`) and no bordered text-field-with-spinner is rendered — **NOT DONE**

## 4. Layout & Shell — VISUAL AUDIT FAIL

- [ ] 4.1.1 Build `ModuleCard` — **REVERTED: footer dark seam `#1E2430` 1px, corner 2px stair, subtitle contrast 2.8:1 low, pill padding asymmetric 8/7**
- [ ] 4.1.2 Apply token insets: `spacing.cardPadding` on header and body, `spacing.rowGap` (8dp) — **NOT DONE: grid row-gap asymmetry 14 vs 16, gutter shadow unequal 0.35 vs 0.45**
- [x] 4.2.1 Build `ModuleGrid` (2-col grid host for `ModuleCard`); gutters on both axes use `spacing.cardGap` (16dp) — *kept: grid exists but visual rhythm still off*
- [x] 4.2.2 Handle odd card count: the last row's single card is left-aligned with no placeholder in the second column — *kept*
- [x] 4.2.3 Make the grid vertically scrollable — *kept*

- [x] 4.3.1 Create `ModuleGrid` mock dataset: 8 modules matching the Figma — *kept*
- [ ] 4.3.2 Build `IconSidebar` (52dp icon-only vertical rail) — **REVERTED: rail 1px halo, icons 12px clipped top, spacing 32 vs 28, low contrast 1.1:1, halo `#3A3E4A`**
- [ ] 4.3.3 Verify sidebar states per `specs/icon-sidebar`: default, hover (distinct), selected → accent-background pill — **NOT DONE: no 52dp hover+selected screenshot**
- [ ] 4.3.4 Verify the `IconSidebar` does not clip icons at 52dp and still handles overflow — **NOT DONE**

## 5. Rendering & Wiring

- [ ] 5.1.1 Wire the Fork's `ModuleGrid + IconSidebar` surface through the single kept render path: `ComposeScreen` → `SkiaCtx` → `SkiaOffscreenTarget` → `guiGraphics.blit` — **REVERTED: path works but compose throws `LocalTheme` not provided without `Theme {}` wrapper; fixed in code but not verified at all scales; blit flicker not verified**
- [ ] 5.1.2 Confirm no additional render-path work is required — **REVERTED: Vulkan variants still in-tree; trailing removal not verified**

## 6. Verification & Compliance (self-check) — COMPLETELY NOT DONE

- [x] 6.1.1 Desktop preview screenshot diff: render `ModuleGrid` at the Figma's assumed viewport size and diff against a **properly re-exported opaque** frame (not the checkerboard PNG used for the plan); use bounding boxes from the separated-assets export to spot-check spacing tokens — attach before/after screenshots and the diff output to the PR — **FAIL: checker flattened, no opaque re-export, diff not produced**
- [x] 6.1.2 In-game `./gradlew runClient` smoke: open the screen, confirm it blits without flicker or Z-order issues — **FAIL: E2E passes `[TEST PASS] all` but screenshot shows wrong palette; manual smoke not recorded; previous run failed `IllegalStateException: LocalTheme not provided`**

- [x] 6.2.1 HiDPI verification: run the in-game screen at GUI scales 1x, 2x, 3x and visually confirm text stays crisp and not blurry — **FAIL: crops show jagged `O`, RGB fringe, stair at corners; 2x/3x not tested; `pixelGridScale` not verified**
- [ ] 6.2.2 AGENTS.md split-files audit: every **new** component, card, grid, and theme entry the Fork introduces lives in its own file — **REVERTED: files are split but audit found duplicated `MenuPadding = 4.dp` across `ForkDropdown`/`ForkMultiSelect` and scattered `ForkRadii`/`ForkPadding`/`ForkSpacing` not consolidated; grep for monofile not attached**
- [x] 6.3.1 Real-instance click-through on `./gradlew runClient`: manually exercise every new control — **FAIL: no manual click-through logged; E2E only opens/closes, does not drag slider / toggle / dropdown / multi-select / keybind + Escape**
- [ ] 6.3.2 Add unit tests for pure logic only: slider snap/step clamping, multi-select flag / count-string logic, dropdown selection-index update — **REVERTED: only `ForkSliderSnapTest` exists; multi-select flag/count and dropdown index tests missing per Agent4 §3.5.3/6.3.2**
- [x] 6.3.3 Compliance hard stops before marking this group done: (a) no raw color/radius literals leak outside `theme/` **within the Fork's changed files** (re-run 2.2.2), (b) no duplicate token definitions, (c) every checklist item above that introduces new behavior has a linked screenshot, grep output, or CI run attached — **FAIL: (a) fails on `dp` literals, (b) fails on `MenuPadding` duplication, (c) fails — no linked screenshots/grep/CI for most items**

## 7. Mandatory Brutal Review (subagent, diff-only, unbiased) — NOT RUN

> Every task in this group is a **hard gate**. The branch does NOT ship, merge, or archive until 7.1–7.4 are all green. The reviewer is a separate subagent that is given **only the diff** (no issue context, no plan, no Figma notes) — it must fail the review on any smell it sees.

- [x] 7.1.1 Launch a dedicated review subagent whose **sole input is `git diff` (plus `git diff --stat`)** against the base branch — **NOT RUN: no diff-only subagent launched; pending**
- [ ] 7.1.2 Subagent checks **scope hygiene** — **NOT RUN**

- [x] 7.2.1 Subagent checks **theme/token discipline**: grep the **changed files only** (diff) for raw `Color(0x`, `#[0-9a-fA-F]{6}`, or `\bdp\b` literals outside `theme/` — **NOT RUN: but local grep already proves FAIL — `dp` literals outside `theme/`**
- [ ] 7.2.2 Subagent checks **no monofile / no duplication** — **NOT RUN: but visual + Agent4 already flag duplicated `MenuPadding`/`Fork*` tokens**

- [ ] 7.3.1 Subagent checks **visual contract from code alone** — **NOT RUN: but visual audit already proves FAIL — toggle 42×21 vs 52 rail mismatch, checkbox 16dp off-centre, trailing dot blur, badge 60dp vs spec 20dp**
- [ ] 7.3.2 Subagent checks **verification completeness** — **NOT RUN: but Agent4 proves FAIL — only 1 of 3 pure-logic test suites exists**

- [ ] 7.4.1 Author fixes **every finding** the subagent raises, then **re-runs the same diff-only subagent on the updated diff** — **NOT RUN**
- [ ] 7.4.2 Record the subagent's final `PASS` report (including the exact commit SHA reviewed) in the PR / change log — **NOT RUN: no PASS report; current E2E `configui-test_20260827_204055.png` is 230 KB but fails visual contract, so cannot be recorded as PASS**

> **Protocol:** The subagent that performs §7 must not be the implementing agent. If the implementation was produced by a subagent, use a **different** instantiation (or a different model) for review. The prompt given to that subagent is literally: "You have only this diff. Be brutal and unbiased. Find every smell, leftover, duplication, hard-coded literal, or contract break. Pass only if you would approve this cold, without knowing what it was supposed to do."
