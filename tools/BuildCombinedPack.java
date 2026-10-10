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
  for(var font:fonts.entrySet()) { if(font.getValue().size()!=4)throw new IllegalStateException("Expected all four font references");JsonObject object=new JsonObject();object.add("providers",font.getValue());files.put(font.getKey(),object.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
  files.put("pack.mcmeta","{\"pack\":{\"pack_format\":46,\"description\":\"TilkiCraft - Weapons, Login, HUD, Tags | 1.21.4\"}}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
  Files.createDirectories(Path.of("dist"));Path zip=Path.of("dist/TilkiCraft-ResourcePack.zip");
  try(var output=new ZipOutputStream(Files.newOutputStream(zip))) { for(var file:files.entrySet()) {var entry=new ZipEntry(file.getKey());entry.setTime(0);output.putNextEntry(entry);output.write(file.getValue());output.closeEntry();} }
  String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(zip)));Files.writeString(Path.of("dist/resourcepack-sha1.txt"),hash+"\n");System.out.println(files.size()+" assets; SHA1 "+hash);
 }
}
