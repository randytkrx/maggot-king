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

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.HitsplatID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Maggot King",
	description = "Highlights and session tracking for the Maggot King boss",
	tags = {"maggot", "king", "boss", "pvm", "overlay", "larvae", "tracker"}
)
public class MaggotKingPlugin extends Plugin implements RenderCallback
{
	/** Zone array index offset for the extended scene padding. */
	private static final int ZONE_OFFSET = (Constants.EXTENDED_SCENE_SIZE - Constants.SCENE_SIZE) / 2 >> 3;

	private static final String PB_KEY = "personalBest";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MaggotKingConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SceneOverlay sceneOverlay;

	@Inject
	private StatusOverlay statusOverlay;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private BossTracker tracker;

	@Inject
	private SessionStats stats;

	@Inject
	private RenderCallbackManager renderCallbackManager;

	private MaggotKingPanel panel;
	private NavigationButton navButton;

	/**
	 * The corpse is looted by interacting with it rather than through a kill
	 * drop, so no loot event fires for it. Loot is captured by snapshotting
	 * the inventory when the corpse is clicked and diffing what arrives.
	 */
	private Map<Integer, Integer> lootSnapshot;
	private int lootWindowTicks;

	@Provides
	MaggotKingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MaggotKingConfig.class);
	}

	@Override
	protected void startUp()
	{
		panel = new MaggotKingPanel(this, stats);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		navButton = NavigationButton.builder()
			.tooltip("Maggot King")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);

		overlayManager.add(sceneOverlay);
		overlayManager.add(statusOverlay);
		renderCallbackManager.register(this);
		clientThread.invokeLater(this::invalidateHideableZones);
	}

	@Override
	protected void shutDown()
	{
		renderCallbackManager.unregister(this);
		clientThread.invokeLater(this::invalidateHideableZones);
		overlayManager.remove(sceneOverlay);
		overlayManager.remove(statusOverlay);
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
		tracker.reset();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();
		switch (npc.getId())
		{
			case MaggotKingIds.BOSS:
				tracker.setBoss(npc);
				tracker.setInArena(true);
				stats.killStarted(Instant.now());
				break;
			case MaggotKingIds.LARVA:
				tracker.getLarvae().add(npc);
				break;
			case MaggotKingIds.CORPSE:
				tracker.corpseSpawned(npc);
				break;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		tracker.removeNpc(event.getNpc());
	}

	/**
	 * Render callback: suppress the hidden scenery at draw time. Called from
	 * the map loader thread, so this only reads config and static id sets.
	 */
	@Override
	public boolean drawObject(Scene scene, TileObject object)
	{
		return !isHiddenObject(object.getId());
	}

	private boolean isHiddenObject(int id)
	{
		return (config.hideScreechRocks() && MaggotKingIds.SCREECH_ROCK_OBJECTS.contains(id))
			|| (config.hideTrees() && MaggotKingIds.isTree(id));
	}

	/** Any id one of the hide options covers, regardless of current toggles. */
	private static boolean isHideableObject(int id)
	{
		return MaggotKingIds.SCREECH_ROCK_OBJECTS.contains(id)
			|| MaggotKingIds.isTree(id);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!MaggotKingConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		final String key = event.getKey();
		if ("hideTrees".equals(key) || "hideScreechRocks".equals(key))
		{
			// redraw the affected scene zones so the change applies at once
			clientThread.invokeLater(this::invalidateHideableZones);
		}
	}

	/**
	 * Invalidate every scene zone containing an object one of the hide options
	 * covers, so the renderer rebuilds those zones with the current settings.
	 * Only zones where a matching object is found are touched; those are
	 * guaranteed to be initialized by the renderer.
	 */
	private void invalidateHideableZones()
	{
		final WorldView wv = client.getTopLevelWorldView();
		final DrawCallbacks dc = client.getDrawCallbacks();
		if (wv == null || dc == null)
		{
			return;
		}

		final Scene scene = wv.getScene();
		final Set<Long> done = new HashSet<>();
		for (Tile[][] plane : scene.getTiles())
		{
			if (plane == null)
			{
				continue;
			}
			for (Tile[] column : plane)
			{
				if (column == null)
				{
					continue;
				}
				for (Tile tile : column)
				{
					if (tile == null)
					{
						continue;
					}
					final TileObject match = findHideableObject(tile);
					if (match != null)
					{
						invalidateZoneForObject(scene, dc, match, done);
					}
				}
			}
		}
	}

	private static TileObject findHideableObject(Tile tile)
	{
		final GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null)
		{
			for (GameObject gameObject : gameObjects)
			{
				if (gameObject != null && isHideableObject(gameObject.getId()))
				{
					return gameObject;
				}
			}
		}
		return null;
	}

	private static void invalidateZoneForObject(Scene scene, DrawCallbacks dc, TileObject object, Set<Long> done)
	{
		// scene tile coordinates from the object hash (bits 0-6 x, 7-13 z)
		final long hash = object.getHash();
		final int zx = ((int) (hash & 127) >> 3) + ZONE_OFFSET;
		final int zz = ((int) ((hash >> 7) & 127) >> 3) + ZONE_OFFSET;
		final long key = ((long) zx << 32) | zz;
		if (done.add(key))
		{
			dc.invalidateZone(scene, zx, zz);
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		final Actor actor = event.getActor();
		if (actor == tracker.getBoss())
		{
			Duration time = stats.killFinished(Instant.now());
			if (time != null)
			{
				updatePersonalBest(time);
			}
			refreshPanel();
		}
		else if (actor == client.getLocalPlayer() && tracker.isInArena())
		{
			stats.addDeath();
			refreshPanel();
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() == tracker.getBoss()
			&& event.getHitsplat().getHitsplatType() == HitsplatID.BURN)
		{
			tracker.setBurnDamage(tracker.getBurnDamage() + event.getHitsplat().getAmount());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.HOPPING)
		{
			tracker.reset();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		tracker.tick();
		if (lootWindowTicks > 0 && --lootWindowTicks == 0)
		{
			lootSnapshot = null;
		}
		refreshPanel();
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event)
	{
		final int npcId = event.getNpc().getId();
		if (npcId != MaggotKingIds.BOSS && npcId != MaggotKingIds.CORPSE)
		{
			return;
		}

		for (ItemStack item : event.getItems())
		{
			addLoot(item.getId(), item.getQuantity());
		}
		refreshPanel();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// arm the loot capture window when the corpse is interacted with
		if (tracker.getCorpse() != null && event.getMenuEntry().getNpc() == tracker.getCorpse())
		{
			lootSnapshot = snapshotInventory();
			lootWindowTicks = 10;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (lootWindowTicks <= 0 || lootSnapshot == null || event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		boolean gainedAny = false;
		final Map<Integer, Integer> now = snapshotInventory();
		for (Map.Entry<Integer, Integer> entry : now.entrySet())
		{
			final int gained = entry.getValue() - lootSnapshot.getOrDefault(entry.getKey(), 0);
			if (gained > 0)
			{
				addLoot(entry.getKey(), gained);
				gainedAny = true;
			}
		}

		if (gainedAny)
		{
			lootWindowTicks = 0;
			lootSnapshot = null;
			refreshPanel();
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		// loot that spills onto the floor beside the corpse
		if (lootWindowTicks > 0
			&& tracker.getCorpse() != null
			&& event.getTile().getWorldLocation().distanceTo(tracker.getCorpse().getWorldLocation()) <= 3)
		{
			addLoot(event.getItem().getId(), event.getItem().getQuantity());
			refreshPanel();
		}
	}

	private void addLoot(int itemId, int quantity)
	{
		stats.addLoot(itemId, quantity, itemValue(itemId) * quantity);
	}

	/**
	 * Coins are worth their face value and untradeables have no exchange
	 * price, so fall back to the high alchemy value for those.
	 */
	private long itemValue(int itemId)
	{
		if (itemId == MaggotKingIds.COINS)
		{
			return 1;
		}
		final int gePrice = itemManager.getItemPrice(itemId);
		if (gePrice > 0)
		{
			return gePrice;
		}
		return itemManager.getItemComposition(itemId).getHaPrice();
	}

	private Map<Integer, Integer> snapshotInventory()
	{
		final Map<Integer, Integer> counts = new HashMap<>();
		final ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory != null)
		{
			for (Item item : inventory.getItems())
			{
				if (item.getId() != -1)
				{
					counts.merge(item.getId(), item.getQuantity(), Integer::sum);
				}
			}
		}
		return counts;
	}

	/**
	 * Reset the session totals from the panel button.
	 */
	public void resetSession()
	{
		stats.reset();
		refreshPanel();
	}

	/**
	 * All time personal best in seconds from the current RS profile, or null.
	 */
	Duration loadPersonalBest()
	{
		Long seconds = configManager.getRSProfileConfiguration(MaggotKingConfig.GROUP, PB_KEY, Long.class);
		return seconds != null ? Duration.ofSeconds(seconds) : null;
	}

	private void updatePersonalBest(Duration killTime)
	{
		Duration pb = loadPersonalBest();
		if (pb == null || killTime.compareTo(pb) < 0)
		{
			configManager.setRSProfileConfiguration(MaggotKingConfig.GROUP, PB_KEY, killTime.getSeconds());
		}
	}

	private void refreshPanel()
	{
		if (panel != null)
		{
			Duration pb = loadPersonalBest();
			SwingUtilities.invokeLater(() -> panel.refresh(pb));
		}
	}

	/**
	 * Format a duration as H:MM:SS (hours dropped when zero).
	 */
	static String formatDuration(Duration duration)
	{
		long seconds = Math.max(0, duration.getSeconds());
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		long secs = seconds % 60;
		if (hours > 0)
		{
			return String.format("%d:%02d:%02d", hours, minutes, secs);
		}
		return String.format("%d:%02d", minutes, secs);
	}
}
