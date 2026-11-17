package me.kryniowesegryderiusz.kgenerators.utils.objects;

import me.kryniowesegryderiusz.kgenerators.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import com.cryptomorin.xseries.XMaterial;

public class CustomBlockData {

	@Getter XMaterial xMaterial;
	/** What gets placed: can differ from xMaterial.get(), e.g. CARROTS for CARROT. */
	private final Material blockMaterial;
	
	public CustomBlockData(XMaterial xMaterial){
		this.xMaterial = xMaterial;
		Material block = ItemUtils.getBlockMaterial(xMaterial);
		this.blockMaterial = block != null ? block : xMaterial.get();
	}
	
	public void setBlock(Location location) {
		BlockUtils.setBlock(location, blockMaterial);
	}
	
	public void setBlock(Location location, boolean physics) {
		BlockUtils.setBlock(location, blockMaterial, physics);
	}
	
	public ItemStack getItem() {
		return ItemUtils.getItemStackFromMaterial(xMaterial);
	}
	
	public String toString() {
		return "Material: " + this.xMaterial.name();
	}
	
	/**
	 * Checks xMaterial
	 * @param item
	 * @return true if xMaterials are same
	 */
	public boolean isSimilar(ItemStack is) {
		return XMaterial.matchXMaterial(is) == xMaterial;
	}
	
	public static CustomBlockData load(String configString, String place) {
		if (configString.contains(":")) {
			String[] splitted = configString.split(":");
			if (splitted[0].contains("customhead")) {
				return new HeadCustomBlockData(XMaterial.PLAYER_HEAD, splitted[1]);
			}
		}
		
		return new CustomBlockData(ItemUtils.getXMaterial(configString, place+" CustomBlockData:", true));
		
	}
}
