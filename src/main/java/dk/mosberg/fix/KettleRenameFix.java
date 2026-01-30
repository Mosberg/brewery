package dk.mosberg.fix;

import java.util.Optional;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class KettleRenameFix extends DataFix {
    public KettleRenameFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private static Dynamic<?> rename(Dynamic<?> kettleDynamic) {
        Optional<String> optional = kettleDynamic.get("Name").asString().result();
        if (optional.equals(Optional.of("minecraft:kettle"))) {
            Dynamic<?> dynamic = kettleDynamic.get("Properties").orElseEmptyMap();
            return dynamic.get("level").asString("0").equals("0")
                    ? kettleDynamic.remove("Properties")
                    : kettleDynamic.set("Name",
                            kettleDynamic.createString("minecraft:water_kettle"));
        } else {
            return kettleDynamic;
        }
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("kettle_rename_fix",
                this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
                typed -> typed.update(DSL.remainderFinder(), KettleRenameFix::rename));
    }
}
