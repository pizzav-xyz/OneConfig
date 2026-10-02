package org.polyfrost.oneconfig.internal.ui.layout.fork

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.apache.logging.log4j.LogManager
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.ConfigManager
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.internal.OneConfigConfig
import org.polyfrost.oneconfig.internal.ThemeConfig
import org.polyfrost.oneconfig.internal.ui.api.ConfigRegistry
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry
import org.polyfrost.oneconfig.internal.ui.themes.updateAccent
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Fork-owned appearance preferences: accent colour (themes.json), UI scale
 * (oneconfig.json) and theme selection. Same static-fallback + explicit-save
 * shape as [RealOneConfigModule]: the Tree is the source of truth when
 * present, statics cover a registry miss, and every writer saves explicitly
 * (production screen-close also saves, but nothing here depends on it).
 */
object ForkAppearance {
    const val TEST_KEY = "appearance"
    const val THEMES_TREE = "themes.json"
    const val CONFIG_TREE = "oneconfig.json"
    const val ACCENT_PROP = "accentColor"
    const val CUSTOM_SIZE_PROP = "useCustomUiSize"
    const val PIXEL_SIZE_PROP = "uiPixelSize"

    val FALLBACK_ACCENT = Color(0xFFFF9676)

    @JvmField
    val FALLBACK_ACCENT_ARGB = 0xFFFF9676.toInt()

    private val logger = LogManager.getLogger("OneConfig/ForkAppearance")
    private val reapplied = AtomicBoolean(false)

    fun themesTree(): Tree? =
        runCatching { ConfigRegistry.findTree(THEMES_TREE) }.getOrNull()

    fun configTree(): Tree? =
        runCatching { ConfigRegistry.findTree(CONFIG_TREE) }.getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun readAccent(tree: Tree?): Color {
        val raw = runCatching { tree?.getProp(ACCENT_PROP)?.getAs<Any>() }.getOrNull()
        val poly = raw as? PolyColor
            ?: return ThemeRegistry.activeTheme?.accentColor ?: FALLBACK_ACCENT
        return Color(poly.argb)
    }

    fun readAccentArgb(tree: Tree?): Int = readAccent(tree).toArgb()

    fun writeAccentArgb(tree: Tree?, argb: Int) = writeAccent(tree, Color(argb))

    fun writeAccent(tree: Tree?, color: Color) {
        runCatching { (tree?.getProp(ACCENT_PROP) as? Property<Any>)?.setAs(PolyColor(color.toArgb())) }
        ThemeConfig.accentColor = PolyColor(color.toArgb())
        applyAccent(color)
        saveThemes()
        ForkTestHooks.record("accent:$TEST_KEY=${color.toArgb().toUInt().toString(16)}")
    }

    fun applyAccent(color: Color) {
        val current = ThemeRegistry.activeTheme ?: return
        if (current.accentColor != color) {
            ThemeRegistry.activeTheme = current.withAccentColor(color)
        }
        updateAccent()
    }

    fun syncAccentFromStore() {
        val live = readAccent(themesTree())
        ThemeConfig.accentColor = PolyColor(live.toArgb())
        applyAccent(live)
    }

    fun reapplyPersistedAccent() {
        if (!reapplied.compareAndSet(false, true)) return
        val persisted = readAccent(themesTree())
        if (ThemeRegistry.activeTheme?.accentColor != persisted) {
            applyAccent(persisted)
            logger.info("fork-appearance: re-applied persisted accent after (re)start")
        }
    }

    fun saveThemes() {
        runCatching { ConfigManager.active().save(THEMES_TREE) }
            .onFailure { logger.warn("fork-appearance: save themes.json failed", it) }
    }

    @Suppress("UNCHECKED_CAST")
    fun readCustomSize(tree: Tree?): Boolean {
        val raw = runCatching { tree?.getProp(CUSTOM_SIZE_PROP)?.getAs<Any>() }.getOrNull()
        return (raw as? Boolean) ?: OneConfigConfig.useCustomUiSize
    }

    @Suppress("UNCHECKED_CAST")
    fun readPixelSize(tree: Tree?): Float {
        val raw = runCatching { tree?.getProp(PIXEL_SIZE_PROP)?.getAs<Any>() }.getOrNull()
        return (raw as? Number)?.toFloat() ?: OneConfigConfig.uiPixelSize
    }

    fun writeScale(tree: Tree?, enabled: Boolean, size: Float) {
        runCatching { (tree?.getProp(CUSTOM_SIZE_PROP) as? Property<Any>)?.setAs(enabled) }
        runCatching { (tree?.getProp(PIXEL_SIZE_PROP) as? Property<Any>)?.setAs(size) }
        OneConfigConfig.useCustomUiSize = enabled
        OneConfigConfig.uiPixelSize = size
        saveConfig()
        ForkTestHooks.record("slider:$TEST_KEY-scale=$size")
    }

    fun saveConfig() {
        runCatching {
            OneConfigConfig.INSTANCE?.save()
                ?: treeSave(CONFIG_TREE)
        }.onFailure { logger.warn("fork-appearance: save oneconfig.json failed", it) }
    }

    private fun treeSave(treeId: String) {
        val tree = when (treeId) {
            CONFIG_TREE -> configTree()
            else -> null
        } ?: return
        ConfigManager.active().save(tree)
    }

    fun themeNames(): List<String> = ThemeRegistry.registry.map { it.name }

    fun activateTheme(name: String): Boolean {
        val theme = ThemeRegistry.registry.find { it.name == name } ?: return false
        ThemeRegistry.activate(theme)
        return true
    }
}
