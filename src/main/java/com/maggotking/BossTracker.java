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

import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;

/**
 * Live fight state, fed by the plugin's event handlers and read by the overlays.
 * Everything here is written and read on the client thread only.
 */
@Singleton
class BossTracker
{
	@Getter
	@Setter
	private NPC boss;

	@Getter
	@Setter
	private NPC corpse;

	@Getter
	private final Set<NPC> larvae = new HashSet<>();

	@Getter
	private final Set<NPC> airborneLarvae = new HashSet<>();

	/** Last attack style seen from the boss. */
	@Getter
	private AttackStyle currentStyle = AttackStyle.UNKNOWN;

	/** True after a screech until the next attack shows the new style. */
	@Getter
	private boolean styleSwapPending;

	@Getter
	private int screechTicks;

	@Getter
	private int slamCueTicks;

	@Getter
	private int corpseReminderTicks;

	@Getter
	@Setter
	private long burnDamage;

	@Getter
	@Setter
	private boolean inArena;

	void setStyle(AttackStyle style)
	{
		currentStyle = style;
		styleSwapPending = false;
	}

	void screechStarted()
	{
		screechTicks = MaggotKingIds.SCREECH_ALERT_TICKS;
		// The boss swaps attack style after every screech; until the next
		// attack the indicator shows the last style as "last" information.
		styleSwapPending = true;
	}

	void slamSeen()
	{
		slamCueTicks = MaggotKingIds.SLAM_CUE_TICKS;
	}

	void corpseSpawned(NPC npc)
	{
		corpse = npc;
		corpseReminderTicks = MaggotKingIds.CORPSE_REMINDER_TICKS;
	}

	boolean isScreechActive()
	{
		return screechTicks > 0;
	}

	boolean isSlamCueActive()
	{
		return slamCueTicks > 0;
	}

	boolean isCorpseReminderActive()
	{
		return corpseReminderTicks > 0 && corpse != null;
	}

	/**
	 * Estimated boss hit points from the health bar ratio, or -1 when the bar
	 * is not currently visible.
	 */
	int estimatedHp()
	{
		if (boss == null || boss.getHealthScale() <= 0 || boss.getHealthRatio() < 0)
		{
			return -1;
		}
		return boss.getHealthRatio() * MaggotKingIds.MAX_HP / boss.getHealthScale();
	}

	/**
	 * Advance one game tick: count down the alert windows.
	 */
	void tick()
	{
		if (screechTicks > 0)
		{
			screechTicks--;
		}
		if (slamCueTicks > 0)
		{
			slamCueTicks--;
		}
		if (corpseReminderTicks > 0)
		{
			corpseReminderTicks--;
		}
	}

	void removeNpc(NPC npc)
	{
		if (npc == boss)
		{
			boss = null;
		}
		if (npc == corpse)
		{
			corpse = null;
		}
		larvae.remove(npc);
		airborneLarvae.remove(npc);

		// the fight is over once neither the boss nor its corpse remains
		if (boss == null && corpse == null)
		{
			inArena = false;
		}
	}

	void reset()
	{
		boss = null;
		corpse = null;
		larvae.clear();
		airborneLarvae.clear();
		currentStyle = AttackStyle.UNKNOWN;
		styleSwapPending = false;
		screechTicks = 0;
		slamCueTicks = 0;
		corpseReminderTicks = 0;
		burnDamage = 0;
	}
}
