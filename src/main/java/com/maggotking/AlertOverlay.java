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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Big flashing callouts for the boss's telegraphed mechanics: screech, airborne
 * larvae and the slam. Purely reactive to visible animations; the banners state
 * what the boss is doing and leave the response to the player.
 */
class AlertOverlay extends Overlay
{
	private static final Font ALERT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 28);

	private final Client client;
	private final MaggotKingConfig config;
	private final BossTracker tracker;

	@Inject
	AlertOverlay(Client client, MaggotKingConfig config, BossTracker tracker)
	{
		this.client = client;
		this.config = config;
		this.tracker = tracker;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// flash on and off every couple of client cycles so the banner pulses
		boolean visiblePhase = client.getGameCycle() % 30 < 20;

		if (config.screechAlert() && tracker.isScreechActive())
		{
			if (visiblePhase)
			{
				String text = config.screechText();
				drawBanner(graphics, text.isEmpty() ? "Screech!" : text, Color.WHITE, 0);
			}
			return null;
		}

		int row = 0;
		if (config.airborneAlert() && !tracker.getAirborneLarvae().isEmpty())
		{
			drawBanner(graphics, "Larva airborne", new Color(0, 230, 230), row++);
		}
		if (config.slamCue() && tracker.isSlamCueActive())
		{
			drawBanner(graphics, "Slam!", new Color(255, 200, 0), row);
		}
		return null;
	}

	private void drawBanner(Graphics2D graphics, String text, Color color, int row)
	{
		graphics.setFont(ALERT_FONT);
		FontMetrics metrics = graphics.getFontMetrics();

		int x = client.getViewportXOffset() + (client.getViewportWidth() - metrics.stringWidth(text)) / 2;
		int y = client.getViewportYOffset() + 60 + row * (metrics.getHeight() + 8);

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 2, y + 2);
		graphics.setColor(color);
		graphics.drawString(text, x, y);
	}
}
