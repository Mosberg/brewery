package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BoilerBlockEntity {

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(new Identifier(Brewery.MOD_ID, "boiler"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return new Identifier(Brewery.MOD_ID, "boiler");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.BOIL;
    }
}
