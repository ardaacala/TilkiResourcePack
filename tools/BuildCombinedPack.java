import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.security.*;
class BuildCombinedPack {
 public static void main(String[] args) throws Exception {
  Map<String,byte[]> files=new TreeMap<>(); Map<String,JsonArray> fonts=new HashMap<>();
  for(String project:List.of("CombatGuns","TilkiLoginScreen","TilkiInterface","TilkiTags")) {
   Path root=Path.of("..",project,"resourcepack");
   try(var walk=Files.walk(root)) { for(Path file:walk.filter(Files::isRegularFile).toList()) {
    String name=root.relativize(file).toString().replace('\\','/'); byte[] bytes=Files.readAllBytes(file);
    if(name.equals("pack.mcmeta"))continue;
    if(name.matches("assets/minecraft/font/(default|uniform)\\.json")) {
     JsonArray target=fonts.computeIfAbsent(name,k->new JsonArray());
     for(JsonElement provider:JsonParser.parseString(new String(bytes,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("providers")) if(!target.contains(provider))target.add(provider);
    } else { byte[] old=files.putIfAbsent(name,bytes);if(old!=null&&!Arrays.equals(old,bytes))throw new IllegalStateException("Conflicting asset: "+name); }
   }}
  }
  for(var font:fonts.entrySet()) if(font.getValue().size()!=5)throw new IllegalStateException("Expected four modules and one text font reference");
  files.put("pack.mcmeta","{\"pack\":{\"pack_format\":46,\"description\":\"TilkiCraft - Weapons, Login, HUD, Tags | 1.21.4\"}}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
  Files.createDirectories(Path.of("dist"));JsonObject profiles=new JsonObject();
  for(String style:List.of("modern","classic","fantasy")) {
   for(var font:fonts.entrySet()) {
    JsonArray providers=new JsonArray();for(JsonElement original:font.getValue()) {JsonObject p=original.deepCopy().getAsJsonObject();if(p.has("id")&&p.get("id").getAsString().startsWith("tilkiinterface:text_"))p.addProperty("id","tilkiinterface:text_"+style);providers.add(p);}
    JsonObject object=new JsonObject();object.add("providers",providers);files.put(font.getKey(),object.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
   }
   String name="TilkiCraft-ResourcePack"+(style.equals("modern")?"":"-"+style)+".zip";Path zip=Path.of("dist",name);
   try(var output=new ZipOutputStream(Files.newOutputStream(zip))) { for(var file:files.entrySet()) {var entry=new ZipEntry(file.getKey());entry.setTime(0);output.putNextEntry(entry);output.write(file.getValue());output.closeEntry();} }
   String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(zip)));
   JsonObject profile=new JsonObject();profile.addProperty("url","https://github.com/ardaacala/TilkiResourcePack/releases/download/v1.0.2/"+name);profile.addProperty("sha1",hash);profiles.add(style,profile);
   if(style.equals("modern"))Files.writeString(Path.of("dist/resourcepack-sha1.txt"),hash+"\n");System.out.println(style+": "+files.size()+" assets; SHA1 "+hash);
  }
  Files.writeString(Path.of("dist/font-profiles.json"),profiles.toString());
 }
}
