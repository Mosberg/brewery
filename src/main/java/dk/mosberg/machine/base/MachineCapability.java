package dk.mosberg.machine.base;

import dk.mosberg.data.MachineStage;
import dk.mosberg.runtime.LiquidStack;

public interface MachineCapability {
    MachineStage stage();

    boolean canProcess(LiquidStack input);

    LiquidStack process(LiquidStack input);
}
