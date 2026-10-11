# TilkiResourcePack

Spigot 1.21.4 / Java 21 için CombatGuns, TilkiLoginScreen, TilkiInterface ve TilkiTags varlıklarını tek kaynak paketinde birleştirir. Oyuncu giriş paketi tamamlandıktan sonra ortak paket gönderilir.

## Kurulum

Sunucudaki eski TilkiResourcePack ve TilkiInterface JAR dosyalarını kaldırıp [v1.0.9](https://github.com/ardaacala/TilkiResourcePack/releases/tag/v1.0.9) ve [TilkiInterface v1.4.4](https://github.com/ardaacala/TilkiInterface/releases/tag/v1.4.4) JAR dosyalarını `plugins` klasörüne koyun. CombatGuns, TilkiLoginScreen ve TilkiTags kurulu kalır. Ayar klasörlerini koruyun. Resmî eski paket adresleri ve SHA1 değerleri açılışta güncellenir; özel adresler korunur.

Birleşik ZIP yaklaşık 595 KB'dir. Normal yazıların ASCII karakterleri için deneme paketindeki `assets/minecraft/textures/font/ascii.png` kullanılır. Türkçe karakterler Minecraft 1.21.4'ün Latin Avrupa/Unifont fontlarından gelir. TTF font profilleri ve `/tilkipack font` komutu kaldırılmıştır. Giriş logosu, silahlar, rozetler ve TilkiCraft logolu ESC çerçevesi kalır. HUD ile TAB çerçeveleri kaldırıldı; HUD üstünde küçük tilki başı, TAB yazılarının üstünde ayrı TilkiCraft logosu bulunur. Eski `font-style` ve `profiles` ayarları otomatik temizlenir. Varsayılan ikinci paket gecikmesi 10 tick'tir.

HUD ve TAB varsayılan Minecraft zeminini kullanır. İki logo yazılardan ayrı yerleştirilir. `sidebar.enabled: false` ve `tab.enabled: false` ile paneller kapatılabilir.

`/tilkipack reload` ayarları yeniden okur ve paketi tekrar gönderir. `/tilkiui reload` ve `/tilkitags reload` ortak paket etkinken ek indirme başlatmaz.

## Geliştirme

Java 21 ile `mvn test package` çalıştırın. `tools/BuildCombinedPack.java` dört projenin `resourcepack` klasörünü birleştirir ve yalnızca [ASCII atlasını](overlay/assets/minecraft/textures/font/ascii.png) ekler. `tools/ValidatePack.ps1` ZIP içeriğini, font referanslarını, tüm yazdırılabilir ASCII karakterlerini, atlas sınırlarını ve SHA1 değerini doğrular; paket 2 MB'ı aşarsa hata verir.

Canlı Minecraft istemcisinde görünüm ve gerçek indirme süresi ayrıca kontrol edilmelidir.
