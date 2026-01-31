package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DistillerBlockEntity {

    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(new Identifier(Brewery.MOD_ID, "distiller"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return new Identifier(Brewery.MOD_ID, "distiller");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.DISTILL;
    }
}
