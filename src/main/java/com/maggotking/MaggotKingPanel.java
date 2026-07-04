/*
 * Copyright (c) 2026, s59
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.QuantityFormatter;

class MaggotKingPanel extends PluginPanel
{
	/** Egg item id to display name, in drop table order. */
	private static final Map<Integer, String> EGG_NAMES = new LinkedHashMap<>();

	static
	{
		EGG_NAMES.put(33665, "Maggot egg");
		EGG_NAMES.put(33667, "Sickly egg");
		EGG_NAMES.put(33669, "Warm egg");
		EGG_NAMES.put(33671, "Pulsating egg");
		EGG_NAMES.put(33673, "Wriggling egg");
		EGG_NAMES.put(33675, "Writhing egg");
	}

	private final SessionStats stats;

	private final JLabel timeValue = valueLabel();
	private final JLabel killsValue = valueLabel();
	private final JLabel deathsValue = valueLabel();
	private final JLabel lastKillValue = valueLabel();
	private final JLabel bestValue = valueLabel();
	private final JLabel pbValue = valueLabel();
	private final JLabel lootValue = valueLabel();
	private final JLabel gpHourValue = valueLabel();
	private final Map<Integer, JLabel> eggValues = new LinkedHashMap<>();

	MaggotKingPanel(MaggotKingPlugin plugin, SessionStats stats)
	{
		this.stats = stats;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		content.add(header("Maggot King"));
		content.add(Box.createVerticalStrut(10));

		content.add(statRow("Session time", timeValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Kills", killsValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Deaths", deathsValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Last kill", lastKillValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Session best", bestValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Personal best", pbValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("Loot value", lootValue));
		content.add(Box.createVerticalStrut(4));
		content.add(statRow("GP per hour", gpHourValue));
		content.add(Box.createVerticalStrut(12));

		content.add(header("Eggs"));
		content.add(Box.createVerticalStrut(6));
		for (Map.Entry<Integer, String> egg : EGG_NAMES.entrySet())
		{
			JLabel value = valueLabel();
			eggValues.put(egg.getKey(), value);
			content.add(statRow(egg.getValue(), value));
			content.add(Box.createVerticalStrut(4));
		}
		content.add(Box.createVerticalStrut(8));

		JButton reset = new JButton("Reset session");
		reset.setFocusPainted(false);
		reset.setAlignmentX(Component.LEFT_ALIGNMENT);
		reset.addActionListener(e -> plugin.resetSession());
		content.add(reset);

		add(content, BorderLayout.NORTH);
		refresh(null);
	}

	/**
	 * Pull the latest totals into the labels. Must be called on the EDT.
	 */
	void refresh(Duration personalBest)
	{
		timeValue.setText(MaggotKingPlugin.formatDuration(
			Duration.between(stats.getSessionStart(), Instant.now())));
		killsValue.setText(String.valueOf(stats.getKillCount()));
		deathsValue.setText(String.valueOf(stats.getDeaths()));
		lastKillValue.setText(formatOrDash(stats.getLastKillTime()));
		bestValue.setText(formatOrDash(stats.getSessionBest()));
		pbValue.setText(formatOrDash(personalBest));
		lootValue.setText(QuantityFormatter.quantityToStackSize(stats.getLootValue()) + " gp");
		gpHourValue.setText(QuantityFormatter.quantityToStackSize(stats.gpPerHour()) + " gp");

		for (Map.Entry<Integer, JLabel> entry : eggValues.entrySet())
		{
			entry.getValue().setText(String.valueOf(
				stats.getEggCounts().getOrDefault(entry.getKey(), 0)));
		}
	}

	private static String formatOrDash(Duration duration)
	{
		return duration != null ? MaggotKingPlugin.formatDuration(duration) : "-";
	}

	private static JLabel header(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setForeground(Color.WHITE);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	private static JPanel statRow(String label, JLabel value)
	{
		JPanel row = new JPanel(new GridLayout(1, 2));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel name = new JLabel(label);
		name.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		name.setFont(FontManager.getRunescapeSmallFont());
		row.add(name);
		row.add(value);
		return row;
	}

	private static JLabel valueLabel()
	{
		JLabel label = new JLabel("0");
		label.setForeground(Color.WHITE);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}
}
