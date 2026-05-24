# Plan de migration FinSim Android → Kotlin Multiplatform

> Document de travail pour la migration future de l'app Android FinSim vers KMP,
> de manière à supporter iOS sans réécrire en Swift et garder toute la stack
> en Kotlin (back Ktor + front KMP + Compose Multiplatform).

**Date** : 2026-05-24
**Statut** : à attaquer plus tard

---

## TL;DR

**Choix retenu** : **Option B — KMP + Compose Multiplatform** (UI partagée à 90-95%).

On garde la stack tout-Kotlin (back Ktor + front KMP), on ne réécrit rien en
Swift, on profite du design system premium qu'on vient de polir directement
sur iOS.

**Effort estimé** : ~2 semaines pour migrer tout l'existant.

---

## Pourquoi KMP plutôt qu'une réécriture SwiftUI

| Critère | Réécriture SwiftUI | Option A (KMP shared logic) | Option B (KMP + CMP) ✅ |
|---|---|---|---|
| Effort iOS | 4-6 semaines | 3-4 semaines | **2 semaines** |
| Maintenance long terme | 2 UIs (Android + iOS) | 2 UIs | **1 UI** |
| Look natif iOS | parfait | parfait | correct, pas 100% natif |
| Réutilise le design system actuel | non | non | **oui, direct** |
| App size iOS | natif | ~natif | +~12 MB (runtime Kotlin/Native) |
| Risque | bas | bas | moyen (CMP iOS encore jeune) |

