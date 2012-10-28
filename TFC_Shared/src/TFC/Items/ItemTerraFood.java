package TFC.Items;

import java.util.List;

import TFC.Core.TFC_ItemHeat;
import TFC.Core.TFC_Settings;
import TFC.Core.Player.TFC_PlayerServer;
import TFC.Enums.EnumSize;
import TFC.Enums.EnumWeight;

import net.minecraft.src.Enchantment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.World;

public class ItemTerraFood extends ItemFood implements ISize
{
	String texture;
	public int foodID;

	public ItemTerraFood(int id, int healAmt) 
	{
		super(id, healAmt, true);
	}
	public ItemTerraFood(int id, int healAmt, String tex) 
	{
		this(id, healAmt);
		texture = tex;
	}
	
	public ItemTerraFood(int id, int healAmt, float saturation, boolean wolfFood, String tex, int foodid)
    {
	    super(id, healAmt, saturation, wolfFood);
	    texture = tex;
	    foodID = foodid;
    }
	
	public ItemTerraFood setFoodID(int id)
	{
		foodID = id;
		return this;
	}

	@Override
	public String getTextureFile()
	{
		return texture;
	}

	public ItemTerraFood setTexturePath(String t)
	{
		texture = t;
		return this;
	}
	
	public void addInformation(ItemStack is, List arraylist) 
	{
	    if (is.hasTagCompound())
        {
            NBTTagCompound stackTagCompound = is.getTagCompound();

            if(stackTagCompound.hasKey("temperature"))
            {
                float temp = stackTagCompound.getFloat("temperature");
                float meltTemp = 0;
                
                meltTemp = TFC_ItemHeat.getMeltingPoint(is);

                if(meltTemp != -1)
                {
                    if(is.getItem() instanceof ItemFood)
                        arraylist.add(TFC_ItemHeat.getHeatColorFood(temp, meltTemp));
                }
            }
        }
	}
	
	@Override
    public void onUpdate(ItemStack is, World world, Entity entity, int i, boolean isSelected) 
    {
        if (!world.isRemote && is.hasTagCompound())
        {
            NBTTagCompound stackTagCompound = is.getTagCompound();

            if(stackTagCompound.hasKey("temperature"))
            {
            	TFC_ItemHeat.HandleItemHeat(is, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
            }
        }
    }
	
	@Override
	public ItemStack onFoodEaten(ItemStack is, World world, EntityPlayer player)
    {
        --is.stackSize;
        TFC_PlayerServer playerServer = (TFC_PlayerServer) ((EntityPlayerMP)player).getServerPlayerBase("TFC Player Server");
        playerServer.getFoodStatsTFC().addStats(this);
        player.getFoodStats().addStats(this);
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        this.func_77849_c(is, world, player);
        return is;
    }
	
	public boolean getShareTag()
    {
        return true;
    }
	@Override
	public EnumSize getSize() {
		// TODO Auto-generated method stub
		return EnumSize.VERYSMALL;
	}
	@Override
	public EnumWeight getWeight() {
		// TODO Auto-generated method stub
		return EnumWeight.LIGHT;
	}
	@Override
	public boolean canStack() {
		// TODO Auto-generated method stub
		return true;
	}
}
