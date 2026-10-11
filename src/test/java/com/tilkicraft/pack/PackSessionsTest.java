package com.tilkicraft.pack;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PackSessionsTest {
    @Test void mainDownloadWaitsForBothLoginPackCompletionAndAuthentication() {
        var sessions=new PackSessions();UUID id=UUID.randomUUID();
        assertNull(sessions.beginWhenReady(id,false,false));
        assertNull(sessions.beginWhenReady(id,false,true)); // Small pack loads before /login.
        assertNull(sessions.beginWhenReady(id,true,false)); // Fast /login while small pack downloads.
        assertNotNull(sessions.beginWhenReady(id,true,true));
        assertNull(sessions.beginWhenReady(id,true,true)); // Polling cannot request the main pack twice.
        sessions.remove(id);
        assertNull(sessions.beginWhenReady(id,false,false)); // Reconnect has independent stages.
    }
    @Test void failedDownloadRetriesOnlyOnceAndOldRetryCannotAffectReconnect() {
        var sessions=new PackSessions();UUID id=UUID.randomUUID();var original=sessions.begin(id);
        assertSame(original,sessions.retry(id));assertNull(sessions.retry(id));
        sessions.remove(id);sessions.begin(id);assertFalse(sessions.current(id,original));
        sessions.status(id,true);assertNull(sessions.retry(id));
    }
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
