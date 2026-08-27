package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.oneconfig.test.e2e.screenshot.ScreenshotHelper;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;
import org.polyfrost.oneconfig.api.platform.v1.Platform;

public class ConfigUITest {
    private final E2ETestRunner runner;

    public ConfigUITest(E2ETestRunner runner) {
        this.runner = runner;
    }

    public void run() {
        // Open OneConfig UI directly
        Platform.screen().display(new OneConfigUIScreen());
        runner.delayTicks(40, () -> {
            Minecraft mc = runner.mc();

            // Verify UI is open - version-agnostic screen check via reflection
            Screen current = getCurrentScreen();
            boolean uiOpen = current instanceof OneConfigUIScreen;
            if (!uiOpen) {
                runner.fail("configui: OneConfigUIScreen did not open (screen=" + current + ")");
                runner.fail("all");
                return;
            }

            // Take screenshot of the open UI
            try {
                String path = ScreenshotHelper.takeScreenshot(runner.mc(), "configui-test");
                runner.pass("configui: screenshot saved to " + path);
            } catch (Exception e) {
                runner.fail("configui: screenshot failed: " + e.getMessage());
                return;
            }

            Platform.screen().close();

            runner.delayTicks(20, () -> {
                Screen after = getCurrentScreen();
                if (after == null) {
                    runner.pass("configui: UI closed successfully");
                } else {
                    runner.fail("configui: UI did not close (screen=" + after + ")");
                }

                if (runner.isAllPassed()) {
                    runner.pass("all");
                } else {
                    runner.fail("all");
                }
            });
        });
    }

    private static Screen getCurrentScreen() {
        try {
            Object mc = Minecraft.getInstance();
            try {
                return (Screen) mc.getClass().getField("screen").get(mc);
            } catch (NoSuchFieldException e) {
                Object gui = mc.getClass().getField("gui").get(mc);
                return (Screen) gui.getClass().getMethod("screen").invoke(gui);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
