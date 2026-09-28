# Reference-Parity Tasks — merged 34-point + 38-section reviews

Order per §37: **P1 Shell → P2 Layout → P3 Components → P4 Visual → P5 Nav → P6 Polish**.
Each task ends with jar + E2E `[TEST PASS] all` + screenshot evidence. Prior spec
(sub-rail, avatar, glow, labelless toggles, muted secondary, ambient light, sheen,
key boxes, collapse, inline enums, checkbox dot) carries over where not repeated below.

## P1 — Shell (do first; biggest delta)

- [ ] P1.1 Floating panel: centered 640dp / 82% viewport max, 12dp radius, 16dp shadow, dim scrim @55% w/ game visible, intrinsic height, internal scroll, fixed rail (`ForkSurface.kt`, new `ForkPanel.kt` — split files per AGENTS.md rule 2)
- [ ] P1.2 Kill full-bleed layout: surface draws scrim + panel only; E2E pixels outside panel show dimmed game

## P2 — Layout

- [ ] P2.1 Dense grid: 2-up in panel, gutters 12→8dp, cards content-sized, collapsed header-only ≈44dp (`ModuleGrid.kt`, `ForkTokens`)
- [ ] P2.2 Spacing scale: card pad 12→8, row gap 6→4, control gap 4→2, rail gap 28→16 (`ForkTokens`, verify in crops)
- [ ] P2.3 Collapse control: header chevron toggling body, session-remembered (`ModuleCard.kt`)

## P3 — Components (small client-style controls)

- [ ] P3.1 Tiny toggles: 28×16/knob 11/inset 2, no On/Off text (`ForkTokens`, `ForkToggle.kt`, `ModuleCard.kt` label removal)
- [ ] P3.2 Tiny keycaps: 44dp min, no glyph, 11sp, 4dp radius, near-black (`ForkTokens`, `ForkKeybindBadge.kt`)
- [ ] P3.3 Compact sliders: track 6→4dp, thumb 14→10dp, value right-aligned on label row (`ForkTokens`, `ForkSlider.kt`, `MockModules.kt` bodies)
- [ ] P3.4 Compact dropdowns: trigger 32→28dp, 11sp, card-matched fill (`ForkTokens`, `ForkDropdown.kt`)
- [x] P3.5 Inline enum options ≤4 with coral active dot; checkbox glow dot (verify `ForkCheckbox.kt` first) — **PASS 2026-09-10 E2E clickui: `ForkEnumOptions` renders labeled dot lists, option tap fires `enum:auto-clicker=1`, selection persists visually (`clickui-07b`)**

## P4 — Visual language

- [ ] P4.1 Radius hierarchy: card 8, control 4, pill 6, panel 12 (`ForkTokens`)
- [ ] P4.2 Border system: card 1dp @50%, inner controls borderless except keycap/dropdown (`ModuleCard.kt`, components)
- [ ] P4.3 Surface layering: panel/card/control-row/hover alpha steps, no new theme fields (`DarkOrangeTheme.kt` usage sites)
- [ ] P4.4 Typography scale: titles 13sp, labels 11sp, values 10sp, drop uppercase (`ModuleCard.kt`, `ForkSettingRow`, badge)
- [ ] P4.5 Ambient light + sheen + pill glow (from prior spec B.1–B.3)
- [ ] P4.6 Muted secondary toward `#8A8C9E` if hex-sample shows visible ΔE

## P5 — Navigation

- [x] P5.1 Inner sub-rail for grouped categories (`IconSidebar.kt`, `ForkSurface.kt`) — **PASS 2026-09-10 E2E clickui: dual-rail shell renders (dark outer app rail + inner category rail, both with avatars); mock declares no child hierarchy so the sub-rail branch stays unexercised**
- [ ] P5.2 Rail avatar 28dp bottom-pinned (`IconSidebar.kt`)
- [ ] P5.3 Hover states: toggle track, keycap border, card border (`ForkToggle.kt`, `ForkKeybindBadge.kt`, `ModuleCard.kt`)

## P6 — Polish & exit

- [ ] P6.1 Missing-state grep clean (only tokens + non-interactive files lack `hoverable`)
- [ ] P6.2 §38 15-row table: every row flips to Target in E2E pixels; §32 hierarchy + §36 checklist pass

## Explicitly NOT doing

- Floating OS window / rounded screen corners / fixed compact resolution (#1/#2/#6 literal).
- Stale claims #8/#25. Dedicated pixel-icon asset pipeline (#11/#8-asset-half). Poppins replacement (#20).
- Old 38×20 toggle + 6/14 slider tokens (REVERSED by stakeholder direction). Search revival of any kind.
