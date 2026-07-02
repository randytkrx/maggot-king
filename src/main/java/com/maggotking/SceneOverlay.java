/*
 * Copyright (c) 2026, Randy <nightlight681@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.maggotking;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class SceneOverlay extends Overlay
{
	private final Client client;
	private final MaggotKingConfig config;
	private final BossTracker tracker;

	@Inject
	SceneOverlay(Client client, MaggotKingConfig config, BossTracker tracker)
	{
		this.client = client;
		this.config = config;
		this.tracker = tracker;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.highlightLarvae())
		{
			renderLarvae(graphics);
		}
		if (config.bossTrueTile())
		{
			renderBossTile(graphics);
		}
		if (config.corpseReminder())
		{
			renderCorpse(graphics);
		}
		return null;
	}

	private void renderLarvae(Graphics2D graphics)
	{
		for (NPC larva : tracker.getLarvae())
		{
			Shape poly = larva.getCanvasTilePoly();
			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, config.larvaeColor());
			}
		}
	}

	private void renderBossTile(Graphics2D graphics)
	{
		NPC boss = tracker.getBoss();
		if (boss == null)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, boss.getWorldLocation());
		if (lp == null)
		{
			return;
		}

		int size = boss.getComposition() != null ? boss.getComposition().getSize() : 1;
		// shift from the south west tile to the center of the occupied area
		LocalPoint center = new LocalPoint(
			lp.getX() + (size - 1) * Perspective.LOCAL_TILE_SIZE / 2,
			lp.getY() + (size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

		Polygon poly = Perspective.getCanvasTileAreaPoly(client, center, size);
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, phaseColor());
		}
	}

	private Color phaseColor()
	{
		int hp = tracker.estimatedHp();
		if (hp < 0)
		{
			return Color.WHITE;
		}
		if (hp <= MaggotKingIds.SLAM_PHASE_HP)
		{
			return new Color(255, 80, 80);
		}
		if (hp <= MaggotKingIds.LARVAE_PHASE_HP)
		{
			return new Color(255, 200, 0);
		}
		return new Color(120, 255, 120);
	}

	private void renderCorpse(Graphics2D graphics)
	{
		if (!tracker.isCorpseReminderActive())
		{
			return;
		}

		NPC corpse = tracker.getCorpse();
		Shape poly = corpse.getCanvasTilePoly();
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, new Color(120, 255, 120));
		}

		String action = config.corpsePreference().toString();
		Point textPoint = corpse.getCanvasTextLocation(graphics, action, corpse.getLogicalHeight() + 40);
		if (textPoint != null)
		{
			OverlayUtil.renderTextLocation(graphics, textPoint, action, Color.GREEN);
		}
	}
}
