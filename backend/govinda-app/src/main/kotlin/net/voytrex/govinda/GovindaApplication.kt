/*
 * Govinda ERP - Main Application
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Govinda ERP Application
 *
 * Open Source Enterprise Resource Planning for Swiss Health Insurance
 *
 * @see <a href="https://www.voytrex.net/">Voytrex</a>
 */
@SpringBootApplication
class GovindaApplication

fun main(args: Array<String>) {
    runApplication<GovindaApplication>(*args)
}
