# Herobrine Ritual (Fabric, Minecraft 1.21.11)

Ek harmless Herobrine: ritual se summon hota hai, koi AI nahi (kuch nahi karta), aur uske spawn point ke around nonstop bijli girti rehti hai.

## Ritual

Zameen par (upar se dekho):

```
G G G
G M G     G = Gold Block (8 total)
G G G     M = Mossy Cobblestone (beech me)
```

1. Beech wale Mossy Cobblestone ke **upar 1 Netherrack** rakho.
2. Netherrack ke **upar wale face par Flint and Steel** se right-click karo.
3. Herobrine fire ke upar spawn hoga aur bijli girna shuru.

## Settings (HerobrineEntity.java ke upar constants)

- `STRIKE_INTERVAL_TICKS` : kitni jaldi bijli girti hai (12 = lagbhag 1.7 baar/second)
- `MIN_STRIKE_DISTANCE` / `MAX_STRIKE_DISTANCE` : Herobrine se kitni door bijli girti hai
- `VISUAL_ONLY_LIGHTNING` : `true` karo to bijli sirf dikhegi (na damage, na aag)

## Notes

- Herobrine ko bijli ya aag se damage nahi hota. Baaki damage se mar sakta hai; marne par bijli ruk jaati hai.
- `/kill @e[type=herobrineritual:herobrine]` se hata sakte ho.
- Build: `gradle build` (Gradle 9.2+, Java 21). Jar `build/libs/` me aayega.
