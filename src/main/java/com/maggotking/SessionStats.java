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

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import lombok.Getter;

/**
 * Session totals: kills, deaths, kill times, loot value and egg outcomes.
 * Plain state so it is easy to unit test; the plugin feeds it events.
 */
@Singleton
class SessionStats
{
	@Getter
	private Instant sessionStart = Instant.now();

	@Getter
	private int killCount;

	@Getter
	private int deaths;

	@Getter
	private long lootValue;

	/** Egg item id to number received this session. */
	@Getter
	private final Map<Integer, Integer> eggCounts = new HashMap<>();

	@Getter
	private Duration lastKillTime;

	@Getter
	private Duration sessionBest;

	/** When the last kill, death or loot happened, for the idle reset. */
	@Getter
	private Instant lastActivity = Instant.now();

	private Instant killStart;

	/** True when nothing has been recorded this session yet. */
	boolean isEmpty()
	{
		return killCount == 0 && deaths == 0 && lootValue == 0;
	}

	/**
	 * A kill attempt began (boss spawned or first engaged).
	 */
	void killStarted(Instant now)
	{
		killStart = now;
	}

	/**
	 * The boss died. Returns the kill duration, or null if the start was never seen.
	 */
	Duration killFinished(Instant now)
	{
		killCount++;
		lastActivity = now;
		if (killStart == null)
		{
			return null;
		}
		Duration time = Duration.between(killStart, now);
		killStart = null;
		lastKillTime = time;
		if (sessionBest == null || time.compareTo(sessionBest) < 0)
		{
			sessionBest = time;
		}
		return time;
	}

	void addDeath()
	{
		deaths++;
		lastActivity = Instant.now();
	}

	void addLoot(int itemId, int quantity, long value)
	{
		lootValue += value;
		lastActivity = Instant.now();
		if (MaggotKingIds.EGGS.contains(itemId))
		{
			eggCounts.merge(itemId, quantity, Integer::sum);
		}
	}

	int totalEggs()
	{
		return eggCounts.values().stream().mapToInt(Integer::intValue).sum();
	}

	long gpPerHour()
	{
		long seconds = Duration.between(sessionStart, Instant.now()).getSeconds();
		if (seconds <= 0)
		{
			return 0;
		}
		return lootValue * 3600L / seconds;
	}

	void reset()
	{
		sessionStart = Instant.now();
		killCount = 0;
		deaths = 0;
		lootValue = 0;
		eggCounts.clear();
		lastKillTime = null;
		sessionBest = null;
		killStart = null;
		lastActivity = Instant.now();
	}
}
