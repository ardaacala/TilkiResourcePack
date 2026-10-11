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
  if($definition.providers.Count -ne 4) { throw 'Missing merged module font reference' }
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
    $characters=foreach($row in $provider.chars) { $enumerator=[Globalization.StringInfo]::GetTextElementEnumerator($row);while($enumerator.MoveNext()){$enumerator.GetTextElement()} }
    $texture=$provider.file.Split(':');$entry=$zip.GetEntry("assets/$($texture[0])/textures/$($texture[1])")
    if(!$entry) { throw "Missing bitmap $($provider.file)" }
    $stream=$entry.Open();$bitmap=[Drawing.Bitmap]::new($stream)
    try { if($bitmap.Width -gt 256 -or $bitmap.Height -gt 256) { throw 'Bitmap exceeds client atlas bounds' } } finally { $bitmap.Dispose();$stream.Dispose() }
   } else { continue }
   foreach($character in $characters) { if($seen.ContainsKey($character)) { throw "Overlapping glyph: $character" };$seen[$character]=$true }
  }
 }
 if(!$zip.GetEntry('assets/tilkiinterface/textures/font/mascot.png') -or !$zip.GetEntry('assets/tilkiinterface/textures/font/tab_logo.png')) { throw 'HUD or TAB logo missing' }
 if(!$zip.GetEntry('assets/tilkiinterface/textures/font/esc_frame.png') -or !$zip.GetEntry('assets/tilkiinterface/textures/font/esc_frame_bottom.png')) { throw 'Responsive ESC frame missing' }
 if(!($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/tab_frame/*' })) { throw 'Dynamic TAB frame missing' }
 foreach($language in @('tr_tr','en_us','en_gb')) {
  $translation=Read-Json $zip.GetEntry("assets/minecraft/lang/$language.json")
  if($translation.'menu.game' -ne ([string][char]0xea03+[string][char]0xea0a+[string][char]0xea06)) { throw "ESC frame translation missing: $language" }
 }
 if($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/hd/*' -or $_ -like 'assets/tilkiinterface/textures/font/sidebar_frame*' -or $_ -match 'assets/tilkiinterface/textures/font/tab_[0-9]' }) { throw 'Frame texture remains' }
 if($zip.Entries.FullName | Where-Object { $_ -like '*.ttf' -or $_ -like 'assets/tilkiinterface/font/text_*' }) { throw 'Legacy font files remain' }
} finally { $zip.Dispose() }
$size=(Get-Item $path).Length
if($size -gt 2MB) { throw "Combined pack grew beyond 2 MB: $size bytes" }
$hash=(Get-FileHash $path -Algorithm SHA1).Hash.ToLowerInvariant()
if(!([IO.File]::ReadAllText((Join-Path $root 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Combined config SHA1 mismatch' }
Write-Output "VALID: complete ASCII atlas, dynamic TAB and responsive ESC frames, themed controls, no TTF, ZIP $size bytes and SHA1."
