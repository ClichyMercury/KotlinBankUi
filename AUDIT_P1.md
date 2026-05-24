# Audit P1 — KotlinBankUi → FinSim

**Date** : 2026-05-23
**Périmètre** : Audit complet du frontend Android Kotlin existant (KotlinBankUi) en vue de la reprise pour le projet FinSim (cf. `plan_frontend_finsim.pdf` v1.0).
**Statut** : Phase 1 — Setup & Audit (cf. roadmap §4 du plan).

---

## 1. Build Gradle

**Résultat** : `BUILD SUCCESSFUL in 3m 53s` — 95 tasks (81 exécutées, 14 cache), tests unit passés, lint OK, **0 erreur**.

**18 warnings**, tous des deprecations (aucun bloquant) :

| Warning | Occurrences | Fix |
|---|---|---|
| `Icons.Filled.ArrowBack` deprecated | 3 (CardDetailScreen, NotificationDetailScreen, TransactionDetailScreen) | → `Icons.AutoMirrored.Filled.ArrowBack` |
| `Icons.Filled.TrendingUp / TrendingDown` deprecated | 5 (HomeScreen ×2, ProfileScreen, TransactionsScreen ×2) | → versions AutoMirrored |
| `Icons.Rounded.StarHalf` deprecated | 3 (FinanceSection) | → `Icons.AutoMirrored.Rounded.StarHalf` |
| `Icons.Filled.Help / ExitToApp / Undo` deprecated | 3 (ProfileScreen ×2, TransactionDetailScreen) | → versions AutoMirrored |
| `Divider` deprecated → `HorizontalDivider` | 4 (CardDetailScreen, ProfileScreen, TransactionsScreen, TransactionDetailScreen) | renommer |

---

## 2. Modules & Architecture

**Modules** : 1 seul (`:app`) — projet monolithique. `settings.gradle.kts` n'inclut que `:app`.

**Architecture réelle** :
- MVVM correct par écran (ViewModel + UiState + StateFlow) ✅
- Pas de Clean Architecture : pas de couche `domain` métier (le dossier `domain/models/` n'est qu'un sac de data classes), pas de `data`, pas de repository ❌
- Pas de `network/` ni `core/` ni `feature/` ❌
- Pas de DI (Hilt absent) ❌

**Écart vs cible FinSim §3.1** : restructuration multi-module complète requise.

**Arborescence actuelle** :
```
app/src/main/java/com/example/kotlinbankui/
├── MainActivity.kt                    (entry point)
├── domain/models/                     (4 data classes : CardItem, CurrencyItem, FinanceItem, BottomNavigationItem)
├── presentation/
│   ├── navigation/                    (BankNavigation + NavigationRoutes)
│   ├── components/                    (BottomNavigationBar, CurrenciesSection, WalletSection, FinanceSection)
│   │   └── cards/                     (CardsSection + detail/CardDetail*)
│   └── screens/                       (home, wallet, transactions, notifications, profile + 3 détails)
└── ui/theme/                          (Color, Theme, Type)
```

**Statistiques** : ~5 730 lignes de Kotlin, 79 `@Composable` functions.

---

## 3. Écrans Compose

| Écran | ViewModel | UiState | Mocks dans | viewModelScope | Notes |
|---|---|---|---|---|---|
| HomeScreen | HomeViewModel | HomeUiState | ViewModel | ❌ | Balance hardcodé dans Composable, pas lu de UiState |
| WalletScreen | WalletViewModel | WalletUiState | **Composable** | ❌ | VM existe mais inutilisé, données dans `WalletScreen()` directement |
| TransactionsScreen | TransactionsViewModel | TransactionsUiState | top-level `getTransactions()` | ❌ | VM appelle `getTransactions()` mais Screen aussi |
| NotificationsScreen | NotificationsViewModel | NotificationsUiState | top-level `getNotifications()` | ✅ | VM avec `markAsRead`/`deleteNotification` non câblés à l'UI |
| ProfileScreen | ProfileViewModel | ProfileUiState | **Composable** | ✅ | VM existe mais UI re-crée tout localement avec `remember` |
| TransactionDetailScreen | TransactionDetailViewModel | TransactionDetailUiState | VM | ❌ | OK |
| NotificationDetailScreen | NotificationDetailViewModel | NotificationDetailUiState | VM | ❌ | OK |
| CardDetailScreen | CardDetailViewModel | CardDetailUiState | VM | ❌ | OK |

