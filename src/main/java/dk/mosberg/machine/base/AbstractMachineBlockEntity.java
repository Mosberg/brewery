package dk.mosberg.machine.base;

import java.util.List;
import dk.mosberg.data.MachineRecipe;
import dk.mosberg.data.MachineStage;
import dk.mosberg.registry.MachineRecipeRegistry;
import dk.mosberg.runtime.LiquidStack;
import dk.mosberg.runtime.MetadataEngine;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractMachineBlockEntity extends BlockEntity {

    protected LiquidStack inputTank;
    protected LiquidStack outputTank;

    protected AbstractMachineBlockEntity(Identifier typeId, BlockPos pos, BlockState state) {
        super(null, pos, state); // replace null with your BlockEntityType
    }

    protected abstract Identifier machineId();

    protected abstract MachineStage stage();

    public void tickServer() {
        if (inputTank == null || inputTank.isEmpty())
            return;

        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipesForMachine(machineId());
        for (MachineRecipe recipe : recipes) {
            if (matches(recipe)) {
                process(recipe);
                break;
            }
        }
    }

    protected boolean matches(MachineRecipe recipe) {
        // For now, assume any recipe for this machine matches if we have a liquid.
        return true;
    }

    protected void process(MachineRecipe recipe) {
        var outputDef = recipe.output();
        LiquidStack out = new LiquidStack(outputDef.liquid(), outputDef.amount(),
                inputTank.metadata().copy());

        MetadataEngine.applyStage(out.liquidId(), out.metadata(), stage().name().toLowerCase(),
                outputDef.metadataDelta());

        this.outputTank = out;
        this.inputTank = null;
    }
}
