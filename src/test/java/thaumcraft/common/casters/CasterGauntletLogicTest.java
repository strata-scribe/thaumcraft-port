package thaumcraft.common.casters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CasterGauntletLogic Unit Tests")
class CasterGauntletLogicTest {

    private static final float EPSILON = 1.0e-5f;

    @Nested
    @DisplayName("Cooldown Ticks Calculation")
    class CooldownTicksTests {

        @Test
        @DisplayName("Negative or zero complexity defaults to 5 ticks")
        void testZeroOrNegativeComplexity() {
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(0));
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(-10));
        }

        @Test
        @DisplayName("Valid complexities produce expected cooldown ticks")
        void testValidComplexity() {
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(4));
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(5));
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(8));
            assertEquals(5, CasterGauntletLogic.calculateCooldownTicks(10));
            assertEquals(6, CasterGauntletLogic.calculateCooldownTicks(12));
            assertEquals(9, CasterGauntletLogic.calculateCooldownTicks(15));
            assertEquals(20, CasterGauntletLogic.calculateCooldownTicks(20));
            assertEquals(30, CasterGauntletLogic.calculateCooldownTicks(25));
            assertEquals(80, CasterGauntletLogic.calculateCooldownTicks(40));
        }
    }

    @Nested
    @DisplayName("Total Vis Discount Aggregation")
    class TotalVisDiscountTests {

        @Test
        @DisplayName("Null or empty list returns 0")
        void testNullOrEmptyList() {
            assertEquals(0, CasterGauntletLogic.calculateTotalVisDiscount(null));
            assertEquals(0, CasterGauntletLogic.calculateTotalVisDiscount(Collections.emptyList()));
        }

        @Test
        @DisplayName("Normal list of discounts aggregates correctly")
        void testNormalDiscounts() {
            List<Integer> discounts = Arrays.asList(5, 5, 2, 3);
            assertEquals(15, CasterGauntletLogic.calculateTotalVisDiscount(discounts));
        }

        @Test
        @DisplayName("Negative and null discounts are ignored")
        void testNegativeAndNullDiscounts() {
            List<Integer> discounts = Arrays.asList(5, null, -2, 3);
            assertEquals(8, CasterGauntletLogic.calculateTotalVisDiscount(discounts));
        }

        @Test
        @DisplayName("Total discount is strictly capped at 50%")
        void testDiscountCap() {
            List<Integer> overCap = Arrays.asList(20, 20, 15, 5);
            assertEquals(50, CasterGauntletLogic.calculateTotalVisDiscount(overCap));
        }
    }

    @Nested
    @DisplayName("Consumption Modifier Conversion")
    class ConsumptionModifierTests {

        @Test
        @DisplayName("0 discount translates to 1.0f modifier")
        void testZeroDiscount() {
            assertEquals(1.0f, CasterGauntletLogic.calculateConsumptionModifier(0), EPSILON);
        }

        @Test
        @DisplayName("Standard discounts translate to appropriate modifiers")
        void testStandardDiscounts() {
            assertEquals(0.95f, CasterGauntletLogic.calculateConsumptionModifier(5), EPSILON);
            assertEquals(0.85f, CasterGauntletLogic.calculateConsumptionModifier(15), EPSILON);
            assertEquals(0.70f, CasterGauntletLogic.calculateConsumptionModifier(30), EPSILON);
        }

        @Test
        @DisplayName("Discounts over 50% are capped to 0.5f modifier")
        void testOvercapDiscounts() {
            assertEquals(0.5f, CasterGauntletLogic.calculateConsumptionModifier(55), EPSILON);
            assertEquals(0.5f, CasterGauntletLogic.calculateConsumptionModifier(100), EPSILON);
        }

        @Test
        @DisplayName("Negative discounts are treated as 0")
        void testNegativeDiscounts() {
            assertEquals(1.0f, CasterGauntletLogic.calculateConsumptionModifier(-10), EPSILON);
        }
    }
}
