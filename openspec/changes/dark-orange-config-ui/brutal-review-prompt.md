# Brutal Visual Review — reusable prompt

Copy everything below the line into your runner. This is a GLOBAL review:
the entire fork UI surface vs Figma ground truth in one pass.

---

1. TASK: Brutally review the GLOBAL visuals of the OneConfig dark-orange
config UI fork vs Figma ground truth — every surface in one pass: rail
(pill, glyphs, divider, avatar), panel + grid + cards (all three states),
toggles, checkboxes, sliders, dropdowns, multiselects, enums, keybind badges,
popups, labels/values/titles, density, empty states. Report confirmed defects
with evidence, or an explicit no-findings verdict. Review ONLY; change
nothing.
2. EXPECTED OUTCOME: Write your findings to
`/home/pizzav/Documents/oneconfig-fork/openspec/changes/dark-orange-config-ui/brutal-review-output.md`
and ONLY there (no chat-only verdict). Format: one section per finding with
file path + line number + pixel evidence, then a final `## Verdict` section
containing either a defect list or the exact sentence
`No defects found.`
3. REQUIRED TOOLS: Read (images + code), Glob/Grep (code context). Do NOT use
Edit, Write (except the output file), or Bash.
4. MUST DO:
   (a) Read the decision history FIRST:
   `/home/pizzav/Documents/oneconfig-fork/openspec/changes/dark-orange-config-ui/tasks.md`
   — anything recorded there as a deliberate departure (e.g. opaque toggle +
   white knob, neutral surfaces, warm rail pill) is NOT a defect. Do not
   re-flag it.
   (b) Read ground truth with the Read tool, full images, no thumbnails:
   `/home/pizzav/Documents/oneconfig-fork/figma-gui-complete.png`,
   `/home/pizzav/Documents/oneconfig-fork/figma-assets-seperated.png`,
   and the relevant sections of
   `/home/pizzav/Documents/oneconfig-fork/reference.css`.
   (c) Read the implementation under
   `/home/pizzav/Documents/oneconfig-fork/modules/internal/src/main/kotlin/org/polyfrost/oneconfig/internal/ui/`
   (`components/fork/`, `layout/fork/`, `themes/fork/`).
   (d) Rendered screenshots EXIST at
   `/home/pizzav/Documents/oneconfig-fork/minecraft/run/run/screenshots/`
   (2000+ PNGs, `clickui-*` / `configui-*`). Find the newest shots covering
   your axis and read them with the Read tool. If you cannot see a needed
   state, say so in the output file — do not substitute code-only guesses
   for pixels.
   (e) Compare geometry AND color AND glow. Cite observed pixel values.
   Reference CSS values by `reference.css:<line>`, code by `<file>:<line>`.
5. MUST NOT DO: No code edits. No new files except the output file (no
fixtures, no probes, no scripts). Do not invent hex values — every color
claim must cite a file line or a pixel you read. Cover ALL surfaces listed
in §1 — a global review that skips the rail or the popups is incomplete.
Do not claim environmental causes (flakes, load, timing); judge
pixels as pixels. Do not contradict `reference.css` with eyeball estimates —
the CSS export outranks impressions.
6. CONTEXT: OneConfig dark-orange config UI fork, branch `v1`. Canonical
Accent is `#FF9676` (`Provider.kt`). Known spec anchors: toggle 24x14 track +
10dp knob + 2dp inset; checkbox 14dp box + 4dp dot; slider 4dp track + 4dp
white knob; chevron 12px; panel 736 max / 472 min, r16; rail 56dp, icons 16px.
Prior solo audits found the work converged; you are a fresh hostile re-check.
Prove them wrong or confirm it — with evidence.
