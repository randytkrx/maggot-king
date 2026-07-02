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
 * boss shipped after the latest stable runelite-api release; the gameval constant
 * each value maps to (from cache rev 239, 2026-06-30) is noted alongside.
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

	// Animations (gameval AnimationID), confirmed in game 2026-07-02
	static final int ANIM_SCREECH = 13922; // MAGGOT_KING_SCREECH
	static final int ANIM_RANGED = 13933; // MAGGOT_KING_RANGEATTACK
	static final int ANIM_MAGIC = 13934; // MAGGOT_KING_MAGEATTACK
	static final int ANIM_LARVA_FLY = 13914; // UR_MAGGOT_LARVAE_FLY
	static final Set<Integer> ANIM_DEATH = ImmutableSet.of(13936, 13937, 13938); // NPC_MAGGOT_KING_DEATH_01..03

	/**
	 * The slam. The cache names this animation MAGGOT_KING_MELEEATTACK but it is
	 * only used in the slam phase and spawns the 3x5 ground graphics (3998); the
	 * boss's normal rotation is ranged and magic only.
	 */
	static final int ANIM_SLAM = 13925;

	/**
	 * The rock game objects that rain down and litter the arena after each
	 * screech, observed in game (not name tagged in the cache). Removable for
	 * visual clarity.
	 */
	static final Set<Integer> SCREECH_ROCK_OBJECTS = ImmutableSet.of(33425);

	/*
	 * NOTE: the "Odd tree" NPC (16239) is the way OUT of the arena and must
	 * never be hidden, so tree hiding covers scenery objects only.
	 */

	/**
	 * The darkwood and fallen tree scenery around the arena, observed in game
	 * as ids 40788-40825 plus the 61049 darkwood cluster.
	 */
	static boolean isTree(int objectId)
	{
		return (objectId >= 40788 && objectId <= 40825) || objectId == 61049;
	}

	/**
	 * The gore decals carpeting the arena floor, observed in game as ground
	 * objects 40832-40842, 60850, 60932 and 61065-61095. Currently unused: the
	 * hide floor option was shelved because ground decals do not go through
	 * the object draw callback; kept for when that feature is revisited.
	 */
	static boolean isFloorDecal(int objectId)
	{
		return (objectId >= 40832 && objectId <= 40842)
			|| objectId == 60850
			|| objectId == 60932
			|| (objectId >= 61065 && objectId <= 61095);
	}

	/*
	 * Deliberately NOT used: projectile landing tiles and ground hazard
	 * graphics (blood rain telegraphs, spit impacts, the slam area). Jagex's
	 * third party client guidelines prohibit showing projectile impact
	 * locations and anything that automatically indicates where to stand or
	 * not to stand, so this plugin does not mark danger tiles.
	 *
	 * Also unused: VarbitID MAGGOT_KING_ENTERED (15449) is a one time "has
	 * entered" unlock flag that never resets, so it cannot be used for
	 * presence detection; boss presence gates the plugin instead.
	 */

	// Egg loot (gameval ItemID)
	static final Set<Integer> EGGS = ImmutableSet.of(
		33665, // MAGGOT_EGG
		33667, // SICKLY_MAGGOT_EGG
		33669, // WARM_MAGGOT_EGG
		33671, // PULSATING_MAGGOT_EGG
		33673, // WRIGGLING_MAGGOT_EGG
		33675  // WRITHING_MAGGOT_EGG
	);

	// Boss health, confirmed in game from damage vs health bar ratio (the boss
	// heals during the larvae phase, which the live estimate handles).
	static final int MAX_HP = 1500;
	static final int LARVAE_PHASE_HP = 1250;
	static final int SLAM_PHASE_HP = 1000;

	/** How many ticks the screech callout stays up after the animation starts. */
	static final int SCREECH_ALERT_TICKS = 8;

	/** How many ticks the slam punish window cue stays up. */
	static final int SLAM_CUE_TICKS = 4;

	/** How many ticks the loot reminder stays up after the corpse appears. */
	static final int CORPSE_REMINDER_TICKS = 50;
}
