package qwq.wumie.systems.music.player;

import qwq.wumie.systems.music.MusicManager;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyCookieJar implements CookieJar {
    @Override
    public void saveFromResponse(HttpUrl httpUrl, @NotNull List<Cookie> list) {
        if (MusicManager.cookie.cookieStore.containsKey(httpUrl.host())) {
            List<Cookie> cookies = MusicManager.cookie.cookieStore.get(httpUrl.host());
            for (Cookie item : list) {
                for (Cookie item1 : cookies) {
                    if (item.name().equalsIgnoreCase(item1.name())) {
                        cookies.remove(item1);
                        break;
                    }
                }
                cookies.add(item);
            }
            MusicManager.cookie.cookieStore.put(httpUrl.host(), cookies);
        } else {
            MusicManager.cookie.cookieStore.put(httpUrl.host(), list);
        }
        MusicManager.saveCookie();
    }

    @Override
    public @NotNull List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = MusicManager.cookie.cookieStore.get(httpUrl.host());
        return cookies != null ? cookies : new CopyOnWriteArrayList<>();
    }
}
