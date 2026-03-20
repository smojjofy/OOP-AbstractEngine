package com.p1_7.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.input.InputState;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.render.IRenderable;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.Settings;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.dungeon.CellType;
import com.p1_7.game.dungeon.Room;
import com.p1_7.game.input.GameActions;
import com.p1_7.game.logic.AnswerEvaluator;
import com.p1_7.game.logic.GameState;
import com.p1_7.game.logic.LabelledRoom;
import com.p1_7.game.logic.LevelOrchestrator;
import com.p1_7.game.managers.IFontManager;
import com.p1_7.game.platform.GdxDrawContext;

import java.util.List;

/**
 * Core gameplay scene that drives one dungeon run.
 *
 * Displays a minimap with smooth grid-based player movement, a HUD showing
 * the current question and player status, and room-entry logic that evaluates
 * answers via AnswerEvaluator. The scene wires together LevelOrchestrator
 * (map, question, labels), GameState (score/health), and AnswerEvaluator
 * (correct/wrong outcome). Only shape primitives are used — no asset textures.
 */
public class GameScene extends Scene {

    // -- colour constants --

    private static final Color COLOUR_WALL           = new Color(0.15f, 0.15f, 0.15f, 1f);
    private static final Color COLOUR_FLOOR          = new Color(0.50f, 0.50f, 0.50f, 1f);
    private static final Color COLOUR_ROOM_HIGHLIGHT = new Color(0.65f, 0.55f, 0.35f, 1f);
    private static final Color COLOUR_PLAYER         = new Color(0.20f, 0.70f, 1.00f, 1f);
    private static final Color COLOUR_HUD_BG         = new Color(0.10f, 0.10f, 0.10f, 0.85f);

    // -- timing constants --

    /** duration in seconds for smooth movement between adjacent tiles */
    private static final float MOVE_DURATION = 0.12f;

    /** minimum gap in seconds before a held direction key repeats movement */
    private static final float MOVE_REPEAT_DELAY = 0.08f;

    /**
     * prevents double-triggering room entry immediately after a spawn reset,
     * since the player starts inside the spawn room
     */
    private static final float ROOM_ENTRY_COOLDOWN = 0.5f;

    // -- layout constant --

    /** pixel height reserved at the bottom of the window for the HUD */
    private static final float HUD_HEIGHT = 100f;

    // -- services --

    private LevelOrchestrator orchestrator;
    private AnswerEvaluator   answerEvaluator;
    private IInputQuery       input;
    private IFontManager      fonts;

    // -- player grid position (integer tile coordinates) --

    private int playerGridX;
    private int playerGridY;

    // -- player pixel position (centre of current tile, updated by lerp each frame) --

    private float playerRenderX;
    private float playerRenderY;

    // -- movement state --

    private int     playerTargetX;
    private int     playerTargetY;
    private boolean playerMoving;
    private float   moveElapsed;
    private float   moveRepeatTimer;

    // -- room entry cooldown --

    private float roomEntryCooldown;

    // -- minimap layout (recomputed on startLevel and onEnter) --

    private float tileSize;
    private float mapOffsetX;
    private float mapOffsetY;

    // -- renderables --

    private DungeonRenderable dungeonRenderable;
    private HudRenderable     hudRenderable;

    /**
     * Constructs the game scene with the scene key "game".
     */
    public GameScene() {
        this.name = "game";
    }

    /**
     * Initialises all scene state when the scene is entered.
     *
     * Resolves services from the context, generates the first dungeon floor,
     * computes the minimap layout, and places the player at the spawn room.
     *
     * @param context the engine service context
     */
    @Override
    public void onEnter(SceneContext context) {
        input  = context.get(IInputQuery.class);
        fonts  = context.get(IFontManager.class);

        orchestrator    = new LevelOrchestrator();
        answerEvaluator = new AnswerEvaluator();

        orchestrator.startLevel(1);
        computeLayout();
        placePlayerAtSpawn();

        dungeonRenderable = new DungeonRenderable();
        hudRenderable     = new HudRenderable();

        roomEntryCooldown = 0f;
    }

