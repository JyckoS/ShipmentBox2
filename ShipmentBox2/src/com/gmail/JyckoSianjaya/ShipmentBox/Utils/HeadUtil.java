package com.gmail.JyckoSianjaya.ShipmentBox.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
 
import java.lang.reflect.Field;
import java.util.UUID;
 
class CustomSkull {
 
    // Package private, so I won't use this class
    ItemStack getSkull(final String texture) throws Exception {
        final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        final ItemMeta meta = item.getItemMeta();
        final Object skin = this.createGameProfile(texture, UUID.randomUUID());
        this.setField(meta, "profile", skin);
        item.setItemMeta(meta);
        return item;
    }
 
    // Package private, so I won't use this class
    static ItemStack setTexture(ItemStack item, String texture) throws Exception {
        final ItemStack temp = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        final ItemMeta meta = temp.getItemMeta();
        meta.setLore(item.getItemMeta().getLore());
        final Object skin = createGameProfile(texture, UUID.randomUUID());
        setField(meta, "profile", skin);
        temp.setItemMeta(meta);
        return temp;
    }
 
    private static Field getField(final Class<?> c, final String field) throws Exception {
        return c.getDeclaredField(field);
    }
 
    private static GameProfile createGameProfile(final String texture, final UUID id) {
        final GameProfile profile = new GameProfile(id, (String)null);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }
 
    private static String getTextureValue(final String url) {
        return new String(Base64.encodeBase64(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes()), Charsets.UTF_8);
    }
 
    private static void setField(final Object obj, final String field, final Object value) throws Exception {
        final Field f = getField(obj.getClass(), field);
        f.setAccessible(true);
        f.set(obj, value);
    }
}
