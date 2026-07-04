# Maggot King

A quality of life plugin for the Maggot King boss. It highlights what is on the
field and keeps your session numbers, while all decisions during the fight stay
with the player.

## Highlights

Larvae are outlined with their tile so you can see where they are crawling.

The boss true tile is outlined in a configurable color.

After the kill the corpse is highlighted and labelled with your preferred
interaction, either take eggs or open stomach, so you do not wander off
without looting.

Two visual cleanup options reduce the clutter of the fight. Hide screech rocks
removes the carrion litter that rains down during the fight, and hide trees
removes the darkwood scenery around the arena. Both are cosmetic, off by
default, and never touch anything you can interact with, such as the tree you
leave through or the bile you need to avoid.

An optional block exit door left click option demotes the exit door's left
click action in the arena, so an accidental click mid fight does not end the
instance. It is off by default, adds no new options and removes none — the
door is still one right-click away.

## Boss information

Boss health is shown as a plain number in the overlay.

An optional burn counter totals the burn damage you have dealt for fire spell
setups.

## Tracking

The side panel keeps session statistics: kills, deaths, kill times with a
session best and an all time personal best, total loot value, gold per hour and
a breakdown of every maggot egg received. The personal best is stored per game
profile. A reset button starts a fresh session at any time.

## What it does not do

The plugin contains no callouts or alerts for boss mechanics, no attack style
or prayer indication, no phase indicators or thresholds, no hazard tile marking
and no projectile landing tiles.
The third party client guidelines prohibit those aids, so reading the boss's
telegraphs stays part of the fight.

## Building

This is a standard RuneLite external plugin. Build it with Gradle:

```
./gradlew build
```
