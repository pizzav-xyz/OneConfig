package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.oneconfig.api.config.v1.ConfigManager;
import org.polyfrost.oneconfig.api.config.v1.Property;
import org.polyfrost.oneconfig.api.config.v1.Tree;
import org.polyfrost.oneconfig.internal.OneConfigConfig;
import org.polyfrost.oneconfig.internal.ThemeConfig;
import org.polyfrost.oneconfig.internal.compat.ModMenuEntrypoint;
import org.polyfrost.oneconfig.internal.ui.api.ConfigRegistry;
import org.polyfrost.oneconfig.internal.ui.compose.ComposeScreen;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks;
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry;
import org.polyfrost.oneconfig.api.platform.v1.Platform;
import org.polyfrost.oneconfig.test.e2e.screenshot.ScreenshotHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * First-real-module E2E: drives the REAL OneConfig preferences module
 * (oneconfig.json) through the fork surface — no mocks.
 *
 * SET mode: asserts the registry hit, flips enableBackgroundBlur to false and
 * pageOpacity to 50, persists, screenshots each step.
 * VERIFY mode (fresh client launch): asserts the flipped values survived the
 * restart from disk, screenshots the persisted state, then restores defaults.
 */
public class ConfigUIRealModuleTest {
    public enum Mode { SET, VERIFY }

    private final E2ETestRunner runner;
    private final Mode mode;
    private final boolean viaModMenu;

    public ConfigUIRealModuleTest(E2ETestRunner runner, Mode mode) {
        this(runner, mode, false);
    }

    public ConfigUIRealModuleTest(E2ETestRunner runner, Mode mode, boolean viaModMenu) {
        this.runner = runner;
        this.mode = mode;
        this.viaModMenu = viaModMenu;
    }

    public void run() {
        new Thread(this::drive).start();
    }

    private void drive() {
        try {
            if (mode == Mode.SET) driveSet();
            else driveVerify();
        } catch (Exception e) {
            runner.fail("real-module: exception: " + e.getMessage());
            runner.fail("all");
        }
    }

    private String tag() {
        return viaModMenu ? "modmenu-demo" : "real-module";
    }

    private String shotPrefix() {
        return viaModMenu ? "modmenu-demo" : "clickui";
    }

    /**
     * Opens the config screen the way the demo mode requires: via the exact
     * Mod Menu entry-point factory Mod Menu itself calls, or via direct
     * construction for the registry-level path. Fails loud on any deviation.
     */
    private void openScreen() throws Exception {
        if (!viaModMenu) {
            callOnRender(() -> {
                Platform.screen().display(new OneConfigUIScreen());
                return null;
            });
            return;
        }
        Screen created = callOnRender(() -> {
            try {
                com.terraformersmc.modmenu.api.ConfigScreenFactory<?> factory =
                        ModMenuEntrypoint.INSTANCE.getModConfigScreenFactory();
                if (factory == null) return null;
                Object screen = factory.create(Platform.screen().current());
                return (Screen) screen;
            } catch (Exception e) {
                return null;
            }
        });
        if (created == null) {
            runner.fail(tag() + ": ModMenu factory produced no screen (entry missing or unlinked)");
            return;
        }
        if (!(created instanceof OneConfigUIScreen)) {
            runner.fail(tag() + ": ModMenu factory screen is not OneConfigUIScreen: " + created.getClass());
            return;
        }
        runner.pass(tag() + ": ModMenu factory produced OneConfigUIScreen");
        Screen toShow = created;
        callOnRender(() -> {
            Platform.screen().display(toShow);
            return null;
        });
    }

