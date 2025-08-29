package me.hypinohaizin.candyplusrewrite.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class FriendManager {
    private static final ArrayList<Friend> friends = new ArrayList<>();
    private static final ArrayList<Ignore> ignores = new ArrayList<>();
    private static final File FILE = new File("candyplusrewrite/friend.json");
    private static final Gson GSON = new Gson();

    static {
        load();
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static ArrayList<Ignore> getIgnores() {
        return ignores;
    }

    public static ArrayList<String> getFriendsByName() {
        ArrayList<String> names = new ArrayList<>();
        for (Friend f : getFriends()) names.add(f.getName());
        return names;
    }

    public static ArrayList<String> getIgnoresByName() {
        ArrayList<String> names = new ArrayList<>();
        for (Ignore i : getIgnores()) names.add(i.getName());
        return names;
    }

    public static boolean isFriend(String name) {
        return isOnFriendList(name);
    }

    public static boolean isFriend(EntityPlayer p) {
        if (p == null) return false;
        return isOnFriendList(p.getName());
    }

    public static boolean isOnFriendList(String name) {
        String n = normalize(name);
        for (Friend f : getFriends()) {
            if (normalize(f.getName()).equals(n)) return true;
        }
        return false;
    }

    public static boolean isIgnore(String name) {
        String n = normalize(name);
        for (Ignore i : getIgnores()) {
            if (normalize(i.getName()).equals(n)) return true;
        }
        return false;
    }

    public static boolean isOnIgnoreList(String name) {
        String n = normalize(name);
        for (Ignore i : getIgnores()) {
            if (normalize(i.getName()).equals(n)) return true;
        }
        return false;
    }

    public static Friend getFriend(String name) {
        String n = normalize(name);
        for (Friend f : getFriends()) {
            if (normalize(f.getName()).equals(n)) return f;
        }
        return null;
    }

    public static Ignore getIgnore(String name) {
        String n = normalize(name);
        for (Ignore i : getIgnores()) {
            if (normalize(i.getName()).equals(n)) return i;
        }
        return null;
    }

    public static void addFriend(String name) {
        if (!isOnFriendList(name)) {
            friends.add(new Friend(name));
            save();
        }
    }

    public static void delFriend(String name) {
        Friend f = getFriend(name);
        if (f != null) {
            friends.remove(f);
            save();
        }
    }

    public static void addIgnore(String name) {
        if (!isOnIgnoreList(name)) {
            ignores.add(new Ignore(name));
            save();
        }
    }

    public static void delIgnore(String name) {
        Ignore i = getIgnore(name);
        if (i != null) {
            ignores.remove(i);
            save();
        }
    }

    public static void clearIgnoreList() {
        ignores.clear();
        save();
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim();
        StringBuilder b = new StringBuilder(t.length());
        boolean code = false;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c == '§') { code = true; continue; }
            if (code) { code = false; continue; }
            b.append(c);
        }
        return b.toString().toLowerCase(Locale.ROOT);
    }

    public static void save() {
        try {
            if (!FILE.getParentFile().exists()) FILE.getParentFile().mkdirs();
            FriendData data = new FriendData(friends, ignores);
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!FILE.exists()) return;
        try (Reader reader = new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8)) {
            Type type = new TypeToken<FriendData>() {}.getType();
            FriendData data = GSON.fromJson(reader, type);
            friends.clear();
            ignores.clear();
            if (data != null) {
                if (data.friends != null) friends.addAll(data.friends);
                if (data.ignores != null) ignores.addAll(data.ignores);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Friend {
        private final String name;
        public Friend(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public static class Ignore {
        private final String name;
        public Ignore(String name) { this.name = name; }
        public String getName() { return name; }
    }

    private static class FriendData {
        ArrayList<Friend> friends;
        ArrayList<Ignore> ignores;
        FriendData(ArrayList<Friend> friends, ArrayList<Ignore> ignores) {
            this.friends = new ArrayList<>(friends);
            this.ignores = new ArrayList<>(ignores);
        }
    }
}