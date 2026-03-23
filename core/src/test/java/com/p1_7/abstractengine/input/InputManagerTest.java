package com.p1_7.abstractengine.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the InputManager's core polling loop.
 * Ensures that it correctly combines the InputMapping registry with the physical IInputSource hardware.
 */
public class InputManagerTest {

    private IInputSource mockSource;
    private InputManager inputManager;
    private InputMapping mapping;
    private ActionId jumpAction;
    private ActionId shootAction;

    @BeforeEach
    public void setUp() throws Exception {
        // Mock the hardware source so we don't need a real keyboard to test
        mockSource = Mockito.mock(IInputSource.class);
        inputManager = new InputManager(mockSource);
        
        // Safely extract the InputMapping using Reflection to respect encapsulation
        Field mappingField = null;
        for (Field field : InputManager.class.getDeclaredFields()) {
            if (field.getType().equals(InputMapping.class)) {
                mappingField = field;
                break;
            }
        }
        
        if (mappingField != null) {
            mappingField.setAccessible(true);
            mapping = (InputMapping) mappingField.get(inputManager);
        } else {
            fail("InputManager does not contain an internal InputMapping field!");
        }
        
        jumpAction = new ActionId("JUMP");
        shootAction = new ActionId("SHOOT");

        // Boot up the manager exactly like the Engine would
        inputManager.init();
    }

    @Test
    public void testIsActionActive_ViaKeyboard() {
        // Arrange: Bind physical key 51 to logical action JUMP
        mapping.bindKey(51, jumpAction);
        
        // Tell the mocked hardware to pretend key 51 is currently being pressed down
        Mockito.when(mockSource.isKeyPressed(51)).thenReturn(true);
        
        // ACT: Tick the Engine! 
        // This forces the InputManager to poll the hardware and update its internal per-frame cache.
        inputManager.update(0.016f);
        
        // Assert
        assertTrue(inputManager.isActionActive(jumpAction), "Manager should return true when the mapped key is physically pressed");
        assertFalse(inputManager.isActionActive(shootAction), "Manager should return false for unbound/unpressed actions");
    }

    @Test
    public void testIsActionActive_ViaMouse() {
        // Arrange: Bind physical mouse button 0 to logical action SHOOT
        mapping.bindButton(0, shootAction);
        
        // Tell the mocked hardware to pretend mouse button 0 is currently being pressed down
        Mockito.when(mockSource.isButtonPressed(0)).thenReturn(true);
        
        // ACT: Tick the Engine!
        inputManager.update(0.016f);
        
        // Assert
        assertTrue(inputManager.isActionActive(shootAction), "Manager should return true when the mapped button is physically pressed");
        assertFalse(inputManager.isActionActive(jumpAction), "Manager should return false for unbound/unpressed actions");
    }
    
    @Test
    public void testIsActionActive_MultipleKeysMapped() {
        // Arrange: Bind BOTH key 51 and key 19 to JUMP
        mapping.bindKey(51, jumpAction);
        mapping.bindKey(19, jumpAction);
        
        // Simulate pressing ONLY the alternate key (19)
        Mockito.when(mockSource.isKeyPressed(51)).thenReturn(false);
        Mockito.when(mockSource.isKeyPressed(19)).thenReturn(true);
        
        // ACT: Tick the Engine!
        inputManager.update(0.016f);
        
        // Assert: The action should still trigger!
        assertTrue(inputManager.isActionActive(jumpAction), "Action should trigger if ANY of its mapped keys are pressed");
    }
}