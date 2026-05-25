package com.finsim.presentation.util

import com.finsim.data.network.ApiException

/** Generic user-facing message for any throwable, with friendly defaults for ApiException. */
fun Throwable.uiMessage(): String = when (this) {
    is ApiException.Http -> userMessage
    is ApiException.Unauthorized -> "Session expirée"
    is ApiException.Network -> "Pas de connexion"
    else -> message ?: "Erreur inconnue"
}

/** Login/Register-specific message: ApiException.Unauthorized means bad credentials. */
fun Throwable.authMessage(): String = when (this) {
    is ApiException.Http -> userMessage
    is ApiException.Unauthorized -> "Email ou mot de passe incorrect"
    is ApiException.Network -> "Pas de connexion au serveur"
    else -> message ?: "Erreur inconnue"
}
