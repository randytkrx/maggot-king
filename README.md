# Maggot King

A quality of life plugin for the Maggot King boss. It highlights what is on the
field, shows where the fight stands and keeps your session numbers, while all
decisions during the fight stay with the player.

## Highlights

Larvae are outlined with their tile so you can see where they are crawling.

The boss true tile is outlined and colored by phase, green at full strength,
yellow once the larvae phase is close and red in the slam phase.

After the kill the corpse is highlighted and labelled with your preferred
interaction, either take eggs or open stomach, so you do not wander off
without looting.

Two visual cleanup options reduce the clutter of the fight. Hide screech rocks
removes the carrion litter that rains down during the fight, and hide trees
removes the darkwood scenery around the arena. Both are cosmetic, off by
default, and never touch anything you can interact with, such as the tree you
leave through or the bile you need to avoid.

## Boss information

Boss health is shown with the phase thresholds, larvae at 1250 and slam at
1000, so you always know where the fight stands.

An optional burn counter totals the burn damage you have dealt for fire spell
setups.

## Tracking

The side panel keeps session statistics: kills, deaths, kill times with a
session best and an all time personal best, total loot value, gold per hour and
a breakdown of every maggot egg received. The personal best is stored per game
profile. A reset button starts a fresh session at any time.

## What it does not do

The plugin contains no callouts or alerts for boss mechanics, no attack style
or prayer indication, no hazard tile marking and no projectile landing tiles.
The third party client guidelines prohibit those aids, so reading the boss's
telegraphs stays part of the fight.

## Building

This is a standard RuneLite external plugin. Build it with Gradle:

```
./gradlew build
```
