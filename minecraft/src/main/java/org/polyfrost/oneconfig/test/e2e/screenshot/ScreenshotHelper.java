package org.polyfrost.oneconfig.test.e2e.screenshot;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Utility for taking screenshots during e2e tests.
 * <p>
 * Must be called on the render thread. Uses Minecraft's built-in Screenshot API.
 */
public final class ScreenshotHelper {
    private static final Logger LOGGER = LogManager.getLogger("OneConfig/E2ETest/Screenshot");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Path SCREENSHOTS_DIR = FabricLoader.getInstance()
            .getGameDir()
            .resolve("run/screenshots");

    private ScreenshotHelper() {}

    /**
     * Take a screenshot of the current game state and save it to run/screenshots/.
     *
     * @param mc       the Minecraft client instance
     * @param testName name to include in the filename
     * @return the path of the saved screenshot
     * @throws IOException if saving fails
     */
    public static String takeScreenshot(Minecraft mc, String testName) throws IOException {
        Files.createDirectories(SCREENSHOTS_DIR);

        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        String filename = testName + "_" + timestamp + ".png";
        Path filePath = SCREENSHOTS_DIR.resolve(filename);

        try {
            Object renderTarget = getRenderTarget(mc);
            NativeImage image = takeScreenshotWithTarget(renderTarget);
            try (NativeImage img = image) {
                img.writeToFile(filePath);
            }
        } catch (Exception e) {
            throw new IOException("screenshot failed: " + e.getMessage(), e);
        }

        String absolute = filePath.toAbsolutePath().toString();
        LOGGER.info("[E2E] screenshot saved: {}", absolute);
        return absolute;
    }

    private static Object getRenderTarget(Minecraft mc) throws Exception {
        try {
            return mc.getClass().getMethod("getMainRenderTarget").invoke(mc);
        } catch (NoSuchMethodException e) {
            Object gameRenderer = mc.getClass().getMethod("getGameRenderer").invoke(mc);
            if (gameRenderer == null) gameRenderer = mc.getClass().getField("gameRenderer").get(mc);
            return gameRenderer.getClass().getMethod("mainRenderTarget").invoke(gameRenderer);
        }
    }

    private static NativeImage takeScreenshotWithTarget(Object target) throws Exception {
        try {
            java.lang.reflect.Method m = net.minecraft.client.Screenshot.class.getMethod("takeScreenshot", target.getClass());
            return (NativeImage) m.invoke(null, target);
        } catch (NoSuchMethodException e) {
            CompletableFuture<NativeImage> future = new CompletableFuture<>();
            java.lang.reflect.Method m = net.minecraft.client.Screenshot.class.getMethod("takeScreenshot", target.getClass(), java.util.function.Consumer.class);
            m.invoke(null, target, (java.util.function.Consumer<NativeImage>) future::complete);
            return future.get();
        }
    }
}