**~35 TODOs** dans le code (notifs, search, share, refund, biométrie, dark mode, logout, etc.).

### Bugs & dette technique détectés

1. **`NotificationType` défini deux fois** avec valeurs **différentes** :
   - `NotificationsScreen.kt` : `TRANSACTION, SECURITY, PROMOTION, REMINDER, SYSTEM, CARD`
   - `NotificationDetailScreen.kt` : `PAYMENT, SECURITY, ACCOUNT, PROMOTION, SYSTEM, REMINDER`
   - Packages différents donc compile, mais incohérence de modèle qui cassera au branchement API.

2. **`ProfileMenuItem` Composable bug** (`ProfileScreen.kt:489-491`) : `onCheckedChange = { onToggle }` ne fait que **référencer** la lambda sans l'invoquer. Les switches ne fonctionnent pas. De plus `onToggle: Any` (l.444) est un type bidon hérité du `when` ligne 422-427 qui retourne soit une lambda soit `Unit`.

3. **Symbole `FilterChip` shadowing** (`TransactionsScreen.kt:53`) : data class locale jamais utilisée, le composant `FilterChip` de Material3 est utilisé à la place.

4. **Theme dynamique forcé** : `KotlinBankUITheme(dynamicColor = true)` par défaut → sur Android 12+ la couleur primaire vient du wallpaper utilisateur, le branding défini dans `Color.kt` n'apparaît que dans les composants qui les référencent en dur.

5. **Police Michroma non chargée** : `res/font/michroma_regular.ttf` présent, mais `Type.kt` actif utilise `FontFamily.Default` (config Michroma commentée). README et plan parlent de Michroma → faux positif.

6. **`WalletViewModel` vide** : `loadWalletData()` n'écrit rien dans le state. `WalletUiState.transactions: List<Transaction> = emptyList()` reste vide en permanence, et la Screen ignore le state de toute façon.

7. **ViewModels sans `viewModelScope`** pour Home/Wallet/CardDetail/TransactionDetail : `loadX()` synchrones, OK pour mocks, à refaire au branchement Ktor.

8. **Double `enableEdgeToEdge` + `Accompanist SystemUiController`** dans `MainActivity` : l'API native suffit, Accompanist fait doublon.

---

## 4. Dépendances — audit vs FinSim

### Présentes

