package edu.iis.mto.oven;

import static edu.iis.mto.oven.Oven.HEAT_UP_AND_FINISH_SETTING_TIME;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OvenTest {
    @Mock
    private HeatingModule heating_module;
    @Mock
    private Fan fan;
    private Oven oven;

    @Test
    void itCompiles() {
        MatcherAssert.assertThat(true, equalTo(true));
    }

    @BeforeEach
    void setUp() {
        oven = new Oven(heating_module, fan);
    }

    @Test
    void runningOvenWithHeatingStageInProgram_shouldCompleteWithoutExceptions() {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.HEATER).build();
        BakingProgram program = BakingProgram.builder()
                .withStages(List.of(stage))
                .build();

        oven.start(program);

        verify(heating_module).heater(any());
    }

    @Test
    void runningOvenWithGrillStageInProgram_shouldCompleteWithoutExceptions() throws HeatingException {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.GRILL).build();
        BakingProgram program = BakingProgram.builder()
                .withStages(List.of(stage))
                .build();

        oven.start(program);

        verify(heating_module).grill(any());
    }

    @Test
    void runningOvenWithInitialTemperatureAndHeatingStageInProgram_shouldCompleteWithoutExceptions() {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.HEATER).build();
        int initialTemp = 180;
        BakingProgram program = BakingProgram.builder()
                .withStages(List.of(stage))
                .withInitialTemp(initialTemp)
                .build();

        oven.start(program);

        HeatingSettings heatUpSettings = HeatingSettings.builder()
                .withTargetTemp(initialTemp)
                .withTimeInMinutes(HEAT_UP_AND_FINISH_SETTING_TIME)
                .build();
        InOrder inorder = Mockito.inOrder(heating_module);

        inorder.verify(heating_module).heater(heatUpSettings);
        HeatingSettings heatingStageSettings = HeatingSettings.builder()
                .withTargetTemp(stage.getTargetTemp())
                .withTimeInMinutes(stage.getStageTime())
                .build();

        inorder.verify(heating_module).heater(heatingStageSettings);
    }

    @Test
    void runningOvenWithThermoCirculationStageInProgram_shouldCompleteWithoutExceptions() {

    }

    @Test
    void runningOvenWithThermoCirculationStageInProgram_HeatingModuleThrowsException_ovenShouldThrow_OvenException() {

    }
}
