package thaumcraft.api.capabilities;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
public interface IPlayerWarp
{
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);

    /**
     * Clears all warp. 
     */
    void clear();
    

    /**
     * @param type The warp type to query
     * @return the amount of warp the player has
     */
    int get(@Nonnull EnumWarpType type);
    
    /**
     * @param type The type of warp to set
     * @param amount how much to set it to
     */
    void set(@Nonnull EnumWarpType type, int amount);

    /**
     * @param type The type of warp to add
     * @param amount how much to add
     * @return the new total
     */
    int add(@Nonnull EnumWarpType type, int amount);
    
    
    /**
     * @param type The type of warp to reduce
     * @param amount how much to reduce
     * @return the new total
     */
    int reduce(@Nonnull EnumWarpType type, int amount);
    
    public enum EnumWarpType {
    	PERMANENT, NORMAL, TEMPORARY;
    }
    
	
	/**
     * @param player the player to sync
     */
	void sync(ServerPlayer player);
	
	/**
     * @return the counter that is used to keep track of warp gains
     */
    int getCounter();
    
    /**
     * @param amount how much to set the counter it to
     */
    void setCounter(int amount);
	
}
