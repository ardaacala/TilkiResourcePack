package com.tilkicraft.pack;
import java.util.*;

final class PackSessions {
    static final class Session { boolean loaded; }
    private final Map<UUID,Session> sessions=new HashMap<>();
    Session begin(UUID id) {
        if(sessions.containsKey(id)) return null;
        Session session=new Session();sessions.put(id,session);return session;
    }
    boolean current(UUID id,Session session) { return sessions.get(id)==session; }
    boolean loaded(UUID id) { Session session=sessions.get(id);return session!=null&&session.loaded; }
    void status(UUID id,boolean loaded) { Session session=sessions.get(id);if(session!=null)session.loaded=loaded; }
    void remove(UUID id) { sessions.remove(id); }
    void clear() { sessions.clear(); }
}
