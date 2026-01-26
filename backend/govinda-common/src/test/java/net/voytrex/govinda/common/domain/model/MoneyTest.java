/*
 * Govinda ERP - Money Value Object Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        void shouldCreateMoneyWithAmountAndDefaultCurrency() {
            Money money = new Money(new BigDecimal("100.50"));

            assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
            assertThat(money.getCurrency()).isEqualTo(Currency.CHF);
        }

        @Test
        void shouldCreateMoneyWithExplicitCurrency() {
            Money money = new Money(new BigDecimal("100.00"), Currency.EUR);

            assertThat(money.getCurrency()).isEqualTo(Currency.EUR);
        }

        @Test
        void shouldCreateUsingChfFactoryMethods() {
            assertThat(Money.chf(100.50).getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
            assertThat(Money.chf(100).getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(Money.chf("100.50").getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        }

        @Test
        void zeroConstantShouldBeZeroChf() {
            assertThat(Money.ZERO.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(Money.ZERO.getCurrency()).isEqualTo(Currency.CHF);
        }
    }

    @Nested
    @DisplayName("Swiss Rappen Rounding")
    class SwissRappenRounding {

        @Test
        void shouldRound10_12To10_10() { // CHECKSTYLE:OFF: MethodName - Descriptive test name with numbers
            Money money = Money.chf("10.12");
            assertThat(money.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("10.10"));
        }

        @Test
        void shouldRound10_13To10_15() { // CHECKSTYLE:OFF: MethodName - Descriptive test name with numbers
            Money money = Money.chf("10.13");
            assertThat(money.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("10.15"));
        }

        @Test
        void shouldRound10_17To10_15() { // CHECKSTYLE:OFF: MethodName - Descriptive test name with numbers
            Money money = Money.chf("10.17");
            assertThat(money.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("10.15"));
        }

        @Test
        void shouldRound10_18To10_20() { // CHECKSTYLE:OFF: MethodName - Descriptive test name with numbers
            Money money = Money.chf("10.18");
            assertThat(money.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("10.20"));
        }

        @Test
        void shouldKeepExact5RappenValuesUnchanged() {
            Money money = Money.chf("10.15");
            assertThat(money.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("10.15"));
        }

        @Test
        void shouldRoundPremiumExampleCorrectly() {
            Money premium = Money.chf("325.43");
            assertThat(premium.roundToRappen().getAmount()).isEqualByComparingTo(new BigDecimal("325.45"));
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticOperations {

        @Test
        void shouldAddSameCurrency() {
            Money a = Money.chf(100);
            Money b = Money.chf(50);

            assertThat(a.add(b).getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        }

        @Test
        void shouldSubtractSameCurrency() {
            Money a = Money.chf(100);
            Money b = Money.chf(30);

            assertThat(a.subtract(b).getAmount()).isEqualByComparingTo(new BigDecimal("70.00"));
        }

        @Test
        void shouldMultiplyByInteger() {
            Money money = Money.chf(100);

            assertThat(money.multiply(3).getAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        }

        @Test
        void shouldMultiplyByDecimal() {
            Money money = Money.chf(100);

            assertThat(money.multiply(new BigDecimal("0.10")).getAmount())
                .isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        void shouldNegate() {
            Money money = Money.chf(100);

            assertThat(money.negate().getAmount()).isEqualByComparingTo(new BigDecimal("-100.00"));
        }

        @Test
        void shouldRejectAddingDifferentCurrencies() {
            Money chf = Money.chf(100);
            Money eur = new Money(new BigDecimal("100"), Currency.EUR);

            assertThatThrownBy(() -> chf.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
        }

        @Test
        void shouldRejectSubtractingDifferentCurrencies() {
            Money chf = Money.chf(100);
            Money eur = new Money(new BigDecimal("100"), Currency.EUR);

            assertThatThrownBy(() -> chf.subtract(eur))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Comparison")
    class Comparison {

        @Test
        void shouldCompareSameCurrency() {
            Money a = Money.chf(100);
            Money b = Money.chf(200);
            Money c = Money.chf(100);

            assertThat(a.compareTo(b)).isLessThan(0);
            assertThat(b.compareTo(a)).isGreaterThan(0);
            assertThat(a.compareTo(c)).isEqualTo(0);
        }

        @Test
        void shouldCheckPositiveNegativeZero() {
            assertThat(Money.chf(100).isPositive()).isTrue();
            assertThat(Money.chf(-100).isNegative()).isTrue();
            assertThat(Money.ZERO.isZero()).isTrue();
        }

        @Test
        void shouldRejectComparingDifferentCurrencies() {
            Money chf = Money.chf(100);
            Money eur = new Money(new BigDecimal("100"), Currency.EUR);

            assertThatThrownBy(() -> chf.compareTo(eur))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        void shouldFormatWithCurrencySymbol() {
            Money money = Money.chf("123.45");

            assertThat(money.toString()).isEqualTo("CHF 123.45");
        }
    }
}
