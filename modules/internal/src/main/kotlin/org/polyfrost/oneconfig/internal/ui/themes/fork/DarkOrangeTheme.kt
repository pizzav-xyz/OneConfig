package org.polyfrost.oneconfig.internal.ui.themes.fork

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import org.polyfrost.oneconfig.internal.ui.themes.Radii as CoreRadii
import org.polyfrost.oneconfig.internal.ui.themes.UITheme
import org.polyfrost.oneconfig.internal.ui.themes.UIBranding
import org.polyfrost.oneconfig.internal.ui.themes.UITypography
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens

/**
 * Dark-orange design-token table and single [UITheme] instance for the Fork's config surface.
 *
 * Accent decision (F5 P0): orange #FF7A45 is canonical for this fork.
 * Rationale: (1) fork is named "Dark Orange" — orange IS the identity;
 * (2) Figma spec uses peach/orange family (#FF8A65/#FF7A50);
 * (3) controlTrackColor 0xFF5C3A2A is already warm/orange — accent must match;
 * (4) the base OneConfig blue #2D5AFF was leaking through ThemeConfig defaults, not fork-intentional.
 * Figma reference should be updated to match #FF7A45 as single source of truth.
 *
 * The glass-vs-opaque decision is a single token swap via [Color.copy(alpha = ...)] on [pageBackground]
 * plus optional BlurRenderer backdrop usage — see task 2.1.4 / 2.3.1.
 */
val DarkOrangeTheme = UITheme(
    previewImage = "fork/dark-orange",
    name = "Dark Orange Fork",

    // Page / surface backgrounds — alpha controls glass-vs-opaque intent
    pageBackground = Color(0x660F0F13),        // ~40% opaque for glass + BlurRenderer backdrop
    sidebarBackground = Color(0x8B151C22),     // semi-transparent rail fill
    chipBackground = Color(0x8B232D32),        // semi-transparent chip / tag surface
    modCardBackground = Color(0x96232D32),     // semi-transparent card interior
    componentBackground = Color(0xCC1A2229),   // control-row surface
    popupBackground = Color(0xCC1A2229),       // dropdown / popup surface

    // Borders & text
    borderColor = Color(0x1AFFFFFF),           // 1px low-alpha border
    textColor = Color(0xFFD5DBFF),             // primary text
    textColorSecondary = Color(0xFF757883),    // secondary / placeholder text
    accentTextColor = Color(0xFFFFFFFF),       // accent text (white on dark)

    // Shadow & controls
    shadowColor = Color(0xFF000000),
    controlThumbColor = Color(0xFFFFFFFF),

    shadowEnabled = true,

    // Shapes — radii from ForkTokens, the single source of truth
    backgroundShape = RoundedCornerShape(CoreRadii.LG),
    sideBarNavigationEntryShape = RoundedCornerShape(CoreRadii.SM),
    modCardShape = ForkTokens.cardShape,
    checkBoxShape = ForkTokens.controlShape,
    buttonShape = ForkTokens.pillShape,
    popupShape = RoundedCornerShape(CoreRadii.MD),
    circleShape = CircleShape,

    branding = UIBranding("assets/oneconfig/brand/oneconfig.svg"),
    typography = UITypography(
        family = FontFamily(
            Font("assets/oneconfig/fonts/Poppins/Poppins-Black.ttf", FontWeight.Black),
            Font("assets/oneconfig/fonts/Poppins/Poppins-ExtraBold.ttf", FontWeight.ExtraBold),
            Font("assets/oneconfig/fonts/Poppins/Poppins-ExtraLight.ttf", FontWeight.ExtraLight),
            Font("assets/oneconfig/fonts/Poppins/Poppins-Regular.ttf", FontWeight.Normal),
            Font("assets/oneconfig/fonts/Poppins/Poppins-Light.ttf", FontWeight.Light),
            Font("assets/oneconfig/fonts/Poppins/Poppins-Medium.ttf", FontWeight.Medium),
            Font("assets/oneconfig/fonts/Poppins/Poppins-SemiBold.ttf", FontWeight.SemiBold),
            Font("assets/oneconfig/fonts/Poppins/Poppins-Thin.ttf", FontWeight.Thin)
        )
    )
).withAccentColor(Color(0xFFFF7A45))       // Figma peach 500 — canonical accent for dark-orange fork
  .withControlTrackColor(Color(0xFF5C3A2A)) // matching warm-orange unfilled track
