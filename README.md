# TilkiResourcePack

Spigot 1.21.4 / Java 21 için CombatGuns, TilkiLoginScreen, TilkiInterface ve TilkiTags varlıklarını tek kaynak paketinde birleştirir. Oyuncu giriş paketi tamamlandıktan sonra ortak paket gönderilir.

## Kurulum

Sunucudaki eski TilkiResourcePack, TilkiInterface, TilkiTags ve AuraCosmetics JAR dosyalarını kaldırıp [v1.0.22](https://github.com/ardaacala/TilkiResourcePack/releases/tag/v1.0.22), [TilkiInterface v1.5.0](https://github.com/ardaacala/TilkiInterface/releases/tag/v1.5.0), [TilkiTags v1.3.6](https://github.com/ardaacala/TilkiTags/releases/tag/v1.3.6) ve [AuraCosmetics v1.1.0](https://github.com/ardaacala/TilkiAuraCosmetics/releases/tag/v1.1.0) JAR dosyalarını `plugins` klasörüne koyun. CombatGuns ve TilkiLoginScreen kurulu kalır. Ayar klasörlerini koruyun. Resmî eski paket adresleri ve SHA1 değerleri açılışta güncellenir; özel adresler korunur.

Birleşik ZIP yaklaşık 1,1 MB'dir. Normal yazıların ASCII karakterleri için deneme paketindeki `assets/minecraft/textures/font/ascii.png` kullanılır. Türkçe karakterler Minecraft'ın Latin Avrupa/Unifont fontlarından gelir. Paket şunları içerir: silahlar, giriş logosu, rütbe rozetleri, aura dokuları, orman temalı ESC menüsü, istatistik paneli simgeleri ve logosu, TAB çerçevesi ve ahşap/turuncu kontrol dokuları. Varsayılan ikinci paket gecikmesi 10 tick'tir.

`sidebar.enabled: false` ve `tab.enabled: false` ile paneller kapatılabilir; `tab.style: text` düz TAB başlığına döner.

v1.0.22:
- ESC menüsü, orman tasarımından 1x piksel-art olarak üretilir.
- İstatistik paneli vanilla yarı saydam zeminde kalır; satırlarda temalı simgeler, üstünde küçük bir TilkiCraft logosu vardır.
- TAB listesi, oyuncu sayısıyla büyüyen yapraklı ve fenerli bir ahşap panoya alınır.
- AuraCosmetics dokuları pakete eklenir.
- Paket 1.21.4 ile 26.x istemcilerini destekler; 26.2+ simge satırı için ayrı bir overlay vardır.

v1.0.13–v1.0.19 yalnızca ayrı `TilkiCraft-HD.zip` paket sürümleridir. Ayrıntılar TilkiInterface 1.5.0, TilkiTags 1.3.6 ve AuraCosmetics README dosyalarındadır.

`/tilkipack reload` ayarları yeniden okur ve paketi tekrar gönderir. `/tilkiui reload` ve `/tilkitags reload` ortak paket etkinken ek indirme başlatmaz.

## Geliştirme

Java 21 ile `mvn test package` çalıştırın. `tools/BuildCombinedPack.java` dört projenin `resourcepack` klasörünü birleştirir ve yalnızca [ASCII atlasını](overlay/assets/minecraft/textures/font/ascii.png) ekler. `tools/ValidatePack.ps1` ZIP içeriğini, font referanslarını, tüm yazdırılabilir ASCII karakterlerini, atlas sınırlarını ve SHA1 değerini doğrular; paket 2 MB'ı aşarsa hata verir.

Canlı Minecraft istemcisinde görünüm ve gerçek indirme süresi ayrıca kontrol edilmelidir.
