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
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(MaggotKingConfig.GROUP)
public interface MaggotKingConfig extends Config
{
	String GROUP = "maggotking";

	@ConfigSection(
		name = "Reminders",
		description = "Post kill reminders",
		position = 0
	)
	String alerts = "alerts";

	@ConfigSection(
		name = "Highlights",
		description = "NPC and tile highlighting",
		position = 1
	)
	String highlights = "highlights";

	@ConfigSection(
		name = "Boss info",
		description = "Health information",
		position = 2
	)
	String bossInfo = "bossInfo";

	@ConfigSection(
		name = "Tracking",
		description = "Kill and loot tracking",
		position = 3
	)
	String tracking = "tracking";

	// Reminders

	@ConfigItem(
		keyName = "corpseReminder",
		name = "Corpse highlight",
		description = "Highlight the corpse after the kill with your preferred interaction",
		section = alerts,
		position = 0
	)
	default boolean corpseReminder()
	{
		return true;
	}

	@ConfigItem(
		keyName = "corpsePreference",
		name = "Corpse preference",
		description = "Which corpse interaction to label on the highlight",
		section = alerts,
		position = 1
	)
	default CorpsePreference corpsePreference()
	{
		return CorpsePreference.TAKE_EGGS;
	}

	// Highlights

	@ConfigItem(
		keyName = "highlightLarvae",
		name = "Highlight larvae",
		description = "Outline larvae tiles",
		section = highlights,
		position = 0
	)
	default boolean highlightLarvae()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "larvaeColor",
		name = "Larvae color",
		description = "Color for larvae",
		section = highlights,
		position = 1
	)
	default Color larvaeColor()
	{
		return new Color(255, 200, 0, 150);
	}

	@ConfigItem(
		keyName = "bossTrueTile",
		name = "Boss true tile",
		description = "Outline the tiles the boss actually occupies",
		section = highlights,
		position = 3
	)
	default boolean bossTrueTile()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "trueTileColor",
		name = "True tile color",
		description = "Color of the boss true tile outline",
		section = highlights,
		position = 4
	)
	default Color trueTileColor()
	{
		return new Color(255, 255, 255, 160);
	}

	@ConfigItem(
		keyName = "hideScreechRocks",
		name = "Hide screech rocks",
		description = "Remove the rocks that rain down and litter the arena after each screech",
		section = highlights,
		position = 5
	)
	default boolean hideScreechRocks()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideTrees",
		name = "Hide trees",
		description = "Hide the darkwood trees around the arena",
		section = highlights,
		position = 6
	)
	default boolean hideTrees()
	{
		return false;
	}

	// Boss info

	@ConfigItem(
		keyName = "showStatusOverlay",
		name = "Show status overlay",
		description = "Show the boss health and session panel on screen",
		section = bossInfo,
		position = 0
	)
	default boolean showStatusOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBossHealth",
		name = "Boss health",
		description = "Show the boss's health in the overlay",
		section = bossInfo,
		position = 2
	)
	default boolean showBossHealth()
	{
		return true;
	}

	@ConfigItem(
		keyName = "burnCounter",
		name = "Burn damage counter",
		description = "Count burn damage dealt to the boss for fire spell setups",
		section = bossInfo,
		position = 3
	)
	default boolean burnCounter()
	{
		return false;
	}

	// Tracking

	@ConfigItem(
		keyName = "showSessionStats",
		name = "Session stats in overlay",
		description = "Show kill count, kill times and loot in the status overlay",
		section = tracking,
		position = 0
	)
	default boolean showSessionStats()
	{
		return true;
	}

	enum CorpsePreference
	{
		TAKE_EGGS("Take eggs"),
		OPEN_STOMACH("Open stomach");

		private final String action;

		CorpsePreference(String action)
		{
			this.action = action;
		}

		@Override
		public String toString()
		{
			return action;
		}
	}
}
