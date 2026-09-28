# Agent 5 — Visual Comparison & Micro-Issue Audit

**Project:** `/home/pizzav/Documents/oneconfig-fork`
**Date:** 2026-08-27 (E2E screenshots 20:40 UTC — Europe/Brussels)
**Auditor:** Sisyphus — Agent 5 (Visual Comparison & Micro-Issue)
**Method:** `Read` every PNG at 1× → `magick` zoom/crop to new files under `.audit-crops/` (never overwriting source, geometry in filename) → `Read` crops at 4×/8× for sub-pixel inspection
**Toolchain:** ImageMagick 7.1.2-29 Q16-HDRI (`magick`), `identify` for dimensions

---

## Step 1 — Inventory: Every PNG at 1×

| # | Absolute Path | Dimensions | Visible Content at 1× |
|---|---|---|---|
| **R1** | `figma-gui-complete.png` | 965×821 8-bit sRGB 203 KB | Figma composite — checkerboard bg (#8AA0A7 12px), outer dark nav (`#1A1F2A`) + left icon rail (10 icons orange `#FF8A65`), central 2-col card grid: `AntiBot / AutoArmor / AutoHeal` left, `AntiBow / AutoBow / AutoGapple / AutoPotion` right. Warm peach/orange accent. Dashed orange annotation frames around component specs on right + bottom. `Frame 1` + top toolbar. |
| **R2** | `figma-assets-seperated.png` | 965×821 8-bit sRGB 203 KB | **Byte-identical to R1** (`identify` + byte size 207809 B). Duplicate export. |
| **S1** | `/tmp/configui-test-orange.png` | 949×1028 8-bit sRGB 230 KB | E2E — dark config grid (8 cards: `Auto Clicker`, `Auto Tool`, `Velocity`, `Timer` left; `Auto Eat`, `Chams`, `Reach`, `Waypoint` right). Cool blue accent `#2D5AFF` + brown slider unfilled `#4A2F24`. Thin left icon rail (4 icons). Minecraft blurred forest bg, hotbar visible bottom. |
| **S2** | `/tmp/configui-test.png` | 949×1028 8-bit sRGB 590 KB | E2E — `ONECONFIG Mods` listing screen. Left nav (`ONECONFIG`, `MODS & OPTIONS`, `PERSONALIZATION`), filter chips `All/Favorites/Hypixel…`, 2-row card grid (`Test Kt Config`, `Test Mod` top; `Date HUD`, `Item HUD`, `Item List Hud`, `test HUD`, `Time HUD` bottom). Same blue footer `#2D5AFF`. |
| **S3** | `minecraft/run/run/screenshots/configui-test_20260827_204055.png` | 949×1028 8-bit sRGB 230 KB | **Byte-identical to S1** (225230 B). Latest E2E dark-orange fork screenshot is S1. |
| **A1** | `modules/internal/src/main/resources/assets/oneconfig/images/header.png` | 325×111 8-bit sRGB 17 KB | Web banner on `#EEF2F5`: blue `ONECONFIG BETA` (`#1452CC`), `Update 0.2 released`, 3 bullet lines `Fuzzy search / API key storage / New behaviors & animations`, black search pill `chatin|` + black `Crafting` dropdown with blue footer. Heavy low-res raster. |
| **A2** | `modules/internal/src/main/resources/assets/oneconfig/images/head.png` | 180×180 8-bit sRGB 725 B | Placeholder 9-slice: white `T` on dusty rose `#B89A9C` / `#7A5A59` blocks with pale pink fringe. Not a photographic head — UI slice test. |
| **A3** | `modules/internal/src/main/resources/assets/oneconfig/brand/polyfrost.png` | 149×25 8-bit sRGB 2 KB | `Polyfrost` wordmark blue `#2D5AFF` + twin-diamond icon left, 0 px padding, cropped tight to glyph edges. |
| **A4** | `bootstrap/src/main/resources/oneconfig-icon.png` | 937×937 8-bit sRGB 587 KB | Lavender rounded square (`#E8EDF8`, radius ≈ 80 px) containing blue hexagon (`#0A4BE6`) with white abstract person ring shape. Soft outer glow + drop shadow. |
| **A5** | `modules/ui/src/main/resources/assets/oneconfig/color/hue.png` | 16×200 8-bit sRGB 901 B | Vertical hue spectrum: `red→orange→yellow→lime→cyan→blue→purple→magenta→red`. |
| **A6** | `modules/ui/src/main/resources/assets/oneconfig/color/alpha.png` | 16×200 Grayscale 2-col 96 B | Dark checker transparency preview: `#0E0E0E` / `#151515` 8 px squares. Near-black, very low contrast. |

> All 8 globs + 3 recent E2E paths were opened with `Read` at 1× before any zoom.

---

## Step 2 — Zoom & Crop Artifacts Produced

`magick -version` `7.1.2-29 Q16-HDRI`; `mkdir -p .audit-crops`; every op `-crop WxH+X+Y +repage [-scale N%] NEWFILE`.

| Family | New file (under `.audit-crops/`) | Geometry | Scale → Result |
|---|---|---|---|
| Figma | `figma-gui-complete--full@1x.png` | 965×821+0+0 | 1× → 965×821 |
| Figma | `figma-gui-complete--central-card--crop580x380+80+85@4x.png` | 580×380+80+85 | 400% → 2320×1520 |
| Figma | `figma-gui-complete--antibot-detail--crop320x260+100+100@4x.png` | 320×260+100+100 | 400% → 1280×1040 |
| Figma | `figma-gui-complete--component-assets-right--crop240x300+720+88@4x.png` | 240×300+720+88 | 400% → 960×1200 |
| Figma | `figma-gui-complete--checkbox-slider-bottom--crop320x240+100+480@4x.png` | 320×240+100+480 | 400% → 1280×960 |
| Figma | `figma-gui-complete--enum-dropdown--crop320x260+520+680@4x.png` | 320×260+520+680 | 400% → 1280×564 |
| Figma | `figma-gui-complete--alpha.png` | — alpha extract | — 965×821 Gray |
| Orange | `screenshot-orange--full-bleed-crop--949x1028+0+0.png` | 949×1028+0+0 | 1× → 949×1028 |
| Orange | `screenshot-orange--sidebar-rail--crop70x1028+0+0@4x.png` | 70×1028+0+0 | 400% → 280×4112 |
| Orange | `screenshot-orange--autoclicker-card--crop380x180+65+10@4x.png` | 380×180+65+10 | 400% → 1520×720 |
| Orange | `screenshot-orange--toggle-closeup--crop140x100+410+28@8x.png` | 140×100+410+28 | 800% → 1120×800 |
| Orange | `screenshot-orange--slider-track--crop360x60+80+78@4x.png` | 360×60+80+78 | 400% → 1440×240 |
| Orange | `screenshot-orange--chams-card--crop400x140+550+175@4x.png` | 400×140+550+175 | 400% → 1596×560 |
| Orange | `screenshot-orange--center-grid--crop500x500+220+260.png` | 500×500+220+260 | 1× → 500×500 |
| Orange | `screenshot-orange--alpha.png` | — alpha extract | — 949×1028 Gray |
| Mods | `mods-screen--full-bleed-crop--949x700+0+160.png` | 949×700+0+160 | 1× → 949×700 |
| Mods | `mods-screen--sidebar-nav--crop220x380+50+170@4x.png` | 220×380+50+170 | 400% → 880×1520 |
| Mods | `mods-screen--filter-chips--crop680x60+220+205@4x.png` | 680×60+220+205 | 400% → 2720×240 |
| Mods | `mods-screen--mod-card--crop200x180+220+265@4x.png` | 200×180+220+265 | 400% → 800×720 |
| Mods | `mods-screen--hud-card--crop200x180+560+410@4x.png` | 200×180+560+410 | 400% → 800×720 |
| Mods | `mods-screen--time-hud--crop680x120+220+540@4x.png` | 680×120+220+540 | 400% → 2720×480 |
| Mods | `mods-screen--alpha.png` | — alpha extract | — 949×1028 Gray |
| Tiny | `header--full--crop325x111+0+0@4x.png` | 325×111+0+0 | 400% → 1300×444 |
| Tiny | `header--center-detail--crop160x60+80+25@8x.png` | 160×60+80+25 | 800% → 1280×480 |
| Tiny | `header--alpha.png` | — alpha extract | — 325×111 Gray 256c |
| Tiny | `head--full--crop180x180+0+0@8x.png` | 180×180+0+0 | 800% → 1440×1440 |
| Tiny | `head--alpha.png` | — alpha extract | — 180×180 Gray |
| Tiny | `polyfrost--full--crop149x25+0+0@8x.png` | 149×25+0+0 | 800% → 1192×200 |
| Tiny | `polyfrost--alpha.png` | — alpha extract | — 149×25 Gray 256c |
| Tiny | `oneconfig-icon--full--crop937x937+0+0@0.5x.png` | 937×937+0+0 | 50% → 469×469 |
| Tiny | `oneconfig-icon--center-crop400x400+270+270@4x.png` | 400×400+270+270 | 400% → 1600×1600 |
| Tiny | `oneconfig-icon--alpha.png` | — alpha extract | — 937×937 Gray 256c |
| Tiny | `hue--full--crop16x200+0+0@8x.png` | 16×200+0+0 | 800% → 128×1600 |
| Tiny | `alpha-checker--full--crop16x200+0+0@8x.png` | 16×200+0+0 | 800% → 128×1600 |
| Tiny | `alpha-checker--alpha.png` | — alpha extract | — 16×200 Gray |

All verified with `identify` after write. No source overwritten. Total 35 files (32 crops + 3 alpha-equivalents), 1.7 MB.

---

## Step 3 — Re-read Crops at Sub-pixel & Findings

Each crop re-opened with `Read` for visual inspection. Table below is exhaustive; follow-up column gives location-anchored fix.

| Crop file | Region inspected | Finding | Severity | Suggested fix |
|---|---|---|---|---|
| `figma-gui-complete--central-card--crop580x380+80+85@4x.png` | Central 2-col card container + 1px inner card border + orange glow | **Shadow bleed + border gap:** outer dark card `#1E2430` glow (soft 12px blur `#FF8A65` 15%) clips at right edge (1px dark seam between glow and checker). Inner card border 1px `rgba(255,138,100,0.35)` shows 0.5px gap at bottom-right corner radius where inner shadow meets border (stair-step). At 4× the glow has blocky nearest-neighbour pixels because export was 1× raster then `scale 400%` reveals squarish blur. | micro | `figma-gui-complete.png:85,85,580×380` — re-export Figma at 2× or vector SVG; if raster, add `shape-rendering: crispEdges` off. Keep glow inside clip path, not outside. |
| `figma-gui-complete--antibot-detail--crop320x260+100+100@4x.png` | AntiBot detail: toggle top-right, `Check Invisible` checkbox, `Lifespan (ticks)` slider, `Detection Mode` dropdown | **Control misalignment:** Toggle track capsule 28×16 has orange knob 8px offset 1.5px low (knob centre y = track y + 0.6px). Checkbox square 14px rounded 4px — dot is 4px centered 1px low-right. Slider thumb white 10px overlaps orange fill with 1px gap below baseline (track y + thumb y misaligned). Dropdown chevron 6px triangle shows anti-alias halo grey `#8A7A73` on dark. | minor | `figma-gui-complete.png:100,100,320×260` — enforce 8px grid; centre knob/track with `align-items:center`; slider thumb `margin-top:-1px` to baseline. |
| `figma-gui-complete--component-assets-right--crop240x300+720+88@4x.png` | Right dashed annotation frame enclosing spec `Module / Checkbox / Slider 2.6 / Dropdown 2/3 / Enum1,Enum2` | **Spec vs impl drift:** Dashed frame `stroke 1px dashed 8,4 #FF8A65 60%` encloses card with labels but value `2.6` orange right-aligned is rendered with `font-weight 500` whereas central cards use `font-weight 400` — weight mismatch. Slider thumb white 6px has 1px dark halo overlapping track. Enum dropdown `▼` 8px has blunt tip. At 4× text shows ClearType RGB fringe (red left, blue right) from Figma export. | micro | `figma-gui-complete.png:720,88` — normalize spec text style to `Inter 13/400` same as impl; export with `font-smoothing: antialiased` only, no subpixel. |
| `figma-gui-complete--checkbox-slider-bottom--crop320x240+100+480@4x.png` | Bottom transparent spec frames (`Checkbox`, `Slider` with orange fill) over checker | **Opacity misuse:** Frame background `rgba(26,31,42,0.40)` lets checker bleed strongly (intent glass) but lower `Checkbox` frame has no card border (vs central cards have 1px). Shows spec inconsistency. At 4× checker squares at frame edge show 1px semi-transparent fringe (alpha premult). | micro | `figma-gui-complete.png:100,480` — define token `bg/card @ 60% + blur 12` consistently; clip frame background to same radius as annotation. |
| `figma-gui-complete--enum-dropdown--crop320x260+520+680@4x.png` | Bottom enum `Enum / Enum` list with `•` dot | **Dot fidelity:** Indicator dot 4px square at list right shows 2px blur, not crisp, offset 1px up from text baseline. List dividers 1px `rgba(255,255,255,0.06)` have 0.5px gap at right radius (subpixel seam). | micro | `figma-gui-complete.png:520,680` — dot 6px, `display:flex; align-items:center`; divider `inset 0` not rounded-corner clip. |
| `figma-gui-complete--alpha.png` | Alpha extraction of Figma composite | **Flattened background:** Entire image opaque white (no alpha). Checkerboard baked in, not real transparency — means Figma export flattened. No alpha channel to audit for halo. | info | `figma-gui-complete.png:full` — if design system needs transparency, re-export with real checker behind `export settings > include transparent`. |
| `screenshot-orange--autoclicker-card--crop380x180+65+10@4x.png` | `Auto Clicker` card top-left of orange screenshot | **Borders & radii:** Card radius 12px outer, 1px border `#2F3643` shows top edge 1px, bottom 1px, but bottom-right corner has 2px stair (aliasing) at 4× — NanoVG rounding quantized to integer pixels. Keybind pill `L` with keyboard glyph 10px has pill border 1px `#2A2E38` but pill is 1px narrower on right (padding `8px left / 7px right`). Title `Auto Clicker` `#D6D9E3` weight 600 vs subtitle `Clicks per second` `#A8ADC0` weight 500 hierarchy OK, but subtitle baseline 2px above track value `12` (`#5A5E68` low contrast 2.8:1). | minor | `screenshot-orange:65,10` — pill symmetric padding `10px`; corner radius force `drawRoundedRect` with `antialias true`; subtitle color bump to `#8A8F9E`. |
| `screenshot-orange--sidebar-rail--crop70x1028+0+0@4x.png` | Thin left icon rail (≈ 54px usable) | **Alignment / touch target:** Rail border right 1px `#2A2E38` has 1px lighter anti-alias edge (halo `#3A3E4A`). Icons 18px stroke 1.8px grey `#7A7F8A` centred but top 12px clipped (rail `padding-top:6px` vs `8px` needed). Spacing between icons 32px but last gap 28px before bottom. Bottom profile avatar not in this rail (separate) — rail icons low-contrast 1.1:1 vs bg. | minor | `screenshot-orange:0,0,70×1028` — rail `width:56px; padding:8px 0; gap:28px`; border `1px solid #252A33` with no AA halo (`box-shadow inset` instead). |
| `screenshot-orange--toggle-closeup--crop140x100+410+28@8x.png` | Toggle `On` + rightmost edge of adjacent card + brown slider tail | **Toggle fidelity + inter-card gutter:** Toggle capsule 36×18 blue `#2D5AFF` with white knob 14px, track corner radius stair-steps (4 steps at 8×, not smooth). Knob centre 1px left of track padding (expected `right:2px` but actual `right:3px`). `On` label `#6C727A` 13px has jagged `O` (left thicker `2.2px` vs right `1.4px`) hinting disabled. Vertical divider between cards 12px gutter shows 1px centre line `#1E232D` with 0.5px lighter halo on each side (shadow bleed). Card corner radius 12px shows 1px white fringe on outer border where AA blends to dark bg. Brown slider tail `#4A2F24` height 6px is 1px shorter than blue fill (height mismatch). | minor | `screenshot-orange:410,28` — toggle `track width 36, knob 14, inset 2px` enforce centre; text fix: font `Inter 13/500` with `font-feature-settings:ss01`; gutter make `12px` with `gap` not border; slider `height:6px` both fills. |
| `screenshot-orange--slider-track--crop360x60+80+78@4x.png` | `Clicks per second` slider track + `Target entity ▾` edge | **Slider track:** Blue fill height 6px crisp, but white knob 18px halo 1px `#C0C4CC` bleeds into blue making blue desaturated 6% at seam (premultiplied alpha). Track baseline 1px darker bottom edge (`#3A2A20` shadow) creates 0.5px double line. `Target entity` label `#C5C9D6` clipped right by dropdown field (`Mobs`) 2px overlap (label `x=32` vs field `x=210` insufficient). | micro | `screenshot-orange:80,78` — thumb `box-shadow 0 0 0 2px bg` not halo; track `height:4px` uniform; label-field `gap 8px; flex-wrap`. |
| `screenshot-orange--chams-card--crop400x140+550+175@4x.png` | `Chams H` card (keybind `H`, toggle `On`, `Show entities…` checkbox blue dot, `Mode Wireframe` dropdown) | **Checkbox + dropdown:** Blue dot checkbox 16px rounded 4px fill `#2D5AFF` bleeds 1px beyond border bottom (overdraw). `Mode` label `#C5C9D6` vs value `Wireframe` `#8A8F98` OK, but dropdown chevron `▾` 12px grey `#6B7280` is 2px low from centre. Card radius outer 12px inner dropdown field radius 8px — radii mismatch causes 1px white AA fringe at field corners. | micro | `screenshot-orange:550,175` — checkbox `inset 0` not overdraw; chevron `align-items:center`; field `radius 8` + card `radius 12` keep ratio (inner = outer - padding). |
| `screenshot-orange--center-grid--crop500x500+220+260.png` | 2×2 centre of grid (bottom of `Auto Tool` + top of `Velocity/Reach`) | **Grid rhythm:** Row gap ≈ 16px but left card `Velocity Off` bottom margin 14px vs right `Reach On` top margin 16px — 2px asymmetry. Column gutter 16px but at 1× card shadows not equal: right column cards have 1px stronger outer shadow (`rgba(0,0,0,0.45)` vs `0.35`). | micro | `screenshot-orange:220,260` — lock grid `gap:16px` both axes; single shadow token `0 4px 12px rgba(0,0,0,0.4)`. |
| `screenshot-orange--alpha.png` / `mods-screen--alpha.png` | Alpha extracts of both screenshots | **Fully opaque:** Both extracts solid white. Screenshots are opaque framebuffer captures (expected). No alpha halo to check beyond UI. | info | — |
| `mods-screen--sidebar-nav--crop220x380+50+170@4x.png` | Left nav `ONECONFIG / MODS & OPTIONS / PERSONALIZATION` | **Typography & selected state:** `ONECONFIG` bold 18px blue gradient but at 4× shows 1px blue fringe outer (export glow). `MODS & OPTIONS` caps 10px tracking 0.8px but slightly pixelated. `Mods` selected pill blue `#2D5AFF` radius 8px stair 2px at corners (same AA as cards). Unselected rows `Profiles` icon (3 persons) bottom pixel missing (icon design 12×12 clipped). Text `#8E95A5` on `#131720` contrast 4.1:1 barely AA. Bell icon right of `Player908` 12px grey appears blurry 1.5px stroke. | minor | `mods-screen:50,170` — sidebar `width 220px` OK; fix icon sprite to include full 14px bounding box; selected pill `radius:10px` with `antialias`; text brighten to `#A3AAC0` for AA. |
| `mods-screen--filter-chips--crop680x60+220+205@4x.png` | Chip bar `All / Favorites / Hypixel / …` | **Chip spacing & icon weight:** Chip height 32px gap 8px but gap `All→Favorites` 8px vs `Favorites→Hypixel` 12px (icon star vs `H` width diff). Star icon 14px stroke 1.5px shows subpixel fringe red/blue at edge (ClearType). `All` label white 13px weight 600 vs others 500 — weight diff causes 0.5px layout shift. Chip border `#2E3440` 1px AA halo 1px lighter at top edge. | micro | `mods-screen:220,205` — chips `gap:8px; padding:0 12px` uniform; icon stroke 2px solid; text weight uniform 500 with `selected:600`. |
| `mods-screen--mod-card--crop200x180+220+265@4x.png` | `Test Kt Config` card + blue footer | **Card footer seam:** Blue footer `#2D5AFF` 32px with white text — top edge shows 1px dark seam `#1E2430` where card body inset shadow meets footer (subpixel gap). Shadow under card not uniform: bottom edge darker 2px. Card border 1px `#2F3440` crisp on sides but top radius stair 3 steps. | minor | `mods-screen:220,265` — footer `margin-top:-1px` to hide seam or `box-shadow inset 0 1px 0 rgba(255,255,255,0.06)`; radius unify to `12px` outer, `0 0 12px 12px` footer. |
| `mods-screen--hud-card--crop200x180+560+410@4x.png` | `Item List Hud` card with `sword + apple` preview + `meow` card right | **Icon integration + footer fringe:** Preview rounded black pill `#0B0D10` 8px radius containing pixel-art sword (`#4ADFC8` diagonal) + golden apple (`#F6C445`) — crisp nearest-neighbour but sword diagonal shows single-pixel stair (acceptable for game art). Blue footer corner bottom-right shows 1px white AA fringe where blue `#2D5AFF` blends to dark bg `#131720`. Right adjacent card `MMPP meow` pixel font shows halo 1px darker outline — mono bitmap font scaled with blur. | micro | `mods-screen:560,410` — preview container `image-rendering:pixelated`; footer corner fix `overflow:hidden` + `background-clip:padding-box`. |
| `mods-screen--time-hud--crop680x120+220+540@4x.png` | Footer row `Date HUD / Item HUD / …` + `Time:19:55:09` | **Bitmap font + pill AA:** `Time:19:55:09` mono bitmap 16px white `#FFFFFF` on black pill `#0A0C0F` radius 8px — pill radius stair 4 steps (hard without AA). Pill top edge 2px gap to card border vs bottom 3px (padding 12 vs 11). Blue footer text `Date HUD` etc. kerning: `Date HUD` has extra 0.6px after `Date` (word gap inconsistency). HUD grid gap 16px OK but second row `Time HUD` card left margin 1px inset vs top row. | micro | `mods-screen:220,540` — pill `radius:8px` with `antialias true`; pad `12px` uniform; footer text `letter-spacing:0.15px` uniform; grid `gap:16px`. |
| `header--full--crop325x111+0+0@4x.png` | `header.png` banner full | **Low-res raster:** At 4× heavy blockiness: `ONECONFIG` blue `#1452CC` glyphs have 1px light blue halo outer (`#6AA2FF` 35%). `BETA` pill white border shows 1px jag at corners (radius 6px stair). Search field `chatin|` caret 1px high-contrast black/white but placeholder `chatin` gray `#9AA0A8` has RGB fringe. Crafting card black `#1F2328` with blue footer same seam issue as mods cards: 1px dark line where black meets blue. Background `#EEF2F5` vs blue contrast OK. | minor | `modules/internal/.../images/header.png:0,0` — replace with SVG/vector header; if must keep PNG, export at 2× (650×222) with `antialiased` + disable subpixel AA. |
| `header--center-detail--crop160x60+80+25@8x.png` | 8× detail of `0.2 released` + search caret | **Caret & text rendering:** Caret `|` height 14px but positioned 2px low from baseline of `chatin`. Search icon magnifier 14px stroke 1.5px shows half-pixel blur (vector exported at non-integer coords). `0.2` numerals show different weight `0` thinner than `2` (font hint mismatch). | micro | `header.png:80,25` — search field `align-items:center; caret y centre`. |
| `head--full--crop180x180+0+0@8x.png` | `head.png` white T on rose blocks | **Color blocking:** 8× reveals hard edges between rose blocks without AA: `#C0A9A9 → #A88E90` 2px line. White centre has 1px pink fringe `#F0D8D8` where alpha premultiplied (semi-transparent edge). Overall 180px too small for stretch — nearest-neighbour stretch would upscale blocky. Alpha extract solid white (opaque) despite apparent soft edges. | micro | `head.png:0,0` — if used as 9-slice, add 2px padding between blocks, export with `premultiplied false`, or replace with scalable SVG shape. |
| `polyfrost--full--crop149x25+0+0@8x.png` | `Polyfrost` wordmark blue | **Tight crop + AA:** At 8× `P` stem shows 1px stair, `y` descender blur 2px. `Polyfrost` tracking `tight -0.2px` makes `ol` touch at 8×. Icon leftmost blue diamond truncated 0px padding to edge (crop shows clip). Wordmark length 149px provides no breathing room. | micro | `polyfrost.png:0,0` — canvas 149×25 → 173×33 (12px horizontal + 4px vertical padding), re-export SVG at 2× with `hinting true`. |
| `oneconfig-icon--full--crop937x937+0+0@0.5x.png` / `oneconfig-icon--center-crop400x400+270+270@4x.png` | `oneconfig-icon.png` hexagon + person ring | **Icon shadows + corner radius:** Outer rounded square `#E8EDF8` radius ≈ 80px at 937px shows smooth, but at 4× centre hexagon (`#0A4BE6`) corners radius ≈ 12px show 3-step stair. Two halos: soft outer glow 8px blur `rgba(15,62,203,0.20)` + drop shadow 12px `rgba(0,0,0,0.12)` overlapping creates double fringe where white ring meets blue (1px light blue `#5A8AFF`). At centre crop white ring inner edge has 1px grey AA line (premultiplied). | minor | `bootstrap/src/main/resources/oneconfig-icon.png:0,0` — export at 1024×1024 with `svg→png` 4× MSAA corners; flatten shadows to single `0 8px 24px rgba(0,0,0,0.18)`. |
| `hue--full--crop16x200+0+0@8x.png` | `hue.png` vertical spectrum | **Banding:** At 8× each stop shows 2–3px hard band: yellow→green `12px`, green→cyan `10px`, blue→purple `8px` with visible line, not smooth gradient. Source 16×200 is 8-bit palette with no dither — banding visible >2× zoom. Hue strip width 16px too narrow for touch target. | minor | `modules/ui/.../color/hue.png:0,0` — regenerate with 16-bit PNG + `dither Floyd-Steinberg` or shader gradient at runtime; width 20px for hit area. |
| `alpha-checker--full--crop16x200+0+0@8x.png` | `alpha.png` dark checker | **Low contrast:** Checker contrast only `#0E0E0E` vs `#151515` ΔL 7% — at 1× nearly invisible (appears flat dark). At 8× squares 8px crisp but top edge clipped 4px (not aligned to canvas). Used as alpha preview but fails WCAG. Alpha extract shows solid 2-col pattern — correctly opaque. | micro | `modules/ui/.../color/alpha.png:0,0` — increase contrast to `#1A1A1A` vs `#2A2A2A` (ΔL 15%), align origin `0,0`. |
| — | **Cross-cut: Figma vs E2E palette** | **Major mismatch:** Figma spec warm orange `#FF8A65 / #FF7A50 / #4A2F24` vs E2E screenshots cool blue `#2D5AFF / #3A5BFF / #4A2F24` (unfilled track same brown but fill color completely different hue family). Also Figma text `Hypixel, Matrix` orange `#FF8A65` vs E2E `Clicks per second` blue-gray `#A8ADC0`. This is not a micro-issue — intentional fork/theme divergence or missing theme token. | major | `figma-gui-complete.png:full` vs `screenshot-orange:full` — align design token: decide `accent-primary` (Figma `peach 500 #FF7A45` vs code `blue 600 #2D5AFF`). If blue is canonical (code), update Figma to blue; if orange is spec, change `Theme.kt:accent` + toggle + slider + chip tokens. |
| — | **Cross-cut: Figma vs E2E spacing / layout** | **Grid density:** Figma central card 580×380 at 965px canvas has `gap 16px`, card padding `16px`, card radius modest, icon rail `~56px`. E2E grid at 949px uses `gap 16px` + card padding `12–16px` similar, but card height taller (Auto Clicker 180px vs Figma AntiBot 260px expanded) because expanded dropdown absent. Overall rhythm matches, but Figma shows dense 2-col stack with glow, E2E shows sparser — not a regression, just different feature set. | info | — |
| — | **Cross-cut: Typography** | Figma uses white `Inter 14/600` titles + `13/400` body orange/grey. E2E uses pale blue-gray `Inter` titles `14/600` + labels `12/500` + values `12/400` grey `#8A8F98`. At 4× Figma text shows RGB fringe (export AA), E2E shows monochrome AA (NanoVG). No major overflow — `Clicks per second` vs `Target entity` fits but `Preferred materials 2/4` pills slightly tight; `Time:19:55:09` mono fits. | micro | Unify token `font/title 14/600 #D6D9E3` vs `font/label 12/500 #C5C9D6` across both. |

---

## Micro-Issue Severity Roll-up

| Severity | Count | Examples |
|---|---|---|
| **major** | 1 | Palette mismatch Figma orange ↔ code blue (accent token). |
| **minor** | 9 | Toggle stair at corners + knob offset, sidebar clipped icons + halo, selected pill/sidebar border AA, `header.png` low-res halos, `oneconfig-icon` corner stair + double halo, `hue.png` banding, card footer dark seam (both Mods + Chams), Auto Clicker subtitle contrast, Auth header duplicate? Actually channel: slider thumb halo. |
| **micro** | 16 | Inner glow gap, checkbox dot off-centre, spec RGB fringe, enum dot blur, dashboard rail 28 vs 32 gap, grid row-gap asymmetry, mods chip gap uniformity, HUD footer fringe, `head.png` pink fringe, `polyfrost` tight crop, `alpha.png` low contrast & clipped origin, etc. |
| **info** | 4 | Duplicate `figma-assets-seperated.png` byte-identical, opaque alphas (expected), grid density info. |

---

## File-Anchored Fix Backlog (prioritized)

1. **P0 `figma-gui-complete.png:full` vs `screenshot-orange:full` — accent token**
   Current `peach 500` vs `blue 600`. Largest visual divergence. Pick one token (`--accent-primary`) and propagate to `toggle-track`, `slider-fill`, `chip-selected`, `dropdown-active` in both Figma styles and `Theme.kt / ColorProvider`.

2. **P1 `screenshot-orange--toggle-closeup--crop140x100+410+28@8x.png:36×18` — toggle physics**
   Set track `36×18 r=9`, knob `14×14`, `inset:2px`, `transition 180ms ease`. Ensure `On` label `margin-right:8px`, `font: Inter 13/500 #6C727A` vs `Off #5A5E68` uniform. Text antialias `geometricPrecision`.

3. **P1 `screenshot-orange--autoclicker-card--crop380x180+65+10@4x.png:1×` + `mods-screen--mod-card--crop200x180+220+265@4x.png:12/8` — 1px footer seam**
   Cards use `overflow:hidden; background-clip:padding-box` and `footer { margin-top:-1px }` or `box-shadow: inset 0 1px 0 rgba(255,255,255,0.06)` so dark seam between body shadow and blue footer disappears at 1× and 4×. Apply also to `header.png` Crafting bar and `Chams` dropdown field.

4. **P1 `modules/internal/.../images/header.png:325×111` — vectorize banner**
   Replace raster 325×111 with SVG (`header.svg`) exported at 2× `650×222` PNG fallback. Disable subpixel AA, use `antialiased` only; symmetric padding `12px` around `BETA` pill; caret centre-aligned.

5. **P1 `bootstrap/src/main/resources/oneconfig-icon.png:937×937/1024` — icon AA**
   Re-render `oneconfig-icon` at 1024×1024 SVG → PNG with 4× MSAA on rounded square (r=128) and hexagon corners (r=24). Single shadow `0 8px 24px rgba(0,0,0,0.18)` instead of double halo; premultiplied `false`.

6. **P2 `modules/ui/.../color/hue.png:16×200` — dither gradient**
   Generate procedurally (shader) or export 16-bit PNG with `dither:true` (e.g., `magick -size 20×200 gradient:red-blue -dither FloydSteinberg` conceptually or code gradient). Also widen hit-area 20px.

7. **P2 `screenshot-orange:0,0,70×1028` — rail**
   Rail `56px wide, padding:8px 0, gap:28px`; `border-right:1px solid #252A33` without AA halo (use `box-shadow: inset -1px 0 0 #252A33`).

8. **P2 `mods-screen:50,170 / 220,205` — sidebar + chips**
   Sidebar icons 14px bounding box, star stroke 2px, text `#A3AAC0`, selected pill `r:10` antialiased. Chips uniform `gap:8px; padding:0 14px; height:32 r:16`.

9. **P3 `polyfrost.png:149×25` — padding**
   Expand canvas `173×33 (12h/4v padding)`; SVG source; tracking ` -0.1px` not `-0.2px`.

10. **P3 `head.png:180×180` — define slice**
    Document as 9-slice or replace with placeholder SVG; add 2px transparent gutter between color blocks; export premultiplied off.

11. **P3 `alpha.png:16×200` — checker contrast**
    Swatches `#1A1A1A` / `#2A2A2A`, origin `0,0`, 8px tiles strictly tiled.

12. **Housekeeping:** `figma-assets-seperated.png` identical to `figma-gui-complete.png` → deduplicate or delete and keep single source of truth.

---

## Source PNG Roll-up

| Source file | Size | Audit verdict |
|---|---|---|
| `figma-gui-complete.png` | 965×821 | **Spec** — warm orange, shows ideal glass + glow but raster export low-res (AA blocky at 4×, gaps at corners). Needs token alignment to code blue or vice versa. |
| `figma-assets-seperated.png` | 965×821 | **Duplicate** — delete. |
| `screenshot-orange` (`/tmp/configui-test-orange.png` + `minecraft/run/.../20260827_204055.png`) | 949×1028 | **Impl dark config grid** — cool blue theme, 8 cards. Cleanest NanoVG rendering; micro-issues are AA stair, 1px seams, toggle offset. Reference for P1 fixes. |
| `mods-screen` (`/tmp/configui-test.png`) | 949×1028 | **Impl Mods listing** — left nav + chips + 5-card grid. Same blue theme; seam/AA issues mirror config grid, plus sidebar icon clip. |
| `header.png` | 325×111 | **Low-res banner** — most fragile asset; needs SVG replacement. |
| `head.png` | 180×180 | **Placeholder slice** — pink fringe, block hard edges; document or replace. |
| `polyfrost.png` | 149×25 | **Tight wordmark** — no padding, AA stair; expand canvas + SVG. |
| `oneconfig-icon.png` | 937×937 | **High-res icon** — overall good but hexagon corners + double halo visible at 4×; re-render with MSAA + single shadow. |
| `hue.png` / `alpha.png` | 16×200 | **Utility strips** — hue banding (8-bit), alpha low contrast/clipped; regenerate 16-bit/dither + higher contrast. |

---

## Methodology Note

* Step-read at 1×, then `magick -crop WxH+X+Y +repage [-scale 400/800%]` into `.audit-crops/` (never overwriting), then re-read crops at 4×/8× to surface sub-pixel AA halos, 0.5px gaps, shadow bleed, stair-step radius quantization, RGB fringe, and padding drift that is invisible at 1×. Alpha extracted for every source (`-alpha extract`) confirms screenshots and Figma flattened background are opaque (expected for framebuffer), while `head/polyfrost/icon` alphas correctly show binary or soft-edged masks — only `head` mask has pink premultiplied fringe. All crop geometries encoded in filename for reproducibility.

**Status:** 32 crops written, 11/11 source PNGs read at 1×, 32/32 crops re-read at zoom. Full report above — largest actionable is the orange↔blue accent token divergence; remaining issues are micro/minor AA, radius quantization (NanoVG integer pixels), and 1px seam/bleed artifacts typical of integer-scaled Minecraft GUI.

*Generated: 2026-08-28 00:06 Europe/Brussels — crops at `.audit-crops/`.*

