package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.oneconfig.api.platform.v1.Platform;
import org.polyfrost.oneconfig.internal.compat.ModMenuEntrypoint;
import org.polyfrost.oneconfig.internal.ui.compose.impls.OneConfigUIScreen;

import java.util.concurrent.CompletableFuture;

/**
 * Opens the config screen through the exact Mod Menu entry-point factory
 * Mod Menu itself calls, shared by every demo drive that must prove the
 * Mod Menu route rather than a direct construction.
 */
public final class ModMenuScreenOpener {
    private ModMenuScreenOpener() {
    }

    public static void displayViaModMenu(E2ETestRunner runner, String tag) {
        Screen created = onRender(runner, () -> {
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
            runner.fail(tag + ": ModMenu factory produced no screen (entry missing or unlinked)");
            return;
        }
        if (!(created instanceof OneConfigUIScreen)) {
            runner.fail(tag + ": ModMenu factory screen is not OneConfigUIScreen: " + created.getClass());
            return;
        }
        runner.pass(tag + ": ModMenu factory produced OneConfigUIScreen");
        Screen toShow = created;
        onRender(runner, () -> {
            Platform.screen().display(toShow);
            return null;
        });
    }

    private static <T> T onRender(E2ETestRunner runner, java.util.function.Supplier<T> work) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runner.mc().execute(() -> {
            try {
                future.complete(work.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        try {
            return future.get(15, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("render thread call failed", e);
        }
    }
}
