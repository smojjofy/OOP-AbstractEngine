package com.p1_7.game.scenes.settings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.input.ActionId;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IRenderable;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.platform.GdxDrawContext;

final class RemapSlot extends Entity implements IRenderable {

    static final float TABLE_WIDTH = 560f;
    static final float ACTION_COLUMN_WIDTH = 190f;
    static final float KEY_COLUMN_WIDTH = 170f;
    static final float CELL_HEIGHT = 30f;
    static final float CELL_GAP = 15f;

    private static final Color CELL_FILL = new Color(0.54f, 0.58f, 0.86f, 0.94f);
    private static final Color CELL_HOVER = new Color(0.66f, 0.71f, 0.95f, 0.98f);
    private static final Color CELL_ACTIVE = new Color(0.93f, 0.77f, 0.36f, 1f);
    private static final Color CELL_BORDER = new Color(0.90f, 0.94f, 1.0f, 1f);
    private static final Color TEXT_COLOR = new Color(0.12f, 0.16f, 0.28f, 1f);

    enum BindingColumn {
        PRIMARY("Primary"),
        ALTERNATE("Alternate");

        private final String label;

        BindingColumn(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }
    }

    private final String label;
    private final ActionId actionId;
    private final BitmapFont font;
    private final float actionColumnLeft;
    private final float actionColumnWidth;
    private final float cellBaselineY;
    private final float primaryCellX;
    private final float alternateCellX;
    private final float cellY;

    private int primaryKeyCode;
    private int alternateKeyCode;
    private BindingColumn hoveredColumn;
    private BindingColumn activeColumn;

    RemapSlot(String label,
              ActionId actionId,
              int primaryKeyCode,
              int alternateKeyCode,
              float centreX,
              float centreY,
              BitmapFont font) {
        float tableLeft = centreX - TABLE_WIDTH / 2f;
        this.label = label;
        this.actionId = actionId;
        this.primaryKeyCode = primaryKeyCode;
        this.alternateKeyCode = alternateKeyCode;
        this.font = font;
        this.actionColumnLeft = tableLeft;
        this.actionColumnWidth = ACTION_COLUMN_WIDTH;
        this.primaryCellX = tableLeft + ACTION_COLUMN_WIDTH + CELL_GAP;
        this.alternateCellX = primaryCellX + KEY_COLUMN_WIDTH + CELL_GAP;
        this.cellY = centreY - CELL_HEIGHT / 2f;
        this.cellBaselineY = centreY + 8f;
    }

    String getLabel() {
        return label;
    }

    ActionId getActionId() {
        return actionId;
    }

    int getPrimaryKeyCode() {
        return primaryKeyCode;
    }

    int getAlternateKeyCode() {
        return alternateKeyCode;
    }

    BindingColumn hitTest(float x, float y) {
        if (contains(primaryCellX, y, x)) {
            return BindingColumn.PRIMARY;
        }
        if (contains(alternateCellX, y, x)) {
            return BindingColumn.ALTERNATE;
        }
        return null;
    }

    private boolean contains(float cellX, float y, float x) {
        return x >= cellX && x <= cellX + KEY_COLUMN_WIDTH
            && y >= cellY && y <= cellY + CELL_HEIGHT;
    }

    void setHoveredColumn(BindingColumn column) {
        this.hoveredColumn = column;
    }

    void setActiveColumn(BindingColumn column) {
        this.activeColumn = column;
    }

    BindingColumn findColumnForKey(int keyCode) {
        if (primaryKeyCode == keyCode) {
            return BindingColumn.PRIMARY;
        }
        if (alternateKeyCode == keyCode) {
            return BindingColumn.ALTERNATE;
        }
        return null;
    }

    int getKeyCode(BindingColumn column) {
        return column == BindingColumn.PRIMARY ? primaryKeyCode : alternateKeyCode;
    }

    int getOtherKeyCode(BindingColumn column) {
        return column == BindingColumn.PRIMARY ? alternateKeyCode : primaryKeyCode;
    }

    void setKeyCode(BindingColumn column, int keyCode) {
        if (column == BindingColumn.PRIMARY) {
            primaryKeyCode = keyCode;
        } else {
            alternateKeyCode = keyCode;
        }
    }

    @Override
    public String getAssetPath() {
        return null;
    }

    @Override
    public ITransform getTransform() {
        return null;
    }

    @Override
    public void render(IDrawContext ctx) {
        GdxDrawContext gdxCtx = (GdxDrawContext) ctx;
        GlyphLayout actionLayout = new GlyphLayout(font, label);
        gdxCtx.drawFont(font, label,
            actionColumnLeft + (actionColumnWidth - actionLayout.width) / 2f,
            cellBaselineY);

        renderCell(gdxCtx, BindingColumn.PRIMARY, primaryCellX, displayText(BindingColumn.PRIMARY));
        renderCell(gdxCtx, BindingColumn.ALTERNATE, alternateCellX, displayText(BindingColumn.ALTERNATE));
    }

    private void renderCell(GdxDrawContext gdxCtx, BindingColumn column, float cellX, String text) {
        Color fill = CELL_FILL;
        if (activeColumn == column) {
            fill = CELL_ACTIVE;
        } else if (hoveredColumn == column) {
            fill = CELL_HOVER;
        }

        gdxCtx.rect(CELL_BORDER, cellX - 2f, cellY - 2f, KEY_COLUMN_WIDTH + 4f, CELL_HEIGHT + 4f, true);
        gdxCtx.rect(fill, cellX, cellY, KEY_COLUMN_WIDTH, CELL_HEIGHT, true);

        Color originalColor = font.getColor().cpy();
        font.setColor(TEXT_COLOR);
        GlyphLayout keyLayout = new GlyphLayout(font, text);
        gdxCtx.drawFont(font, text,
            cellX + (KEY_COLUMN_WIDTH - keyLayout.width) / 2f,
            cellBaselineY);
        font.setColor(originalColor);
    }

    private String displayText(BindingColumn column) {
        if (activeColumn == column) {
            return "PRESS KEY";
        }
        return formatKey(getKeyCode(column));
    }

    private String formatKey(int keyCode) {
        String keyText = Input.Keys.toString(keyCode);
        if (keyText == null || keyText.trim().isEmpty()) {
            return "UNKNOWN";
        }
        return keyText.toUpperCase();
    }
}
