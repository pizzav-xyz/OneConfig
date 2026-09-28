package org.polyfrost.oneconfig.test.e2e.screenshot;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotHelper {
    private static final Logger LOGGER = LogManager.getLogger("OneConfig/E2ETest/Screenshot");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Path SCREENSHOTS_DIR = FabricLoader.getInstance()
            .getGameDir()
            .resolve("run/screenshots");

    private ScreenshotHelper() {}

    /**
     * Polls for a screenshot file with the given prefix written at or after
     * {@code sinceMs}. Returns the file, or null on timeout.
     */
    public static java.io.File awaitScreenshot(String prefix, long sinceMs, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            java.io.File[] files = SCREENSHOTS_DIR.toFile().listFiles((d, n) -> {
                java.io.File f = new java.io.File(d, n);
                return n.startsWith(prefix) && n.endsWith(".png") && f.lastModified() >= sinceMs && f.length() > 0;
            });
            if (files != null && files.length > 0) {
                Arrays.sort(files, Comparator.comparingLong(java.io.File::lastModified).reversed());
                return files[0];
            }
            Thread.sleep(500);
        }
        return null;
    }

    public static String takeScreenshot(Minecraft mc, String testName) throws IOException {
        Files.createDirectories(SCREENSHOTS_DIR);

        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        String filename = testName + "_" + timestamp + ".png";
        Path filePath = SCREENSHOTS_DIR.resolve(filename);

        try {
            RenderTarget target = getRenderTarget(mc);
            takeScreenshotAsync(target, filePath);
        } catch (Exception e) {
            throw new IOException("screenshot failed: " + e.getMessage(), e);
        }

        String absolute = filePath.toAbsolutePath().toString();
        LOGGER.info("[E2E] screenshot saved: {}", absolute);
        return absolute;
    }

    private static void takeScreenshotAsync(RenderTarget target, Path filePath) {
        Screenshot.takeScreenshot(target, (NativeImage image) -> {
            try (NativeImage img = image) {
                img.writeToFile(filePath);
            } catch (Exception e) {
                LOGGER.error("Failed to write screenshot", e);
            }
        });
    }

    private static RenderTarget getRenderTarget(Minecraft mc) throws Exception {
        try {
            return (RenderTarget) mc.getClass().getMethod("getMainRenderTarget").invoke(mc);
        } catch (NoSuchMethodException e) {
            Object gameRenderer = mc.getClass().getField("gameRenderer").get(mc);
            return (RenderTarget) gameRenderer.getClass().getMethod("mainRenderTarget").invoke(gameRenderer);
        }
    }
}
