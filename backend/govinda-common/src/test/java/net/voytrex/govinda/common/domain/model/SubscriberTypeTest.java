/*
 * Govinda ERP - SubscriberType Enum Tests
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SubscriberTypeTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        void shouldContainAllDefinedSubscriberTypes() {
            assertThat(EnumSet.allOf(SubscriberType.class))
                .containsExactlyInAnyOrder(
                    SubscriberType.INDIVIDUAL,
                    SubscriberType.PRIVATE_HOUSEHOLD,
                    SubscriberType.COLLECTIVE_HOUSEHOLD,
                    SubscriberType.CORPORATE_SMALL,
                    SubscriberType.CORPORATE_MEDIUM,
                    SubscriberType.CORPORATE_LARGE,
                    SubscriberType.NONPROFIT,
                    SubscriberType.PUBLIC_INSTITUTION
                );
        }
    }

    @Nested
    @DisplayName("Classification Helpers")
    class ClassificationHelpers {

        @Test
        void shouldIdentifyHouseholdTypesCorrectly() {
            assertThat(SubscriberType.PRIVATE_HOUSEHOLD.isHousehold()).isTrue();
            assertThat(SubscriberType.COLLECTIVE_HOUSEHOLD.isHousehold()).isTrue();

            assertThat(SubscriberType.INDIVIDUAL.isHousehold()).isFalse();
            assertThat(SubscriberType.CORPORATE_SMALL.isHousehold()).isFalse();
        }

        @Test
        void shouldIdentifyCorporateTypesCorrectly() {
            assertThat(SubscriberType.CORPORATE_SMALL.isCorporate()).isTrue();
            assertThat(SubscriberType.CORPORATE_MEDIUM.isCorporate()).isTrue();
            assertThat(SubscriberType.CORPORATE_LARGE.isCorporate()).isTrue();
            assertThat(SubscriberType.NONPROFIT.isCorporate()).isTrue();
            assertThat(SubscriberType.PUBLIC_INSTITUTION.isCorporate()).isTrue();

            assertThat(SubscriberType.INDIVIDUAL.isCorporate()).isFalse();
            assertThat(SubscriberType.PRIVATE_HOUSEHOLD.isCorporate()).isFalse();
        }
    }
}

