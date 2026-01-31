package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import dk.mosberg.machine.base.AbstractMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CrusherBlockEntity extends AbstractMachineBlockEntity {

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(Identifier.tryParse(Brewery.MOD_ID + ":crusher"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return Identifier.tryParse(Brewery.MOD_ID + ":crusher");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.MASH; // Or PRESS, depending on context
    }
}
