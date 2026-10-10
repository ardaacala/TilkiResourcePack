$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.Drawing
$root=Split-Path $PSScriptRoot -Parent
$path=Join-Path $root 'dist/TilkiCraft-ResourcePack.zip'
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
  if($definition.providers.Count -ne 4) { throw 'Missing merged font reference' }
 }
 $default=Read-Json $zip.GetEntry('assets/minecraft/font/default.json')
 foreach($reference in $default.providers) {
  $parts=$reference.id.Split(':')
  $font=Read-Json $zip.GetEntry("assets/$($parts[0])/font/$($parts[1]).json")
  foreach($provider in $font.providers) {
   if($provider.type -eq 'space') { $characters=$provider.advances.PSObject.Properties.Name }
   elseif($provider.type -eq 'bitmap') {
    $characters=foreach($row in $provider.chars) { foreach($char in $row.ToCharArray()) { [string]$char } }
    $texture=$provider.file.Split(':');$entry=$zip.GetEntry("assets/$($texture[0])/textures/$($texture[1])")
    if(!$entry) { throw "Missing bitmap $($provider.file)" }
    $stream=$entry.Open();$bitmap=[Drawing.Bitmap]::new($stream)
    try { if($bitmap.Width -gt 256 -or $bitmap.Height -gt 256) { throw 'Bitmap exceeds client atlas bounds' } } finally { $bitmap.Dispose();$stream.Dispose() }
   } else { continue }
   foreach($character in $characters) { if($seen.ContainsKey($character)) { throw "Overlapping glyph: $character" };$seen[$character]=$true }
  }
 }
 foreach($width in @(128,144,160,192)) {
  $suffix=if($width -eq 128) { '' } else { "_$width" }
  $stream=$zip.GetEntry("assets/tilkiinterface/textures/font/sidebar_frame$suffix.png").Open();$bitmap=[Drawing.Bitmap]::new($stream)
  try { if($bitmap.Width -ne $width -or $bitmap.Height -ne 126 -or $bitmap.GetPixel(20,20).A -ne 0) { throw 'HUD frame dimensions/transparency invalid' } } finally { $bitmap.Dispose();$stream.Dispose() }
 }
 if(!$zip.GetEntry('assets/tilkiinterface/textures/font/tab_9_20.png') -or !$zip.GetEntry('assets/tilkiinterface/textures/font/mascot.png')) { throw 'Missing TAB frame or fox mascot' }
} finally { $zip.Dispose() }
$hash=(Get-FileHash $path -Algorithm SHA1).Hash.ToLowerInvariant()
if(!([IO.File]::ReadAllText((Join-Path $root 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Combined config SHA1 mismatch' }
Write-Output 'VALID: four merged fonts, no overlapping glyphs, bitmap bounds, compact transparent HUD frames, TAB borders, mascot, ZIP and SHA1.'
