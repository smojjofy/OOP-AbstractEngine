package com.p1_7.abstractengine.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the core Engine lifecycle bootstrapper.
 * Ensures that registered managers receive their init, update, and shutdown calls.
 */
public class EngineTest {

    private Engine engine;

    // --- Dummy Managers to track lifecycle calls ---
    private static class DummyStandardManager extends Manager {
        boolean initCalled = false;
        boolean shutdownCalled = false;

        @Override protected void onInit() { initCalled = true; }
        @Override protected void onShutdown() { shutdownCalled = true; }
    }

    private static class DummyUpdatableManager extends UpdatableManager {
        boolean initCalled = false;
        boolean updateCalled = false;
        boolean shutdownCalled = false;

        @Override protected void onInit() { initCalled = true; }
        @Override protected void onUpdate(float dt) { updateCalled = true; }
        @Override protected void onShutdown() { shutdownCalled = true; }
    }

    @BeforeEach
    public void setUp() {
        engine = new Engine();
    }

    @Test
    public void testEngineLifecycle() {
        // Arrange
        DummyStandardManager standardManager = new DummyStandardManager();
        DummyUpdatableManager updatableManager = new DummyUpdatableManager();

        engine.registerManager(standardManager);
        engine.registerManager(updatableManager);

        // Act 1: Initialization
        engine.init();

        // Assert 1: Both managers should be initialized
        assertTrue(standardManager.initCalled, "Standard manager should be initialized");
        assertTrue(updatableManager.initCalled, "Updatable manager should be initialized");

        // Act 2: Update Loop
        engine.update(1.0f);

        // Assert 2: ONLY the UpdatableManager should receive the update tick
        assertTrue(updatableManager.updateCalled, "Updatable manager should receive update ticks");

        // Act 3: Shutdown
        engine.shutdown();

        // Assert 3: Both managers should be shut down
        assertTrue(standardManager.shutdownCalled, "Standard manager should be shut down");
        assertTrue(updatableManager.shutdownCalled, "Updatable manager should be shut down");
    }

    @Test
    public void testGetManager() {
        DummyStandardManager standardManager = new DummyStandardManager();
        engine.registerManager(standardManager);
        
        // Assert we can retrieve it by its exact class
        DummyStandardManager retrieved = engine.getManager(DummyStandardManager.class);
        assertNotNull(retrieved);
        assertEquals(standardManager, retrieved);
    }
}