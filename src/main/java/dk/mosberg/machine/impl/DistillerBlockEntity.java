package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import dk.mosberg.machine.base.AbstractMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DistillerBlockEntity extends AbstractMachineBlockEntity {

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(Identifier.tryParse(Brewery.MOD_ID + ":distiller"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return Identifier.tryParse(Brewery.MOD_ID + ":distiller");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.DISTILL;
    }
}
