package org.polyfrost.oneconfig.test.e2e;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent;
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class E2ETestRunner {
    private static final Logger LOGGER = LogManager.getLogger("OneConfig/E2ETest");
    private static E2ETestRunner instance;
    private final Map<String, Consumer<E2ETestRunner>> tests = new HashMap<>();
    private int ticksUntilRun = -1;
    private Runnable pendingCallback;
    private boolean worldLoaded = false;
    private boolean testScheduled = false;
    private boolean allPassed = true;

    private E2ETestRunner() {
        tests.put("configui", r -> new ConfigUITest(r).run());
        tests.put("clickui", r -> new ConfigUIClickTest(r).run());
        tests.put("clickui-real-module", r -> new ConfigUIRealModuleTest(r, ConfigUIRealModuleTest.Mode.SET).run());
        tests.put("clickui-real-module-verify", r -> new ConfigUIRealModuleTest(r, ConfigUIRealModuleTest.Mode.VERIFY).run());
        tests.put("modmenu-demo", r -> new ConfigUIRealModuleTest(r, ConfigUIRealModuleTest.Mode.SET, true).run());
        tests.put("modmenu-demo-verify", r -> new ConfigUIRealModuleTest(r, ConfigUIRealModuleTest.Mode.VERIFY, true).run());
    }

    public static void init() {
        String test = System.getProperty("oneconfig.e2e.test");
        if (test == null || test.isEmpty()) return;
        if (instance == null) instance = new E2ETestRunner();
        instance.registerTest(test.toLowerCase());
    }

    private void registerTest(String name) {
        if (!tests.containsKey(name)) {
            LOGGER.error("[TEST FAIL] unknown e2e test: {}", name);
            return;
        }
        EventManager.register(TickEvent.End.class, this::onTick);
        EventManager.register(WorldEvent.Load.class, e -> {
            worldLoaded = true;
            LOGGER.info("[TEST] world loaded, waiting for first tick");
        });
        LOGGER.info("[TEST] registered e2e test: {}", name);
    }

    private void onTick(TickEvent.End event) {
        if (worldLoaded && !testScheduled) {
            testScheduled = true;
            LOGGER.info("[TEST] world loaded, delaying 20 ticks");
            delayTicks(20, () -> {
                LOGGER.info("[TEST] in world, running test");
                try {
                    tests.get(System.getProperty("oneconfig.e2e.test")).accept(this);
                } catch (Exception ex) {
                    fail("exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        // Delayed callback execution
        if (ticksUntilRun > 0) {
            ticksUntilRun--;
            if (ticksUntilRun == 0 && pendingCallback != null) {
                pendingCallback.run();
                pendingCallback = null;
                ticksUntilRun = -1;
            }
        }
    }

    public void delayTicks(int ticks, Runnable callback) {
        this.ticksUntilRun = ticks;
        this.pendingCallback = callback;
    }

    public void pass(String message) {
        LOGGER.info("[TEST PASS] {}", message);
    }

    public void fail(String message) {
        LOGGER.error("[TEST FAIL] {}", message);
        allPassed = false;
    }

    public boolean isAllPassed() {
        return allPassed;
    }

    public Minecraft mc() {
        return Minecraft.getInstance();
    }

    public static Screen getCurrentScreen() {
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
