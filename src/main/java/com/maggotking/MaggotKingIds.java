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

import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * All Maggot King game ids in one place. Values are numeric literals because the
 * boss shipped after the latest stable runelite-api release; gameval names are
 * noted where the cache tags them.
 */
final class MaggotKingIds
{
	private MaggotKingIds()
	{
	}

	// NPCs (gameval NpcID)
	static final int BOSS = 15742; // MAGGOT_KING
	static final int CORPSE = 15741; // MAGGOT_KING_CORPSE
	static final int LARVA = 15743; // UR_MAGGOT_LARVAE

	/*
	 * NOTE: the "Odd tree" NPC (16239) is the way OUT of the arena and must
	 * never be hidden, so tree hiding covers scenery objects only.
	 */

	/**
	 * The rock game objects that rain down and litter the arena after each
	 * screech, observed in game (not name tagged in the cache). Removable for
	 * visual clarity.
	 */
	static final Set<Integer> SCREECH_ROCK_OBJECTS = ImmutableSet.of(33425);

	/**
	 * The darkwood and fallen tree scenery around the arena, observed in game
	 * as ids 40788-40825 plus the 61049 darkwood cluster.
	 */
	static boolean isTree(int objectId)
	{
		return (objectId >= 40788 && objectId <= 40825) || objectId == 61049;
	}

	/** Coins (gameval ItemID.COINS). */
	static final int COINS = 995;

	// Egg loot (gameval ItemID)
	static final Set<Integer> EGGS = ImmutableSet.of(
		33665, // MAGGOT_EGG
		33667, // SICKLY_MAGGOT_EGG
		33669, // WARM_MAGGOT_EGG
		33671, // PULSATING_MAGGOT_EGG
		33673, // WRIGGLING_MAGGOT_EGG
		33675  // WRITHING_MAGGOT_EGG
	);

	// Boss health, confirmed in game
	static final int MAX_HP = 1500;

	/** How many ticks the loot reminder stays up after the corpse appears. */
	static final int CORPSE_REMINDER_TICKS = 50;
}
