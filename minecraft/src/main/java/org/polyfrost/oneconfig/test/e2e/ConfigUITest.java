package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.oneconfig.test.e2e.screenshot.ScreenshotHelper;
import org.polyfrost.oneconfig.internal.ThemeConfig;
import org.polyfrost.oneconfig.internal.ui.themes.ThemeRegistry;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;
import org.polyfrost.oneconfig.api.platform.v1.Platform;

public class ConfigUITest {
    private final E2ETestRunner runner;

    public ConfigUITest(E2ETestRunner runner) {
        this.runner = runner;
    }

    public void run() {
        ThemeConfig.activeTheme = "Dark Orange Fork";
        ThemeRegistry.INSTANCE.loadFromConfig();
        runner.pass("configui: ThemeConfig=" + ThemeConfig.activeTheme + " will load Dark Orange Fork");
        Platform.screen().display(new OneConfigUIScreen());
        runner.pass("configui: opened OneConfigUIScreen, waiting 2s");
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            Minecraft.getInstance().execute(() -> {
                runner.pass("configui: 2s elapsed, checking screen");
                Minecraft mc = runner.mc();

                Screen current = getCurrentScreen();
                runner.pass("configui: current screen is " + current);
                boolean uiOpen = current instanceof OneConfigUIScreen;
                if (!uiOpen) {
                    runner.fail("configui: OneConfigUIScreen did not open (screen=" + current + ")");
                    runner.fail("all");
                    return;
                }

                // Kick off async screenshot — must not block render thread
                try {
                    ScreenshotHelper.takeScreenshot(runner.mc(), "configui-test");
                    runner.pass("configui: screenshot scheduled");
                } catch (Exception e) {
                    runner.fail("configui: screenshot schedule failed: " + e.getMessage());
                    runner.fail("all");
                    return;
                }

                long startTime = System.currentTimeMillis();
                // Poll for screenshot file newer than startTime on background thread, then close UI
                new Thread(() -> {
                    try {
                        String expected = "configui-test_";
                        for (int i = 0; i < 60; i++) {
                            Thread.sleep(500);
                            java.io.File dir = new java.io.File(
                                    net.fabricmc.loader.api.FabricLoader.getInstance()
                                            .getGameDir().toFile(), "run/screenshots");
                            java.io.File[] files = dir.listFiles((d, n) -> n.startsWith(expected) && n.endsWith(".png") && new java.io.File(dir, n).lastModified() >= startTime);
                            if (files != null && files.length > 0 && files[0].length() > 0) {
                                runner.pass("configui: screenshot ready (" + files[0].getName() + ")");
                                Minecraft.getInstance().execute(() -> {
                                    Platform.screen().close();
                                    new Thread(() -> {
                                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                                        Minecraft.getInstance().execute(() -> {
                                            Screen after = getCurrentScreen();
                                            if (after == null || !(after instanceof OneConfigUIScreen)) {
                                                runner.pass("configui: UI closed successfully (now " + after + ")");
                                            } else {
                                                runner.fail("configui: UI did not close (screen=" + after + ")");
                                            }
                                            if (runner.isAllPassed()) {
                                                runner.pass("all");
                                            } else {
                                                runner.fail("all");
                                            }
                                        });
                                    }).start();
                                });
                                return;
                            }
                        }
                        runner.fail("configui: screenshot file never appeared after 30s");
                        runner.fail("all");
                    } catch (InterruptedException ignored) {
                        runner.fail("configui: screenshot poll interrupted");
                        runner.fail("all");
                    }
                }).start();
            });
        }).start();
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
