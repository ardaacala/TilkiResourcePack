package com.tilkicraft.pack;
import java.util.*;

final class PackSessions {
    static final class Session { boolean loaded; boolean retried; }
    private final Map<UUID,Session> sessions=new HashMap<>();
    Session begin(UUID id) {
        return beginWhenReady(id,true,true);
    }
    Session beginWhenReady(UUID id,boolean authenticated,boolean loginSettled) {
        if(!authenticated || !loginSettled)return null;
        if(sessions.containsKey(id)) return null;
        Session session=new Session();sessions.put(id,session);return session;
    }
    boolean current(UUID id,Session session) { return sessions.get(id)==session; }
    boolean loaded(UUID id) { Session session=sessions.get(id);return session!=null&&session.loaded; }
    void status(UUID id,boolean loaded) { Session session=sessions.get(id);if(session!=null)session.loaded=loaded; }
    Session retry(UUID id) {
        Session session=sessions.get(id);
        if(session==null || session.loaded || session.retried)return null;
        session.retried=true;return session;
    }
    void remove(UUID id) { sessions.remove(id); }
    void clear() { sessions.clear(); }
}
