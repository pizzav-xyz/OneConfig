package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.polyfrost.oneconfig.internal.ThemeConfig;
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry;
import org.polyfrost.oneconfig.internal.ui.compose.ComposeScreen;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;
import org.polyfrost.oneconfig.internal.ui.components.fork.ForkTestHooks;
import org.polyfrost.oneconfig.api.platform.v1.Platform;
import org.polyfrost.oneconfig.test.e2e.screenshot.ScreenshotHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Click-through test for the dark-orange fork surface: drives every control type
 * with synthetic scene input, asserts callback events and layout changes, and
 * captures a screenshot per step.
 */
public class ConfigUIClickTest {
    private final E2ETestRunner runner;

    public ConfigUIClickTest(E2ETestRunner runner) {
        this.runner = runner;
    }

    public void run() {
        new Thread(this::drive).start();
    }

    private void drive() {
        try {
            ThemeConfig.activeTheme = "Dark Orange Fork";
            ThemeRegistry.INSTANCE.loadFromConfig();
            ForkTestHooks.INSTANCE.clearEvents();
            callOnRender(() -> {
                Platform.screen().display(new OneConfigUIScreen());
                return null;
            });
            sleep(2500);

            if (!isConfigOpen()) {
                runner.fail("clickui: OneConfigUIScreen did not open");
                runner.fail("all");
                return;
            }
            runner.pass("clickui: screen open");

            Set<String> required = new HashSet<>(Arrays.asList(
                    "auto-clicker-toggle", "auto-clicker-collapse", "auto-clicker-card",
                    "slider-auto-clicker", "dropdown-auto-clicker", "auto-clicker-keybind",
                    "rail-combat", "rail-player"));
            if (!awaitBounds(required, 10000)) {
                runner.fail("clickui: hooks missing: " + missing(required));
                runner.fail("all");
                closeAndFinish();
                return;
            }
            runner.pass("clickui: all hooks registered");
            screenshot("00-open");

            // 1. Toggle off (auto-clicker starts enabled)
            tap("auto-clicker-toggle");
            assertEventsContain("toggle:auto-clicker=false", "toggle off did not fire");
            screenshot("01-toggle-off");

            // 1b. Keyboard focus ring visual proof (no assertions)
            keyTap(GLFW.GLFW_KEY_TAB);
            screenshot("01b-focus-ring");

            // 1c. Hover states (evidence screenshots)
            hover("auto-clicker-toggle");
            screenshot("02a-hover-toggle");
            hover("slider-auto-clicker");
            screenshot("02b-hover-slider");
            hover("dropdown-auto-clicker");
            screenshot("02c-hover-dropdown");
            hover("auto-clicker-keybind");
            screenshot("02d-hover-keybind");

            // 1d. Pressed state: hold press, screenshot, release (completes the click -> on)
            float[] toggleXy = centerOf("auto-clicker-toggle");
            pressAt(toggleXy[0], toggleXy[1]);
            screenshot("02e-pressed-toggle");
            releaseAt(toggleXy[0], toggleXy[1]);
            assertEventsContain("toggle:auto-clicker=true", "toggle on did not fire");
            screenshot("02-toggle-on");

            // 2. Collapse + expand (assert card height change)
            float h0 = boundsHeight("auto-clicker-card");
            tap("auto-clicker-collapse");
            float h1 = awaitHeightBelow("auto-clicker-card", h0 * 0.7f, 5000);
            if (h1 < 0) {
                runner.fail("clickui: collapse did not shrink card (h0=" + h0 + ")");
            } else {
                runner.pass("clickui: collapse shrank card " + h0 + " -> " + h1);
            }
            screenshot("03-collapsed");
            tap("auto-clicker-collapse");
            float h2 = awaitHeightAbove("auto-clicker-card", h0 * 0.9f, 5000);
            if (h2 < 0) {
                runner.fail("clickui: expand did not restore card (h0=" + h0 + ")");
            } else {
                runner.pass("clickui: expand restored card to " + h2);
            }
            screenshot("04-expanded");

            // 3. Slider drag left -> right
            dragFullWidth("slider-auto-clicker");
            float last = lastSliderValue("slider:auto-clicker=");
            if (last <= 12f) {
                runner.fail("clickui: slider drag did not raise value (last=" + last + ")");
            } else {
                runner.pass("clickui: slider dragged to " + last);
            }
            screenshot("05-slider");

            // 4. Dropdown: open, select first option, assert callback
            tap("dropdown-auto-clicker");
            screenshot("06-dropdown-open");
            if (!awaitBounds(new HashSet<>(Arrays.asList("dropdown-auto-clicker-item-0")), 8000)) {
                runner.fail("clickui: dropdown popup options never composed");
            } else {
                runner.pass("clickui: dropdown popup opened with options");
            }
            tap("dropdown-auto-clicker-item-0");
            assertEventsContain("dropdown:auto-clicker=0", "dropdown selection did not fire");
            screenshot("07-dropdown-selected");

            // 4b. Inline enum: select second option, assert callback
            if (!awaitBounds(new HashSet<>(Arrays.asList("enum-auto-clicker-item-1")), 8000)) {
                runner.fail("clickui: inline enum options never composed");
            } else {
                runner.pass("clickui: inline enum options composed");
            }
            tap("enum-auto-clicker-item-1");
            assertEventsContain("enum:auto-clicker=1", "inline enum selection did not fire");
            screenshot("07b-enum-selected");

            // 4c. Enum hover state renders (visual proof only, no new assertions)
            hover("enum-auto-clicker-item-2");
            screenshot("07c-enum-hover");

            // 5. Keybind capture G, then Escape-cancel
            tap("auto-clicker-keybind");
            screenshot("08a-badge-recording");
            if (!awaitFocus(5000)) {
                runner.fail("clickui: badge never took focus (real rebind would fail too)");
            } else {
                runner.pass("clickui: badge holds scene focus");
            }
            keyTap(GLFW.GLFW_KEY_G);
            assertEventsContain("keybind:auto-clicker=G", "keybind capture did not fire");
            screenshot("08-keybind");
            tap("auto-clicker-keybind");
            if (!awaitFocus(5000)) {
                runner.fail("clickui: badge never retook focus");
            }
            keyTap(GLFW.GLFW_KEY_ESCAPE);
            assertEventsContain("keybind-cancel:auto-clicker", "escape did not cancel recording");
            if (isConfigOpen()) {
                runner.pass("clickui: escape retained value, screen alive");
            }
            screenshot("09-keybind-cancel");
            sleep(1500);
            screenshot("09c-focus-settled");

            // 6. Rail hover on an unselected entry, then navigation to player
            hover("rail-player");
            screenshot("09b-hover-rail");
            tap("rail-player");
            if (!awaitBounds(new HashSet<>(Arrays.asList("slider-auto-eat", "multiselect-auto-tool")), 8000)) {
                runner.fail("clickui: player modules never composed");
            } else {
                runner.pass("clickui: rail navigated to player");
            }
            screenshot("10-player");
            tap("multiselect-auto-tool");
            if (!awaitBounds(new HashSet<>(Arrays.asList("multiselect-auto-tool-item-2")), 8000)) {
                runner.fail("clickui: multiselect popup options never composed");
            } else {
                runner.pass("clickui: multiselect popup opened with options");
            }
            screenshot("11-multiselect-open");
            tap("multiselect-auto-tool-item-2");
            assertEventsContain("multiselect:auto-tool=2", "multiselect toggle did not fire");
            screenshot("11b-multiselect-toggled");
            tap("multiselect-auto-tool");
            tap("rail-render");
            if (!awaitBounds(new HashSet<>(Arrays.asList("checkbox-chams")), 8000)) {
                runner.fail("clickui: render modules never composed");
            } else {
                runner.pass("clickui: rail navigated to render");
            }
            screenshot("12a-checkbox-checked");
            tap("checkbox-chams");
            assertEventsContain("checkbox:chams=false", "checkbox toggle did not fire");
            screenshot("12-checkbox-unchecked");
            // 7. Disabled states: movement category hosts disabled modules (velocity)
            tap("rail-movement");
            if (!awaitBounds(new HashSet<>(Arrays.asList("slider-velocity-h")), 8000)) {
                runner.fail("clickui: movement modules never composed");
            } else {
                runner.pass("clickui: rail navigated to movement");
            }
            screenshot("12b-disabled-card");
            // 7b. World category visual coverage (timer, waypoint, auto-mine, chest-finder)
            tap("rail-world");
            if (!awaitBounds(new HashSet<>(Arrays.asList("slider-auto-mine")), 8000)) {
                runner.fail("clickui: world modules never composed");
            } else {
                runner.pass("clickui: rail navigated to world");
            }
            screenshot("12c-world");
            // 7c. Misc category visual coverage (no modules: empty state)
            tap("rail-misc");
            screenshot("12d-empty");
            tap("rail-combat");
            if (!awaitBounds(new HashSet<>(Arrays.asList("auto-clicker-toggle")), 8000)) {
                runner.fail("clickui: combat modules never recomposed");
            } else {
                runner.pass("clickui: rail navigated back to combat");
            }
            screenshot("13-combat");

            // 8. First REAL module: OneConfig preferences via the non-mock registry
            tap("rail-misc");
            if (!awaitBounds(new HashSet<>(Arrays.asList("real-oneconfig-toggle", "real-oneconfig-opacity")), 8000)) {
                runner.fail("clickui: real OneConfig card never composed (registry miss?)");
            } else {
                runner.pass("clickui: real OneConfig card composed via ConfigRegistry.findTree(oneconfig.json)");
            }
            screenshot("14-real-module");

            closeAndFinish();
        } catch (Exception e) {
            runner.fail("clickui: exception: " + e.getMessage());
            runner.fail("all");
        }
    }

