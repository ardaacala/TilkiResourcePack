# TilkiResourcePack

Spigot 1.21.4 / Java 21 için CombatGuns, TilkiLoginScreen, TilkiInterface ve TilkiTags varlıklarını tek kaynak paketinde birleştirir. Oyuncu giriş paketi tamamlandıktan sonra ortak paket gönderilir.

## Kurulum

Sunucudaki eski TilkiResourcePack ve TilkiInterface JAR dosyalarını kaldırıp [v1.0.7](https://github.com/ardaacala/TilkiResourcePack/releases/tag/v1.0.7) ve [TilkiInterface v1.4.2](https://github.com/ardaacala/TilkiInterface/releases/tag/v1.4.2) JAR dosyalarını `plugins` klasörüne koyun. CombatGuns, TilkiLoginScreen ve TilkiTags kurulu kalır. Ayar klasörlerini koruyun. Resmî eski paket adresleri ve SHA1 değerleri açılışta güncellenir; özel adresler korunur.

Birleşik ZIP yaklaşık 596 KB'dir. Genel Minecraft yazı fontunu değiştirmez; font profilleri ve `/tilkipack font` komutu kaldırılmıştır. Giriş logosu, silahlar, rozetler, ESC teması, HUD ve TAB çerçeveleri kalır. Eski `font-style` ve `profiles` ayarları otomatik temizlenir. Varsayılan ikinci paket gecikmesi 10 tick'tir.

HUD'un üst kenarında tilki başı, TAB çerçevesinin üstünde TilkiCraft logosu bulunur. İnce ve kesintisiz yan kenarlar oyuncu listesi uzarken dikkat çeken bir tekrar oluşturmaz. `sidebar.frame: false` ve `tab.enabled: false` ile paneller kapatılabilir. Çok küçük pencere veya yüksek GUI ölçeğinde TAB kenarı kırpılabilir.

`/tilkipack reload` ayarları yeniden okur ve paketi tekrar gönderir. `/tilkiui reload` ve `/tilkitags reload` ortak paket etkinken ek indirme başlatmaz.

## Geliştirme

Java 21 ile `mvn test package` çalıştırın. `tools/BuildCombinedPack.java` dört projenin `resourcepack` klasörünü birleştirir. `tools/ValidatePack.ps1` ZIP içeriğini, font referanslarını, atlas sınırlarını ve SHA1 değerini doğrular; paket 2 MB'ı aşarsa hata verir.

Canlı Minecraft istemcisinde görünüm ve gerçek indirme süresi ayrıca kontrol edilmelidir.