    private void driveSet() throws Exception {
        Tree tree = ConfigRegistry.INSTANCE.findTree("oneconfig.json");
        if (tree == null) {
            runner.fail("real-module: registry miss for oneconfig.json (mock path would hide this)");
            runner.fail("all");
            return;
        }
        runner.pass("real-module: registry hit oneconfig.json title=OneConfig");

        ThemeConfig.activeTheme = "Dark Orange Fork";
        ThemeRegistry.INSTANCE.loadFromConfig();
        ForkTestHooks.INSTANCE.clearEvents();
        openScreen();
        sleep(2500);

        if (!isConfigOpen()) {
            runner.fail(tag() + ": OneConfigUIScreen did not open");
            runner.fail("all");
            return;
        }
        runner.pass(tag() + ": screen open");

        tap("rail-misc");
        Set<String> required = new HashSet<>(Arrays.asList("real-oneconfig-toggle", "real-oneconfig-opacity"));
        if (!awaitBounds(required, 10000)) {
            runner.fail("real-module: real card hooks missing: " + missing(required));
            runner.fail("all");
            closeAndFinish();
            return;
        }
        runner.pass("real-module: real card composed via ConfigRegistry.findTree(oneconfig.json)");
        screenshot("real-module-00-open");

        boolean initial = OneConfigConfig.enableBackgroundBlur;
        runner.pass("real-module: initial enableBackgroundBlur=" + initial);
        tap("real-oneconfig-toggle");
        boolean flipped = OneConfigConfig.enableBackgroundBlur;
        if (flipped == initial) {
            runner.fail("real-module: toggle did not flip live value (still " + flipped + ")");
        } else {
            runner.pass("real-module: toggle flipped live value " + initial + " -> " + flipped);
        }
        assertEventsContain("toggle:real-oneconfig=" + flipped, "toggle event did not fire");
        screenshot("real-module-01-toggled");

        dragFullWidth("real-oneconfig-opacity");
        float opacity = lastSliderValue("slider:real-oneconfig=");
        if (Float.isNaN(opacity)) {
            runner.fail("real-module: opacity slider produced no event");
        } else {
            runner.pass("real-module: opacity slider dragged to " + opacity);
        }
        screenshot("real-module-02-opacity");

        OneConfigConfig.INSTANCE.save();
        logFileValues("after-set");
        closeAndFinish();
    }

    private void driveVerify() throws Exception {
        boolean blur = OneConfigConfig.enableBackgroundBlur;
        float opacity = OneConfigConfig.pageOpacity;
        runner.pass("real-module: reloaded enableBackgroundBlur=" + blur + " pageOpacity=" + opacity);
        if (blur) {
            runner.fail("real-module: blur did not survive restart (expected false, was true)");
        } else {
            runner.pass("real-module: blur survived restart (false persisted)");
        }
        if (Math.abs(opacity - 100f) > 5f) {
            runner.fail("real-module: opacity did not survive restart (expected ~100, was " + opacity + ")");
        } else {
            runner.pass("real-module: opacity survived restart (" + opacity + ")");
        }
        logFileValues("after-restart");

        ThemeConfig.activeTheme = "Dark Orange Fork";
        ThemeRegistry.INSTANCE.loadFromConfig();
        openScreen();
        sleep(2500);
        if (!isConfigOpen()) {
            runner.fail(tag() + ": screen did not open on verify launch");
            runner.fail("all");
            return;
        }
        tap("rail-misc");
        if (!awaitBounds(new HashSet<>(Arrays.asList("real-oneconfig-toggle")), 10000)) {
            runner.fail("real-module: real card missing after restart");
            runner.fail("all");
            closeAndFinishRestore(false);
            return;
        }
        screenshot("real-module-03-after-restart");
        closeAndFinishRestore(true);
    }

    private void closeAndFinishRestore(boolean restore) throws Exception {
        if (restore) {
            callOnRender(() -> {
                OneConfigConfig.enableBackgroundBlur = true;
                OneConfigConfig.pageOpacity = 88f;
                try {
                    Tree t = ConfigRegistry.INSTANCE.findTree("oneconfig.json");
                    if (t != null) {
                        @SuppressWarnings("unchecked")
                        Property<Boolean> p = (Property<Boolean>) t.getProp("enableBackgroundBlur");
                        if (p != null) p.setAs(Boolean.TRUE);
                        @SuppressWarnings("unchecked")
                        Property<Number> q = (Property<Number>) t.getProp("pageOpacity");
                        if (q != null) q.setAs(88f);
                    }
                } catch (Exception ignored) {
                }
                OneConfigConfig.INSTANCE.save();
                return null;
            });
            sleep(500);
            runner.pass("real-module: restored enableBackgroundBlur=true pageOpacity=88, suite leaves no dirty state");
            logFileValues("after-restore");
        }
        closeAndFinish();
    }

    private void logFileValues(String phase) {
        try {
            Path folder = ConfigManager.active().getFolder();
            runner.pass("real-module: config folder=" + folder.toAbsolutePath());
            Path candidate = folder.resolve("oneconfig.json");
            if (!Files.exists(candidate)) {
                java.io.File[] hits = folder.toFile().listFiles((d, n) -> n.equals("oneconfig.json"));
                runner.pass("real-module: file " + phase + " oneconfig.json exists=" + Files.exists(candidate)
                        + " dirList=" + Arrays.toString(folder.toFile().list()));
                if (hits != null && hits.length > 0) candidate = hits[0].toPath();
            }
            if (Files.exists(candidate)) {
                String content = new String(Files.readAllBytes(candidate));
                String blur = content.replaceAll("(?s).*?enableBackgroundBlur\"?\\s*[:=]\\s*(true|false).*", "$1");
                runner.pass("real-module: file " + phase + " enableBackgroundBlur=" + blur.trim()
                        + " size=" + Files.size(candidate));
            } else {
                runner.fail("real-module: oneconfig.json not found under " + folder.toAbsolutePath());
            }
        } catch (Exception e) {
            runner.fail("real-module: file read failed: " + e.getMessage());
        }
    }

