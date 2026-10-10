package com.tilkicraft.pack;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TilkiResourcePack extends JavaPlugin implements Listener,CommandExecutor {
    public static final UUID PACK_ID=UUID.fromString("2c17449c-7006-4595-ae41-bef76e22ea31");
    private final PackSessions sessions=new PackSessions();
    private String url,prompt;
    private byte[] hash;
    private boolean required;
    private long delay;
    @Override public void onEnable() {
        saveDefaultConfig();
        try { settings(); } catch(IllegalArgumentException ex) { getLogger().severe(ex.getMessage()); Bukkit.getPluginManager().disablePlugin(this);return; }
        Bukkit.getPluginManager().registerEvents(this,this);
        Objects.requireNonNull(getCommand("tilkipack")).setExecutor(this);
        for(Player player:Bukkit.getOnlinePlayers()) request(player);
    }
    private void settings() {
        if(getConfig().getString("url","").equals("https://github.com/ardaacala/TilkiResourcePack/releases/download/v1.0.0/TilkiCraft-ResourcePack.zip")) {
            getConfig().set("url","https://github.com/ardaacala/TilkiResourcePack/releases/download/v1.0.1/TilkiCraft-ResourcePack.zip");
            getConfig().set("sha1","46e69aec4adaa31c756f679a78b90016681c7ce2");saveConfig();
        }
        String candidate=getConfig().getString("url","");
        byte[] digest=HexFormat.of().parseHex(getConfig().getString("sha1",""));
        if(!candidate.startsWith("https://")||digest.length!=20) throw new IllegalArgumentException("Combined pack requires HTTPS URL and 40-character SHA1");
        url=candidate;hash=digest;prompt=getConfig().getString("prompt","TilkiCraft");
        required=getConfig().getBoolean("required",true);delay=Math.max(1,getConfig().getLong("delay-ticks",10));
    }
    public boolean isLoaded(UUID playerId) { return sessions.loaded(playerId); }
    private void request(Player player) {
        UUID id=player.getUniqueId();var session=sessions.begin(id);
        if(session==null)return;
        Bukkit.getScheduler().runTaskLater(this,()->{
            Player current=Bukkit.getPlayer(id);
            if(current==null||!sessions.current(id,session))return;
            current.addResourcePack(PACK_ID,url,hash,prompt,required);
        },delay);
    }
    @EventHandler public void join(PlayerJoinEvent event) { request(event.getPlayer()); }
    @EventHandler public void quit(PlayerQuitEvent event) { sessions.remove(event.getPlayer().getUniqueId()); }
    @EventHandler public void status(PlayerResourcePackStatusEvent event) {
        if(!PACK_ID.equals(event.getID()))return;
        sessions.status(event.getPlayer().getUniqueId(),event.getStatus()==PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED);
    }
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if(!sender.hasPermission("tilkipack.admin"))return true;
        reloadConfig();
        try { settings(); } catch(IllegalArgumentException ex) { sender.sendMessage("Paket ayarı hatalı: "+ex.getMessage());return true; }
        sessions.clear();for(Player player:Bukkit.getOnlinePlayers())request(player);
        sender.sendMessage("Birleşik paket ayarları yenilendi; oyunculara tek paket gönderildi.");return true;
    }
    @Override public void onDisable() { sessions.clear(); }
}
