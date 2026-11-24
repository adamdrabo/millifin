package com.adamdrabo.millifin.core.money

import java.math.BigDecimal
import kotlin.math.absoluteValue

object MoneyFormatter {

    private const val GROUP_SEPARATOR = ' '
    private const val NO_BREAK_SPACE = ' '

    fun format(amountMinor: Long, currency: Currency): String {
        val sign = if (amountMinor < 0) "-" else ""
        val absolute = amountMinor.absoluteValue
        val factor = pow10(currency.decimals)
        val units = absolute / factor
        val fraction = absolute % factor
        val groupedUnits = units.toString()
            .reversed()
            .chunked(3)
            .joinToString(GROUP_SEPARATOR.toString())
            .reversed()
        val decimals = if (currency.decimals > 0) {
            "," + fraction.toString().padStart(currency.decimals, '0')
        } else {
            ""
        }
        return "$sign$groupedUnits$decimals$NO_BREAK_SPACE${currency.symbol}"
    }

    fun parse(input: String, currency: Currency): Long? {
        val cleaned = input
            .trim()
            .replace(" ", "")
            .replace(NO_BREAK_SPACE.toString(), "")
            .replace(GROUP_SEPARATOR.toString(), "")
            .replace(',', '.')
        if (cleaned.isEmpty() || cleaned.count { it == '.' } > 1) return null
        val value = cleaned.toBigDecimalOrNull() ?: return null
        if (value <= BigDecimal.ZERO) return null
        if (value.stripTrailingZeros().scale() > currency.decimals) return null
        return runCatching { value.movePointRight(currency.decimals).longValueExact() }.getOrNull()
    }

    fun toInput(amountMinor: Long, currency: Currency): String {
        val factor = pow10(currency.decimals)
        val units = amountMinor / factor
        val fraction = amountMinor % factor
        return if (currency.decimals == 0) {
            units.toString()
        } else {
            "$units," + fraction.toString().padStart(currency.decimals, '0')
        }
    }

    private fun pow10(exponent: Int): Long {
        var result = 1L
        repeat(exponent) { result *= 10 }
        return result
    }
}