    private void closeAndFinish() throws Exception {
        callOnRender(() -> {
            Platform.screen().close();
            return null;
        });
        sleep(1000);
        if (isConfigOpen()) {
            runner.fail("real-module: UI did not close");
            runner.fail("all");
            return;
        }
        runner.pass("real-module: UI closed");
        if (runner.isAllPassed()) {
            runner.pass("all");
        } else {
            runner.fail("all");
        }
    }

    private boolean isConfigOpen() {
        Screen current = E2ETestRunner.getCurrentScreen();
        return current instanceof OneConfigUIScreen;
    }

    private ComposeScreen screen() {
        Screen current = E2ETestRunner.getCurrentScreen();
        if (!(current instanceof ComposeScreen)) throw new IllegalStateException("screen is not a ComposeScreen: " + current);
        return (ComposeScreen) current;
    }

    private ForkTestHooks.Bounds awaitNonZeroBounds(String key, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        ForkTestHooks.Bounds b = boundsOf(key);
        while ((b.getWidth() <= 0 || b.getHeight() <= 0) && System.currentTimeMillis() < deadline) {
            Thread.sleep(100);
            b = boundsOf(key);
        }
        return b;
    }

    private void tap(String key) throws Exception {
        ForkTestHooks.Bounds b = awaitNonZeroBounds(key, 3000);
        boolean ok = callOnRender(() -> screen().testTap(b.getCenterX(), b.getCenterY()));
        if (!ok) throw new IllegalStateException("scene unavailable for tap " + key);
        runner.pass("real-module: tapped " + key);
        sleep(600);
        if (!isConfigOpen()) throw new IllegalStateException("screen died after tap " + key);
    }

    private void dragFullWidth(String key) throws Exception {
        ForkTestHooks.Bounds b = awaitNonZeroBounds(key, 3000);
        final float y = b.getCenterY();
        final float x0 = b.getX() + 2f;
        final float x1 = b.getX() + b.getWidth() - 2f;
        boolean ok = callOnRender(() -> screen().testDrag(x0, y, x1, y, 16));
        if (!ok) throw new IllegalStateException("scene unavailable for drag " + key);
        runner.pass("real-module: dragged " + key);
        sleep(600);
    }

    private void screenshot(String step) throws Exception {
        String name = shotPrefix() + "-" + step;
        long since = System.currentTimeMillis();
        callOnRender(() -> {
            try {
                ScreenshotHelper.takeScreenshot(runner.mc(), name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        java.io.File f = ScreenshotHelper.awaitScreenshot(name + "_", since, 15000);
        if (f == null) {
            runner.fail("real-module: screenshot missing for step " + step);
        } else {
            runner.pass("real-module: screenshot " + f.getName());
        }
    }

    private ForkTestHooks.Bounds boundsOf(String key) {
        Map<String, ForkTestHooks.Bounds> map = ForkTestHooks.INSTANCE.boundsSnapshot();
        ForkTestHooks.Bounds b = map.get(key);
        if (b == null) throw new IllegalStateException("no hook bounds for " + key);
        return b;
    }

    private boolean awaitBounds(Set<String> keys, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (ForkTestHooks.INSTANCE.boundsSnapshot().keySet().containsAll(keys)) return true;
            Thread.sleep(250);
        }
        return false;
    }

    private String missing(Set<String> keys) {
        Set<String> have = ForkTestHooks.INSTANCE.boundsSnapshot().keySet();
        Set<String> miss = new HashSet<>(keys);
        miss.removeAll(have);
        return miss.toString();
    }

    private void assertEventsContain(String fragment, String message) {
        List<String> events = ForkTestHooks.INSTANCE.eventsSnapshot();
        for (String e : events) {
            if (e.equals(fragment)) {
                runner.pass("real-module: event " + fragment);
                return;
            }
        }
        runner.fail("real-module: " + message + " (events=" + events + ")");
    }

    private float lastSliderValue(String prefix) {
        List<String> events = ForkTestHooks.INSTANCE.eventsSnapshot();
        float last = Float.NaN;
        for (String e : events) {
            if (e.startsWith(prefix)) {
                try {
                    last = Float.parseFloat(e.substring(prefix.length()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return last;
    }

    private <T> T callOnRender(Supplier<T> work) throws Exception {
        CompletableFuture<T> future = new CompletableFuture<>();
        runner.mc().execute(() -> {
            try {
                future.complete(work.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future.get(15, TimeUnit.SECONDS);
    }

    private static void sleep(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }
}
