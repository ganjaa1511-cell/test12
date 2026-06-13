# Airealm Companion V4 — Design Premium

## Nouveautés V4

### Correctif critique
- **Bouton Sauver** maintenant fixé avec `navigationBarsPadding()` — compatible S25 Ultra, Pixel 9, tout appareil avec barre gestuelle.
- **Edge-to-edge** activé (`WindowCompat.setDecorFitsSystemWindows(false)`) avec gestion fine des insets sur chaque écran.

### Design premium
- **Police Cinzel** (Google Fonts) pour les titres, sections, noms d'entités — typographie classique/épique sans tomber dans le cliché medieval-clipart.
- **Hero header** plein écran 220dp : photo de personnage si disponible, sinon fond texturé obsidienne avec initiale géante translucide. Gradient scrim pour lisibilité du nom en toutes circonstances.
- **LoreField** — champs customisés qui ressemblent à des annotations de manuscrit plutôt qu'à un formulaire SaaS.
- **LoreDivider** — séparateurs ambre avec icône + texte espacé, remplacent les labels ALL-CAPS génériques.
- **Secrets collapsibles** — section Secrets verrouillée par défaut (tap pour révéler), thématiquement approprié pour le RPG.
- **Codex header** sur la liste des campagnes — "AIREALM" en petites capitales espacées + "Codex" en Cinzel 32sp.
- **Entity rows** avec initiale stylisée comme avatar de fallback quand pas de photo.
- Palette consolidée : Obsidian / Coal / Ember / Gold / Parchment / Vellum / Teal / Crimson.

## Stack
- AGP 8.7.3 / Kotlin 2.0.21 / Compose BOM 2024.10.00
- Coil 2.7.0 / ui-text-google-fonts
- JVM 21 / compileSdk 35 / minSdk 26

## Build
1. Android Studio → ouvrir `airealm_companion_v4`
2. Gradle JDK : Embedded JDK 21
3. Sync Gradle (télécharge Cinzel depuis GMS automatiquement à l'exécution)
4. Build APK

## V7 Premium polish
- Nouveau fond global premium avec vignette chaude, grain discret et halo doré/teal.
- Cartes campagne et entités refaites en panneaux premium avec bordure subtile, meilleurs badges et meilleure hiérarchie visuelle.
- Header de campagne refait avec stats rapides et onglets mieux intégrés.
- Gradle wrapper aligné sur Gradle 8.10.2 avec AGP 8.7.3, Java/Kotlin 17 pour une compilation plus stable dans Android Studio.


## v10 — photo persistence + portrait UI polish

- Photo import now creates a private app copy in `filesDir/airealm_photos` instead of relying on the external picker URI.
- Entity save no longer overwrites `photoUris`, so pressing “Sauver le personnage / lieu” after adding a photo keeps the photo.
- Photo picking uses `OpenDocument` with a persistable read grant before the internal copy is made.
- SharedPreferences now uses `commit()` to reduce the chance of losing a quick save if Android kills the app.
- Portrait-heavy NPC images use `ContentScale.Fit` with a blurred ambience layer instead of brutal crop.
- Detail hero, list cards, and the photo strip were adjusted for a more premium portrait-friendly look.
