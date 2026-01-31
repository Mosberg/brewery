package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class InfuserBlockEntity {

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(new Identifier(Brewery.MOD_ID, "infuser"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return new Identifier(Brewery.MOD_ID, "infuser");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.INFUSE;
    }
}
