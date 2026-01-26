/*
 * Govinda ERP - Money Value Object Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTest {

    @Nested
    inner class `Creation` {

        @Test
        fun `should create money with amount and default currency`() {
            val money = Money(BigDecimal("100.50"))

            assertThat(money.amount).isEqualByComparingTo(BigDecimal("100.50"))
            assertThat(money.currency).isEqualTo(Currency.CHF)
        }

        @Test
        fun `should create money with explicit currency`() {
            val money = Money(BigDecimal("100.00"), Currency.EUR)

            assertThat(money.currency).isEqualTo(Currency.EUR)
        }

        @Test
        fun `should create using chf factory methods`() {
            assertThat(Money.chf(100.50).amount).isEqualByComparingTo(BigDecimal("100.50"))
            assertThat(Money.chf(100).amount).isEqualByComparingTo(BigDecimal("100.00"))
            assertThat(Money.chf("100.50").amount).isEqualByComparingTo(BigDecimal("100.50"))
        }

        @Test
        fun `ZERO constant should be zero CHF`() {
            assertThat(Money.ZERO.amount).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(Money.ZERO.currency).isEqualTo(Currency.CHF)
        }
    }

    @Nested
    inner class `Swiss Rappen Rounding` {

        @Test
        fun `should round 10_12 to 10_10`() {
            val money = Money.chf("10.12")
            assertThat(money.roundToRappen().amount).isEqualByComparingTo(BigDecimal("10.10"))
        }

        @Test
        fun `should round 10_13 to 10_15`() {
            val money = Money.chf("10.13")
            assertThat(money.roundToRappen().amount).isEqualByComparingTo(BigDecimal("10.15"))
        }

        @Test
        fun `should round 10_17 to 10_15`() {
            val money = Money.chf("10.17")
            assertThat(money.roundToRappen().amount).isEqualByComparingTo(BigDecimal("10.15"))
        }

        @Test
        fun `should round 10_18 to 10_20`() {
            val money = Money.chf("10.18")
            assertThat(money.roundToRappen().amount).isEqualByComparingTo(BigDecimal("10.20"))
        }

        @Test
        fun `should keep exact 5 Rappen values unchanged`() {
            val money = Money.chf("10.15")
            assertThat(money.roundToRappen().amount).isEqualByComparingTo(BigDecimal("10.15"))
        }

        @Test
        fun `should round premium example correctly`() {
            // Real-world example: calculated premium of 325.43 CHF
            val premium = Money.chf("325.43")
            assertThat(premium.roundToRappen().amount).isEqualByComparingTo(BigDecimal("325.45"))
        }
    }

    @Nested
    inner class `Arithmetic Operations` {

        @Test
        fun `should add same currency`() {
            val a = Money.chf(100)
            val b = Money.chf(50)

            assertThat((a + b).amount).isEqualByComparingTo(BigDecimal("150.00"))
        }

        @Test
        fun `should subtract same currency`() {
            val a = Money.chf(100)
            val b = Money.chf(30)

            assertThat((a - b).amount).isEqualByComparingTo(BigDecimal("70.00"))
        }

        @Test
        fun `should multiply by integer`() {
            val money = Money.chf(100)

            assertThat((money * 3).amount).isEqualByComparingTo(BigDecimal("300.00"))
        }

        @Test
        fun `should multiply by decimal`() {
            val money = Money.chf(100)

            assertThat((money * BigDecimal("0.10")).amount).isEqualByComparingTo(BigDecimal("10.00"))
        }

        @Test
        fun `should negate`() {
            val money = Money.chf(100)

            assertThat(money.negate().amount).isEqualByComparingTo(BigDecimal("-100.00"))
        }

        @Test
        fun `should reject adding different currencies`() {
            val chf = Money.chf(100)
            val eur = Money(BigDecimal("100"), Currency.EUR)

            assertThatThrownBy { chf + eur }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("different currencies")
        }

        @Test
        fun `should reject subtracting different currencies`() {
            val chf = Money.chf(100)
            val eur = Money(BigDecimal("100"), Currency.EUR)

            assertThatThrownBy { chf - eur }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class `Comparison` {

        @Test
        fun `should compare same currency`() {
            val a = Money.chf(100)
            val b = Money.chf(200)
            val c = Money.chf(100)

            assertThat(a < b).isTrue()
            assertThat(b > a).isTrue()
            assertThat(a.compareTo(c)).isEqualTo(0)
        }

        @Test
        fun `should check positive negative zero`() {
            assertThat(Money.chf(100).isPositive()).isTrue()
            assertThat(Money.chf(-100).isNegative()).isTrue()
            assertThat(Money.ZERO.isZero()).isTrue()
        }

        @Test
        fun `should reject comparing different currencies`() {
            val chf = Money.chf(100)
            val eur = Money(BigDecimal("100"), Currency.EUR)

            assertThatThrownBy { chf.compareTo(eur) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class `String Representation` {

        @Test
        fun `should format with currency symbol`() {
            val money = Money.chf("123.45")

            assertThat(money.toString()).isEqualTo("CHF 123.45")
        }
    }
}
