package thaumcraft.api.aspects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.CompoundTag;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;


public class AspectListTest {

    @Test
    public void testSortByPrimalAspectHierarchy() {
        AspectList list = new AspectList();
        list.add(compound, 5);
        list.add(primal1, 10);
        list.add(compound2, 2);

        Aspect[] sorted = list.getAspectsSortedByPrimalAspectHierarchy();
        assertEquals(3, sorted.length);
        assertTrue(sorted[0].isPrimal());
        assertFalse(sorted[1].isPrimal());
        assertFalse(sorted[2].isPrimal());
    }

    @Test
    public void testCodecSerialization() {
        AspectList list = new AspectList();
        list.add(primal1, 5);
        list.add(primal2, 10);

        DataResult<Tag> encodeResult = AspectList.CODEC.encodeStart(NbtOps.INSTANCE, list);
        assertTrue(encodeResult.isSuccess(), "Codec encoding failed");

        Tag serializedTag = encodeResult.getOrThrow();

        DataResult<AspectList> decodeResult = AspectList.CODEC.parse(NbtOps.INSTANCE, serializedTag);
        assertTrue(decodeResult.isSuccess(), "Codec decoding failed");

        AspectList decodedList = decodeResult.getOrThrow();
        assertEquals(2, decodedList.size());
        assertEquals(5, decodedList.getAmount(primal1));
        assertEquals(10, decodedList.getAmount(primal2));
    }


    private Aspect primal1;
    private Aspect primal2;
    private Aspect primal3;
    private Aspect compound;
    private Aspect compound2;

    @BeforeEach
    public void setup() {
        Aspect.aspects.clear();

        primal1 = new Aspect("aer", 0xffff00, null, null, 1);
        primal2 = new Aspect("terra", 0x00ff00, null, null, 1);
        primal3 = new Aspect("ignis", 0xff0000, null, null, 1);
        compound = new Aspect("motus", 0x00ffff, new Aspect[]{primal1, primal2}, null, 1);
        compound2 = new Aspect("lux", 0x0000ff, new Aspect[]{primal1, primal3}, null, 1);
    }

    @Test
    public void testAdd() {
        AspectList list = new AspectList();
        list.add(primal1, 5);
        assertEquals(5, list.getAmount(primal1));

        list.add(primal1, 3);
        assertEquals(8, list.getAmount(primal1));

        list.add(primal2, 10);
        assertEquals(10, list.getAmount(primal2));

        assertEquals(2, list.size());
        assertEquals(18, list.visSize());
    }

    @Test
    public void testReduceAndRemove() {
        AspectList list = new AspectList();
        list.add(primal1, 10);

        assertTrue(list.reduce(primal1, 4));
        assertEquals(6, list.getAmount(primal1));

        assertFalse(list.reduce(primal1, 10)); // Cannot reduce more than amount
        assertEquals(6, list.getAmount(primal1));

        list.remove(primal1, 2);
        assertEquals(4, list.getAmount(primal1));

        list.remove(primal1, 4); // Reduces to 0, completely removes
        assertEquals(0, list.getAmount(primal1));
        assertEquals(0, list.size());

        list.add(primal2, 5);
        list.remove(primal2); // Simply removes
        assertEquals(0, list.getAmount(primal2));
    }

    @Test
    public void testMergeLists() {
        AspectList list1 = new AspectList();
        list1.add(primal1, 5);
        list1.add(primal2, 10);

        AspectList list2 = new AspectList();
        list2.add(primal1, 8); // larger amount
        list2.add(primal2, 5); // smaller amount
        list2.add(primal3, 2);

        list1.merge(list2);

        assertEquals(8, list1.getAmount(primal1));
        assertEquals(10, list1.getAmount(primal2));
        assertEquals(2, list1.getAmount(primal3));
    }

    @Test
    public void testMergeAspects() {
        AspectList list1 = new AspectList();
        list1.add(primal1, 5);

        list1.merge(primal1, 10);
        assertEquals(10, list1.getAmount(primal1));

        list1.merge(primal1, 3);
        assertEquals(10, list1.getAmount(primal1)); // keeps higher amount
    }

    @Test
    public void testAddLists() {
        AspectList list1 = new AspectList();
        list1.add(primal1, 5);
        list1.add(primal2, 10);

        AspectList list2 = new AspectList();
        list2.add(primal1, 8);
        list2.add(primal3, 2);

        list1.add(list2);

        assertEquals(13, list1.getAmount(primal1));
        assertEquals(10, list1.getAmount(primal2));
        assertEquals(2, list1.getAmount(primal3));
    }

    @Test
    public void testRemoveLists() {
        AspectList list1 = new AspectList();
        list1.add(primal1, 10);
        list1.add(primal2, 10);

        AspectList list2 = new AspectList();
        list2.add(primal1, 4);
        list2.add(primal2, 15);

        list1.remove(list2);

        assertEquals(6, list1.getAmount(primal1));
        assertEquals(0, list1.getAmount(primal2));
    }

    @Test
    public void testSortByAmount() {
        AspectList list = new AspectList();
        list.add(primal1, 5);
        list.add(primal2, 10);
        list.add(primal3, 2);

        Aspect[] sorted = list.getAspectsSortedByAmount();
        assertEquals(3, sorted.length);
        assertEquals(primal2, sorted[0]); // 10
        assertEquals(primal1, sorted[1]); // 5
        assertEquals(primal3, sorted[2]); // 2
    }

    @Test
    public void testSortByName() {
        AspectList list = new AspectList();
        list.add(primal1, 5); // aer
        list.add(primal2, 10); // terra
        list.add(primal3, 2); // ignis

        Aspect[] sorted = list.getAspectsSortedByName();
        assertEquals(3, sorted.length);
        assertEquals(primal1, sorted[0]); // aer
        assertEquals(primal3, sorted[1]); // ignis
        assertEquals(primal2, sorted[2]); // terra
    }

    @Test
    public void testCopy() {
        AspectList list1 = new AspectList();
        list1.add(primal1, 5);

        AspectList list2 = list1.copy();
        assertEquals(5, list2.getAmount(primal1));

        list1.add(primal1, 5);
        assertEquals(10, list1.getAmount(primal1));
        assertEquals(5, list2.getAmount(primal1));
    }

    @Test
    public void testNBTSerialization() {
        AspectList list = new AspectList();
        list.add(primal1, 5);
        list.add(primal2, 10);

        CompoundTag nbt = new CompoundTag();
        list.writeToNBT(nbt);

        assertTrue(nbt.contains("Aspects"));

        AspectList readList = new AspectList();
        readList.readFromNBT(nbt);

        assertEquals(2, readList.size());
        assertEquals(5, readList.getAmount(primal1));
        assertEquals(10, readList.getAmount(primal2));
    }

    @Test
    public void testNBTSerializationWithLabel() {
        AspectList list = new AspectList();
        list.add(primal1, 5);
        list.add(primal2, 10);

        CompoundTag nbt = new CompoundTag();
        list.writeToNBT(nbt, "CustomLabel");

        assertTrue(nbt.contains("CustomLabel"));

        AspectList readList = new AspectList();
        readList.readFromNBT(nbt, "CustomLabel");

        assertEquals(2, readList.size());
        assertEquals(5, readList.getAmount(primal1));
        assertEquals(10, readList.getAmount(primal2));
    }
}
