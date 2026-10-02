# user-configurable-preferences — Tasks

## 1. Appearance surface (checkpoint 1)
- [x] 1.1 New fork-owned `ForkAppearance` helper (resolve `themes.json`/`oneconfig.json` trees, static fallbacks, accent apply + re-apply, saves) mirroring the `RealOneConfigModule` pattern.
- [x] 1.2 Appearance card on the fork surface: accent row (upstream `ColorOption` on the live prop), scale slider 1–4 + enable toggle rows, theme dropdown row; reachable from the Mod Menu entry by default.
- [x] 1.3 Compile `:modules:internal` + unit tests green.

## 2. Live demo (checkpoint 1 proof)
- [x] 2.1 `preference-demo` / `preference-demo-verify` E2E: flip accent + scale + theme live, save, screenshot, restart fresh, assert registry + files + pixels, restore defaults after.
- [x] 2.2 `clickui` + `configui` + `modmenu-demo` suites stay green.
- [x] 2.3 Fresh-client readback holds on the real display with screenshot evidence for SET and VERIFY (the shipped feature).

## 3. Ship
- [x] 3.1 Commit on a fork branch, push, open PR citing demo screenshots + file end state.
