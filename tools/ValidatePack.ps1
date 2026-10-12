$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.Drawing
$root=Split-Path $PSScriptRoot -Parent
$path=Join-Path $root 'dist/TilkiResourcePack-ResourcePack.zip'
$zip=[IO.Compression.ZipFile]::OpenRead($path)
function Read-Json($entry) {
 $reader=[IO.StreamReader]::new($entry.Open())
 try { $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
}
try {
 if(($zip.Entries.FullName | Select-Object -Unique).Count -ne $zip.Entries.Count) { throw 'Duplicate ZIP entries' }
 $null=Read-Json $zip.GetEntry('pack.mcmeta')
 $seen=@{}
 foreach($font in @('default','uniform')) {
  $definition=Read-Json $zip.GetEntry("assets/minecraft/font/$font.json")
  if($definition.providers.Count -ne 5) { throw 'Missing merged module font reference (four modules and auras)' }
  if($definition.providers | Where-Object {$_.id -like 'tilkiinterface:text_*'}) { throw 'Global font override remains' }
 }
 $asciiEntry=$zip.GetEntry('assets/minecraft/textures/font/ascii.png')
 if(!$asciiEntry) { throw 'Missing Minecraft ASCII atlas' }
 $sourceHash=(Get-FileHash (Join-Path $root 'overlay/assets/minecraft/textures/font/ascii.png') -Algorithm SHA256).Hash
 $asciiStream=$asciiEntry.Open()
 try {
  $bytes=[IO.MemoryStream]::new()
  try { $asciiStream.CopyTo($bytes); $sha=[Security.Cryptography.SHA256]::Create(); try { $atlasHash=[BitConverter]::ToString($sha.ComputeHash($bytes.ToArray())).Replace('-','') } finally { $sha.Dispose() } }
  finally { $bytes.Dispose() }
  if($atlasHash -ne $sourceHash) { throw 'ASCII atlas differs from the selected source' }
 } finally { $asciiStream.Dispose() }
 $asciiStream=$asciiEntry.Open();$ascii=[Drawing.Bitmap]::new($asciiStream)
 try {
  if($ascii.Width -ne 256 -or $ascii.Height -ne 256) { throw 'ASCII atlas must be 16 by 16 cells of 16 pixels' }
  # The 1.21.4 vanilla mapping uses the code point as the cell index.
  # Cover player names, chat, menu labels, digits and punctuation.
  for($code=33;$code -le 126;$code++) {
   $x=($code%16)*16;$y=[int][Math]::Floor($code/16)*16;$visible=$false
   for($yy=$y;$yy -lt $y+16 -and !$visible;$yy++) {
    for($xx=$x;$xx -lt $x+16;$xx++) { if($ascii.GetPixel($xx,$yy).A -gt 0) { $visible=$true;break } }
   }
   if(!$visible) { throw "Empty printable ASCII glyph: $code" }
  }
 } finally { $ascii.Dispose();$asciiStream.Dispose() }
 $default=Read-Json $zip.GetEntry('assets/minecraft/font/default.json')
 foreach($reference in $default.providers) {
  $parts=$reference.id.Split(':')
  $font=Read-Json $zip.GetEntry("assets/$($parts[0])/font/$($parts[1]).json")
  foreach($provider in $font.providers) {
   if($provider.type -eq 'ttf') { throw 'Global text font remains' }
   if($provider.type -eq 'space') { $characters=$provider.advances.PSObject.Properties.Name }
   elseif($provider.type -eq 'bitmap') {
    # Grid providers pad unused cells with U+0000, which the client skips.
    $characters=foreach($row in $provider.chars) { $enumerator=[Globalization.StringInfo]::GetTextElementEnumerator($row);while($enumerator.MoveNext()){ if($enumerator.GetTextElement() -ne [string][char]0){$enumerator.GetTextElement()} } }
    $texture=$provider.file.Split(':');$entry=$zip.GetEntry("assets/$($texture[0])/textures/$($texture[1])")
    if(!$entry) { throw "Missing bitmap $($provider.file)" }
    $stream=$entry.Open();$bitmap=[Drawing.Bitmap]::new($stream)
    # Each glyph cell must fit the client's 256 px font page.
    $rows=@($provider.chars);$columns=[Globalization.StringInfo]::new($rows[0]).LengthInTextElements
    try { if($bitmap.Width/$columns -gt 256 -or $bitmap.Height/$rows.Count -gt 256) { throw 'Glyph cell exceeds client font page' } } finally { $bitmap.Dispose();$stream.Dispose() }
   } else { continue }
   foreach($character in $characters) { if($seen.ContainsKey($character)) { throw "Overlapping glyph: $character" };$seen[$character]=$true }
  }
 }
 foreach($tile in @('esc_frame_0','sidebar_13_0','tab_header_1_1_0','tab_post_left_20_0','ascii_title')) { if(!$zip.GetEntry("assets/tilkiinterface/textures/font/v3/$tile.png")) { throw "UI v3 glyph missing: $tile" } }
 if(!$zip.GetEntry('assets/auracosmetics/font/effects.json') -or !$zip.GetEntry('assets/auracosmetics/textures/font/fox.png')) { throw 'Aura effect glyphs missing' }
 $meta=Read-Json $zip.GetEntry('pack.mcmeta')
 # UI v3 needs no overlay: its ESC frame leaves only the first button open in both pause layouts.
 if($meta.pack.min_format -ne 46 -or $meta.pack.max_format -ne 999 -or $meta.overlays -or ($zip.Entries.FullName | Where-Object { $_ -like 'tilki_*/*' })) { throw 'pack.mcmeta must cover 46..999 without overlays' }
 if($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/esc_menu/*' -or $_ -like 'assets/tilkiinterface/textures/font/hud/*' -or $_ -like 'assets/tilkiinterface/textures/font/sidebar_logo.png' }) { throw 'Screenshot-derived v2 interface assets remain' }
 if($zip.GetEntry('assets/tilkiinterface/textures/font/esc_frame_bottom.png') -or ($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/tab_frame/*' })) { throw 'Rejected frame assets remain' }
 foreach($language in @('tr_tr','en_us','en_gb')) {
  $translation=Read-Json $zip.GetEntry("assets/minecraft/lang/$language.json")
  if($translation.'menu.game' -ne '' -or [int]$translation.'menu.returnToGame'[0] -lt 0xf000 -or [int]$translation.'menu.returnToGame'[0] -gt 0xf00f) { throw "ESC frame translation missing: $language" }
 }
 if($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/hd/*' -or $_ -match 'assets/tilkiinterface/textures/font/tab_[0-9]' }) { throw 'Frame texture remains' }
 if($zip.Entries.FullName | Where-Object { $_ -like '*.ttf' -or $_ -like 'assets/tilkiinterface/font/text_*' }) { throw 'Legacy font files remain' }
} finally { $zip.Dispose() }
$size=(Get-Item $path).Length
# UI v3 carries per-row-count TAB posts and per-column header art (about 2.2 MB on its own).
if($size -gt 4MB) { throw "Combined pack grew beyond 4 MB: $size bytes" }
$hash=(Get-FileHash $path -Algorithm SHA1).Hash.ToLowerInvariant()
if(!([IO.File]::ReadAllText((Join-Path $root 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Combined config SHA1 mismatch' }
Write-Output "VALID: complete ASCII atlas, UI v3 ESC/sidebar/TAB glyphs, no overlays, rank badges, aura glyphs, themed controls, no TTF, ZIP $size bytes and SHA1."
