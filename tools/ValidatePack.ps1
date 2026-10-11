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
 if(!($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/textures/font/hd/*' }) -or !$zip.GetEntry('assets/tilkiinterface/textures/font/mascot.png')) { throw 'Missing HD tiles or fox mascot' }
 if($zip.Entries.FullName | Where-Object { $_ -like 'assets/tilkiinterface/font/fonts/*' -or $_ -like 'assets/tilkiinterface/font/text_*' }) { throw 'Legacy font files remain' }
} finally { $zip.Dispose() }
$size=(Get-Item $path).Length
if($size -gt 2MB) { throw "Combined pack grew beyond 2 MB: $size bytes" }
$hash=(Get-FileHash $path -Algorithm SHA1).Hash.ToLowerInvariant()
if(!([IO.File]::ReadAllText((Join-Path $root 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Combined config SHA1 mismatch' }
Write-Output "VALID: four module fonts, no global text font, atlas-safe compact frames, ZIP $size bytes and SHA1."
