# 📈 FinSim — Kotlin Multiplatform

Client mobile **Android + iOS** du simulateur de trading **FinSim**. Application
d'éducation financière gamifiée : portfolios fictifs, ordres d'achat simulés
sur des actifs réels (crypto pour l'instant, actions et BRVM à venir), prix
de marché en temps réel.

> Stack 100% Kotlin — **Compose Multiplatform** côté UI, **Ktor Client** pour
> l'API, **Koin** pour la DI, **Navigation Compose KMP** pour la navigation.
> Backend Ktor séparé dans [`KotlinBankAPI`](https://github.com/ClichyMercury/KotlinBankAPI)
> (ou local dans `~/javaman/KotlinBankAPI`).

---

## 📱 Aperçu

L'app est branchée live sur le backend FinSim. Pas de mock.

| Écran | Endpoint backend |
|---|---|
| Splash | `GET /api/v1/auth/me` (auto-login si token valide) |
| Login / Register | `POST /api/v1/auth/login` / `POST /api/v1/auth/register` |
| Dashboard | `GET /api/v1/portfolio` — solde + positions + PnL non réalisé |
| Marché | `GET /api/v1/market/assets[?type=]` — polling 30s quand visible |
| Détail actif | `GET /api/v1/market/assets/{id}` + candles |
| Acheter | `POST /api/v1/orders/buy` |
| Ordres | `GET /api/v1/orders` + jointure tickers via `/market/assets` |
| Profil | `GET /api/v1/auth/me` + logout |

---

## 🛠️ Stack

| Composant | Choix |
|---|---|
| Langage | Kotlin **2.1.20**, JVM 17 |
| UI | **Compose Multiplatform 1.8.2** (Material 3) |
| Build | AGP 8.13.2, Gradle 8.13, Xcode 26.5, CocoaPods 1.16 |
| DI | **Koin 4.1.0** (KMP) |
| Navigation | **`org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta03`** (JB KMP fork) |
| Lifecycle / ViewModel | **`org.jetbrains.androidx.lifecycle:* 2.9.1`** (JB KMP fork) |
| HTTP | Ktor Client 3.0.3 (CIO sur Android, **Darwin** sur iOS, auto-discovery) |
| Sérialisation | kotlinx.serialization 1.7.3 + JSON |
| Date/time | **kotlinx.datetime 0.6.2** (zéro `java.time`) |
| BigDecimal | **`com.ionspin.kotlin:bignum:0.3.10`** (zéro `java.math.BigDecimal`) |
| UUID | **`kotlin.uuid.Uuid`** stdlib (`@ExperimentalUuidApi`) |
| Charts | Vico **2.1.3 multiplatform-m3** |
| Fonts | Inter + Roboto Mono variable fonts bundlés en commonResources |
| Stockage local | DataStore Preferences 1.1.1 (Android) / NSUserDefaults (iOS) |
| Coroutines | kotlinx-coroutines 1.9.0 |

`minSdk = 24`, `targetSdk = 35`, `iOS deployment target = 15.0`.

---

## 🏗️ Architecture — multi-module KMP

```
KotlinBankUi/
├── shared/                            ← 100% du code applicatif (KMP)
│   └── src/
│       ├── commonMain/kotlin/com/finsim/
│       │   ├── data/
│       │   │   ├── network/           ApiClient, ApiException, DTOs, Serializers
│       │   │   ├── auth/              TokenStore (interface), SessionManager, AuthRepository
│       │   │   ├── preferences/       ThemePreferenceStore (interface)
│       │   │   ├── portfolio/, market/, orders/, util/
│       │   ├── di/Modules.kt          Koin sharedModule (repos + ViewModels)
│       │   ├── presentation/
│       │   │   ├── navigation/        BankNavigation, NavigationRoutes
│       │   │   ├── components/        Design System (15 composants) + AppBottomBar
│       │   │   ├── screens/           Splash, Login, Register, Dashboard, Market,
│       │   │   │                      AssetDetail, Buy, Orders, Profile
│       │   │   │                      (XxxScreen = stateless content,
│       │   │   │                       XxxRoute = host + koinViewModel)
│       │   │   ├── util/UiMessages.kt
│       │   │   └── FinSimAppRoot.kt   Entry point unique cross-platform
│       │   └── ui/theme/              Color, Theme, Typography (variable fonts),
│       │                              ThemePreference enum
│       │
│       ├── commonMain/composeResources/font/
│       │   ├── inter.ttf              ← variable font (5 graisses dans 1 fichier)
│       │   └── roboto_mono.ttf
│       │
│       ├── androidMain/kotlin/com/finsim/
│       │   ├── data/auth/AndroidTokenStore.kt           DataStore
│       │   ├── data/preferences/AndroidThemePreferenceStore.kt
│       │   └── di/AndroidModule.kt                      Koin Android impl
│       │
│       └── iosMain/kotlin/com/finsim/
│           ├── data/auth/IosTokenStore.kt               NSUserDefaults
│           ├── data/preferences/IosThemePreferenceStore.kt
│           ├── di/IosModule.kt                          Koin iOS impl
│           └── shared/MainViewController.kt             init Koin + ComposeUIViewController
│
├── app/                               ← shell Android (2 fichiers Kotlin)
│   └── src/main/java/com/example/kotlinbankui/
│       ├── FinSimApp.kt               startKoin avec ApiClientConfig depuis BuildConfig
│       └── MainActivity.kt            setContent { FinSimAppRoot() }
│
└── iosApp/                            ← projet Xcode
    ├── project.yml                    spec xcodegen (versionné)
    ├── Podfile                        pod 'shared', :path => '../shared'
    └── iosApp/
        ├── iOSApp.swift               @main App
        └── ContentView.swift          UIViewControllerRepresentable → MainViewController()
```

**Le pattern clé** : chaque écran a un duo `XxxScreen` (Composable stateless en
`shared`) + `XxxRoute` (host qui résout le VM via Koin et bind la nav). Le
graphe Navigation appelle les `XxxRoute`. iOS et Android utilisent strictement
le même graphe.

---

## 🚀 Démarrage — Prérequis communs

### Backend FinSim sur localhost

L'app attaque `http://localhost:8080`. Lance le back avant de démarrer l'app :

```bash
cd ~/javaman/KotlinBankAPI    # ou ton chemin
docker compose up -d           # Postgres :5434 + Redis :6380
./gradlew run                  # API sur :8080
```

Sanity check : `curl http://localhost:8080/health` → `{"status":"ok","db":true,"redis":true}`.

### Outils

- **JDK 17** (Temurin / Oracle, peu importe)
- **Android Studio** Ladybug+ (avec AGP 8.13)
- **Xcode 15+** (testé sur 26.5)
- **CocoaPods 1.15+** (`brew install cocoapods` ou `sudo gem install cocoapods`)
- **xcodegen** (`brew install xcodegen`) — pour générer `iosApp.xcodeproj` depuis `project.yml`

---

## 🤖 Lancer sur Android

### Premier setup

```bash
# Depuis la racine du repo
./gradlew :app:assembleDebug          # build l'APK (~5 min première fois)
```

### Run sur émulateur

1. Ouvre Android Studio → **Run ▶️** (le sélecteur de device détecte tes AVD)
2. **OU** en CLI :
```bash
# Liste les AVD disponibles
$ANDROID_HOME/emulator/emulator -list-avds

# Démarre un AVD
$ANDROID_HOME/emulator/emulator -avd Pixel_8_API_35 &

# Install + run
./gradlew :app:installDebug
adb shell am start -n com.example.kotlinbankui/.MainActivity
```

L'`API_BASE_URL` est hardcodée à `http://10.0.2.2:8080` côté Android (alias de
`localhost` depuis l'émulateur).

### Run sur device physique

Édite `app/build.gradle.kts` :
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.X.Y:8080\"")  // IP locale du Mac
```

Vérifie que `app/src/main/res/xml/network_security_config.xml` autorise cleartext
pour cette IP (par défaut `10.0.2.2` y est déjà).

---

## 🍎 Lancer sur iOS

### Premier setup (une seule fois)

```bash
# 1. Génère le framework Kotlin "dummy" pour que CocoaPods puisse résoudre le podspec
./gradlew :shared:generateDummyFramework

# 2. Génère le projet Xcode depuis iosApp/project.yml
cd iosApp && xcodegen && cd ..

# 3. Installe le framework :shared comme CocoaPod
cd iosApp && pod install && cd ..
```

À partir de là, **ouvre toujours `iosApp/iosApp.xcworkspace`** dans Xcode
(jamais le `.xcodeproj` directement — CocoaPods exige le workspace).

### Build + run sur Simulator

**Option A — Xcode (recommandé pour dev)**
1. Ouvre `iosApp/iosApp.xcworkspace`
2. Sélectionne un simulateur dans le picker (haut)
3. Run ▶️ (`Cmd+R`)

**Option B — CLI**
```bash
cd iosApp

# Liste les simulateurs dispos
xcrun simctl list devices available | grep iPhone

# Boot un simulateur + ouvre Simulator.app
xcrun simctl boot "iPhone 17"
open -a Simulator

# Build pour le simulateur
xcodebuild \
  -workspace iosApp.xcworkspace \
  -scheme iosApp \
  -configuration Debug \
  -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPhone 17' \
  build

# Install + launch
APP_PATH=$(xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -configuration Debug \
  -sdk iphonesimulator -showBuildSettings 2>/dev/null \
  | grep -m1 'BUILT_PRODUCTS_DIR' | awk '{print $3}')/iosApp.app
xcrun simctl install "iPhone 17" "$APP_PATH"
xcrun simctl launch "iPhone 17" com.finsim.ios
```

> **Note** : `Info.plist` autorise `NSAllowsArbitraryLoads=YES` pour permettre
> `http://localhost:8080` en dev. **Ne ship jamais en release avec ça.**

### Workflow de dev iOS

Quand tu modifies du code Kotlin dans `:shared`, le plugin CocoaPods Gradle
intégré rebuild le framework automatiquement au build Xcode suivant. Pas besoin
de relancer `pod install` ou `xcodegen`.

**Si tu modifies `iosApp/project.yml`** : relance `xcodegen` puis `pod install`.

### Build pour device physique

Édite `iosApp/project.yml` et renseigne ton équipe Apple :
```yaml
settings:
  base:
    DEVELOPMENT_TEAM: "ABC123XYZ"   # ton Team ID Apple Developer
```
Puis `xcodegen && pod install`. Modifie aussi `MainViewController.kt` pour
pointer vers ton API publique en debug (pas `localhost`).

---

## 🧰 Commandes Gradle utiles

```bash
# Build complet (Android + iOS framework)
./gradlew build

# Juste compile Kotlin (rapide, pas d'APK/framework)
./gradlew :shared:compileDebugKotlinAndroid
./gradlew :shared:compileKotlinIosSimulatorArm64

# APK debug
./gradlew :app:assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# Install sur AVD courant
./gradlew :app:installDebug

# Framework iOS (sans Xcode)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
# → shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework

# Re-générer le podspec après changement de :shared/build.gradle.kts
./gradlew :shared:podspec

# Re-générer le dummy framework (si pod install se plaint)
./gradlew :shared:generateDummyFramework

# Nettoyage complet
./gradlew clean
rm -rf shared/build app/build iosApp/Pods iosApp/iosApp.xcworkspace iosApp/iosApp.xcodeproj

# Voir toutes les tâches d'un module
./gradlew :shared:tasks --group "build"
./gradlew :app:tasks --group "install"
```

---

## 🐛 Debug & introspection

### Logs

**Android** :
```bash
adb logcat -s FinSimHttp:V          # logs Ktor (depuis ApiClient)
adb logcat | grep -i koin            # logs Koin DI
adb logcat *:E                       # tous les errors
```

**iOS Simulator** :
```bash
# Stream les logs de l'app en temps réel
xcrun simctl spawn "iPhone 17" log stream --predicate 'processImagePath contains "iosApp"' --level=debug

# Récupérer les logs filtrés (FinSimHttp = label ajouté dans ApiClient.kt)
xcrun simctl spawn "iPhone 17" log stream --predicate 'eventMessage CONTAINS "FinSim"'
```

### Screenshots simulateur iOS

```bash
xcrun simctl io "iPhone 17" screenshot /tmp/screen.png && open /tmp/screen.png
```

### Reset full d'un simulateur

```bash
xcrun simctl shutdown "iPhone 17"
xcrun simctl erase "iPhone 17"
```

### Inspecter une klib KMP (utile pour vérifier qu'un symbole est exporté)

```bash
ls shared/build/classes/kotlin/iosSimulatorArm64/main/klib/
```

### Voir les deps transitives résolues

```bash
./gradlew :shared:dependencies --configuration commonMainCompileClasspath | less
./gradlew :app:dependencies --configuration debugRuntimeClasspath | less
```

### Forcer une re-résolution Koin (si "no definition found")

Souvent : ajout d'un nouveau VM oublié dans `Modules.kt`. Cherche dans le module :
```bash
grep -n "viewModel" shared/src/commonMain/kotlin/com/finsim/di/Modules.kt
```

### Backend joignable depuis iOS Simulator ?

```bash
# Depuis le simulateur, localhost = Mac hôte. Test depuis l'app via Login,
# ou depuis le Mac :
curl http://localhost:8080/health
```

### Backend joignable depuis émulateur Android ?

```bash
# 10.0.2.2 = localhost du host depuis l'émulateur
adb shell 'curl -s http://10.0.2.2:8080/health'
```

---

## 🔐 Auth & Session

- **JWT HMAC256**, 60 min de vie.
- **Android** : stocké en clair dans DataStore (`finsim_auth.preferences_pb`).
- **iOS** : stocké en clair dans `NSUserDefaults` (à migrer vers Keychain pour prod).
- Au lancement : Splash lit le token, appelle `/auth/me` pour vérifier la validité.
- `SessionManager.guard {}` wrap chaque call authentifié. Sur 401 → token vidé →
  event `loggedOut` broadcast → `FinSimAppRoot` navigue vers Login.
- Pas de refresh token (à venir back side).

---

## 🗺️ Roadmap

- [x] Auth complet (register / login / me / logout)
- [x] Dashboard avec portfolio temps réel + PnL
- [x] Market feed avec filtres + polling 30s
- [x] Buy order + historique
- [x] Auto-logout sur 401
- [x] Migration KMP : Android + iOS depuis une base de code unique (Phases 1→5)
- [x] Graphiques candles (Vico multiplatform)
- [x] Order SELL (avec `realizedPnl` affiché en fin d'ordre)
- [ ] **Refresh token** (bloqué côté back)
- [ ] Keychain pour le token côté iOS (prod-ready)
- [ ] Dark mode (toggle déjà présent, à valider visuellement)
- [ ] Pull-to-refresh
- [ ] Tests unit (Kotest + Turbine) + tests Compose UI
- [ ] CI GitHub Actions : build Android APK + iOS .ipa (job macos)
- [ ] WebSocket pour le market feed (remplacer le polling 30s)
- [ ] Stocks / Forex / BRVM (sprint 4 back)
- [ ] Multi-devises FCFA / USD / EUR
- [ ] Gamification (badges, XP, leaderboard) — sprint 3 back

Voir aussi [`KMP_MIGRATION_PLAN.md`](KMP_MIGRATION_PLAN.md) pour le plan
détaillé suivi pendant la migration.

---

## 🧭 Conventions

- **Commits** : [Conventional Commits](https://www.conventionalcommits.org/) — `feat:`, `fix:`, `refactor:`, `chore:`, etc.
- **Package** : tout le code partagé est sous `com.finsim.*`. Le shell `:app`
  reste sous `com.example.kotlinbankui` (legacy applicationId, non critique).
- **Pattern écran** : `XxxScreen` stateless + `XxxRoute` host. Nouveau écran =
  écrire les deux + ajouter au `Modules.kt` + au `BankNavigation`.
- **Pas de `java.*` dans `:shared/commonMain`** : utiliser `kotlin.*` ou KMP libs.

---

## 👨‍💻 Auteur

**Gael SASSAN** — WHARPE Corp.

- GitHub : [@ClichyMercury](https://github.com/ClichyMercury)
- LinkedIn : [Gael SASSAN](https://www.linkedin.com/in/gael-yad-eugene-sassan-17a69b1b6/)

---

## 📄 Licence

MIT.
