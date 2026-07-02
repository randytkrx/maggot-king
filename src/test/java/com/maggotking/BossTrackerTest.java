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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BossTrackerTest
{
	@Test
	public void screechMarksStyleAsSwapping()
	{
		BossTracker tracker = new BossTracker();
		tracker.setStyle(AttackStyle.RANGED);
		assertFalse(tracker.isStyleSwapPending());

		tracker.screechStarted();
		assertTrue(tracker.isStyleSwapPending());
		assertTrue(tracker.isScreechActive());
		// the last seen style stays available while the swap is pending
		assertEquals(AttackStyle.RANGED, tracker.getCurrentStyle());

		tracker.setStyle(AttackStyle.MAGIC);
		assertFalse(tracker.isStyleSwapPending());
		assertEquals(AttackStyle.MAGIC, tracker.getCurrentStyle());
	}

	@Test
	public void alertWindowsExpire()
	{
		BossTracker tracker = new BossTracker();
		tracker.screechStarted();
		tracker.slamSeen();

		for (int i = 0; i < MaggotKingIds.SCREECH_ALERT_TICKS; i++)
		{
			tracker.tick();
		}

		assertFalse(tracker.isScreechActive());
		assertFalse(tracker.isSlamCueActive());
	}

	@Test
	public void resetClearsState()
	{
		BossTracker tracker = new BossTracker();
		tracker.setStyle(AttackStyle.RANGED);
		tracker.screechStarted();
		tracker.setBurnDamage(120);
		tracker.reset();

		assertEquals(AttackStyle.UNKNOWN, tracker.getCurrentStyle());
		assertFalse(tracker.isScreechActive());
		assertEquals(0, tracker.getBurnDamage());
	}
}
