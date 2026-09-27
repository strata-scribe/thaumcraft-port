import net.minecraft.world.item.Item;
public class test_use extends Item {
    public test_use(Properties properties) { super(properties); }
    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) { return null; }
}
