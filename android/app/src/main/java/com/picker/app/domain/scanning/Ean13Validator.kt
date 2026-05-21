package com.picker.app.domain.scanning

/**
 * Проверка контрольной суммы EAN-13 по модулю 10 с весами 1 и 3.
 *   Σ(d_i · w_i) mod 10 == 0,  где w = [1,3,1,3,...,1] для разрядов с 0 по 12.
 */
object Ean13Validator {
    fun isValid(code: String): Boolean {
        if (code.length != 13 || code.any { !it.isDigit() }) return false
        var sum = 0
        for (i in 0..12) {
            val d = code[i].digitToInt()
            sum += if (i % 2 == 0) d else d * 3
        }
        return sum % 10 == 0
    }

    fun checksum(first12: String): Int {
        require(first12.length == 12 && first12.all(Char::isDigit))
        var sum = 0
        for (i in 0..11) {
            val d = first12[i].digitToInt()
            sum += if (i % 2 == 0) d else d * 3
        }
        return (10 - sum % 10) % 10
    }
}