**Pour notre contexte** (solo, design premium déjà fait, app trading où les
gens s'attendent à du custom, back en Kotlin) → Option B gagne largement.

---

## Ce qui peut migrer tel quel vers `commonMain`

✅ **Data layer entière**
- DTOs (`AuthDto`, `MarketDto`, `PortfolioDto`, `OrderDto`, `CandleDto`)
- Serializers custom (UUID, Instant, BigDecimal)
- `ApiClient`, `ApiException`
- `AuthRepository`, `PortfolioRepository`, `MarketRepository`, `OrderRepository`
- `SessionManager` + helper `guard{}`
- `TokenStore` (sera `expect` avec une `actual` par plateforme pour le path)

✅ **Domain / business logic**
- Tous les modèles
- Tout le `Formatters.kt` (BigDecimal → String)
- `computePnlPercent` et autres helpers

✅ **Design System**
- `Color.kt` (palette light + dark)
- `Type.kt` (Inter + Roboto Mono via `compose.ui.text.googlefonts` qui devient
  compose-multiplatform-text-googlefonts en CMP)
- `Theme.kt`
- Tous les composants `presentation/components/finsim/`

✅ **Tous les écrans**
- Splash, Login, Register
- Dashboard, Market, AssetDetail, Buy, Orders, Profile
- Tous les ViewModels (Compose Multiplatform a son équivalent ViewModel
  depuis 1.6+, ou on utilise `androidx.lifecycle:lifecycle-viewmodel` qui est
  KMP)
- Navigation Compose existe en KMP (en alpha mais stable en pratique pour les
  besoins simples) — sinon Voyager ou Decompose

---

## Migrations obligatoires (libs Android-only → libs KMP)

| Lib actuelle | Remplacement KMP | Effort | Notes |
|---|---|---|---|
| **Hilt** | **Koin** | 0.5 j | mécanique : `@HiltViewModel` → `class XxxViewModel(...)` + `viewModel { XxxViewModel(get(), get()) }` dans un Koin module |
| **DataStore Preferences** | `androidx.datastore:datastore-preferences` (déjà KMP depuis 1.1) | 0.5 j | API identique, juste `okio` + path différent par plateforme |
| **kotlinx-coroutines-android** | `kotlinx-coroutines-core` | trivial | déjà inclus transitivement |
| **vico-compose-m3** | `vico-multiplatform` | trivial | branche officielle CMP, même API |
| **`hilt-navigation-compose`** | injection directe via Koin compose | trivial | `koinViewModel()` au lieu de `hiltViewModel()` |
| **Splash gradient** | inchangé | — | Brush KMP-compatible |

---

## Plateforme-spécifique (`expect`/`actual`)

Trucs qui ne peuvent pas être 100% commun :

| Sujet | Android `actual` | iOS `actual` |
|---|---|---|
| **Base URL dev** | `BuildConfig.API_BASE_URL` = `http://10.0.2.2:8080` | hardcodé `http://localhost:8080` ou IP locale |
| **Cleartext HTTP en dev** | `network_security_config.xml` | `Info.plist` `NSAppTransportSecurity` |
| **Edge-to-edge** | `enableEdgeToEdge()` | rien (déjà natif) |
| **DataStore path** | `context.dataStore` | `NSFileManager.documentsDir + "/finsim_auth.preferences_pb"` |
| **System bars color** | optionnel via `enableEdgeToEdge` | gérer via SwiftUI `.statusBar` ou `UIApplication` |
| **Coil image loader** | `coil-android` | `coil-multiplatform` (existe) ou pas d'images = pas de souci |

Ces `expect/actual` sont triviaux, ~50 lignes au total côté iOS.

---

## Structure cible du projet

```
KotlinBankUi/
├── shared/                              ← nouveau module KMP
│   └── src/
│       ├── commonMain/kotlin/com/finsim/
│       │   ├── data/                    (déplacé depuis :app)
│       │   ├── domain/
│       │   ├── presentation/            (tous les écrans Compose)
│       │   └── ui/theme/
│       ├── androidMain/kotlin/com/finsim/
│       │   ├── TokenStorePath.kt        (actual)
│       │   └── ApiBaseUrl.kt            (actual)
│       └── iosMain/kotlin/com/finsim/
│           ├── TokenStorePath.kt        (actual)
│           └── ApiBaseUrl.kt            (actual)
│
├── androidApp/                          ← :app minimal qui hoste :shared
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/finsim/android/
│           ├── FinSimApp.kt             (Koin start)
│           └── MainActivity.kt          (juste enableEdgeToEdge + setContent { FinSimApp() })
│
└── iosApp/                              ← projet Xcode
    └── iosApp/
        ├── iOSApp.swift                 (entry point Swift)
        └── ContentView.swift            (host le ComposeUIViewController)
```

---

## Plan d'attaque step-by-step

### Phase 1 — Préparation (0.5 j)
1. Créer le module `:shared` avec setup KMP (Android + iOS targets)
2. Ajouter les plugins `org.jetbrains.kotlin.multiplatform` + `org.jetbrains.compose`
3. Configurer `iosTargets` (iosArm64, iosSimulatorArm64)

### Phase 2 — Migration data (1 j)
1. Déplacer `data/network/dto/` → `:shared/commonMain`
2. Déplacer DTOs + Serializers (UUID/Instant/BigDecimal) — vérifier que
   `java.math.BigDecimal` n'est pas utilisé directement (sinon utiliser
   `kotlin.BigDecimal` ou `BigDecimal` multi-plateforme via une lib comme
   `com.ionspin.kotlin:bignum`)
3. Pareil pour `java.time.Instant` → `kotlinx.datetime.Instant`
4. Déplacer repositories
5. Remplacer Hilt par Koin dans le module shared

### Phase 3 — Migration UI (3-4 j)
1. Déplacer `ui/theme/` → `:shared/commonMain`
2. `compose.ui.text.googlefonts` → équivalent CMP (vérifier la dispo iOS,
   sinon utiliser des fonts bundlées en resources commonMain)
3. Déplacer `presentation/components/finsim/` (le design system)
4. Déplacer écrans un par un, vérifier que Vico marche en CMP
5. Migrer Navigation Compose vers la version multiplatform

### Phase 4 — Android app shell (0.5 j)
1. Réduire `:app` à un shell qui ne contient que `MainActivity` + `FinSimApp`
2. `MainActivity` appelle juste `setContent { FinSimAppRoot() }` du module shared

### Phase 5 — iOS app shell (1 j)
1. Créer le projet Xcode `iosApp/`
2. Configurer le framework Kotlin via CocoaPods ou XCFramework manuel
3. `iOSApp.swift` :
   ```swift
   @main
   struct iOSApp: App {
       var body: some Scene {
           WindowGroup {
               ComposeView()
           }
       }
   }
   struct ComposeView: UIViewControllerRepresentable {
       func makeUIViewController(context: Context) -> UIViewController {
           Main_iosKt.MainViewController()
       }
       func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
       typealias UIViewControllerType = UIViewController
   }
   ```
