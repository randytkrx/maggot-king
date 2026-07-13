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

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.runelite.api.coords.WorldPoint;

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

	/** Arena region for the boundary tiles. */
	static final int ARENA_REGION = 11645;

	/**
	 * The arena edge tiles (region relative), resolved to instance world points
	 * at render time. Used to draw the border when trees are hidden.
	 */
	static final List<WorldPoint> ARENA_BORDER = buildArenaBorder();

	private static List<WorldPoint> buildArenaBorder()
	{
		final int[][] tiles =
		{
			{23, 32}, {24, 32}, {25, 32}, {26, 32}, {27, 32}, {28, 32}, {29, 32},
			{30, 32}, {31, 32}, {32, 32}, {33, 32}, {34, 32}, {35, 32}, {36, 32},
			{37, 32}, {38, 32}, {39, 32},
			{23, 33}, {23, 34}, {23, 35},
			{39, 33}, {39, 34}, {39, 35}, {39, 36}
		};
		final List<WorldPoint> border = new ArrayList<>(tiles.length);
		for (int[] t : tiles)
		{
			border.add(WorldPoint.fromRegion(ARENA_REGION, t[0], t[1], 0));
		}
		return border;
	}

	enum Edge
	{
		NORTH, EAST, SOUTH, WEST
	}

	/** A single edge line of the arena border, drawn along one side of a tile. */
	static final class BorderLine
	{
		final WorldPoint tile;
		final Edge edge;

		BorderLine(int regionX, int regionY, Edge edge)
		{
			this.tile = WorldPoint.fromRegion(ARENA_REGION, regionX, regionY, 0);
			this.edge = edge;
		}
	}

	/**
	 * The arena border as thin edge lines, from a line marker export. Draws a
	 * west, south and east edge around the arena.
	 */
	static final List<BorderLine> ARENA_BORDER_LINES = buildBorderLines();

	private static List<BorderLine> buildBorderLines()
	{
		final List<BorderLine> lines = new ArrayList<>();
		// west edge, region x 24, y 33-36
		for (int y = 33; y <= 36; y++)
		{
			lines.add(new BorderLine(24, y, Edge.WEST));
		}
		// south edge, y 33, x 24-38
		for (int x = 24; x <= 38; x++)
		{
			lines.add(new BorderLine(x, 33, Edge.SOUTH));
		}
		// east edge, x 38, y 33-36
		for (int y = 33; y <= 36; y++)
		{
			lines.add(new BorderLine(38, y, Edge.EAST));
		}
		return lines;
	}

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
	 * as ids 40788-40825. The 61049 exit door is handled separately.
	 */
	static boolean isTree(int objectId)
	{
		return objectId >= 40788 && objectId <= 40825;
	}

	/** The quick exit door object in the arena. */
	static final int EXIT_DOOR = 61049;

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
