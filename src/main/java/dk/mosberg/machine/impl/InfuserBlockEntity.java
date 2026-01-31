package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import dk.mosberg.machine.base.AbstractMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class InfuserBlockEntity extends AbstractMachineBlockEntity {

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(Identifier.tryParse(Brewery.MOD_ID + ":infuser"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return Identifier.tryParse(Brewery.MOD_ID + ":infuser");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.INFUSE;
    }
}