4. Côté Kotlin iOS, exposer `fun MainViewController(): UIViewController = ComposeUIViewController { FinSimAppRoot() }`

### Phase 6 — Cleanup & polish (1 j)
1. Tester sur simulateur iOS
2. Adapter les expects/actuals
3. Vérifier les fonts (Inter et Roboto Mono via Google Fonts iOS — peut
   nécessiter de bundler des TTF en commonMain resources)
4. Ajuster les couleurs status bar iOS
5. CI : ajouter un job Xcode build dans GitHub Actions

**Total** : ~2 semaines à temps plein, ~3-4 semaines à mi-temps.

---

## Risques connus à anticiper

1. **BigDecimal n'est pas en commonMain** — utiliser `kotlin.BigDecimal`
   (apparu en Kotlin 2.2 expérimental) ou la lib `bignum`. Impact : tous nos
   serializers + Formatters à adapter.

2. **Instant** — passer à `kotlinx.datetime.Instant` (drop-in remplacement
   moderne). Les `Duration` aussi via `kotlin.time.Duration`.

3. **Google Fonts iOS** — la lib compose `ui-text-google-fonts` est Android-
   only. Sur iOS, soit bundler des TTF de Inter + Roboto Mono dans
   `commonMain/resources`, soit télécharger au runtime via une lib custom.

4. **Vico iOS** — vérifier la stabilité. Si trop instable, on a déjà notre
   Sparkline Canvas qui marche cross-platform. On peut faire un PriceChart
   custom Canvas aussi (plus de travail mais 0 dépendance).

5. **Navigation Compose en KMP** — encore en alpha sur certaines API. Si ça
   bloque, fallback Voyager (lib KMP mature) ou Decompose.

6. **Hot reload iOS** — pas aussi rapide qu'Android Studio. Utiliser
   AppCode/Fleet ou faire des cycles build-run.

7. **Image loader** — Coil 3.0+ est KMP. Si on veut afficher des logos de
   cryptos depuis le back plus tard, Coil 3 fonctionnera des deux côtés.

8. **JWT decoding éventuel** — `com.auth0:java-jwt` est Java-only. Si on a
   besoin de décoder côté front (pour debug/expiration), utiliser une lib KMP
   ou décoder le base64 à la main.

---

## Ce qu'on garde déjà KMP-ready aujourd'hui

Bonne nouvelle : grâce aux choix tech faits dès le début, on est déjà bien
positionnés.

- ✅ **Ktor Client** — KMP natif
- ✅ **kotlinx.serialization** — KMP natif
- ✅ **kotlinx.coroutines** — KMP natif
- ✅ **Compose** — la plupart de nos écrans n'utilisent que des APIs
  multiplatform (Material 3, layout, draw, animation)
- ✅ **DataStore Preferences** (1.1.1 actuelle) — déjà KMP
- ✅ **Vico 2.0** — supporte CMP via `vico-multiplatform`

Le seul gros morceau à remplacer est **Hilt → Koin**. Tout le reste, c'est
des `expect/actual` ou des changements d'import.

---

## Quand on reprend

1. Relire ce fichier
2. Lancer la session Claude en disant "on attaque la migration KMP, regarde
   `KMP_MIGRATION_PLAN.md`"
3. Commencer par la Phase 1 — créer le module `:shared` et valider qu'un
   `hello world` Compose tourne sur les deux plateformes
4. Migrer la stack data en premier (peu de risque, gains immédiats), garder
   l'UI Android intacte pendant ce temps
5. Migrer l'UI progressivement, écran par écran, en gardant Android
   fonctionnel à chaque étape

---

## Décision validée

- Cible : **iOS via KMP + Compose Multiplatform**
- Stack 100% Kotlin sur back ET front
- Pas de SwiftUI à écrire
- Migration différée, l'app Android actuelle continue de vivre normalement
