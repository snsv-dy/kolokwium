package edu.iis.mto.oven;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Test
    void itCompiles() {
        MatcherAssert.assertThat(true, equalTo(true));
    }

    @Test
    void runningOvenWithHeatingStageInProgram_shouldCompleteWithoutExceptions(){

    }

    @Test
    void runningOvenWithGrillStageInProgram_shouldCompleteWithoutExceptions(){

    }

    @Test
    void runningOvenWithInitialTemperatureAndHeatingStageInProgram_shouldCompleteWithoutExceptions(){

    }

    @Test
    void runningOvenWithThermoCirculationStageInProgram_shouldCompleteWithoutExceptions(){

    }

    @Test
    void runningOvenWithThermoCirculationStageInProgram_HeatingModuleThrowsException_ovenShouldThrow_OvenException(){

    }
}
