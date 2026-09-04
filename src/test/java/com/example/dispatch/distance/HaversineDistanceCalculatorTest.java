package com.example.dispatch.distance;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HaversineDistanceCalculatorTest {
    private final HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    void sameCoordinatesHaveZeroDistance() {
        assertThat(calculator.calculateDistance(28.6139, 77.2090, 28.6139, 77.2090)).isZero();
    }

    @Test
    void calculatesKnownDistanceWithinTolerance() {
        double distance = calculator.calculateDistance(28.6139, 77.2090, 28.5355, 77.3910);
        assertThat(distance).isBetween(18.0, 20.0);
    }

    @Test
    void rejectsInvalidCoordinates() {
        assertThatThrownBy(() -> calculator.calculateDistance(91, 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> calculator.calculateDistance(0, 181, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
