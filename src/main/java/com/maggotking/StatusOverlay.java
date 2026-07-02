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
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

class StatusOverlay extends OverlayPanel
{
	private final MaggotKingConfig config;
	private final BossTracker tracker;
	private final SessionStats stats;

	@Inject
	StatusOverlay(MaggotKingConfig config, BossTracker tracker, SessionStats stats)
	{
		this.config = config;
		this.tracker = tracker;
		this.stats = stats;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showStatusOverlay() || (!tracker.isInArena() && tracker.getBoss() == null))
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Maggot King")
			.build());

		if (config.showBossHealth())
		{
			int hp = tracker.estimatedHp();
			if (hp >= 0)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Boss HP")
					.right(hp + " / " + MaggotKingIds.MAX_HP)
					.build());
			}
		}

		if (config.burnCounter() && tracker.getBurnDamage() > 0)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Burn dmg")
				.right(String.valueOf(tracker.getBurnDamage()))
				.rightColor(new Color(255, 140, 0))
				.build());
		}

		if (config.showSessionStats())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Kills")
				.right(String.valueOf(stats.getKillCount()))
				.build());
			if (stats.getLastKillTime() != null)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Last kill")
					.right(MaggotKingPlugin.formatDuration(stats.getLastKillTime()))
					.build());
			}
			if (stats.getSessionBest() != null)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Best")
					.right(MaggotKingPlugin.formatDuration(stats.getSessionBest()))
					.build());
			}
			if (stats.getLootValue() > 0)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Loot")
					.right(QuantityFormatter.quantityToStackSize(stats.getLootValue()) + " gp")
					.build());
			}
		}

		return super.render(graphics);
	}
}
