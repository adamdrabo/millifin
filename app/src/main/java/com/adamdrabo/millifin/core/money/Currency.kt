package com.adamdrabo.millifin.core.money

enum class Currency(
    val code: String,
    val decimals: Int,
    val symbol: String
) {
    CAD("CAD", 2, "$"),
    EUR("EUR", 2, "€"),
    XOF("XOF", 0, "F CFA");

    companion object {
        fun fromCode(code: String?): Currency? = entries.firstOrNull { it.code == code }
    }
}
