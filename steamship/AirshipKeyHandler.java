package assets.pchan3.steamship;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import assets.pchan3.PChan3Mods;
import assets.pchan3.PacketHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class AirshipKeyHandler extends KeyHandler {
	
    public static final String chestKeyDesc = "OpenAirshipChest";
    public static final String upKeyDesc = "AirshipUp";
    public static final String downKeyDesc = "AirshipDown";
    public static final String fireKeyDesc = "AirshipFire";
    public Minecraft client = Minecraft.getMinecraft();
	
    public AirshipKeyHandler(int CHEST_KEY, int UP_KEY, int DOWN_KEY, int FIRE_KEY) 
    {                
    	super(new KeyBinding[]{new KeyBinding(chestKeyDesc,CHEST_KEY),new KeyBinding(upKeyDesc,UP_KEY),new KeyBinding(downKeyDesc,DOWN_KEY),new KeyBinding(fireKeyDesc,FIRE_KEY)}, new boolean[]{false,false,false,false});       
    }
    
    @Override
    public void keyDown(EnumSet<TickType> es, KeyBinding kb, boolean endTick, boolean repeat) {
    	
		if (client != null && client.thePlayer != null && endTick)
		{
	    	Entity ent=client.thePlayer.ridingEntity;
	    	if (ent!=null && ent instanceof EntityAirship){
	    		
				if (kb.keyDescription.equals(chestKeyDesc) && client.currentScreen == null)
				{
					((EntityPlayer) ent.riddenByEntity).openGui(PChan3Mods.instance, PChan3Mods.instance.GUI_ID, ent.worldObj, ent.serverPosX, ent.serverPosY, ent.serverPosZ);
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(0));
				}
				else if (kb.keyDescription.equals(upKeyDesc) && ((EntityAirship) ent).getFuelTime()!=0)
				{	
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(1));
				}
				else if (kb.keyDescription.equals(downKeyDesc))
				{ 
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(2));
				}
				else if (kb.keyDescription.equals(fireKeyDesc) && ((EntityAirship) ent).getFireCountDown()==0)
				{			
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(3));
				}	
			}	
		}
    }
    @Override
    public void keyUp(EnumSet<TickType> es, KeyBinding kb, boolean tickEnd) 
    {
    	if (client != null && client.thePlayer != null && tickEnd)
		{
	    	Entity ent=client.thePlayer.ridingEntity;
	    	if (ent!=null && ent instanceof EntityAirship){
	    		if (kb.keyDescription.equals(upKeyDesc))
				{	
	    			client.getNetHandler().addToSendQueue(PacketHandler.getPacket(4));
				}
				if (kb.keyDescription.equals(downKeyDesc))
				{ 
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(5));
				}
				if (kb.keyDescription.equals(fireKeyDesc))
				{			
					client.getNetHandler().addToSendQueue(PacketHandler.getPacket(6));
				}	
			}	
		}
    }

    @Override
    public EnumSet<TickType> ticks() 
    {
    	return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() 
    {
    	return "Airship KeyHandler";
    }
    
}
