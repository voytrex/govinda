/*
 * Govinda ERP - Money Value Object
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money value object with currency.
 *
 * Supports Swiss Rappen rounding (to 5 centimes).
 */
@Embeddable
public class Money implements Comparable<Money> {
    public static final Money ZERO = new Money(BigDecimal.ZERO, Currency.CHF);

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private Currency currency = Currency.CHF;

    protected Money() {
    }

    public Money(BigDecimal amount) {
        this(amount, Currency.CHF);
    }

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Money amount must have at most 2 decimal places");
        }
        this.amount = amount;
        this.currency = currency != null ? currency : Currency.CHF;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Money amount must have at most 2 decimal places");
        }
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency != null ? currency : Currency.CHF;
    }

    /**
     * Rounds the amount to Swiss 5-Rappen increments.
     */
    public Money roundToRappen() {
        BigDecimal rounded = amount
            .multiply(new BigDecimal("20"))
            .setScale(0, RoundingMode.HALF_UP)
            .divide(new BigDecimal("20"), 2, RoundingMode.HALF_UP);
        return new Money(rounded, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(int factor) {
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), currency);
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return currency + " " + amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money chf(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP), Currency.CHF);
    }

    public static Money chf(int amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP), Currency.CHF);
    }

    public static Money chf(String amount) {
        return new Money(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP), Currency.CHF);
    }

    private void requireSameCurrency(Money other) {
        if (other == null || currency != other.currency) {
            throw new IllegalArgumentException("Cannot operate on different currencies");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Money money = (Money) other;
        return Objects.equals(amount, money.amount) && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
