package au.lupine.earthy.fabric.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class NBTTraversal {

    private final CompoundTag root;
    private final String[] keys;

    private final List<String> result = new ArrayList<>();

    public NBTTraversal(CompoundTag root, String... keys) {
        this.root = root;
        this.keys = keys;
    }

    public void traverse() {
        traverse(root);
    }

    private void traverse(CompoundTag compound) {
        for (String key : keys) {
            if (compound.contains(key, Tag.TAG_STRING)) {
                String string = compound.getString(key);
                if (!result.contains(string)) result.add(string);
            }
        }

        for (String key : compound.getAllKeys()) {
            Tag element = compound.get(key);

            if (element instanceof CompoundTag next) traverse(next);

            if (element instanceof ListTag list) {
                for (Tag tag : list) {
                    if (tag instanceof CompoundTag next) traverse(next);
                }
            }
        }
    }

    public List<String> getResult() {
        return result;
    }
}
