/*
 * Govinda ERP - Portal Profile Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.masterdata.application.PersonService;
import net.voytrex.govinda.masterdata.domain.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PortalProfileServiceTest {

    @Mock
    private PersonService personService;

    @Test
    @DisplayName("should return person profile when person exists")
    void should_returnProfile_when_personExists() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var person = new Person(
            tenantId,
            new AhvNumber("756.1234.5678.90"),
            "Müller",
            "Hans",
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
        person.setId(personId);

        when(personService.getPerson(personId, tenantId)).thenReturn(person);

        var service = new PortalProfileService(personService);

        var result = service.getProfile(tenantId, personId);

        assertThat(result).isEqualTo(person);
        verify(personService).getPerson(personId, tenantId);
    }
}
