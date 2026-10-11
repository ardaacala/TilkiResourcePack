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
    private Object authApi;
    private java.lang.reflect.Method authenticated,unrestricted;
    @Override public void onEnable() {
        saveDefaultConfig();
        try { settings(); } catch(IllegalArgumentException ex) { getLogger().severe(ex.getMessage()); Bukkit.getPluginManager().disablePlugin(this);return; }
        Bukkit.getPluginManager().registerEvents(this,this);
        bindAuth();
        Bukkit.getScheduler().runTaskTimer(this,()->{for(Player player:Bukkit.getOnlinePlayers())request(player);},20,20);
        Objects.requireNonNull(getCommand("tilkipack")).setExecutor(this);
        for(Player player:Bukkit.getOnlinePlayers()) request(player);
    }
    private void settings() {
        if(!getConfig().getBoolean("delivery.two-stage-v1",false)) {
            getConfig().set("delivery.wait-for-authentication",true);
            getConfig().set("delivery.two-stage-v1",true);saveConfig();
        }
        if(!getConfig().getBoolean("delivery.non-blocking-v1",false)) {
            getConfig().set("required",false);
            getConfig().set("delivery.non-blocking-v1",true);
            saveConfig();
        }
        if(!getConfig().getBoolean("delivery.fast-second-pack-v1",false)) {
            if(getConfig().getLong("delay-ticks",40)==40)getConfig().set("delay-ticks",10);
            getConfig().set("delivery.fast-second-pack-v1",true);
            saveConfig();
        }
        String candidate=getConfig().getString("url","");
        String digestText=getConfig().getString("sha1","");
        if(candidate.startsWith("https://github.com/ardaacala/TilkiResourcePack/releases/download/") && !candidate.contains("/v1.0.9/")) {
            candidate="https://github.com/ardaacala/TilkiResourcePack/releases/download/v1.0.9/TilkiResourcePack-ResourcePack.zip";
            digestText=getConfig().getDefaults().getString("sha1");
            getConfig().set("url",candidate);getConfig().set("sha1",digestText);
        }
        if(getConfig().contains("profiles") || getConfig().contains("font-style")) {
            getConfig().set("profiles",null);getConfig().set("font-style",null);
        }
        saveConfig();
        byte[] digest=HexFormat.of().parseHex(digestText);
        if(!candidate.startsWith("https://")||digest.length!=20) throw new IllegalArgumentException("Combined pack requires HTTPS URL and 40-character SHA1");
        url=candidate;hash=digest;prompt=getConfig().getString("prompt","TilkiCraft");
        required=getConfig().getBoolean("required",false);delay=Math.max(1,getConfig().getLong("delay-ticks",10));
    }
    private void bindAuth() {
        var plugin=Bukkit.getPluginManager().getPlugin("AuthMe");
        if(plugin==null || !plugin.isEnabled())return;
        try {
            Class<?> type=Class.forName("fr.xephi.authme.api.v3.AuthMeApi",true,plugin.getClass().getClassLoader());
            Object api=type.getMethod("getInstance").invoke(null);
            var check=type.getMethod("isAuthenticated",Player.class);
            var bypass=type.getMethod("isUnrestricted",Player.class);
            authApi=api;authenticated=check;unrestricted=bypass;
        } catch(ReflectiveOperationException ex) { getLogger().warning("AuthMe hook unavailable; pack will be sent after the configured delay."); }
    }
    private boolean loginSettled(Player player) {
        var login=Bukkit.getPluginManager().getPlugin("TilkiLoginScreen");
        if(login!=null && login.isEnabled()) try {
            if(!(boolean)login.getClass().getMethod("isInitialPackSettled",UUID.class).invoke(login,player.getUniqueId()))return false;
        } catch(ReflectiveOperationException ignored) { /* Older login plugin: retain authentication gate. */ }
        return true;
    }
    private boolean authenticated(Player player) {
        if(authApi==null || !getConfig().getBoolean("delivery.wait-for-authentication",true))return true;
        try { return (boolean)authenticated.invoke(authApi,player) || (boolean)unrestricted.invoke(authApi,player); }
        catch(ReflectiveOperationException ex) { return false; }
    }
    public boolean isLoaded(UUID playerId) { return sessions.loaded(playerId); }
    private void request(Player player) {
        UUID id=player.getUniqueId();var session=sessions.beginWhenReady(id,authenticated(player),loginSettled(player));
        if(session==null)return;
        Bukkit.getScheduler().runTaskLater(this,()->{
            Player current=Bukkit.getPlayer(id);
            if(current==null||!sessions.current(id,session))return;
            if(!authenticated(current) || !loginSettled(current)) { sessions.remove(id);return; }
            current.addResourcePack(PACK_ID,url,hash,prompt,required);
        },delay);
    }
    @EventHandler public void join(PlayerJoinEvent event) { request(event.getPlayer()); }
    @EventHandler public void quit(PlayerQuitEvent event) { sessions.remove(event.getPlayer().getUniqueId()); }
    @EventHandler public void status(PlayerResourcePackStatusEvent event) {
        if(!PACK_ID.equals(event.getID()))return;
        sessions.status(event.getPlayer().getUniqueId(),event.getStatus()==PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED);
        String status=event.getStatus().name();
        if(status.equals("FAILED_DOWNLOAD") || status.equals("FAILED_RELOAD") || status.equals("INVALID_URL") || status.equals("DECLINED")) {
            getLogger().warning("Resource pack "+status+" for "+event.getPlayer().getName()+"; URL="+url+"; SHA1="+HexFormat.of().formatHex(hash)+"; required="+required);
        }
        if(status.equals("FAILED_DOWNLOAD")) {
            UUID id=event.getPlayer().getUniqueId();var session=sessions.retry(id);
            if(session!=null)Bukkit.getScheduler().runTaskLater(this,()->{
                Player current=Bukkit.getPlayer(id);
                if(current!=null && sessions.current(id,session)) {
                    if(authenticated(current) && loginSettled(current))current.addResourcePack(PACK_ID,url,hash,prompt,required);
                    else sessions.remove(id);
                }
            },100);
        }
    }
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if(!sender.hasPermission("tilkipack.admin"))return true;
        if(args.length>0 && (args.length!=1 || !args[0].equalsIgnoreCase("reload"))) {
            sender.sendMessage("/tilkipack reload");return true;
        }
        reloadConfig();
        try { settings(); } catch(IllegalArgumentException ex) { sender.sendMessage("Paket ayarı hatalı: "+ex.getMessage());return true; }
        sessions.clear();for(Player player:Bukkit.getOnlinePlayers())request(player);
        sender.sendMessage("Birleşik paket ayarları yenilendi; oyunculara tek paket gönderildi.");return true;
    }
    @Override public void onDisable() { sessions.clear(); }
}
