# TilkiCraft ortak kaynak paketi

Spigot 1.21.4 / Java 21. CombatGuns, TilkiLoginScreen, TilkiStatsHUD ve TilkiTags kaynakları tek ZIP içindedir. TilkiResourcePack oyuncu başına tek paket isteği gönderir. Güncellenmiş dört eklenti ortak paketin yüklenme sonucunu kullanır; kendi paketlerini ayrıca göndermez. Ortak eklenti yoksa eski bağımsız paket davranışı sürer.

## Kurulum

1. Sunucuyu kapatın.
2. TilkiCraft-Update.zip içindeki plugins klasöründeki beş JAR'ı sunucunun plugins klasörüne kopyalayın. Aynı isimli eski dosyaların üzerine yazın; farklı sürüm isimli eski kopyaları kaldırın. Mevcut ayar klasörlerini koruyun.
3. AuthMe ve LuckPerms sunucuda kurulu kalmalıdır. Bu paket bunların yerini almaz.
4. Sunucuyu yeniden başlatın. GitHub ZIP adresi ve SHA1 hazırdır, ayrıca paket ZIP'ini sunucuya kopyalamanız gerekmez.
5. server.properties içindeki resource-pack alanında eski bir paket varsa boşaltın. Başka paket gönderen eklentiler de ayrı indirme isteği oluşturabilir.

Sağ HUD turuncu köşeli çerçeve ve üstte TilkiCraft logosu içerir. TAB üstünde logo, turuncu ayraçlar; altında oyuncu, ping ve rütbe bulunur. TilkiTags oyuncu isimleri ve rozetlerini yönetmeye devam eder. TAB dış çerçevesi görünür oyuncu sayısına ve isim genişliğine göre seçilir. Minecraft doğal sütun yerleşimini korur; çok küçük pencereler veya yüksek GUI ölçeği kenarları kırpabilir.

HUD çerçevesi metne göre 128, 144, 160 veya 192 piksel genişliğinde, 13 satır içindir. Çok uzun özel satırlar sınırları aşabilir. Ölüm/öldürme satırları kaldırılmıştır; istatistik simgeleri ve yalnız tilki maskotu eklenmiştir. `sidebar.frame: false` ve `tab.enabled: false` ile kapatılabilir. `tab.header` ve `tab.footer` satır listeleri özelleştirilebilir; `{tab-logo}`, `{online}`, `{max}`, `{player}`, `{ping}`, `{rank}` kullanılabilir.

`/tilkipack reload` ortak paketi yeniden gönderir. `/tilkiui reload` ve `/tilkitags reload`, ortak paket etkin olduğunda yeni indirme istemez.

## Doğrulama

Güncellenen üç eklenti derlenmiştir; 20 birim testi geçmiştir. Diğer iki eklenti önceki güncellemenin derlenmiş sürümleridir. Ortak ZIP 1308 dosya, dört modül fontu ve seçilen metin fontu referansı içerir. Canlı Minecraft istemcisinde görüntü ve eklentiler arası çalışma ayrıca denenmelidir.

## Ada seviyesi ve beyaz isimler

Chat, TAB ve oyuncu üzerinde seviye + rozet + beyaz isim gösterilir. PlaceholderAPI ve SuperiorSkyblock2 kurulu olmalıdır. Varsayılan `%superior_island_level_format%` alanı SuperiorSkyblock tarafından sağlanır: https://wiki.bg-software.com/superiorskyblock/overview/placeholders/island-placeholders . Başka ada eklentisi kullanılırsa TilkiTags ve TilkiStatsHUD config dosyalarında `island-level.placeholder` değiştirilir. Çözülmeyen placeholder veya adası olmayan oyuncu için 0 gösterilir. HUD seviye satırı artık ada seviyesidir.

Eski TilkiTags, TilkiStatsHUD ve TilkiResourcePack JAR sürümlerini kaldırın; klasörlerini silmeyin. Resmî eski kaynak paketi adresleri otomatik güncellenir. Tilki rozeti artık `Tilki` yazılıdır; bütün rozetler kalın piksel yazı kullanır.

## 1.0.2 — HD fontlar ve süslemeli çerçeveler

TilkiStatsHUD ve TilkiTags 1.3.0 ile kullanılır. Rozetler 3 kat çözünürlükte, yumuşatılmış yazı ile çizilir. HUD/TAB ahşap, metal, altın ve tilki başı süslemeli HD çerçeve kullanır. 4 kat kaynak çözünürlüğü, atlas sınırını aşmayan 885 parçaya bölünür. HUD altındaki TilkiCraft • Skyblock satırı seçili fonta göre ortalanır. Paper sayı gizleme API'si ve Spigot'un paket içi CraftObjective metodu üzerinden boş sayı formatı desteklenir. Desteklenmeyen sunucuda açık uyarı yazılır.

Genel oyun fontları (Türkçe destekli, SIL OFL lisanslı Noto):
- modern: Noto Sans; varsayılan.
- classic: Noto Sans Mono.
- fantasy: Noto Serif.

Yönetici komutu: `/tilkipack font modern`, `/tilkipack font classic` veya `/tilkipack font fantasy`. Seçim sunucu genelindedir, config dosyasına kaydedilir ve oyunculara seçilen tek birleşik paket gönderilir. Diğer iki font paketi otomatik olarak ayrıca indirilmez. `/tilkipack reload` ayarları yeniden okur. Çerçeve ve fontların görünümü GUI ölçeğine bağlıdır; küçük pencerelerde TAB kırpılabilir. Canlı istemci kontrolü henüz yapılmamıştır.
