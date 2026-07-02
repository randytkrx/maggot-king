# Maggot King

A helper plugin for the Maggot King boss. It detects and displays what the boss
is doing so the telegraphs are hard to miss in the visual chaos of the fight.
It never acts for you and never tells you what to do: every feature shows game
state, and all decisions and clicks stay with the player.

## Alerts

Screech callout. When the boss uses its screech the plugin flashes a large
callout and mirrors the white aura on the boss outline, so the telegraph is
impossible to miss. The callout text is configurable and defaults to Screech!

Airborne larva callout. Larvae that take flight are flagged on screen and a
callout is shown while one is in the air.

Slam callout. A flash when the boss uses its slam. It reacts to the slam
animation only and never predicts a slam before the boss telegraphs it.

Corpse highlight. After the kill the corpse is highlighted and labelled with
your preferred interaction, either take eggs or open stomach.

## Highlights

Larvae are outlined with their tile so you can see where they are crawling.
Airborne larvae get their own color.

The boss true tile is outlined and colored by phase, green at full strength,
yellow once the larvae phase is close and red in the slam phase. During a
screech the outline turns white to match the aura.

Two visual cleanup options reduce the clutter of the fight. Hide screech rocks
removes the carrion litter that rains down after each screech, and hide trees
removes the darkwood scenery around the arena. Both are cosmetic, off by
default, and never touch anything you can interact with, such as the tree you
leave through or the bile you need to avoid.

The plugin deliberately does not mark projectile landing tiles or danger
areas. The third party client guidelines prohibit showing impact locations or
automatically indicating where to stand, so reading the ground telegraphs
stays part of the fight.

## Boss information

An attack style indicator shows the last attack style observed from the boss.
It only ever reports what has already happened: it starts empty each kill and
fills in once the boss attacks. The boss opens every fight with ranged and
swaps styles after each screech, so while a swap is pending the indicator
labels the style as the last one seen and lets you draw your own conclusion
from the pattern.

Boss health is shown with the phase thresholds, larvae at 1250 and slam at
1000, so you always know where the fight stands.

An optional burn counter totals the burn damage dealt for fire spell setups.

## Tracking

The side panel keeps session statistics: kills, deaths, kill times with a
session best and an all time personal best, total loot value, gold per hour and
a breakdown of every maggot egg received. The personal best is stored per game
profile. A reset button starts a fresh session at any time.

## Building

This is a standard RuneLite external plugin. Build it with Gradle:

```
./gradlew build
```
