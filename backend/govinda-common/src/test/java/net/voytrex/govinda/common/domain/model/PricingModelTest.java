/*
 * Govinda ERP - PricingModel Enum Tests
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PricingModelTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        void shouldContainAllDefinedPricingModels() {
            assertThat(EnumSet.allOf(PricingModel.class))
                .containsExactlyInAnyOrder(
                    PricingModel.FIXED,
                    PricingModel.AGE_BASED,
                    PricingModel.REGION_BASED,
                    PricingModel.TIER_BASED,
                    PricingModel.SUBSCRIBER_TYPE_BASED,
                    PricingModel.USAGE_BASED,
                    PricingModel.COMPOSITE
                );
        }
    }
}

