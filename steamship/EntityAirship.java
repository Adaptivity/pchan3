package mods.pchan3.steamship;

import java.util.List;
import java.util.Random;


import mods.pchan3.EntitySteamExplode;
import mods.pchan3.PChan3Mods;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityAirship extends Entity implements IInventory {
	private ItemStack cargoItems[];
    private int airshipPosRotationIncrements;
    private double airShipX,airShipY,airShipZ;
    private double airshipYaw,airshipPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;
    public boolean isGoingUp, isGoingDown,isFiring;
	
    public EntityAirship(World world) {
	super(world);
	this.preventEntitySpawning = true;
	this.setSize(1.5F, 1.7F);
	this.yOffset = this.height / 2.0F;
	this.cargoItems = new ItemStack[this.getSizeInventory()];
	}
    public EntityAirship(World world, double d, double d1, double d2) {
	this(world);
	this.setPosition(d, d1 + (double) yOffset, d2);
	this.motionX = 0.0D;
	this.motionY = 0.0D;
	this.motionZ = 0.0D;
	this.prevPosX = d;
	this.prevPosY = d1;
	this.prevPosZ = d2;
    }
    @Override
	protected void entityInit() {
    	this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Integer(0));
        this.dataWatcher.addObject(30, new Integer(0));
        this.dataWatcher.addObject(31, new Integer(0));
	}
    @Override
    public void setDead() {
    	 for (int var1 = 0; var1 < this.getSizeInventory(); ++var1)
            {
                ItemStack var2 = this.getStackInSlot(var1);

                if (var2 != null)
                {
                    float var3 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var4 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var5 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (var2.stackSize > 0)
                    {
                        int var6 = this.rand.nextInt(21) + 10;

                        if (var6 > var2.stackSize)
                        {
                            var6 = var2.stackSize;
                        }

                        var2.stackSize -= var6;
                        EntityItem var7 = new EntityItem(this.worldObj, this.posX + (double)var3, this.posY + (double)var4, this.posZ + (double)var5, new ItemStack(var2.itemID, var6, var2.getItemDamage()));

                        if (var2.hasTagCompound())
                        {
                            var7.getEntityItem().setTagCompound((NBTTagCompound)var2.getTagCompound().copy());
                        }

                        float var8 = 0.05F;
                        var7.motionX = (double)((float)this.rand.nextGaussian() * var8);
                        var7.motionY = (double)((float)this.rand.nextGaussian() * var8 + 0.2F);
                        var7.motionZ = (double)((float)this.rand.nextGaussian() * var8);
                        if (!this.worldObj.isRemote)
                        this.worldObj.spawnEntityInWorld(var7);
                    }
                }
            }
    		 PChan3Mods.instance.proxy.displayExplodeFX(this);
    
	super.setDead();
    }
    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
	return entity.boundingBox;
    }
	@Override
    public AxisAlignedBB getBoundingBox() {
	return this.boundingBox;
    }
	@Override
    public boolean canBePushed() {
	return true;
    }
	@Override
	protected void updateFallState(double par1, boolean par3){  }
	@Override
    public String getInvName() {
	return "Airship";
    }
	@Override
    public void onInventoryChanged() {}
	@Override
    public double getMountedYOffset() {
	return (double) this.height * 0.0D - 0.30000001192092896D;
    }
	@Override
    public boolean attackEntityFrom(DamageSource source, int i) {
	if (this.isEntityInvulnerable())
    {
        return false;
    }
    else if (!this.worldObj.isRemote && !this.isDead)
    {
    	this.setForwardDirection(-this.getForwardDirection());
    	this.setTimeSinceHit(1);
    	this.setDamageTaken(this.getDamageTaken()+ i * 10);
    	this.setBeenAttacked();
	if (source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode)
    {
        this.setDamageTaken(400);
    }
	if (this.getDamageTaken() > 300) {
		if (this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(this);
        }
	    this.dropItemWithOffset(PChan3Mods.instance.airShip.itemID, 1, 0.0F);
	    this.setDead();
	    PChan3Mods.instance.proxy.displayShipExplodeFX(source, this);
	}
	return true;
	}
	else return true;	    
}
	@SideOnly(Side.CLIENT)
    public void performHurtAnimation() {
	this.setForwardDirection(-this.getForwardDirection());
	this.setTimeSinceHit(1);
	this.setDamageTaken(this.getDamageTaken() * 2);
    }
	
    public void setDamageTaken(int par1)
    {
        this.dataWatcher.updateObject(19, Integer.valueOf(par1));
    }

    public int getDamageTaken()
    {
        return this.dataWatcher.getWatchableObjectInt(19);
    }

    public void setTimeSinceHit(int par1)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    public int getTimeSinceHit()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    public void setForwardDirection(int par1)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    public int getForwardDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }
	public int getFuelTime()
	{
	return this.dataWatcher.getWatchableObjectInt(30);	
	}
	public void setFuelTime(int par1)
	{
	this.dataWatcher.updateObject(30, Integer.valueOf(par1));
	}
	public int getFireCountDown()
	{
	return this.dataWatcher.getWatchableObjectInt(31);
	}
	public void setFireCountDown(int par1)
	{
		this.dataWatcher.updateObject(31, Integer.valueOf(par1));
	}
	@Override
    public boolean canBeCollidedWith() {
	return !isDead;
    }
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float f, float f1, int i) {	
		
    this.airshipPosRotationIncrements = i + 5;  	
	this.airShipX = x;
	this.airShipY = y;
	this.airShipZ = z;
	this.airshipYaw = f;
	this.airshipPitch = f1;
	this.motionX = this.velocityX;
	this.motionY = this.velocityY;
	this.motionZ = this.velocityZ;
    }
	@SideOnly(Side.CLIENT)
    public void setVelocity(double d, double d1, double d2) {
	this.velocityX = this.motionX = d;
	this.velocityY = this.motionY = d1;
	this.velocityZ = this.motionZ = d2;
    }
	@SideOnly(Side.CLIENT)
	public void displayDeadEffect()
	{   Minecraft mc= FMLClientHandler.instance().getClient();
		for (int i = 1; i < 30; i++) {
		    if (i % 2 == 0) {
		    	mc.effectRenderer.addEffect(new EntitySteamExplode(this.worldObj,
		    			this.posX + (rand.nextInt(i) / 8), this.posY, this.posZ
				- (rand.nextInt(i) / 8), 0D, 0D, 0D));
		    	mc.effectRenderer.addEffect(new EntitySteamExplode(this.worldObj,
		    			this.posX + (rand.nextInt(i) / 8), this.posY, this.posZ
				+ (rand.nextInt(i) / 8), 0D, 0D, 0D));
		    } else {
		    	mc.effectRenderer.addEffect(new EntitySteamExplode(this.worldObj,
		    			this.posX - (rand.nextInt(i) / 8), this.posY, this.posZ
				+ (rand.nextInt(i) / 8), 0D, 0D, 0D));
		    	mc.effectRenderer.addEffect(new EntitySteamExplode(this.worldObj,
		    			this.posX - (rand.nextInt(i) / 8), this.posY, this.posZ
				- (rand.nextInt(i) / 8), 0D, 0D, 0D));
		    }
		}
	}
	@SideOnly(Side.CLIENT)
    public void displayEffect(double d11)
    {	int i=5;
    	double d13 = Math.cos(((double) rotationYaw * 3.1415926535897931D) / 180D);
	    double d15 = Math.sin(((double) rotationYaw * 3.1415926535897931D) / 180D);
	    for (int i1 = 0; (double) i1 < 1.0D + d11 * 60D; i1++) {
		double d18 = rand.nextFloat() * 2.0F - 1.0F;
		double d20 = (double) (rand.nextInt(2) * 2 - 1) * 0.69999999999999996D;

		double d4 = (boundingBox.minY + ((boundingBox.maxY - boundingBox.minY) * (double) (i1 + 0))
			/ (double) i) - 0.125D;
		double d8 = (boundingBox.minY + ((boundingBox.maxY - boundingBox.minY) * (double) (i1 + 1))
			/ (double) i) - 0.125D;
		AxisAlignedBB axisalignedbb = AxisAlignedBB
			.getBoundingBox(boundingBox.minX, d4, boundingBox.minZ, boundingBox.maxX, d8,
			boundingBox.maxZ);

		if (rand.nextBoolean()) {
		    double d21 = (posX - d13 * d18 * 0.80000000000000004D)+ d15 * d20;
		    double d23 = posZ - d15 * d18 * 0.80000000000000004D - d13 * d20;

		    if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
		    	this.worldObj.spawnParticle("splash", d21, this.posY - 0.125D, d23, this.motionX, this.motionY, this.motionZ);
		    }
		} else {
		    double d22 = posX + d13 + d15 * d18 * 0.69999999999999996D;
		    double d24 = (posZ + d15) - d13 * d18 * 0.69999999999999996D;
		    if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
		    	this.worldObj.spawnParticle("splash", d22, this.posY - 0.125D, d24, this.motionX, this.motionY, this.motionZ);
		    }
		}
	    }
    }
	@SideOnly(Side.CLIENT)
    public void displaySmoke()
    {
    	double smoke = this.rand.nextFloat() * 2.0f - 1.0f;
        if (smoke > 0.65f) {
        	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamFX(this.worldObj, this.posX,
    			this.posY + 0.9D, this.posZ, 0.0D, 0.0D, 0.0D));
    }
    }
    public int getFuelScaled(int i) {
	return (this.getFuelTime() * i) / 600;
    }
    @Override
    public void onUpdate() {	
	super.onUpdate();
	
	if (this.getFuelTime() > 0) 
	    this.setFuelTime(this.getFuelTime()-1);
	if (this.getFireCountDown() > 0) 
	    this.setFireCountDown(this.getFireCountDown()-1);
	if (this.getTimeSinceHit() > 0) 
	    this.setTimeSinceHit(this.getTimeSinceHit()-1);
	if (this.getDamageTaken() > 0) 
	    this.setDamageTaken(this.getDamageTaken()-1);
	
	//if (!this.worldObj.isRemote){
	if (this.getFuelTime() == 0 && this.riddenByEntity != null) {
			if (this.getStackInSlot(0)!=null && this.getStackInSlot(0).itemID==Item.coal.itemID){
				this.setFuelTime(1600);
				if (--this.cargoItems[0].stackSize==0)
					this.setInventorySlotContents(0, (ItemStack)null);
			}
			else if (((EntityPlayer) this.riddenByEntity).inventory.hasItem(Item.coal.itemID)) {
		    	this.setFuelTime(1600);
			((EntityPlayer) this.riddenByEntity).inventory.consumeInventoryItem(Item.coal.itemID);
		    }		
	  }
	//}
	this.prevPosX = this.posX;
	this.prevPosY = this.posY;
	this.prevPosZ = this.posZ;
	int i = 5;
	double d = 0.0D;
	for (int j = 0; j < i; j++) {
	    double d5 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (double) (j + 0)) / (double) i) - 0.125D;
	    double d9 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (double) (j + 1)) / (double) i) - 0.125D;
	    AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d5, this.boundingBox.minZ, this.boundingBox.maxX, d9, this.boundingBox.maxZ);
	    if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) 
	    {
		d += 1.0D / (double) i;
	    }
	}
	double d1;
	if (this.worldObj.isRemote) {
	    if (this.airshipPosRotationIncrements > 0) {
		d1 = this.posX + (this.airShipX - this.posX)/ (double) this.airshipPosRotationIncrements;
		double d5 = this.posY + (this.airShipY - this.posY)/ (double) this.airshipPosRotationIncrements;
		double d9 = this.posZ + (this.airShipZ - this.posZ)/ (double) this.airshipPosRotationIncrements;
		double d12 = MathHelper.wrapAngleTo180_double(this.airshipYaw - (double)this.rotationYaw);
	
		this.rotationYaw += d12 / (double) this.airshipPosRotationIncrements;
		this.rotationPitch += (airshipPitch - (double) rotationPitch)
			/ (double) this.airshipPosRotationIncrements;
		--this.airshipPosRotationIncrements;
		this.setPosition(d1, d5, d9);
		this.setRotation(this.rotationYaw, this.rotationPitch);

	    } else {
		d1 = this.posX + this.motionX;
		double d6 = this.posY + this.motionY;
		double d10 = this.posZ + this.motionZ;
		this.setPosition(d1, d6, d10);

		if (this.onGround) {
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			this.posY += 3D;
		}
		this.motionX *= 0.99000000953674316D;
		this.motionY *= 0.94999998807907104D;
		this.motionZ *= 0.99000000953674316D;
	    }
	    //return;
	}
	else{
	if (this.riddenByEntity != null) {
		this.motionX += this.riddenByEntity.motionX * 0.25000000000000001D;
		this.motionZ += this.riddenByEntity.motionZ * 0.25000000000000001D;
		if ( this.isGoingUp) {
	    	//this.setVelocity(this.motionX, this.riddenByEntity.motionY * 0.04000000000000001D,this.motionZ);
			this.motionY -= this.riddenByEntity.motionY * 0.04000000000000001D;
		 }
	    else if ( this.isGoingDown) {
	    	for (int j = 0; j < i; j++) {
			    double d4 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (double) (j - 2))
				    / (double) i) - 0.125D;
			    double d8 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (double) (j - 4))
				    / (double) i) - 0.125D;
			    AxisAlignedBB axisalignedbb = AxisAlignedBB
				    .getBoundingBox(this.boundingBox.minX, d4,
				    		this.boundingBox.minZ, this.boundingBox.maxX, d8,
				    		this.boundingBox.maxZ);
			    if (!this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
			    	this.motionY += this.riddenByEntity.motionY * 0.01000000000000001D;
				//this.setVelocity(this.motionX, this.motionY,this.motionZ);
			    } else {
			    	this.posY += 5D;
			    	this.motionY = 0;
			    }
			}
	    }
	}
	if (this.getFuelTime() == 0 && !this.onGround) {
	    this.motionY -= (0.01D * 10) / 15; // Gravity :P
	}
	double d7 = 1D;
	if (this.motionX < -d7) {this.motionX = -d7;}
	if (this.motionX > d7) {this.motionX = d7;}
	if (this.motionZ < -d7) {this.motionZ = -d7;}
	if (this.motionZ > d7) {this.motionZ = d7;}
	if (this.onGround||this.getFuelTime() == 0) {
		this.motionX *= 0.5D;
		this.motionY *= 0.5D;
		this.motionZ *= 0.5D;
	}
	moveEntity(this.motionX, this.motionY, this.motionZ);

	double d11 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
	if (d11 > 0.14999999999999999D) {
		PChan3Mods.instance.proxy.displaySplashEffect(this,d11);
	}
	if (PChan3Mods.instance.SHOW_BOILER && this.getFuelTime()!=0) {
		PChan3Mods.instance.proxy.displaySmoke(this);
	}

	if (isCollidedHorizontally && d11 > 0.14999999999999999D) {
	} else {
		this.motionX *= 0.99000000953674316D;
		this.motionY *= 0.94999998807907104D;
		this.motionZ *= 0.99000000953674316D;
	}
	this.rotationPitch = 0.0F;
	double d14 = this.rotationYaw;
	double d16 = this.prevPosX - this.posX;
	double d17 = this.prevPosZ - this.posZ;
	if (d16 * d16 + d17 * d17 > 0.001D) {
	    d14 = (float) ((Math.atan2(d17, d16) * 180D) / 3.1415926535897931D);
	}
	double d19;
	for (d19 = d14 - (double) this.rotationYaw; d19 >= 180D; d19 -= 360D) {
	}
	for (; d19 < -180D; d19 += 360D) {
	}
	if (d19 > 30D) {
	    d19 = 30D;
	}
	if (d19 < -30D) {
	    d19 = -30D;
	}
	this.rotationYaw += d19;
	setRotation(this.rotationYaw, this.rotationPitch);
	if (!this.worldObj.isRemote){
	List<?> list = worldObj.getEntitiesWithinAABBExcludingEntity(this,
		this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
	if (list != null && list.size() > 0) {
	    for (int j1 = 0; j1 < list.size(); j1++) {
		Entity entity = (Entity) list.get(j1);
		if (entity != this.riddenByEntity && entity.canBePushed() && (entity instanceof EntityAirship))
		{
		    entity.applyEntityCollision(this);
		}
	    }
	}
	if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
		this.riddenByEntity = null;
	}
	}
	if (this.isFiring && this.getFireCountDown()==0) {
		    this.FireArrow((EntityPlayer) this.riddenByEntity);    
		} 
	}
    }
    public void updateRiderPosition() {
    	if (this.riddenByEntity != null){
	    double d = Math.cos((double) rotationYaw * Math.PI / 180.0D) * 0.4D;
	    double d1 = Math.sin((double) rotationYaw * Math.PI / 180.0D) * 0.4D;
	    this.riddenByEntity.setPosition(this.posX + d, this.posY + this.getMountedYOffset()+ this.riddenByEntity.getYOffset(), this.posZ + d1);
	}
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
    		NBTTagList tags = new NBTTagList();  
    		for (int id = 0; id < this.cargoItems.length; ++id) 
    		{
    			if (this.cargoItems[id] != null) 
    			{
    				NBTTagCompound var4 = new NBTTagCompound();
    				var4.setByte("Slot", (byte) id);
    				this.cargoItems[id].writeToNBT(var4);
    				tags.appendTag(var4);
    			}
    		}
    		par1NBTTagCompound.setTag("Items", tags);
    }
    @Override
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
    		NBTTagList itemTags = par1NBTTagCompound.getTagList("Items");
    		this.cargoItems = new ItemStack[this.getSizeInventory()];
    		for (int id = 0; id < itemTags.tagCount(); ++id) 
    		{
    			NBTTagCompound var4 = (NBTTagCompound) itemTags.tagAt(id);
    			int var5 = var4.getByte("Slot") & 255;
    			if (var5 >= 0 && var5 < this.cargoItems.length) {
    				this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
    			}
    		}
    }
    @Override
    public int getSizeInventory() {
	return 14;
    }
    @Override
    public ItemStack getStackInSlot(int i) {
	return this.cargoItems[i];
    }
    @Override
    public ItemStack decrStackSize(int i, int j) {
    	ItemStack stack = this.getStackInSlot(i);
    	if (stack != null) 
    	{
		if (stack.stackSize <= j) 
	    	{
	    	this.setInventorySlotContents(i, (ItemStack)null);
	    	this.onInventoryChanged();
	    	 }
	    else
	    	{
	    stack = stack.splitStack(j);
	    	if (stack.stackSize == 0) 
	    		{
	    		this.setInventorySlotContents(i, (ItemStack)null);
	    		this.onInventoryChanged();
	    		}
	    	}    
    	}   
	    return stack;
	}
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
	this.cargoItems[i] = itemstack;
	if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
	    itemstack.stackSize = this.getInventoryStackLimit();
	}this.onInventoryChanged();
    }
    @Override
    public int getInventoryStackLimit() {
	return 64;
    }
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
	return 0.0F;
    }
    @Override
    public boolean interact(EntityPlayer entityplayer) {//TODO:Work on this

	if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer
		&& this.riddenByEntity != entityplayer) {
		if (!this.worldObj.isRemote)
			entityplayer.openGui(PChan3Mods.instance, PChan3Mods.instance.GUI_ID, this.worldObj, 0, 0, 0);
	    return true;
	}
	if (!this.worldObj.isRemote) 
	{
	    ItemStack itemstack = entityplayer.inventory.getCurrentItem();
	    if (itemstack != null && itemstack.itemID == Item.coal.itemID){
	    	if (--itemstack.stackSize == 0)
            {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, (ItemStack)null);
            }
	    	if (this.getFuelTime()==0) 
	    		this.setFuelTime(1600);
	    	else if (this.getStackInSlot(0)!=null && this.getStackInSlot(0).itemID==Item.coal.itemID) 
	    		{
	    		 this.cargoItems[0].stackSize++;	
	    		 this.onInventoryChanged();
	    		}
	    	else if (this.getStackInSlot(0)==null) this.setInventorySlotContents(0, new ItemStack(Item.coal));
	    		
	    }
	    else entityplayer.mountEntity(this);	    
	}
		return true;	
	}
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
    	return this.isDead ? false : entityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    public void FireArrow(EntityPlayer entityplayer) {

	boolean playerHasArrows = entityplayer.inventory.hasItem(Item.arrow.itemID); 
	boolean shipHasArrows = this.getStackInSlot(1)!=null && this.getStackInSlot(1).itemID==Item.arrow.itemID;	
	//if (this.getStackInSlot(1)!=null)
	//Entity entity =this.getStackInSlot(1).getItem().createEntity(this.worldObj, location, this.getStackInSlot(1)); 
	//if (entity instanceof IProjectile); 
	if ((playerHasArrows|| shipHasArrows ||entityplayer.capabilities.isCreativeMode)&& this.getFireCountDown() == 0) {	    

		Vec3 vec = entityplayer.getLook(1.0F);
		double d8 = 4D;
		double d1 = this.posX + vec.xCoord * d8;
		double d2 = this.posY + (double) (height / 4.0F);
		double d3 = this.posZ + vec.zCoord * d8;
		EntityArrow arrow = new EntityArrow(this.worldObj, entityplayer, 1.0F);
		
		this.worldObj.playSoundAtEntity(entityplayer, "random.bow", 1.0F,
			1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
		arrow.setLocationAndAngles(d1,d2,d3,2.6F,6F);
		arrow.setDamage(1.0D);
		if (!this.worldObj.isRemote){
			this.worldObj.spawnEntityInWorld(arrow); 
			this.setFireCountDown(20);
			if (!entityplayer.capabilities.isCreativeMode && shipHasArrows)
				if 	(--this.cargoItems[1].stackSize==0)
				this.setInventorySlotContents(1, (ItemStack)null);
			else if (!entityplayer.capabilities.isCreativeMode && playerHasArrows)
				entityplayer.inventory.consumeInventoryItem(Item.arrow.itemID);
		}
		}
    }
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
                this.setInventorySlotContents(slot, null);
        }
        return stack;
    }
    @Override
    public void openChest() {}
    @Override
    public void closeChest() {}
	@Override
	public boolean isInvNameLocalized() {
		return true;
	}
	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return i>2 || (itemstack.getItem() instanceof ItemCoal && i==0) || (itemstack.itemID==Item.arrow.itemID && i==1 );
	}
}
