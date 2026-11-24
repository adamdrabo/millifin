package com.adamdrabo.millifin.settings.ui

import androidx.annotation.StringRes
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.core.money.Currency

@StringRes
fun Currency.labelRes(): Int = when (this) {
    Currency.CAD -> R.string.currency_cad
    Currency.EUR -> R.string.currency_eur
    Currency.XOF -> R.string.currency_xof
}
