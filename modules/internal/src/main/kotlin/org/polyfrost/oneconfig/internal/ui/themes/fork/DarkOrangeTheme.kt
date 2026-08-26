package org.polyfrost.oneconfig.internal.ui.themes.fork

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.internal.ui.themes.Radii as CoreRadii
import org.polyfrost.oneconfig.internal.ui.themes.UITheme
import org.polyfrost.oneconfig.internal.ui.themes.UIBranding
import org.polyfrost.oneconfig.internal.ui.themes.UITypography

/**
 * Token radii specific to the Fork's dark-orange surface.
 *
 * Kept separate from [CoreRadii] so the Fork can tune its own card/control/pill values without
 * changing the upstream radius scale.
 */
object ForkRadii {
    val card = 12.dp
    val control = 6.dp
    val pill = 8.dp
}

/**
 * Dark-orange design-token table and single [UITheme] instance for the Fork's config surface.
 *
 * Colors are estimated from the Figma mock; final hex values are pending opaque Figma re-export.
 * The glass-vs-opaque decision is a single token swap via [Color.copy(alpha = ...)] on [pageBackground]
 * plus optional BlurRenderer backdrop usage — see task 2.1.4 / 2.3.1.
 */
val DarkOrangeTheme = UITheme(
    previewImage = "fork/dark-orange",
    name = "Dark Orange Fork",

    // Page / surface backgrounds — alpha controls glass-vs-opaque intent
    pageBackground = Color(0xBF0F0F13),        // ~75% opaque; drop to ~40% for glass + BlurRenderer
    sidebarBackground = Color(0xB3151C22),     // sidebar rail fill
    chipBackground = Color(0xB2232D32),        // chip / tag surface
    modCardBackground = Color(0x59232D32),     // card interior
    componentBackground = Color(0xFF1A2229),   // control-row surface
    popupBackground = Color(0xFF1A2229),       // dropdown / popup surface

    // Borders & text
    borderColor = Color(0x1AFFFFFF),           // 1px low-alpha border
    textColor = Color(0xFFD5DBFF),             // primary text
    textColorSecondary = Color(0xFF757883),    // secondary / placeholder text
    accentTextColor = Color(0xFFFFFFFF),       // accent text (white on dark)

    // Shadow & controls
    shadowColor = Color(0xFF000000),
    controlThumbColor = Color(0xFFFFFFFF),

    shadowEnabled = true,

    // Shapes — radii per token table
    backgroundShape = RoundedCornerShape(CoreRadii.LG),
    sideBarNavigationEntryShape = RoundedCornerShape(CoreRadii.SM),
    modCardShape = RoundedCornerShape(ForkRadii.card),
    checkBoxShape = RoundedCornerShape(ForkRadii.control),
    buttonShape = RoundedCornerShape(ForkRadii.pill),
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
).withControlTrackColor(Color(0xFF5C3A2A))    // dark-orange track fill for sliders / toggles
