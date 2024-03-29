package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.actions.actionImpl.PlaceEntities;
import net.loganford.nieEditor.actions.actionImpl.PlaceTiles;
import net.loganford.nieEditor.actions.actionImpl.RemoveEntities;
import net.loganford.nieEditor.actions.actionImpl.RemoveTiles;
import net.loganford.nieEditor.data.*;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.TilePlacement;

import java.awt.*;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Pen extends Tool{
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    private List<TilePlacement> tilesToAdd;
    private List<TilePlacement> tilesToRemove;

    private java.awt.Rectangle inactiveZone;

    public Pen(Window window, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(window, room, layer, selectedEntity, isEntity, isLeftClick);

        entitiesToAdd = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();

        tilesToAdd = new ArrayList<>();
        tilesToRemove = new ArrayList<>();
    }

    @Override
    public void mousePressed(int x, int y) {


        if(isEntity()) {
            if (isLeftClick()) {
               addEntityAt(x, y);
            } else {
                removeEntitiesAt(x, y);
            }
        }
        else {
            if(isLeftClick()) {
                placeTilesAt(x, y);
            }
            else {
                removeTilesAt(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {
                addEntityAt(x, y);
            } else {
                removeEntitiesAt(x, y);
            }
        }
        else {
            if(isLeftClick()) {
                placeTilesAt(x, y);
            }
            else {
                removeTilesAt(x, y);
            }
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {
                PlaceEntities placeEntities = new PlaceEntities(getWindow(), getRoom(), getLayer(), entitiesToAdd, entitiesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), placeEntities);
            } else {
                RemoveEntities removeEntities = new RemoveEntities(getWindow(), getRoom(), getLayer(), null, entitiesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), removeEntities);
            }
            entitiesToRemove.forEach(e -> e.setHidden(false));
        }
        else {
            if(isLeftClick()) {
                PlaceTiles placeTiles = new PlaceTiles(getWindow(), getRoom(), getLayer(), tilesToAdd, tilesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), placeTiles);
            }
            else {
                RemoveTiles removeTiles = new RemoveTiles(getWindow(), getRoom(), getLayer(), tilesToAdd, tilesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), removeTiles);
            }
        }
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void renderAboveEntities(Graphics g) {
        for(Entity e: entitiesToAdd) {
            e.render(getWindow(), g);
        }
    }

    @Override
    public void renderBelowEntities(Graphics g) {
        if(!isEntity()) {
            Tileset ts = getLayer().getTileMap().getTileset();
            if(tilesToAdd != null) {
                for(TilePlacement tp : tilesToAdd) {
                    tp.render(g, ts);
                }
            }
        }
    }

    @Override
    public void cancelTool(int x, int y) {
        if(isEntity()) {
            entitiesToRemove.forEach(e -> e.setHidden(false));
        }
        else {
            System.out.println("Replacing tiles");
            for(TilePlacement tp: tilesToRemove) {
                getLayer().getTileMap().placeTile(tp.getX(), tp.getY(), tp.getTileX(), tp.getTileY());
            }
        }
    }

    private void placeTilesAt(int x, int y) {
        if(x < 0 || y < 0) {
            return;
        }

        int tileWidth = getLayer().getTileMap().getTileset().getTileWidth();
        int tileHeight = getLayer().getTileMap().getTileset().getTileHeight();
        int px = x / tileWidth;
        int py = y / tileHeight;
        int selectionWidth = getWindow().getTilePicker().getTileSelectionX2() - getWindow().getTilePicker().getTileSelectionX() + 1;
        int selectionHeight = getWindow().getTilePicker().getTileSelectionY2() - getWindow().getTilePicker().getTileSelectionY() + 1;
        int selectionWidthPx = tileWidth * selectionWidth;
        int selectionHeightPx = tileHeight * selectionHeight;

        if(inactiveZone != null && inactiveZone.contains(new Point(x, y))) {
            return;
        }

        inactiveZone = new Rectangle(
                px * tileWidth - selectionWidthPx + tileWidth,
                py * tileHeight - selectionHeightPx + tileHeight,
                selectionWidthPx + selectionWidthPx - tileWidth,
                selectionHeightPx + selectionHeightPx - tileHeight
        );

        for(int i = 0; i < selectionWidth; i++) {
            for(int j = 0; j < selectionHeight; j++) {

                TilePlacement existingTile = getLayer().getTileMap().getTilePlacement(px + i, py + j);
                if(existingTile != null) {
                    tilesToRemove.add(existingTile);
                }
                tilesToAdd.add(new TilePlacement(px + i, py + j, getWindow().getTilePicker().getTileSelectionX() + i, getWindow().getTilePicker().getTileSelectionY() + j));
            }
        }
    }

    private void removeTilesAt(int x, int y) {
        int tileWidth = getLayer().getTileMap().getTileset().getTileWidth();
        int tileHeight = getLayer().getTileMap().getTileset().getTileHeight();
        int px = x / tileWidth;
        int py = y / tileHeight;
        TilePlacement existingTile = getLayer().getTileMap().getTilePlacement(px, py);
        if(existingTile != null) {
            getLayer().getTileMap().removeTile(px, py);
            tilesToRemove.add(existingTile);
        }
    }

    private void addEntityAt(int x, int y) {
        if(inactiveZone != null && inactiveZone.contains(new Point(x, y))) {
            return;
        }

        int snapX = (Integer) getWindow().getToolPane().getGridWidth().getValue();
        int snapY = (Integer) getWindow().getToolPane().getGridHeight().getValue();
        boolean isSnapped = getWindow().getToolPane().getSnapEntities().isSelected();
        boolean isOverwrite = getWindow().getToolPane().getOverwriteEntities().isSelected();

        int px = x;
        int py = y;
        EntityDefinition def = getWindow().getSelectedEntity();

        if(isSnapped) {
            px = (int) (Math.floor(((double)x)/snapX) * snapX);
            py = (int) (Math.floor(((double)y)/snapY) * snapY);
            inactiveZone = new Rectangle(px, py, def.getWidth(), def.getHeight());
        }
        else {
            inactiveZone = new Rectangle(px - def.getWidth(), py - def.getHeight(), def.getWidth() * 2, def.getHeight() * 2);
        }

        if(isOverwrite) {
            List<Entity> tempEntitiesToRemove = getEntitiesWithinBounds(new java.awt.Rectangle(px, py, def.getWidth(), def.getHeight()));
            tempEntitiesToRemove.forEach(e -> e.setHidden(true));
            entitiesToRemove.addAll(tempEntitiesToRemove);
        }

        entitiesToAdd.add(new Entity(def, px, py));
    }

    private void removeEntitiesAt(int x, int y) {
        List<Entity> hitEntities = new ArrayList<>();
        for(Entity e: getLayer().getEntities()) {
            if(!e.isHidden()) {
                EntityDefinition def = e.getDefinition();
                if (e.collidesWith(def, x, y)) {
                    hitEntities.add(e);
                    e.setHidden(true);
                }
            }
        }

        entitiesToRemove.addAll(hitEntities);
    }
}
