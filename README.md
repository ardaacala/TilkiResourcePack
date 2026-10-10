# TilkiCraft ortak kaynak paketi

Spigot 1.21.4 / Java 21. CombatGuns, TilkiLoginScreen, TilkiStatsHUD ve TilkiTags kaynakları tek ZIP içindedir. TilkiResourcePack oyuncu başına tek paket isteği gönderir. Güncellenmiş dört eklenti ortak paketin yüklenme sonucunu kullanır; kendi paketlerini ayrıca göndermez. Ortak eklenti yoksa eski bağımsız paket davranışı sürer.

## Kurulum

1. Sunucuyu kapatın.
2. TilkiCraft-Update.zip içindeki plugins klasöründeki beş JAR'ı sunucunun plugins klasörüne kopyalayın. Aynı isimli eski dosyaların üzerine yazın; farklı sürüm isimli eski kopyaları kaldırın. Mevcut ayar klasörlerini koruyun.
3. AuthMe ve LuckPerms sunucuda kurulu kalmalıdır. Bu paket bunların yerini almaz.
4. Sunucuyu yeniden başlatın. GitHub ZIP adresi ve SHA1 hazırdır, ayrıca paket ZIP'ini sunucuya kopyalamanız gerekmez.
5. server.properties içindeki resource-pack alanında eski bir paket varsa boşaltın. Başka paket gönderen eklentiler de ayrı indirme isteği oluşturabilir.

Sağ HUD turuncu köşeli çerçeve ve üstte TilkiCraft logosu içerir. TAB üstünde logo, turuncu ayraçlar; altında oyuncu, ping ve rütbe bulunur. TilkiTags oyuncu isimleri ve rozetlerini yönetmeye devam eder. TAB yerleşimi Minecraft'ın doğal oyuncu listesine bağlıdır; referanstaki sabit dört sütunlu dış çerçeve birebir uygulanmamıştır.

HUD çerçevesi 208 piksel genişliğinde, 15 satır içindir. Çok uzun özel satırlar sınırları aşabilir. `sidebar.frame: false` ve `tab.enabled: false` ile kapatılabilir. `tab.header` ve `tab.footer` satır listeleri özelleştirilebilir; `{tab-logo}`, `{online}`, `{max}`, `{player}`, `{ping}`, `{rank}` kullanılabilir.

`/tilkipack reload` ortak paketi yeniden gönderir. `/tilkiui reload` ve `/tilkitags reload`, ortak paket etkin olduğunda yeni indirme istemez.

## Doğrulama

Beş eklenti derlenmiştir; toplam 22 birim testi geçmiştir. Ortak ZIP 195 dosya ve dört font referansı içerir. Canlı Minecraft istemcisinde görüntü ve eklentiler arası çalışma ayrıca denenmelidir.
