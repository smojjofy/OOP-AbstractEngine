package com.p1_7.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
 * Settings scene for Math Quest Maze.
 *
 * Currently a placeholder that shows:
 *   • A "SETTINGS" heading
 *   • A music volume slider (increase / decrease buttons)
 *   • A "BACK" button that returns to the main menu
 *
 * Navigation:
 *   • Mouse: click buttons
 *   • Keyboard: ESC or BACKSPACE returns to the menu
 *
 * Expand this scene later by adding more option rows
 * (sound effects volume, difficulty default, etc.) without
 * touching any other scene or engine class.
 */
public class SettingsScene extends Scene {

    // ── layout ───────────────────────────────────────────────────
    private static final float CENTRE_X = Settings.WINDOW_WIDTH  / 2f;
    private static final float CENTRE_Y = Settings.WINDOW_HEIGHT / 2f;

    // ── entities ─────────────────────────────────────────────────
    private SettingsBackground background;
    private HeadingText        heading;
    private LabelText          volumeLabel;   // "Music Volume: 50%"
    private MenuButton         btnVolumeDown;
    private MenuButton         btnVolumeUp;
    private MenuButton         btnBack;

    // ── constructor ───────────────────────────────────────────────

    public SettingsScene() {
        this.name = "settings";
    }

    // ── lifecycle ─────────────────────────────────────────────────

    @Override
    public void onEnter(SceneContext context) {
        background   = new SettingsBackground();
        heading      = new HeadingText("SETTINGS", CENTRE_X, Settings.WINDOW_HEIGHT * 0.75f, 2.5f);
        volumeLabel  = new LabelText(volumeText(), CENTRE_X, CENTRE_Y + 30f, 1.5f);

        // volume control buttons (small, flanking the label)
        btnVolumeDown = new MenuButton("-", CENTRE_X - 170f, CENTRE_Y - 20f, 1.5f);
        btnVolumeUp   = new MenuButton("+", CENTRE_X + 170f, CENTRE_Y - 20f, 1.5f);

        // back button at the bottom
        btnBack = new MenuButton("BACK", CENTRE_X, CENTRE_Y - 130f, 1.5f);
    }

    @Override
    public void onExit(SceneContext context) {
        if (heading       != null) heading.dispose();
        if (volumeLabel   != null) volumeLabel.dispose();
        if (btnVolumeDown != null) btnVolumeDown.dispose();
        if (btnVolumeUp   != null) btnVolumeUp.dispose();
        if (btnBack       != null) btnBack.dispose();
    }

    // ── update ────────────────────────────────────────────────────

    @Override
    public void update(float deltaTime, SceneContext context) {

        // keyboard shortcut: ESC / BACKSPACE → back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            context.changeScene("menu");
            return;
        }

        // poll buttons
        btnVolumeDown.updateInput();
        btnVolumeUp.updateInput();
        btnBack.updateInput();

        // volume down (step -10%)
        if (btnVolumeDown.isClicked()) {
            btnVolumeDown.resetClick();
            Settings.setMusicVolume(Settings.MUSIC_VOLUME - 0.1f);
            volumeLabel.setText(volumeText());
        }

        // volume up (step +10%)
        if (btnVolumeUp.isClicked()) {
            btnVolumeUp.resetClick();
            Settings.setMusicVolume(Settings.MUSIC_VOLUME + 0.1f);
            volumeLabel.setText(volumeText());
        }

        // back button
        if (btnBack.isClicked()) {
            btnBack.resetClick();
            context.changeScene("menu");
        }
    }

    // ── rendering ─────────────────────────────────────────────────

    @Override
    public void submitRenderable(SceneContext context) {
        context.renderQueue().queue(background);
        context.renderQueue().queue(heading);
        context.renderQueue().queue(volumeLabel);
        context.renderQueue().queue(btnVolumeDown);
        context.renderQueue().queue(btnVolumeUp);
        context.renderQueue().queue(btnBack);
    }

    // ── helpers ───────────────────────────────────────────────────

    /** Formats the current volume as a percentage string. */
    private String volumeText() {
        return "Music Volume:  " + Math.round(Settings.MUSIC_VOLUME * 100) + "%";
    }

    // ══════════════════════════════════════════════════════════════
    // Private inner entities (settings-scene only)
    // ══════════════════════════════════════════════════════════════

    /** Solid background matching the menu's colour scheme. */
    private static class SettingsBackground extends Entity implements IRenderItem, ICustomRenderable {
        private final Transform2D transform;
        private static final Color BG = new Color(0.08f, 0.06f, 0.18f, 1f);

        SettingsBackground() {
            transform = new Transform2D(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform()  { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            sr.setColor(BG);
            sr.rect(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }
    }

    /**
     * Generic centred text entity reused for both heading and volume label.
     * Extracted into its own class to avoid duplicating the font/render boilerplate.
     */
    private static class HeadingText extends Entity implements IRenderItem, ICustomRenderable {
        private final Transform2D transform;
        private final BitmapFont  font;
        private final String      text;

        HeadingText(String text, float cx, float cy, float scale) {
            this.text = text;
            font = new BitmapFont();
            font.getData().setScale(scale);
            font.setColor(new Color(0.85f, 0.85f, 1.0f, 1f));
            transform = new Transform2D(cx, cy, 0f, 0f);
        }

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform()  { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            sr.end();
            com.badlogic.gdx.graphics.g2d.SpriteBatch sb = ((GdxSpriteBatch) batch).unwrap();
            sb.begin();
            GlyphLayout gl = new GlyphLayout(font, text);
            font.draw(sb, text,
                transform.getPosition(0) - gl.width  / 2f,
                transform.getPosition(1) + gl.height / 2f);
            sb.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
        }

        void dispose() { font.dispose(); }
    }

    /** Mutable label — used for the volume percentage display. */
    private static class LabelText extends Entity implements IRenderItem, ICustomRenderable {
        private final Transform2D transform;
        private final BitmapFont  font;
        private       String      text;

        LabelText(String text, float cx, float cy, float scale) {
            this.text = text;
            font = new BitmapFont();
            font.getData().setScale(scale);
            font.setColor(Color.WHITE);
            transform = new Transform2D(cx, cy, 0f, 0f);
        }

        void setText(String newText) { this.text = newText; }

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform()  { return transform; }

        @Override
        public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
            ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
            sr.end();
            com.badlogic.gdx.graphics.g2d.SpriteBatch sb = ((GdxSpriteBatch) batch).unwrap();
            sb.begin();
            GlyphLayout gl = new GlyphLayout(font, text);
            font.draw(sb, text,
                transform.getPosition(0) - gl.width  / 2f,
                transform.getPosition(1) + gl.height / 2f);
            sb.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
        }

        void dispose() { font.dispose(); }
    }
}