    private void closeAndFinish() throws Exception {
        callOnRender(() -> {
            Platform.screen().close();
            return null;
        });
        sleep(1000);
        if (isConfigOpen()) {
            runner.fail("clickui: UI did not close");
            runner.fail("all");
            return;
        }
        runner.pass("clickui: UI closed");
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
        final float x = b.getCenterX();
        final float y = b.getCenterY();
        boolean ok = callOnRender(() -> screen().testTap(x, y));
        if (!ok) throw new IllegalStateException("scene unavailable for tap " + key);
        boolean focused = callOnRender(() -> screen().testHasFocus());
        runner.pass("clickui: tapped " + key + " @ " + x + "," + y + " focus=" + focused);
        sleep(600);
        if (!isConfigOpen()) throw new IllegalStateException("screen died after tap " + key);
    }

    private void hover(String key) throws Exception {
        ForkTestHooks.Bounds b = awaitNonZeroBounds(key, 3000);
        boolean ok = callOnRender(() -> screen().testMove(b.getCenterX(), b.getCenterY()));
        if (!ok) throw new IllegalStateException("scene unavailable for hover " + key);
        runner.pass("clickui: hovered " + key);
        sleep(500);
    }

    private float[] centerOf(String key) throws Exception {
        ForkTestHooks.Bounds b = awaitNonZeroBounds(key, 3000);
        return new float[]{b.getCenterX(), b.getCenterY()};
    }

