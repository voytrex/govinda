/*
 * Govinda ERP - ServiceDomain Enum Tests
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ServiceDomainTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        void shouldContainAllDefinedServiceDomains() {
            assertThat(EnumSet.allOf(ServiceDomain.class))
                .containsExactlyInAnyOrder(
                    ServiceDomain.HEALTHCARE,
                    ServiceDomain.BROADCAST,
                    ServiceDomain.TELECOM,
                    ServiceDomain.UTILITIES,
                    ServiceDomain.CUSTOM
                );
        }
    }
}

