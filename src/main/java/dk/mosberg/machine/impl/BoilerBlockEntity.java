package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import dk.mosberg.machine.base.AbstractMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BoilerBlockEntity extends AbstractMachineBlockEntity {

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(Identifier.tryParse(Brewery.MOD_ID + ":boiler"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return Identifier.tryParse(Brewery.MOD_ID + ":boiler");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.BOIL;
    }
}
