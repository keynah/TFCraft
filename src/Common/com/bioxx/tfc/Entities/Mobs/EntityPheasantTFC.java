package com.bioxx.tfc.Entities.Mobs;

import java.util.ArrayList;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Sounds;
import com.bioxx.tfc.api.Entities.IAnimal;
import com.bioxx.tfc.api.Util.Helper;

public class EntityPheasantTFC extends EntityChickenTFC
{
	private final EntityAIEatGrass aiEatGrass = new EntityAIEatGrass(this);
	private boolean wasRoped = false;

	public EntityPheasantTFC(World par1World)
	{
		super(par1World);
	}

	public EntityPheasantTFC(World world, IAnimal mother,  ArrayList<Float> data)
	{
		super(world, mother, data);
	}

	@Override
	public void addAI()
	{

	}

	@Override
	protected boolean canDespawn()
	{
		return this.ticksExisted > 3000 && !wasRoped;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50);//MaxHealth
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		timeUntilNextEgg = 10000;
		if(this.getLeashed()&&!wasRoped)wasRoped = true;
		super.onLivingUpdate();
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected String getLivingSound ()
	{
		return isChild() ? TFC_Sounds.PHAESANTCHICKSAY : TFC_Sounds.PHAESANTSAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected String getHurtSound ()
	{
		return isChild() ? null : TFC_Sounds.PHAESANTHURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected String getDeathSound ()
	{
		return isChild() ? null : TFC_Sounds.PHAESANTDEATH;
	}

	@Override
	public void roosterCrow()
	{
		//Nulled so that pheasant dont crow since they extend chickens
	}

	/**
	 * Drop 0-2 items of this living's type
	 */
	@Override
	protected void dropFewItems(boolean par1, int par2)
	{
		float ageMod = TFC_Core.getPercentGrown(this);
		this.dropItem(Items.feather, (int)(ageMod * this.size_mod * (5 + this.rand.nextInt(10))));

		if(isAdult())
		{
			float foodWeight = ageMod * (this.size_mod * 40);//528 oz (33lbs) is the average yield of lamb after slaughter and processing
			TFC_Core.animalDropMeat(this, TFCItems.chickenRaw, foodWeight);
			this.dropItem(Items.bone, rand.nextInt(2) + 1);
		}
	}

	@Override
	public EntityPheasantTFC createChild(EntityAgeable entityageable)
	{
		ArrayList<Float> data = new ArrayList<Float>();
		data.add(mateSizeMod);
		return new EntityPheasantTFC(worldObj, this, data);
	}

	@Override
	public boolean canMateWith(IAnimal animal) 
	{
		return false;
	}

	@Override
	public EntityAgeable createChildTFC(EntityAgeable entityageable)
	{
		ArrayList<Float> data = new ArrayList<Float>();
		data.add(entityageable.getEntityData().getFloat("MateSize"));
		return new EntityPheasantTFC(worldObj, this, data);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		wasRoped = nbt.getBoolean("wasRoped");
	}
	
	@Override
	public void writeEntityToNBT (NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("wasRoped", wasRoped);
	}
	
	@Override
	public int getAnimalTypeID()
	{
		return Helper.stringToInt("pheasant");
	}
	
	@Override
	public boolean trySetName(String name, EntityPlayer player) {
		if(this.checkFamiliarity(InteractionEnum.NAME, player) && !this.hasCustomNameTag()){
			this.setCustomNameTag(name);
			return true;
		}
		this.playSound(TFC_Sounds.PHAESANTSAY,  6, (rand.nextFloat()/2F)+(isChild()?1.25F:0.75F));
		return false;
	}
	
	@Override
	public boolean checkFamiliarity(InteractionEnum interaction, EntityPlayer player) {
		boolean flag = false;
		switch(interaction){
		case NAME: flag = familiarity > 60;break;
		default: break;
		}
		if(!flag && !player.worldObj.isRemote){
			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("entity.notFamiliar")));
		}
		return flag;
	}
}