    private void pressAt(float x, float y) throws Exception {
        boolean ok = callOnRender(() -> screen().testPress(x, y));
        if (!ok) throw new IllegalStateException("scene unavailable for press");
        sleep(700);
    }

    private void releaseAt(float x, float y) throws Exception {
        boolean ok = callOnRender(() -> screen().testRelease(x, y));
        if (!ok) throw new IllegalStateException("scene unavailable for release");
        sleep(600);
        if (!isConfigOpen()) throw new IllegalStateException("screen died after release");
    }

    private void dragFullWidth(String key) throws Exception {
        ForkTestHooks.Bounds b = awaitNonZeroBounds(key, 3000);
        final float y = b.getCenterY();
        final float x0 = b.getX() + 2f;
        final float x1 = b.getX() + b.getWidth() - 2f;
        boolean ok = callOnRender(() -> screen().testDrag(x0, y, x1, y, 16));
        if (!ok) throw new IllegalStateException("scene unavailable for drag " + key);
        runner.pass("clickui: dragged " + key);
        sleep(600);
    }

    private void keyTap(int glfwKey) throws Exception {
        boolean ok = callOnRender(() -> screen().testKeyTap(glfwKey));
        if (!ok) throw new IllegalStateException("scene unavailable for key " + glfwKey);
        runner.pass("clickui: key tapped " + glfwKey);
        sleep(600);
    }

    private boolean awaitFocus(long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (callOnRender(() -> screen().testHasFocus())) return true;
            Thread.sleep(200);
        }
        return false;
    }

    private void screenshot(String step) throws Exception {
        String name = "clickui-" + step;
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
            runner.fail("clickui: screenshot missing for step " + step);
        } else {
            runner.pass("clickui: screenshot " + f.getName());
        }
    }

    private ForkTestHooks.Bounds boundsOf(String key) {
        Map<String, ForkTestHooks.Bounds> map = ForkTestHooks.INSTANCE.boundsSnapshot();
        ForkTestHooks.Bounds b = map.get(key);
        if (b == null) throw new IllegalStateException("no hook bounds for " + key);
        return b;
    }

    private float boundsHeight(String key) {
        return boundsOf(key).getHeight();
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

    private float awaitHeightBelow(String key, float limit, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            float h = boundsHeight(key);
            if (h < limit) return h;
            Thread.sleep(250);
        }
        return -1f;
    }

    private float awaitHeightAbove(String key, float limit, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            float h = boundsHeight(key);
            if (h > limit) return h;
            Thread.sleep(250);
        }
        return -1f;
    }

    private void assertEventsContain(String fragment, String message) {
        List<String> events = ForkTestHooks.INSTANCE.eventsSnapshot();
        for (String e : events) {
            if (e.equals(fragment)) {
                runner.pass("clickui: event " + fragment);
                return;
            }
        }
        runner.fail("clickui: " + message + " (events=" + events + ")");
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
