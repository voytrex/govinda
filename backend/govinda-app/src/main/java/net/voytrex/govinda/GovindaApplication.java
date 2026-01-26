/*
 * Govinda ERP - Main Application
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Govinda ERP Application
 *
 * Open Source Enterprise Resource Planning for Swiss Health Insurance
 *
 * @see <a href="https://www.voytrex.net/">Voytrex</a>
 */
@SpringBootApplication
@ComponentScan(basePackages = "net.voytrex.govinda")
public class GovindaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovindaApplication.class, args);
    }
}
