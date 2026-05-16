package com.picker.app.infrastructure.crypto

import de.mkammerer.argon2.Argon2Factory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinHasher @Inject constructor() {
    private val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

    fun hash(pin: String): String =
        argon2.hash(ITER, MEMORY_KB, PARALLELISM, pin.toCharArray())

    fun verify(pin: String, hash: String): Boolean =
        argon2.verify(hash, pin.toCharArray())

    private companion object {
        const val ITER = 3
        const val MEMORY_KB = 64 * 1024
        const val PARALLELISM = 1
    }
}