    /**
     * Releases all scene state when the scene is exited.
     *
     * Fonts are not disposed here as they are owned by FontManager.
     *
     * @param context the engine service context
     */
    @Override
    public void onExit(SceneContext context) {
        orchestrator      = null;
        answerEvaluator   = null;
        input             = null;
        fonts             = null;
        dungeonRenderable = null;
        hudRenderable     = null;
    }

    /**
     * Processes input and advances movement and room-entry logic each frame.
     *
     * @param deltaTime elapsed seconds since the last frame
     * @param context   the engine service context
     */
    @Override
    public void update(float deltaTime, SceneContext context) {
        // escape returns to the main menu
        if (input.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            context.changeScene("menu");
            return;
        }

        // count down the room-entry cooldown so it cannot double-fire
        if (roomEntryCooldown > 0f) {
            roomEntryCooldown = Math.max(0f, roomEntryCooldown - deltaTime);
        }

        if (playerMoving) {
            advanceMovement(deltaTime, context);
        } else {
            processMovementInput(deltaTime);
        }
    }

    /**
     * Queues the dungeon and HUD renderables for this frame.
     *
     * @param renderQueue the render queue accumulator for this frame
     */
    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(dungeonRenderable);
        renderQueue.queue(hudRenderable);
    }

    // -- private helpers --

    /**
     * Recomputes tileSize, mapOffsetX, and mapOffsetY from the current window dimensions.
     *
     * The dungeon grid is always 51x51. Tiles are sized so they fit entirely within
     * the area above the HUD, then centred horizontally and vertically in that area.
     */
    private void computeLayout() {
        float windowW = Settings.getWindowWidth();
        float windowH = Settings.getWindowHeight();
        float availH  = windowH - HUD_HEIGHT;

        // floor ensures tiles never spill outside the available area
        tileSize   = (float) Math.floor(Math.min(windowW / 51f, availH / 51f));
        mapOffsetX = (windowW - tileSize * 51f) / 2f;
        mapOffsetY = HUD_HEIGHT + (availH - tileSize * 51f) / 2f;
    }

    /**
     * Moves the player to the centre of the spawn room and resets all movement state.
     *
     * The spawn room is the first LabelledRoom returned by the orchestrator (index 0),
     * which has no label.
     */
    private void placePlayerAtSpawn() {
        Room spawnRoom = orchestrator.getLabelledRooms().get(0).getRoom();
        int[] centre   = spawnRoom.centre();

        playerGridX   = centre[0];
        playerGridY   = centre[1];
        playerTargetX = playerGridX;
        playerTargetY = playerGridY;

        // convert grid position to pixel centre of the tile
        playerRenderX = mapOffsetX + playerGridX * tileSize + tileSize / 2f;
        playerRenderY = mapOffsetY + playerGridY * tileSize + tileSize / 2f;

        playerMoving      = false;
        moveElapsed       = 0f;
        moveRepeatTimer   = 0f;
    }

    /**
     * Steps the in-progress smooth movement forward by deltaTime.
     *
     * Once the movement completes (t reaches 1), snaps the grid position to the target
     * and evaluates room entry.
     *
     * @param deltaTime elapsed seconds since the last frame
     * @param context   the engine service context (forwarded to checkRoomEntry)
     */
    private void advanceMovement(float deltaTime, SceneContext context) {
        moveElapsed += deltaTime;
        float t = Math.min(1f, moveElapsed / MOVE_DURATION);

        // lerp between the source tile centre and the target tile centre
        float startPx = mapOffsetX + playerGridX  * tileSize + tileSize / 2f;
        float startPy = mapOffsetY + playerGridY  * tileSize + tileSize / 2f;
        float endPx   = mapOffsetX + playerTargetX * tileSize + tileSize / 2f;
        float endPy   = mapOffsetY + playerTargetY * tileSize + tileSize / 2f;

        playerRenderX = startPx + (endPx - startPx) * t;
        playerRenderY = startPy + (endPy - startPy) * t;

        if (t >= 1f) {
            // snap and finalise movement
            playerGridX  = playerTargetX;
            playerGridY  = playerTargetY;
            playerMoving = false;
            checkRoomEntry(context);
        }
    }

    /**
     * Reads directional input and initiates a one-tile move if the target is not a wall.
     *
     * A repeat timer prevents instant re-triggering while a direction key is held.
     *
     * @param deltaTime elapsed seconds since the last frame
     */
    private void processMovementInput(float deltaTime) {
        moveRepeatTimer = Math.max(0f, moveRepeatTimer - deltaTime);
        if (moveRepeatTimer > 0f) {
            return;
        }

        int dx = 0;
        int dy = 0;

        if      (input.isActionActive(GameActions.MOVE_UP))    { dy = +1; }
        else if (input.isActionActive(GameActions.MOVE_DOWN))  { dy = -1; }
        else if (input.isActionActive(GameActions.MOVE_LEFT))  { dx = -1; }
        else if (input.isActionActive(GameActions.MOVE_RIGHT)) { dx = +1; }

        if (dx == 0 && dy == 0) {
            return;
        }

        int nx = playerGridX + dx;
        int ny = playerGridY + dy;

        // only allow movement into non-wall tiles
        if (orchestrator.getMap().getCellAt(nx, ny) != CellType.WALL) {
            playerTargetX   = nx;
            playerTargetY   = ny;
            playerMoving    = true;
            moveElapsed     = 0f;
            moveRepeatTimer = MOVE_REPEAT_DELAY;
        }
    }

    /**
     * Checks whether the player has entered a labelled room and evaluates the answer.
     *
     * On a correct answer: advances the level or ends the game if all levels are cleared.
     * On a wrong answer:   resets the player to spawn or ends the game if health is zero.
     *
     * @param context the engine service context used to trigger scene transitions
     */
    private void checkRoomEntry(SceneContext context) {
        if (roomEntryCooldown > 0f) {
            return;
        }

        GameState gs = orchestrator.getGameState();
        List<LabelledRoom> labelledRooms = orchestrator.getLabelledRooms();

        for (int i = 0; i < labelledRooms.size(); i++) {
            LabelledRoom lr = labelledRooms.get(i);
            if (!lr.hasLabel()) {
                continue;
            }

            Room r = lr.getRoom();
            boolean insideX = playerGridX >= r.x && playerGridX <= r.x + r.width  - 1;
            boolean insideY = playerGridY >= r.y && playerGridY <= r.y + r.height - 1;

            if (!insideX || !insideY) {
                continue;
            }

            // player is inside this labelled room — evaluate the choice
            boolean correct = answerEvaluator.evaluate(lr, gs);

            if (correct && gs.isGameWon()) {
                // all levels cleared
                context.changeScene("menu");
                return;
            }

            if (correct) {
                // advance to the next level and regenerate the dungeon
                gs.advanceLevel();
                orchestrator.startLevel(gs.getLevel());
                computeLayout();
                placePlayerAtSpawn();
                roomEntryCooldown = ROOM_ENTRY_COOLDOWN;
                return;
            }

            if (gs.isGameOver()) {
                // wrong answer and health depleted
                context.changeScene("menu");
                return;
            }

            // wrong answer but still alive — reset to spawn
            placePlayerAtSpawn();
            roomEntryCooldown = ROOM_ENTRY_COOLDOWN;
            return;
        }
    }

    // ── inner renderables ──────────────────────────────────────────────────

    /**
     * Draws the dungeon grid, highlighted answer rooms with labels, and the player.
     */
    private class DungeonRenderable implements IRenderable {

        private final ITransform transform = new Transform2D(0f, 0f, 0f, 0f);

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform() { return transform; }

        /**
         * Renders the dungeon tilemap, room highlights, room labels, and the player circle.
         *
         * @param ctx the draw context for this frame
         */
        @Override
        public void render(IDrawContext ctx) {
            GdxDrawContext gdx = (GdxDrawContext) ctx;

            CellType[][] grid = orchestrator.getMap().getGrid();

            // draw every tile as a wall or floor rectangle
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    Color tileColour = (grid[row][col] == CellType.WALL)
                        ? COLOUR_WALL : COLOUR_FLOOR;
                    gdx.rect(tileColour,
                             mapOffsetX + col * tileSize,
                             mapOffsetY + row * tileSize,
                             tileSize, tileSize,
                             true);
                }
            }

            // highlight labelled rooms and draw their answer value centred inside
            List<LabelledRoom> labelledRooms = orchestrator.getLabelledRooms();
            for (int i = 0; i < labelledRooms.size(); i++) {
                LabelledRoom lr = labelledRooms.get(i);
                if (!lr.hasLabel()) {
                    continue;
                }

                Room rm = lr.getRoom();

                // draw the highlight background over the room
                gdx.rect(COLOUR_ROOM_HIGHLIGHT,
                         mapOffsetX + rm.x * tileSize,
                         mapOffsetY + rm.y * tileSize,
                         rm.width  * tileSize,
                         rm.height * tileSize,
                         true);

                // draw the label value centred inside the room
                BitmapFont labelFont = fonts.getDarkTextFont(Math.max(12, (int) tileSize - 2));
                String value = String.valueOf(lr.getLabel().getValue());
                GlyphLayout gl = new GlyphLayout(labelFont, value);

                float cx = mapOffsetX + (rm.x + rm.width  / 2f) * tileSize;
                float cy = mapOffsetY + (rm.y + rm.height / 2f) * tileSize + gl.height / 2f;

                gdx.drawFont(labelFont, value, cx - gl.width / 2f, cy);
            }

            // draw the player as a filled circle at its current interpolated position
            gdx.circle(COLOUR_PLAYER, playerRenderX, playerRenderY, tileSize * 0.4f, true);
        }
    }

    /**
     * Draws the HUD background, question text, and player status line.
     */
    private class HudRenderable implements IRenderable {

        private final ITransform transform = new Transform2D(0f, 0f, 0f, 0f);

        @Override public String     getAssetPath() { return null; }
        @Override public ITransform getTransform() { return transform; }

        /**
         * Renders the HUD at the bottom of the window with question and status text.
         *
         * @param ctx the draw context for this frame
         */
        @Override
        public void render(IDrawContext ctx) {
            GdxDrawContext gdx     = (GdxDrawContext) ctx;
            float          windowW = Settings.getWindowWidth();

            // semi-opaque dark background panel
            gdx.drawTintedQuad(COLOUR_HUD_BG, 0f, 0f, windowW, HUD_HEIGHT);

            BitmapFont hudFont = fonts.getDarkTextFont(20);

            // question text, centred horizontally near the top of the HUD
            String qText = orchestrator.getQuestion().getDisplayText();
            GlyphLayout gl = new GlyphLayout(hudFont, qText);
            gdx.drawFont(hudFont, qText, windowW / 2f - gl.width / 2f, HUD_HEIGHT - 10f);

            // status line: level, score, and health, centred below the question
            GameState gs     = orchestrator.getGameState();
            String    status = "Level: " + gs.getLevel()
                             + "   Score: " + gs.getScore()
                             + "   Health: " + gs.getHealth();
            GlyphLayout sl = new GlyphLayout(hudFont, status);
            gdx.drawFont(hudFont, status, windowW / 2f - sl.width / 2f, HUD_HEIGHT - 48f);
        }
    }
}