| Dépendance | Actuelle | Stable récente | Status | Action FinSim |
|---|---|---|---|---|
| AGP | 8.8.2 | 8.7.x stable | ✅ | Garder |
| Kotlin | 2.0.0 | 2.1.x | ⚠️ Bump | → 2.1.20 (requis KSP 2.x / Hilt récent) |
| Compose BOM | 2024.04.01 | 2024.12.x / 2025.x | ⚠️ **Gros retard** | → BOM récent |
| Navigation Compose | 2.7.6 | 2.8.x | ⚠️ Bump | → 2.8.x (type-safe routes) |
| Lifecycle ViewModel Compose | 2.7.0 | 2.8.x | ⚠️ Bump | → 2.8.x |
| Lifecycle Runtime KTX | 2.8.7 | 2.8.x | ✅ | Garder |
| Activity Compose | 1.9.3 | 1.9.x | ✅ | Garder |
| Core KTX | 1.15.0 | 1.15.x | ✅ | Garder |
| Material Icons Extended | 1.5.4 | aligné BOM | ⚠️ Bump | Avec BOM |
| UI Text Google Fonts | 1.5.8 | aligné BOM | ⚠️ Bump | Avec BOM |
| **Accompanist SystemUiController** | 0.27.0 | **DEPRECATED** | ❌ | **Remplacer** par `enableEdgeToEdge()` natif (déjà appelé, l'Accompanist fait doublon) |

### Manquantes pour FinSim (cf. §3.1)

| Lib | Version cible | Pour quoi |
|---|---|---|
| **Hilt** | 2.51+ | DI |
| **KSP** | aligné Kotlin | Hilt, Room |
| **Ktor Client** | 3.x | HTTP (cours, auth) |
| **Room** | 2.6.x | Cache local |
| **Vico** | 2.0.x | Graphiques (préféré à MPAndroidChart en mode maintenance) |
| **Lottie Compose** | 6.x | Animations |
| **DataStore Preferences** | 1.1.x | Settings utilisateur |
| **Coil Compose** | 2.6+ | Chargement images (logos actifs, avatars) |
| **AndroidX Biometric** | 1.2.x | Touch ID / Face ID Android |
| **Inter + Roboto Mono** | via ui-text-google-fonts | Typo cible du plan |
| **kotlinx-coroutines** | 1.9.x | Explicite (transitive aujourd'hui) |
| **kotlinx-serialization** | 1.7.x | Sérialisation JSON pour Ktor |

### Autres points

- `libs.versions.toml` **incomplet** : Navigation, Lifecycle ViewModel Compose, Accompanist, Material Icons Extended, Google Fonts hardcodés dans `app/build.gradle.kts`. À refactorer dans le catalog.
- `gradle.properties` minimaliste — ajouter pour FinSim : `org.gradle.parallel=true`, `org.gradle.caching=true`, `kotlin.incremental=true`.
- `targetSdk = 34` vs `compileSdk = 35` → aligner à 35.
- Gradle wrapper 8.10.2 → OK.

---

## 5. Tests

| Type | Fichiers | Tests | État |
|---|---|---|---|
| Unit test | `ExampleUnitTest.kt` | 1 test `assertEquals(4, 2+2)` | 🪦 Stub auto-généré |
| Instrumented | `ExampleInstrumentedTest.kt` | 1 test sur packageName | 🪦 Stub auto-généré |

**Couverture réelle** : 0 % sur le code applicatif. À partir de zéro pour FinSim — liberté complète pour mettre en place : JUnit 5 + MockK + Turbine (StateFlow) + Compose UI Tests + éventuellement Paparazzi pour les screenshot tests du Design System.

---

## 6. Matrice récupérable / à reconstruire pour FinSim

### 🟢 Récupérable tel quel ou avec adaptation mineure

| Élément | Pour FinSim | Adaptation |
|---|---|---|
| Setup Compose + Material 3 + Navigation | Base | Bump versions |
| `MainActivity` + `enableEdgeToEdge()` | Entry point | Garder, retirer Accompanist |
| Pattern MVVM (VM + UiState + StateFlow) | Tous écrans | Garder, ajouter Hilt |
| `BankNavigation` (NavHost central) | Adapter | Renommer `FinSimNavigation`, ajouter graphes nested par feature |
| `BottomNavigationBar` + `BottomNavigationItem` | Bottom nav FinSim | Remplacer items (Home/Markets/Trade/Portfolio/Profile) |
| `BalanceCard` (gradient) Home | Dashboard FinSim | Réutiliser pour total portfolio |
| `QuickActionsSection` Home | Dashboard FinSim | Items différents (Trade / Deposit fictif / Learn / Leaderboard) |
| `CardComposable` + gradients | Detail Actif ou portefeuille de cartes virtuelles | Adapter palette |
| `TransactionDetailItem` + `StatusBadge` | Historique trades | Garder pattern card+icon+status |
| `FilterChip` row | Filtres marchés / périodes | Direct |
| `EmptyState` | États vides FinSim | Direct |
| `InfoRow` | Détails ordre / actif | Direct |
| `StatItem` / `QuickStatItem` | Stats portfolio / gamification | Direct |

### 🟠 Récupérable avec refactor important

| Élément | Pourquoi |
|---|---|
| Tous les ViewModels | Migrer vers Hilt `@HiltViewModel` + injecter Repository |
| `ui/theme/Color.kt` | Garder le pattern, refaire la palette (#1A56A0, #00C896, #E74C3C, #F0A500) |
| `Type.kt` | Activer Google Fonts pour Inter + Roboto Mono à la place de Michroma |
| Top-level `getTransactions()` / `getNotifications()` | Déplacer en `MockRepository` puis derrière interface |
| `CurrenciesSectionFixed` + table buy/sell | Pattern réutilisable pour cours Forex FinSim |
| `NotificationType` (déduplifier) | Garder un seul, en `core/domain` |

### 🔴 À reconstruire / supprimer

| Élément | Action |
|---|---|
| `WalletScreen` (banking perso) | Supprimer, remplacer par `DashboardScreen` FinSim |
| `TransactionsScreen` (bancaire) | Supprimer (catégories Food/Shopping/Bills hors scope) |
| `ProfileScreen` (settings bancaires) | Reconstruire avec sections FinSim |
| Cards "VISA/MASTERCARD" mockées | Supprimer (hors scope) |
| `themes.xml` (Material Light parent) | Migrer vers theme M3 (`Theme.Material3.DayNight.NoActionBar`) |
| `res/drawable/ic_visa.png` + `ic_mastercard.png` | Supprimer, remplacer par logos actifs/marchés |
| `Type.kt` Michroma | Remplacer par Inter |
| Couleurs Purple40/80, Pink40/80 | Supprimer |

### 🆕 À créer ex nihilo

- Multi-module : `app/`, `feature/{auth,dashboard,market,trading,gamification,education,profile}`, `core/{network,data,ui,domain}`
- Module DI Hilt (Application class, modules par feature)
- `core/network` : Ktor + intercepteurs auth + sealed `NetworkResult<T>`
- `core/data` : Repositories + Room entities + DAOs + DataStore
- `core/domain` : Use cases (`GetMarketFeedUseCase`, `PlaceOrderUseCase`, etc.)
- `core/ui` : Design System (palette + typo + composants `PriceCard`, `PortfolioChart`, `OrderButton`, `BadgeWidget`, `LevelProgressBar`, `EducationTip`)
- Auth (login/register/biométrie) + JWT
- Écrans FinSim : Onboarding, Dashboard, Market Feed, Detail Actif (chandelier), Trading, Leaderboard, Education

---

## 7. Estimation chiffrée

**~30 %** du code récupérable (patterns MVVM, composants UI génériques, navigation), **~70 % à reconstruire**.

| Étape | Effort |
|---|---|
| 1. Bumps deps + suppression Accompanist + migration `HorizontalDivider`/`AutoMirrored` | 0.5 j |
| 2. Refonte palette + typo (Inter / Roboto Mono) | 1 j |
| 3. Split multi-module (`app` + `core/*` + `feature/*` vides) | 1 j |
| 4. Setup Hilt + Ktor + Room + DataStore skeletons | 1 j |
| 5. Migration des composants récupérables vers `core/ui` | 1.5 j |
| 6. Auth (login/register + biométrie) | 3 j |
| **Total P1 frontend Android** | **~8 j** |

Cohérent avec l'estimation 6-8 semaines du plan FinSim pour P1-P3 inclus (Android + iOS en parallèle).

---

## 8. Recommandation tactique

Faire un **commit propre des bumps de dépendances en premier** (BOM Compose récent + Navigation 2.8 + suppression Accompanist + migration des deprecations), pour partir sur une base à jour. Le multi-module se fait ensuite avec un projet sain.

L'ordre d'attaque recommandé :
1. Cleanup deprecations + bump deps
2. Refonte Design System (palette + typo)
3. Split multi-module
4. Hilt + Ktor + Room
5. Auth
6. P2 (Dashboard, Market Feed, Detail Actif)
