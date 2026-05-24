# 📈 FinSim — Android

Client Android natif du simulateur de trading **FinSim**. Application mobile d'éducation financière gamifiée : portfolios fictifs, ordres d'achat simulés sur des actifs réels (crypto pour l'instant, actions et BRVM à venir), prix de marché en temps réel.

> Stack Kotlin / Jetpack Compose / Hilt / Ktor — backend Ktor séparé dans [`KotlinBankAPI`](https://github.com/ClichyMercury/KotlinBankAPI) (ou local dans `~/javaman/KotlinBankAPI`).

---

## 📱 Aperçu

L'app sert d'interface au backend FinSim. Tout est branché live sur l'API : pas de mock.

| Écran | Endpoint backend |
|---|---|
| Splash | `GET /api/v1/auth/me` (auto-login si token valide) |
| Login / Register | `POST /api/v1/auth/login` / `POST /api/v1/auth/register` |
| Dashboard | `GET /api/v1/portfolio` — solde + positions + PnL non réalisé |
| Marché | `GET /api/v1/market/assets[?type=]` — polling 30s quand visible |
| Détail actif | `GET /api/v1/market/assets/{id}` |
| Acheter | `POST /api/v1/orders/buy` |
| Ordres | `GET /api/v1/orders` + jointure tickers via `/market/assets` |
| Profil | `GET /api/v1/auth/me` + logout |

---

## 🛠️ Stack

| Composant | Choix |
|---|---|
| Langage | Kotlin 2.1.20, JVM 17 |
| Build | AGP 8.13.2, Gradle 8.13, KSP 2.1.20-1.0.32 |
| UI | Jetpack Compose (BOM 2025.01.00), Material 3 |
| Navigation | Navigation Compose 2.8.5 |
| DI | Hilt 2.56 |
| HTTP | Ktor Client 3.0.3 (CIO engine) |
| Sérialisation | kotlinx.serialization 1.7.3 |
| Stockage local | DataStore Preferences 1.1.1 (JWT) |
| Coroutines | kotlinx-coroutines 1.9.0 |
| Lifecycle | androidx.lifecycle 2.8.7 (runtime + viewmodel + compose) |
| Fonts | Inter + Roboto Mono via `ui-text-google-fonts` |

`minSdk = 24`, `targetSdk = 35`.

---

## 🏗️ Architecture

Layered MVVM côté présentation, layered data + DI Hilt côté infra. Préfiguration d'un découpage multi-module ; pour l'instant tout est dans `:app`.

```
app/src/main/java/com/example/kotlinbankui/
├── FinSimApp.kt                       @HiltAndroidApp
├── MainActivity.kt                    @AndroidEntryPoint + observe SessionManager
├── data/
│   ├── network/
│   │   ├── ApiClient.kt               Ktor HttpClient + content-negotiation + logging
│   │   ├── ApiException.kt            sealed: Network / Http / Unauthorized / Unknown
│   │   └── dto/                       AuthDto, MarketDto, PortfolioDto, OrderDto
│   │                                  + Serializers (UUID / Instant / BigDecimal en String)
│   ├── auth/
│   │   ├── TokenStore.kt              DataStore persistance du JWT
│   │   ├── AuthRepository.kt          register / login / me / logout
│   │   └── SessionManager.kt          guard{} + broadcast LoggedOut SharedFlow
│   ├── portfolio/PortfolioRepository.kt
│   ├── market/MarketRepository.kt
│   ├── orders/OrderRepository.kt
│   └── util/                          requireAuth + runCatchingApi helpers
├── di/NetworkModule.kt                provides HttpClient
├── presentation/
│   ├── navigation/                    NavigationRoutes + BankNavigation (NavHost)
│   ├── components/finsim/             Design System réutilisable (12 composants)
│   └── screens/
│       ├── splash/                    Splash + ViewModel
│       ├── auth/                      Login + Register + ViewModels + UiStates
│       ├── dashboard/                 portfolio user
│       ├── market/                    liste + détail actif
│       ├── trading/                   BuyScreen
│       ├── orders/                    historique
│       └── profile/                   /me + logout
└── ui/theme/                          Color (palette FinSim), Type (Inter + Roboto Mono),
                                       Theme (FinSimTheme = light Material 3)
```

### Patterns

- **MVVM** : un `@HiltViewModel` par écran, expose un `StateFlow<UiState>`, le Composable consomme avec `collectAsState()`.
- **Result wrapping** : chaque appel repo renvoie `Result<T>`. Les exceptions API sont normalisées via `ApiException`.
- **Auto-logout 401** : `SessionManager.guard {}` wrap chaque call authentifié. Sur 401 → token vidé + event broadcast → `MainActivity` redirige vers Login.
- **Lifecycle aware** :
  - Dashboard / Orders / Profile : `LifecycleEventEffect(ON_RESUME)` re-fetch quand l'écran redevient visible.
  - Market : `repeatOnLifecycle(RESUMED)` + boucle `delay(30s)` pour polling auto, annulé quand l'écran est en arrière-plan.

### Design System

Palette (cf. `ui/theme/Color.kt`) :
- Primary `#1A56A0` (bleu finance)
- Secondary `#00C896` (vert hausse)
- Error `#E74C3C` (rouge baisse)
- Tertiary `#F0A500` (or gamification)
- Background `#F5F7FA` / texte `#1A1A2E`

Typo : **Inter** pour les textes (toutes graisses), **Roboto Mono** pour les chiffres financiers (alignement vertical). Chargés via Google Fonts au runtime.

Composants partagés (`presentation/components/finsim/`) : `PriceCard`, `OrderButton`, `StatCard`, `PnLChip`, `AssetAvatar`, `FinSimTextField`, `MoneyText`, `EmptyState`, `ErrorBanner`, `LoadingScreen`, `FinSimTopBar`, `FinSimBottomBar`.

---

## 🚀 Démarrage

### Prérequis

- Android Studio Ladybug+ (AGP 8.13 minimum)
- JDK 17
- Émulateur Android avec API 24+ (ou device physique avec adaptation de l'URL — voir plus bas)
- Le backend FinSim qui tourne en local sur `:8080` ([`KotlinBankAPI`](https://github.com/ClichyMercury/KotlinBankAPI))

### Lancer le back en local

```bash
cd ~/javaman/KotlinBankAPI    # ou ton chemin
docker compose up -d           # Postgres :5434 + Redis :6380
./gradlew run                  # API sur :8080
```

Vérifie : `curl http://localhost:8080/health` → `{"status":"ok","db":true,"redis":true}`

### Lancer l'app Android

1. Cloner et ouvrir dans Android Studio
2. Laisser Gradle sync (la première fois prend 5-10 min, télécharge Hilt/Ktor/etc.)
3. Lancer un AVD (le `10.0.2.2` configuré pointe vers `localhost` de la machine hôte uniquement depuis un émulateur)
4. Run ▶️

### Sur device physique

`BuildConfig.API_BASE_URL` est défini dans `app/build.gradle.kts` :
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"")
```

Pour un device sur le même réseau Wi-Fi, remplace `10.0.2.2` par l'IP locale du Mac (ex. `192.168.1.42`), puis ajoute cette IP à `app/src/main/res/xml/network_security_config.xml`.

---

## 🔐 Auth & Session

- JWT HMAC256, 60 min de vie, stocké en clair dans DataStore (à chiffrer en prod).
- Au lancement : Splash lit le token, appelle `/auth/me` pour vérifier sa validité.
- Toute requête authentifiée passe par `SessionManager.guard {}` qui catch les `ApiException.Unauthorized` (= 401) → vide le token → émet `loggedOut` → MainActivity navigue vers Login.
- Pas de refresh token (dette tech côté back, sprint 2). Au 401, déconnexion brutale.

---

## 🗺️ Roadmap

- [x] Auth complet (register / login / me / logout)
- [x] Dashboard avec portfolio temps réel + PnL
- [x] Market feed avec filtres + polling 30s
- [x] Buy order + historique
- [x] Auto-logout sur 401
- [ ] **Graphiques** sur AssetDetail (bloqué côté back : pas d'endpoint historique de prix pour l'instant)
- [ ] **Order SELL** (bloqué côté back : pas encore implémenté)
- [ ] **Refresh token** (bloqué côté back : sprint 2)
- [ ] Gamification (badges, XP, leaderboard) — sprint 3 back
- [ ] WebSocket pour le market feed (remplacer le polling 30s)
- [ ] Stocks / Forex / BRVM (sprint 4 back)
- [ ] Multi-devises FCFA / USD / EUR
- [ ] Dark mode
- [ ] Splash native Android 12+
- [ ] Pull-to-refresh
- [ ] Tests unit (MockK + Turbine) + tests Compose UI

Voir aussi [`AUDIT_P1.md`](AUDIT_P1.md) pour le détail du plan de migration depuis l'ancienne base bancaire.

---

## 🧰 Convention de commits

[Conventional Commits](https://www.conventionalcommits.org/) : `feat:`, `fix:`, `refactor:`, `chore:`, etc.

---

## 👨‍💻 Auteur

**Gael SASSAN** — WHARPE Corp.

- GitHub : [@ClichyMercury](https://github.com/ClichyMercury)
- LinkedIn : [Gael SASSAN](https://www.linkedin.com/in/gael-yad-eugene-sassan-17a69b1b6/)

---

## 📄 Licence

MIT.
