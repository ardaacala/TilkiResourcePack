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
  // AuraCosmetics: only its own namespace. Its standalone pack also carries an old copy
  // of the interface assets, which must not override the current TilkiInterface files.
  Path aura=Path.of("../AuraCosmetics/resourcepack/assets/auracosmetics");
  try(var walk=Files.walk(aura)) { for(Path file:walk.filter(Files::isRegularFile).toList()) {
   String name="assets/auracosmetics/"+aura.relativize(file).toString().replace('\\','/');
   if(files.putIfAbsent(name,Files.readAllBytes(file))!=null)throw new IllegalStateException("Conflicting asset: "+name);
  }}
  JsonObject auraReference=new JsonObject();auraReference.addProperty("type","reference");auraReference.addProperty("id","auracosmetics:effects");
  for(String font:List.of("assets/minecraft/font/default.json","assets/minecraft/font/uniform.json"))fonts.computeIfAbsent(font,k->new JsonArray()).add(auraReference);
  for(var font:fonts.entrySet()) {
   if(font.getValue().size()!=5)throw new IllegalStateException("Expected five module font references");
   JsonObject object=new JsonObject();object.add("providers",font.getValue());
   files.put(font.getKey(),object.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
  // Keep the four current modules intact; the font experiment contributes only
  // the vanilla ASCII atlas override, never its old HUD, tag or TTF assets.
  Path ascii=Path.of("overlay/assets/minecraft/textures/font/ascii.png");
  String asciiPath="assets/minecraft/textures/font/ascii.png";
  if(files.containsKey(asciiPath))throw new IllegalStateException("Module already supplies "+asciiPath);
  files.put(asciiPath,Files.readAllBytes(ascii));
  // TilkiInterface ships a 26.2+ overlay for the icon-row pause menu; reuse its
  // overlay declaration so 1.21.4 and 26.x clients both pick the right ESC frame.
  JsonObject meta=JsonParser.parseString(Files.readString(Path.of("../TilkiInterface/resourcepack/pack.mcmeta"))).getAsJsonObject();
  meta.getAsJsonObject("pack").addProperty("description","TilkiCraft - Weapons, Login, HUD, Tags | 1.21.4 - 26.x");
  files.put("pack.mcmeta",meta.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
  Files.createDirectories(Path.of("dist"));
  Path zip=Path.of("dist/TilkiResourcePack-ResourcePack.zip");
  try(var output=new ZipOutputStream(Files.newOutputStream(zip))) {
   for(var file:files.entrySet()) {
    var entry=new ZipEntry(file.getKey());entry.setTime(0);
    output.putNextEntry(entry);output.write(file.getValue());output.closeEntry();
   }
  }
  String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(zip)));
  Files.writeString(Path.of("dist/resourcepack-sha1.txt"),hash+"\n");
  System.out.println(files.size()+" assets; "+Files.size(zip)+" bytes; SHA1 "+hash);
 }
}
