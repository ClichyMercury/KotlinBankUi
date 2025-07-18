# 🏦 KotlinBankUI advanced

Une application bancaire moderne développée en Kotlin avec Jetpack Compose, présentant une interface utilisateur élégante et une architecture MVVM robuste.

## 📱 Aperçu

KotlinBankUI est une application de banque mobile complète avec une interface utilisateur moderne utilisant Material Design 3. L'application offre une expérience utilisateur fluide pour la gestion des finances personnelles.

## ✨ Fonctionnalités

### 🏠 Écran d'Accueil
- **Balance Card** avec gradient moderne
- **Quick Actions** (Transfer, Pay Bills, Top Up, More)
- **Salutation dynamique** basée sur l'heure
- **Activité récente** avec états visuels
- **Badge de notifications** avec compteur

### 💳 Portefeuille
- Vue d'ensemble du solde total
- Statistiques Income/Expenses/Savings
- Actions rapides (Send, Receive, Add Card)
- Historique des transactions récentes
- Design avec cards modernes

### 📊 Transactions
- **Filtres avancés** (All, Income, Expenses, Today, This Week)
- **Groupement par date** (Today, Yesterday, This Week, Earlier)
- **8 catégories** avec icônes personnalisées
- **Status badges** (Completed, Pending, Failed)
- **Résumé Income/Expenses**

### 🔔 Notifications
- **6 types** de notifications bancaires
- **Filtrage par type** et statut read/unread
- **Groupement temporel** intelligent
- **Badges visuels** (Important, Action Required)
- **Compteurs statistiques**

### 👤 Profil
- **Informations utilisateur** complètes
- **Quick Stats** (cartes, comptes, transactions)
- **Paramètres organisés** par sections
- **Toggles fonctionnels** (notifications, biométrie, dark mode)
- **Support & Legal** links

### 💱 Devises
- **Section expandable** avec animations fluides
- **6 devises** (USD, EUR, YEN, YUAN, FCFA, Pound)
- **Taux Buy/Sell** en temps réel (mockés)
- **Design responsive** dans LazyColumn

## 🏗️ Architecture

### MVVM Pattern
```
presentation/
├── screens/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── HomeUiState.kt
│   ├── wallet/
│   ├── transactions/
│   ├── notifications/
│   └── profile/
├── components/
│   ├── BottomNavigationBar.kt
│   ├── CardsSection.kt
│   ├── CurrenciesSection.kt
│   └── ...
└── navigation/
    ├── BankNavigation.kt
    ├── NavigationRoutes.kt
    └── NavigationItem.kt

domain/
└── models/
    ├── Card.kt
    ├── Currency.kt
    ├── Transaction.kt
    └── ...
```

### 🎨 Design System
- **Material 3** avec thème personnalisé
- **Police Michroma** pour les titres
- **Couleurs cohérentes** pour l'identité bancaire
- **Animations fluides** avec AnimatedVisibility
- **Layout responsive** pour tous les écrans

## 🛠️ Technologies Utilisées

- **Kotlin** - Langage principal
- **Jetpack Compose** - UI moderne et déclarative
- **Navigation Compose** - Navigation entre écrans
- **Material 3** - Design system moderne
- **MVVM Architecture** - Séparation des responsabilités
- **StateFlow** - Gestion d'état réactive
- **Custom Fonts** - Typography avec Michroma

## 📋 Prérequis

- Android Studio Hedgehog | 2023.1.1 ou plus récent
- SDK Android 24+ (Android 7.0)
- Kotlin 1.9.0+
- Gradle 8.0+

## 🚀 Installation

1. **Cloner le repository**
   ```bash
   git clone https://github.com/votre-username/KotlinBankUI.git
   cd KotlinBankUI
   ```

2. **Ouvrir dans Android Studio**
   - Ouvrir Android Studio
   - File → Open → Sélectionner le dossier du projet

3. **Synchroniser le projet**
   - Android Studio synchronisera automatiquement les dépendances
   - Attendre la fin du build

4. **Lancer l'application**
   - Connecter un appareil Android ou lancer un émulateur
   - Cliquer sur Run ou Ctrl+F5

## 📦 Dépendances Principales

```kotlin
dependencies {
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    
    // Compose
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose:1.8.2'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    
    // ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
    
    // Fonts
    implementation 'androidx.compose.ui:ui-text-google-fonts:1.5.8'
}
```

## 🎯 Roadmap

### 🔄 Phase 1 - MVP (✅ Terminé)
- [x] Architecture MVVM
- [x] 5 écrans principaux
- [x] Navigation fonctionnelle
- [x] UI moderne avec Material 3
- [x] Données mockées

### 🌐 Phase 2 - Backend Integration
- [ ] API REST avec Retrofit
- [ ] Repository Pattern
- [ ] Base de données locale (Room)
- [ ] Gestion des erreurs réseau

### 🔐 Phase 3 - Authentification
- [ ] Écran de login/register
- [ ] Authentification biométrique
- [ ] Gestion des sessions
- [ ] Sécurité renforcée

### 🎨 Phase 4 - UX Avancée
- [ ] Dark mode complet
- [ ] Animations avancées
- [ ] Pull-to-refresh
- [ ] États de chargement sophistiqués

### 📱 Phase 5 - Fonctionnalités Avancées
- [ ] Notifications push
- [ ] Export PDF des relevés
- [ ] Graphiques et analytics
- [ ] Support multi-langues

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -m 'feat: ajouter nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Ouvrir une Pull Request

## 📝 Conventions de Code

- **Commits** : Suivre [Conventional Commits](https://www.conventionalcommits.org/)
- **Kotlin** : Suivre les [conventions officielles](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose** : Composables en PascalCase, `@Preview` pour tous les composants

## 📸 Screenshots

<img width="478" height="1080" alt="image" src="https://github.com/user-attachments/assets/22b2b4c2-43ed-4329-aa7d-7f9c1e613818" />
<img width="478" height="1080" alt="image" src="https://github.com/user-attachments/assets/ced94ead-26cb-423a-992a-04c0c232bb99" />

## Inspiration 
https://youtu.be/pCy93IdWr9s?si=wPPfGgwiIWH-YdHx


## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👨‍💻 Auteur

**Votre Nom**
- GitHub: [@ClichyMercury](https://github.com/ClichyMercury)
- LinkedIn: [Gael SASSAN](https://www.linkedin.com/in/gael-yad-eugene-sassan-17a69b1b6/)

## 🙏 Remerciements

- [Material Design 3](https://m3.material.io/) pour les guidelines de design
- [Jetpack Compose](https://developer.android.com/jetpack/compose) pour le framework UI
- [Google Fonts](https://fonts.google.com/) pour la police Michroma

---

⭐ **N'hésitez pas à starrer le repo si ce projet vous plaît !** ⭐
