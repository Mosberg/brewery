package dk.mosberg.runtime;

import dk.mosberg.data.Metadata;
import net.minecraft.util.Identifier;

public class LiquidStack {
    private final Identifier liquidId;
    private final int amount;
    private final Metadata metadata;

    public LiquidStack(Identifier liquidId, int amount, Metadata metadata) {
        this.liquidId = liquidId;
        this.amount = amount;
        this.metadata = metadata;
    }

    public Identifier liquidId() {
        return liquidId;
    }

    public int amount() {
        return amount;
    }

    public Metadata metadata() {
        return metadata;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }
}
