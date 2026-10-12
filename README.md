# TilkiResourcePack

Spigot 1.21.4 / Java 21 için CombatGuns, TilkiLoginScreen, TilkiInterface ve TilkiTags varlıklarını tek kaynak paketinde birleştirir. Oyuncu giriş paketi tamamlandıktan sonra ortak paket gönderilir.

## Kurulum

Sunucudaki eski TilkiResourcePack, TilkiInterface ve TilkiTags JAR dosyalarını kaldırıp [v1.0.21](https://github.com/ardaacala/TilkiResourcePack/releases/tag/v1.0.21), [TilkiInterface v1.4.9](https://github.com/ardaacala/TilkiInterface/releases/tag/v1.4.9) ve [TilkiTags v1.3.5](https://github.com/ardaacala/TilkiTags/releases/tag/v1.3.5) JAR dosyalarını `plugins` klasörüne koyun. CombatGuns ve TilkiLoginScreen kurulu kalır. Ayar klasörlerini koruyun. Resmî eski paket adresleri ve SHA1 değerleri açılışta güncellenir; özel adresler korunur.

Birleşik ZIP yaklaşık 1,4 MB'dir. Normal yazıların ASCII karakterleri için deneme paketindeki `assets/minecraft/textures/font/ascii.png` kullanılır. Türkçe karakterler Minecraft 1.21.4'ün Latin Avrupa/Unifont fontlarından gelir. TTF font profilleri ve `/tilkipack font` komutu kaldırılmıştır. Giriş logosu, silahlar, rozetler, düğmelere bağlı HD ESC panosu ve yazılardan uzak duran TAB logosu kalır. HUD çerçevesizdir ve üstünde küçük tilki başı bulunur. Seçenek ekranlarının kaydırıcı, onay kutusu ve metin alanları ahşap/turuncu temayı kullanır. Varsayılan ikinci paket gecikmesi 10 tick'tir.

HUD varsayılan Minecraft zeminini kullanır. TAB ekranı oyuncu yazılarının üstünde yalnızca TilkiCraft logosunu kullanır. `sidebar.enabled: false` ve `tab.enabled: false` ile paneller kapatılabilir.

v1.0.21: Rütbe rozetleri referans rozet sayfasından birebir kesilir ve tek sohbet satırına (9 px) sığar; Tilki rozeti aynı tasarımla 14 px yüksekliktedir ve üst satıra doğru taşarak öne çıkar. ESC menüsü tilkili logo, fenerli ahşap pano, turuncu çerçeveli düğmeler ve `/discord` tabelasıyla kaplanır. Pano “Oyuna Dön” düğmesine bağlı olduğundan her GUI ölçeğinde düğmelerle birlikte konumlanır ve 240 GUI yüksekliğinde de ekrana sığar. Paket 1.21.4 ile 26.x istemcilerini destekler; 26.2+ simge satırı için ayrı bir overlay vardır. v1.0.13–v1.0.19 yalnızca ayrı `TilkiCraft-HD.zip` paket sürümleridir. Ayrıntılar TilkiTags 1.3.5 ve TilkiInterface 1.4.9 README dosyalarındadır.

`/tilkipack reload` ayarları yeniden okur ve paketi tekrar gönderir. `/tilkiui reload` ve `/tilkitags reload` ortak paket etkinken ek indirme başlatmaz.

## Geliştirme

Java 21 ile `mvn test package` çalıştırın. `tools/BuildCombinedPack.java` dört projenin `resourcepack` klasörünü birleştirir ve yalnızca [ASCII atlasını](overlay/assets/minecraft/textures/font/ascii.png) ekler. `tools/ValidatePack.ps1` ZIP içeriğini, font referanslarını, tüm yazdırılabilir ASCII karakterlerini, atlas sınırlarını ve SHA1 değerini doğrular; paket 2 MB'ı aşarsa hata verir.

Canlı Minecraft istemcisinde görünüm ve gerçek indirme süresi ayrıca kontrol edilmelidir.
