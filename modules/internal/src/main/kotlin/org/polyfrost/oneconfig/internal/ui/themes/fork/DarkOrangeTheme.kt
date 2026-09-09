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
 * Accent decision (F5 P0, re-tinted to reference): salmon #FF9676 is canonical.
 * Rationale: hex-sampled from Figma spec (dominant accent cluster #FF9676 with
 * AA ramps #E4876C → #B06A5A). Fills are warm plum/cocoa (#2B1F2A card,
 * #241C1C rail) — the reference reads "warm cocoa", never cold slate.
 *
 * The glass-vs-opaque decision is a single token swap via [Color.copy(alpha = ...)] on [pageBackground]
 * plus optional BlurRenderer backdrop usage — see task 2.1.4 / 2.3.1.
 */
val DarkOrangeTheme = UITheme(
    previewImage = "fork/dark-orange",
    name = "Dark Orange Fork",

    // Page / surface backgrounds — alpha controls glass-vs-opaque intent
    pageBackground = Color(0x66141013),        // ~40% opaque for glass + BlurRenderer backdrop
    sidebarBackground = Color(0x8B241C1C),     // semi-transparent cocoa rail fill
    chipBackground = Color(0x8B2E2129),        // semi-transparent warm chip / tag surface
    modCardBackground = Color(0xB02B1F2A),     // warm plum glass card interior (sampled reference)
    componentBackground = Color(0xCC251C22),   // warm control-row surface
    popupBackground = Color(0xCC251C22),       // warm dropdown / popup surface

    // Borders & text
    borderColor = Color(0x1AFFFFFF),           // 1px low-alpha border
    textColor = Color(0xFFFFF3EC),             // warm primary text
    textColorSecondary = Color(0xFF9A8B84),    // warm secondary / placeholder text
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
).withAccentColor(Color(0xFFFF9676))       // Figma salmon — sampled reference dominant cluster
  .withControlTrackColor(Color(0xFF5E3B32)) // dusty mauve-brown unfilled track matching salmon ramps
