package com.tilkicraft.pack;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PackSessionsTest {
    @Test void duplicateJoinOrEnableRequestDoesNotScheduleTwoDownloads() {
        var sessions=new PackSessions();UUID id=UUID.randomUUID();
        assertNotNull(sessions.begin(id));assertNull(sessions.begin(id));
    }
    @Test void oldDelayedTaskCannotSendPackAfterReconnect() {
        var sessions=new PackSessions();UUID id=UUID.randomUUID();var old=sessions.begin(id);
        sessions.remove(id);var fresh=sessions.begin(id);
        assertFalse(sessions.current(id,old));assertTrue(sessions.current(id,fresh));
    }
    @Test void successIsSharedUntilQuitOrExplicitReload() {
        var sessions=new PackSessions();UUID id=UUID.randomUUID();sessions.begin(id);
        assertFalse(sessions.loaded(id));sessions.status(id,true);assertTrue(sessions.loaded(id));
        sessions.clear();assertFalse(sessions.loaded(id));assertNotNull(sessions.begin(id));
    }
}
