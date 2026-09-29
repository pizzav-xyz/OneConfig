package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.apache.logging.log4j.LogManager
import org.polyfrost.oneconfig.api.config.v1.ConfigManager
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.internal.OneConfigConfig
import org.polyfrost.oneconfig.internal.ui.api.ConfigRegistry
import org.polyfrost.oneconfig.internal.ui.components.Text
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkSliderRow
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkToggle
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTokens
import org.polyfrost.oneconfig.internal.ui.themes.LocalTheme

/**
 * First REAL (non-mock) fork module: OneConfig Preferences (`oneconfig.json`).
 *
 * Loads the live [Tree] by stable id from [ConfigRegistry] (which itself mirrors
 * [ConfigManager.active]), bypassing the upstream mod-card hide filter by direct
 * id lookup — the global `hiddenModCardIds` / `shouldShowModCard` behavior is
 * intentionally left unchanged (fork-scoped exemption only).
 *
 * Card header toggle <-> `enableBackgroundBlur`, body slider <->
 * `pageOpacity`. Writes go through the live [Property.setAs] plus the static
 * field mirror, then persist via `OneConfigConfig.INSTANCE.save()` (falling back
 * to `ConfigManager.active().save(tree)`). No shadow state, no test hook
 * required for loading.
 */
object RealOneConfigModule {
    const val TREE_ID = "oneconfig.json"
    const val TEST_KEY = "real-oneconfig"
    const val BLUR_PROP = "enableBackgroundBlur"
    const val OPACITY_PROP = "pageOpacity"

    private val logger = LogManager.getLogger("OneConfig/RealOneConfigModule")

    fun resolveTree(): Tree? = try {
        ConfigRegistry.findTree(TREE_ID)
    } catch (e: Throwable) {
        logger.warn("real-module: ConfigRegistry.findTree failed", e)
        null
    }

    @Suppress("UNCHECKED_CAST")
    fun readBlur(tree: Tree?): Boolean {
        try {
            val prop = tree?.getProp(BLUR_PROP) ?: return OneConfigConfig.enableBackgroundBlur
            return (prop as Property<Boolean>).getAs<Boolean>() ?: OneConfigConfig.enableBackgroundBlur
        } catch (e: Throwable) {
            return OneConfigConfig.enableBackgroundBlur
        }
    }

    fun readOpacity(tree: Tree?): Float {
        try {
            val prop = tree?.getProp(OPACITY_PROP) ?: return OneConfigConfig.pageOpacity
            val v = (prop as Property<*>).getAs<Any>() as? Number
                ?: return OneConfigConfig.pageOpacity
            return v.toFloat()
        } catch (e: Throwable) {
            return OneConfigConfig.pageOpacity
        }
    }

    fun writeBlur(tree: Tree?, value: Boolean) {
        try {
            (tree?.getProp(BLUR_PROP) as? Property<Boolean>)?.setAs(value)
        } catch (e: Throwable) {
            logger.warn("real-module: setAs blur failed", e)
        }
        OneConfigConfig.enableBackgroundBlur = value
        persist(tree)
        ForkTestHooks.record("toggle:real-oneconfig=$value")
    }

    fun writeOpacity(tree: Tree?, value: Float) {
        try {
            @Suppress("UNCHECKED_CAST")
            (tree?.getProp(OPACITY_PROP) as? Property<Number>)?.setAs(value)
        } catch (e: Throwable) {
            logger.warn("real-module: setAs opacity failed", e)
        }
        OneConfigConfig.pageOpacity = value
        persist(tree)
        ForkTestHooks.record("slider:real-oneconfig=$value")
    }

    fun persist(tree: Tree?) {
        try {
            val instance = OneConfigConfig.INSTANCE
            if (instance != null) {
                instance.save()
                return
            }
        } catch (e: Throwable) {
            logger.warn("real-module: INSTANCE.save failed, falling back", e)
        }
        try {
            if (tree != null) ConfigManager.active().save(tree)
        } catch (e: Throwable) {
            logger.warn("real-module: ConfigManager.save failed", e)
        }
    }
}

@Composable
fun RealOneConfigCard(modifier: Modifier = Modifier) {
    val theme = LocalTheme.current
    val tree = remember { RealOneConfigModule.resolveTree() }
    var blur by remember { mutableStateOf(RealOneConfigModule.readBlur(tree)) }
    var opacity by remember { mutableStateOf(RealOneConfigModule.readOpacity(tree)) }

    LaunchedEffect(tree) {
        blur = RealOneConfigModule.readBlur(tree)
        opacity = RealOneConfigModule.readOpacity(tree)
    }

    if (tree == null) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                "OneConfig preferences unavailable (registry miss)",
                color = theme.textColorSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        return
    }

    ModuleCard(
        title = "OneConfig",
        keybind = null,
        onKeybindCapture = { ForkTestHooks.record("keybind:real-oneconfig=$it") },
        onKeybindCancel = { ForkTestHooks.record("keybind-cancel:real-oneconfig") },
        enabled = blur,
        onEnabledChange = {
            blur = it
            RealOneConfigModule.writeBlur(tree, it)
        },
        testKey = RealOneConfigModule.TEST_KEY,
        body = {
            ForkSettingRow(
                label = "Background blur",
                value = "oneconfig.json · real registry",
                control = {
                    ForkToggle(
                        checked = blur,
                        onCheckedChange = {
                            blur = it
                            RealOneConfigModule.writeBlur(tree, it)
                        },
                        testKey = "real-oneconfig-blur-row",
                    )
                },
            )
            ForkSliderRow(
                label = "Page opacity",
                valueText = "${opacity.toInt()}",
                value = opacity,
                onValueChange = {
                    opacity = it
                    RealOneConfigModule.writeOpacity(tree, it)
                },
                min = 0f,
                max = 100f,
                step = 1f,
                testKey = "real-oneconfig-opacity",
            )
            Text(
                "Loaded via ConfigRegistry.findTree(oneconfig.json)",
                color = theme.textColorSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = ForkTokens.Padding.controlRowVertical),
            )
        },
        modifier = modifier,
    )
}
