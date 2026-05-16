package com.picker.app.domain.usecase

import com.picker.app.data.repository.PickerRepository
import com.picker.app.infrastructure.crypto.PinHasher
import com.picker.app.infrastructure.crypto.SessionStore
import com.picker.app.infrastructure.network.AuthApi
import javax.inject.Inject

/**
 * Ф1. Аутентификация пикера: проверка PIN (Argon2id), открытие/привязка
 * к активной смене, выдача JWT с TTL 12 ч. До 5 неуспешных попыток подряд.
 */
class AuthUseCase @Inject constructor(
    private val pickers: PickerRepository,
    private val hasher: PinHasher,
    private val session: SessionStore,
    private val api: AuthApi,
) {
    sealed interface Result {
        data class Success(val pickerId: String, val shiftId: String) : Result
        data object InvalidCredentials : Result
        data object LockedOut : Result
    }

    suspend operator fun invoke(login: String, pin: String): Result {
        require(pin.length >= 6 && pin.all(Char::isDigit)) { "PIN must be 6+ digits" }
        if (session.failedAttempts() >= MAX_ATTEMPTS) return Result.LockedOut
        val picker = pickers.findByLogin(login) ?: return Result.InvalidCredentials.also {
            session.recordFailure()
        }
        if (!hasher.verify(pin, picker.pinHash)) {
            session.recordFailure()
            return Result.InvalidCredentials
        }
        val token = api.exchangeForJwt(picker.id)
        session.persist(token, picker.id)
        session.resetFailures()
        return Result.Success(picker.id, token.shiftId)
    }

    private companion object { const val MAX_ATTEMPTS = 5 }
}
