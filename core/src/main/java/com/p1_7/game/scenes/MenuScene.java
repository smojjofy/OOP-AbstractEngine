package com.p1_7.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.Settings;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.entities.MenuButton;
import com.p1_7.game.platform.GdxShapeRenderer;
import com.p1_7.game.platform.GdxSpriteBatch;

/**
 * Main menu scene for Math Quest Maze.
 *
 * Displays the game title and three buttons:
 *   • Start  → transitions to the game scene (key: "game")
 *   • Settings → transitions to the settings scene (key: "settings")
 *   • Exit   → closes the application
 *
 * Input supported:
 *   • Mouse: hover highlights buttons; left-click activates them
 *   • Keyboard: ESC quits the application from the menu
 *
 * Architecture note: buttons are plain entities managed directly by
 * the scene rather than the EntityManager, because they are purely
 * presentational and do not participate in collision or movement.
 * This keeps the menu self-contained and easy to swap out.
 */
public class MenuScene extends Scene {

    // ── layout constants ─────────────────────────────────────────
    /** Horizontal centre of the screen — buttons are centred here. */
    private static final float CENTRE_X       = Settings.WINDOW_WIDTH  / 2f;

    /** Y position of the topmost button. */
    private static final float FIRST_BUTTON_Y = Settings.WINDOW_HEIGHT * 0.45f;

    /** Vertical gap between button centres. */
    private static final float BUTTON_SPACING = 80f;

    // ── entities ─────────────────────────────────────────────────
    private TitleText  titleText;
    private MenuButton btnStart;
    private MenuButton btnSettings;
    private MenuButton btnExit;

    // ── background colour entity ──────────────────────────────────
    private MenuBackground background;

    // ── constructor ───────────────────────────────────────────────

    /**
     * Constructs the menu scene and registers it under the key "menu".
     */
    public MenuScene() {
        this.name = "menu";
    }

    // ── Scene lifecycle ───────────────────────────────────────────

    @Override
    public void onEnter(SceneContext context) {
        // 1. solid colour background
        background = new MenuBackground();

        // 2. game title (large, centred near the top)
        float titleY = Settings.WINDOW_HEIGHT * 0.75f;
        titleText = new TitleText("MATH QUEST MAZE", CENTRE_X, titleY, 3.0f);

        // 3. create buttons, evenly spaced downward from FIRST_BUTTON_Y
        btnStart    = new MenuButton("START",    CENTRE_X, FIRST_BUTTON_Y,                  1.5f);
        btnSettings = new MenuButton("SETTINGS", CENTRE_X, FIRST_BUTTON_Y - BUTTON_SPACING, 1.5f);
        btnExit     = new MenuButton("EXIT",     CENTRE_X, FIRST_BUTTON_Y - BUTTON_SPACING * 2, 1.5f);
    }

    @Override
    public void onExit(SceneContext context) {
        // release GPU resources held by each button's BitmapFont
        if (btnStart    != null) btnStart.dispose();
        if (btnSettings != null) btnSettings.dispose();
        if (btnExit     != null) btnExit.dispose();
        if (titleText   != null) titleText.dispose();
    }

    // ── per-frame logic ───────────────────────────────────────────

    @Override
    public void update(float deltaTime, SceneContext context) {

        // --- keyboard shortcut: ESC exits the application --------
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }

        // --- poll mouse input for every button -------------------
        btnStart.updateInput();
        btnSettings.updateInput();
        btnExit.updateInput();

        // --- handle button clicks --------------------------------
        if (btnStart.isClicked()) {
            btnStart.resetClick();
            context.changeScene("game");   // TODO: replace "game" with your actual game scene key
            return;
        }

        if (btnSettings.isClicked()) {
            btnSettings.resetClick();
            context.changeScene("settings");
            return;
        }

        if (btnExit.isClicked()) {
            btnExit.resetClick();
            Gdx.app.exit();
        }
    }

    // ── rendering ─────────────────────────────────────────────────

    @Override
    public void submitRenderable(SceneContext context) {
        // render order: background → title → buttons (front to back)
        context.renderQueue().queue(background);
        context.renderQueue().queue(titleText);
        context.renderQueue().queue(btnStart);
        context.renderQueue().queue(btnSettings);
        context.renderQueue().queue(btnExit);
    }

    // ══════════════════════════════════════════════════════════════
    // Inner helper entities (private, menu-only)
    // Keeping them here avoids polluting the entities package with
    // one-off classes only the menu needs.
    // ══════════════════════════════════════════════════════════════

    /**
     * Solid dark-purple background that fills the whole screen.
     * Drawn as a filled rectangle via ICustomRenderable.
     */
    private static class MenuBackground extends Entity implements IRenderItem, ICustomRenderable {

        private final Transform2D transform;
        private static final Color BG_COLOUR = new Color(0.08f, 0.06f, 0.18f, 1f);

        MenuBackground() {
            this.transform = new Transform2D(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }

        @Override public String    getAssetPath() { return null; }
        @Override public ITransform getTransform() { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            sr.setColor(BG_COLOUR);
            sr.rect(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }
    }

    /**
     * Game title text, centred at a given position.
     * Uses a scaled BitmapFont rendered through the SpriteBatch.
     */
    private static class TitleText extends Entity implements IRenderItem, ICustomRenderable {

        private final Transform2D transform;
        private final BitmapFont  font;
        private final String      text;

        TitleText(String text, float centreX, float centreY, float scale) {
            this.text      = text;
            this.font      = new BitmapFont();
            this.font.getData().setScale(scale);
            this.font.setColor(new Color(0.85f, 0.85f, 1.0f, 1f)); // soft white-blue
            // Transform position is the draw origin; centring is done in renderCustom
            this.transform = new Transform2D(centreX, centreY, 0f, 0f);
        }

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform()  { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            sr.end();

            com.badlogic.gdx.graphics.g2d.SpriteBatch sb = ((GdxSpriteBatch) batch).unwrap();
            sb.begin();

            // measure text width so we can centre it
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, text);
            float drawX = transform.getPosition(0) - layout.width  / 2f;
            float drawY = transform.getPosition(1) + layout.height / 2f;
            font.draw(sb, text, drawX, drawY);

            sb.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
        }

        void dispose() {
            font.dispose();
        }
    }
}