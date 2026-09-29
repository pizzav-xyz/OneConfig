## 1. Wire real module into fork surface (DONE — implemented, compiles)

- [x] 1.1 Resolve `ConfigRegistry.findTree("oneconfig.json")` in `ForkConfigSurface`; render `OneConfig` card with live `enableBackgroundBlur` (Switch) + `pageOpacity` (Slider) bound to `Tree` properties via `getAs`/`setAs` + `INSTANCE.save()`; no `MockModules` fallback for this card, no new persistence code.
- [x] 1.2 Fork-scoped exemption only: do NOT change global `hiddenModCardIds`/`shouldShowModCard`; the fork surface bypasses the filter by direct id lookup.
- [x] 1.3 Keep `MockModules` untouched for other cards in v1; the real card loads with zero test hooks (`testBounds` keys only for E2E tapping, events optional).

## 2. Acceptance — clickui E2E with real screenshot + restart persistence (DONE 2026-09-29, all green under Xvfb)

Runs used `xvfb-run -a -s "-screen 0 1280x800x24" ./run-e2e.sh <name>` (headless shell has no DISPLAY; first bare run crashed in GLFW init, unrelated to the change).

Exact steps (implementer runs, in order, on `:minecraft:26.2-fabric`):

1. `ONECONFIG_E2E_TEST=clickui-real-module ./run-e2e.sh clickui-real-module` (or `./gradlew :minecraft:26.2-fabric:runClient -Pdevauth=false -Doneconfig.test=true -Doneconfig.e2e.test=clickui-real-module`):
   - a. Assert `ConfigRegistry.findTree("oneconfig.json") != null` and log `real-module: registry hit oneconfig.json title=OneConfig`.
   - b. Open `OneConfigUIScreen`, await bounds `real-oneconfig-toggle` + `real-oneconfig-opacity`, screenshot to `minecraft/run/screenshots/clickui-real-module-00-open.png`.
   - c. Read `enableBackgroundBlur` initial value, tap toggle, assert flipped value in live `Tree`, screenshot `minecraft/run/screenshots/clickui-real-module-01-toggled.png`.
   - d. Drag `pageOpacity` slider, assert value changed, screenshot `minecraft/run/screenshots/clickui-real-module-02-opacity.png`.
   - e. `INSTANCE.save()`; read `<gamedir>/oneconfig/oneconfig.json` (or `config/oneconfig.json` per `ConfigManager` path) and log `real-module: file enableBackgroundBlur=<v> pageOpacity=<v>`; close UI; assert `[TEST PASS] all`.
2. Full client restart: launch `run-e2e.sh clickui-real-module-verify` (same build, no state reset):
   - a. Assert reloaded `enableBackgroundBlur` equals the flipped value from step 1c (read from file before UI opens).
   - b. Open surface, screenshot `minecraft/run/screenshots/clickui-real-module-03-after-restart.png` showing the toggle in the persisted state.
   - c. Restore original value, save, assert `[TEST PASS] all` so the suite leaves no dirty state.
3. Full `clickui` suite goes green: `./run-e2e.sh clickui` exits 0 with `[TEST PASS] all` — DONE 2026-09-29: 86 `[TEST PASS]`, 0 `[TEST FAIL]` (includes new §8 real-module assertions + `clickui-14-real-module` screenshot; one earlier run hit a checkbox mis-tap flake, clean rerun green).

Pass criteria (all four): real UI screenshot exists at the paths above (not a mock render, not a unit test); file value survives restart (step 2a); module loaded via `findTree("oneconfig.json")` (step 1a log); clickui suite green with the module included (step 3 count).

## 3. Triage of the 39 unpushed commits on v1 (DONE — report only, no push/rebase)

Branch state at spec time: `v1 4638ce78 [origin/v1: ahead 39]`. Full list `git log origin/v1..v1 --oneline` (newest first). Classification below.

### PR-worthy upstream (2, needs upstream confirmation before any PR)

- `1818be85 build(fabric): pin adventure platform to release for 26.2` — build fix on a versioned module; IF it fixes upstream `26.2` build, cherry-pick alone to a clean branch and PR with the build log.
- `6719f822 feat(keybind): suppress keybinds while a text field is focused` — already at/behind the fork point in this clone's history (`git log` shows it below `613dddae`); if not yet upstream, it is the only behavior fix with general value. Verify against `origin/v1` before claiming.

### Local-only noise — DO NOT PR (37)

- Restyle surface (fork-only, never upstream): `613dddae`, `a11e189a`, `0c86b14e`, `bed2a7f2`, `d7446841`, `22098216`, `c5ca468d`, `6946b8a8`, `71285a28`, `f2e895e2`, `5986a9a2`, `c4ead166`, `2741e551`, `f9bfcd24`, `8d2e8cbf`, `da08506a`, `968712d8`, `e3b6511c`, `f171e4f1`, `b01cc689`, `fbca5bc0`, `54ed3a71`, `42b91b78`, `4e3505cd`, `d886acca`, `53a5e262`, `4c354c58`, `029f5bcb`, `21437aba`, `73807fa5`, `20843da8`.
- Fork E2E/mocks (test hooks + synthetic modules, meaningless upstream): `085fbd49 feat(fork): add mock modules and test hooks for E2E`, `45bf4458 test(e2e): add click-through harness with synthetic input`, `d7c91c96 fix(e2e): quick-play and screenshot harness, fork hygiene`.
- Fork docs/chore (audit trail, evidence logs, ignore rules): `835ca8b1`, `33d7e43b`, `2c847536`, `4638ce78`.

No rebase, no squash performed. Spec committed and pushed ordinarily to origin v1 alongside the implementation (see build order 2026-09-29).

## 4. Executed 2026-09-29

Implemented §1, ran §2 (set 17/0, verify-restart 12/0, full clickui 86/0), committed + pushed. Awaiting review, no further work started.
