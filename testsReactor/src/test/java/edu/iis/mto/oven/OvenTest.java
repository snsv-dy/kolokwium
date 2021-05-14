package edu.iis.mto.oven;

import static edu.iis.mto.oven.Oven.HEAT_UP_AND_FINISH_SETTING_TIME;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.verify;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        oven = new Oven(heating_module, fan);
    }

    @Test
    void runningOvenWithHeatingStageInProgram_shouldCompleteWithoutExceptions() {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.HEATER).build();
        BakingProgram program = getProgramForStage(stage);

        oven.start(program);

        verify(heating_module).heater(any());
    }

    @Test
    void runningOvenWithGrillStageInProgram_shouldCompleteWithoutExceptions() throws HeatingException {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.GRILL).build();
        BakingProgram program = getProgramForStage(stage);

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

        HeatingSettings heatUpSettings = makeSettings(initialTemp, HEAT_UP_AND_FINISH_SETTING_TIME);
        HeatingSettings heatingStageSettings = makeSettings(stage.getTargetTemp(), stage.getStageTime());

        InOrder inorder = Mockito.inOrder(heating_module);
        inorder.verify(heating_module).heater(heatUpSettings);
        inorder.verify(heating_module).heater(heatingStageSettings);
    }

    @Test
    void runningOvenWithThermoCirculationStageInProgram_shouldCompleteWithoutExceptions() throws HeatingException {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.THERMO_CIRCULATION).build();
        BakingProgram program = getProgramForStage(stage);

        oven.start(program);

        InOrder inOrder = Mockito.inOrder(heating_module, fan);

        HeatingSettings termoSettings = makeSettings(stage.getTargetTemp(), stage.getStageTime());
        inOrder.verify(fan).on();
        inOrder.verify(heating_module).termalCircuit(termoSettings);
        inOrder.verify(fan).off();
    }

    @Test
    void runningOvenWithGrillStageInProgram_HeatingModuleThrowsException_ovenShouldThrow_OvenException() throws HeatingException {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.GRILL).build();
        BakingProgram program = getProgramForStage(stage);

        Mockito.doThrow(HeatingException.class).when(heating_module).grill(any());
        assertThrows(OvenException.class, () -> oven.start(program));
    }

    @Test
    void runningOvenWithHeatingStageInProgramAndFanTurnedOn_shouldTurnFanOff() {
        ProgramStage stage = ProgramStage.builder().withHeat(HeatType.HEATER).build();
        BakingProgram program = getProgramForStage(stage);

        when(fan.isOn()).thenReturn(true);

        oven.start(program);

        verify(fan).off();
    }

    private HeatingSettings makeSettings(int targetTemp, int stageTime) {
        return HeatingSettings.builder()
                .withTargetTemp(targetTemp)
                .withTimeInMinutes(stageTime)
                .build();
    }

    private BakingProgram getProgramForStage(ProgramStage stage) {
        return BakingProgram.builder()
                .withStages(List.of(stage))
                .build();
    }
}
