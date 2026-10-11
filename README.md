# TilkiResourcePack

Spigot 1.21.4 / Java 21 için CombatGuns, TilkiLoginScreen, TilkiInterface ve TilkiTags varlıklarını tek kaynak paketinde birleştirir. Oyuncu giriş paketi tamamlandıktan sonra ortak paket gönderilir.

## Kurulum

Sunucudaki eski TilkiResourcePack ve TilkiInterface JAR dosyalarını kaldırıp [v1.0.12](https://github.com/ardaacala/TilkiResourcePack/releases/tag/v1.0.12) ve [TilkiInterface v1.4.7](https://github.com/ardaacala/TilkiInterface/releases/tag/v1.4.7) JAR dosyalarını `plugins` klasörüne koyun. TilkiTags için v1.3.3 kullanın. CombatGuns ve TilkiLoginScreen kurulu kalır. Ayar klasörlerini koruyun. Resmî eski paket adresleri ve SHA1 değerleri açılışta güncellenir; özel adresler korunur.

Birleşik ZIP yaklaşık 0,6 MB'dir. Normal yazıların ASCII karakterleri için deneme paketindeki `assets/minecraft/textures/font/ascii.png` kullanılır. Türkçe karakterler Minecraft 1.21.4'ün Latin Avrupa/Unifont fontlarından gelir. TTF font profilleri ve `/tilkipack font` komutu kaldırılmıştır. Giriş logosu, silahlar, rozetler, yüksek kaliteli ve kompakt ESC çerçevesi ile yazılardan uzak duran TAB logosu kalır. HUD çerçevesizdir ve üstünde küçük tilki başı bulunur. Seçenek ekranlarının kaydırıcı, onay kutusu ve metin alanları ahşap/turuncu temayı kullanır. Varsayılan ikinci paket gecikmesi 10 tick'tir.

HUD varsayılan Minecraft zeminini kullanır. TAB ekranı oyuncu yazılarının üstünde yalnızca TilkiCraft logosunu kullanır. `sidebar.enabled: false` ve `tab.enabled: false` ile paneller kapatılabilir.

v1.0.12, büyütülmüş `tilki` etiketini ve `/tilkitags messagecolor <tag> ORANGE` özel Tilki turuncusunu içerir. Diğer rütbe görselleri korunur.

`/tilkipack reload` ayarları yeniden okur ve paketi tekrar gönderir. `/tilkiui reload` ve `/tilkitags reload` ortak paket etkinken ek indirme başlatmaz.

## Geliştirme

Java 21 ile `mvn test package` çalıştırın. `tools/BuildCombinedPack.java` dört projenin `resourcepack` klasörünü birleştirir ve yalnızca [ASCII atlasını](overlay/assets/minecraft/textures/font/ascii.png) ekler. `tools/ValidatePack.ps1` ZIP içeriğini, font referanslarını, tüm yazdırılabilir ASCII karakterlerini, atlas sınırlarını ve SHA1 değerini doğrular; paket 2 MB'ı aşarsa hata verir.

Canlı Minecraft istemcisinde görünüm ve gerçek indirme süresi ayrıca kontrol edilmelidir.
