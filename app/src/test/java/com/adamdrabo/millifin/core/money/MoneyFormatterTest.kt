package com.adamdrabo.millifin.core.money

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun dollarsUseTwoDecimalsAndFrenchSeparators() {
        assertEquals("1 234,50 $", MoneyFormatter.format(123450, Currency.CAD))
    }

    @Test
    fun cfaFrancHasNoDecimals() {
        assertEquals("15 000 F CFA", MoneyFormatter.format(15000, Currency.XOF))
    }

    @Test
    fun negativeAmountsKeepTheSign() {
        assertEquals("-5,00 €", MoneyFormatter.format(-500, Currency.EUR))
    }

    @Test
    fun commaAndDotAreBothAcceptedAsDecimalSeparator() {
        assertEquals(1250L, MoneyFormatter.parse("12,5", Currency.CAD))
        assertEquals(1250L, MoneyFormatter.parse("12.50", Currency.CAD))
    }

    @Test
    fun spacesAreIgnored() {
        assertEquals(1500L, MoneyFormatter.parse("1 500", Currency.XOF))
    }

    @Test
    fun invalidAmountsAreRejected() {
        assertNull(MoneyFormatter.parse("", Currency.CAD))
        assertNull(MoneyFormatter.parse("0", Currency.CAD))
        assertNull(MoneyFormatter.parse("-3", Currency.CAD))
        assertNull(MoneyFormatter.parse("abc", Currency.CAD))
        assertNull(MoneyFormatter.parse("1,234", Currency.CAD))
        assertNull(MoneyFormatter.parse("10,5", Currency.XOF))
    }

    @Test
    fun inputRoundTrip() {
        assertEquals("12,05", MoneyFormatter.toInput(1205, Currency.EUR))
        assertEquals("1500", MoneyFormatter.toInput(1500, Currency.XOF))
    }
}
