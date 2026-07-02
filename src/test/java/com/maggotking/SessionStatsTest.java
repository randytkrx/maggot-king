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

import java.time.Duration;
import java.time.Instant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class SessionStatsTest
{
	@Test
	public void countsKillsAndTimes()
	{
		SessionStats stats = new SessionStats();
		Instant start = Instant.now();

		stats.killStarted(start);
		Duration first = stats.killFinished(start.plusSeconds(95));
		assertEquals(Duration.ofSeconds(95), first);

		stats.killStarted(start.plusSeconds(120));
		Duration second = stats.killFinished(start.plusSeconds(200));
		assertEquals(Duration.ofSeconds(80), second);

		assertEquals(2, stats.getKillCount());
		assertEquals(Duration.ofSeconds(80), stats.getSessionBest());
		assertEquals(Duration.ofSeconds(80), stats.getLastKillTime());
	}

	@Test
	public void killWithoutStartStillCounts()
	{
		SessionStats stats = new SessionStats();
		assertNull(stats.killFinished(Instant.now()));
		assertEquals(1, stats.getKillCount());
		assertNull(stats.getSessionBest());
	}

	@Test
	public void tracksEggLoot()
	{
		SessionStats stats = new SessionStats();
		stats.addLoot(33665, 1, 50000); // maggot egg
		stats.addLoot(33665, 1, 50000);
		stats.addLoot(995, 12000, 12000); // coins are loot but not eggs
		assertEquals(2, stats.totalEggs());
		assertEquals(2, (int) stats.getEggCounts().get(33665));
		assertEquals(112000, stats.getLootValue());
	}

	@Test
	public void resetClearsEverything()
	{
		SessionStats stats = new SessionStats();
		stats.killStarted(Instant.now());
		stats.killFinished(Instant.now());
		stats.addDeath();
		stats.addLoot(33665, 1, 50000);
		stats.reset();
		assertEquals(0, stats.getKillCount());
		assertEquals(0, stats.getDeaths());
		assertEquals(0, stats.getLootValue());
		assertEquals(0, stats.totalEggs());
		assertNull(stats.getSessionBest());
	}

	@Test
	public void formatsDurations()
	{
		assertEquals("1:05", MaggotKingPlugin.formatDuration(Duration.ofSeconds(65)));
		assertEquals("1:01:05", MaggotKingPlugin.formatDuration(Duration.ofSeconds(3665)));
	}
}
