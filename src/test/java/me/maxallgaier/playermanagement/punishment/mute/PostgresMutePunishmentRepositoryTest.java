package me.maxallgaier.playermanagement.punishment.mute;

import me.maxallgaier.playermanagement.punishment.mute.postgres.PostgresMutePunishmentRepository;
import me.maxallgaier.playermanagement.service.PostgresDatabaseHelper;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresMutePunishmentRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    static PostgresMutePunishmentRepository repository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        var connHelper = new PostgresDatabaseHelper(postgres.getHost(), postgres.getFirstMappedPort(),
            postgres.getDatabaseName(), postgres.getUsername(), postgres.getPassword());
        repository = new PostgresMutePunishmentRepository(connHelper);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void findById_findByTargetId_findLatestActiveMuteByTargetId_create_update__failOnInvalidArgument() {
        assertThrows(Exception.class, () -> repository.findById(null));
        assertThrows(Exception.class, () -> repository.findByTargetId(null));
        assertThrows(Exception.class, () -> repository.findLatestActiveMuteByTargetId(null));
        assertThrows(Exception.class, () -> repository.create(null));
        assertThrows(Exception.class, () -> repository.update(null));
    }

    @Test
    void create_findById_update__workForAllValidValuesWithEdgeCaseValues() {
        var minValidInfoMute = MutePunishment.builder().targetId(UUID.randomUUID())
            .issuedDateTime(OffsetDateTime.now().withYear(1971)) // Epoch time starts 1970, Jan 1.
            .build();
        var minValidInfoMuteUpdated = MutePunishment.builder().targetId(UUID.randomUUID())
            .issuedDateTime(OffsetDateTime.now().plusYears(100_000))
            .build();
        var maxValidInfoMute = MutePunishment.builder().targetId(UUID.randomUUID()).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("reason 123").issuedDateTime(OffsetDateTime.now())
            .duration(Duration.ofDays(365 * 100_000))
            .pardoned(true).pardonerId(UUID.randomUUID()).pardonReason("reason 321")
            .build();
        var maxValidInfoMuteUpdated = MutePunishment.builder().targetId(UUID.randomUUID()).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("new reason 123").issuedDateTime(OffsetDateTime.now().plusSeconds(10))
            .pardoned(true).pardonerId(UUID.randomUUID()).pardonReason("new reason 321")
            .build();
        var mapOfMuteToUpdatedMute =
            Map.of(minValidInfoMute, minValidInfoMuteUpdated, maxValidInfoMute, maxValidInfoMuteUpdated);
        for (var entrySet : mapOfMuteToUpdatedMute.entrySet()) {
            var mute = entrySet.getKey();
            var updatedMute = entrySet.getValue();

            var registeredMute = assertDoesNotThrow(() -> repository.create(mute));
            assertNotNull(registeredMute);
            assertNotNull(registeredMute.id());
            assertEquals(registeredMute.toBuilder().id(null).build(), mute);

            var muteOptional = repository.findById(registeredMute.id());
            assertTrue(muteOptional.isPresent());
            assertEquals(registeredMute, muteOptional.get());

            var updatedMuteWithId = updatedMute.toBuilder().id(registeredMute.id()).build();
            assertDoesNotThrow(() -> repository.update(updatedMuteWithId));
            assertEquals(updatedMuteWithId, repository.findById(registeredMute.id()).get());
            assertNotEquals(registeredMute, repository.findById(registeredMute.id()).get());
        }
    }

    @Test
    void create__failsWhenCreatingAlreadyRegisteredMute() {
        // Has a pre-defined id to act as a registered punishment.
        var fakeId = UUID.randomUUID();
        var fakeRegisteredMute = MutePunishment.builder().id(fakeId).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("reason").issuedDateTime(OffsetDateTime.now())
            .duration(Duration.ofHours(9999))
            .build();
        assertThrows(Exception.class, () -> repository.create(fakeRegisteredMute));
        assertTrue(repository.findById(fakeId).isEmpty());
    }

    @Test
    void update__failsWhenProvidedMutePunishmentIsNotRegistered() {
        var unregisteredMute = MutePunishment.builder()
            .targetId(UUID.randomUUID()).issuedDateTime(OffsetDateTime.now()).build();
        assertThrows(Exception.class, () -> repository.update(unregisteredMute));
        var fakeRegisteredMute = unregisteredMute.toBuilder().id(UUID.randomUUID()).build();
        assertThrows(Exception.class, () -> repository.update(fakeRegisteredMute));
    }

    @Test
    void findById_findLatestActiveMuteByTargetId_findByTargetId__haveEmptyResultsForNonExistingIds() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        assertTrue(repository.findLatestActiveMuteByTargetId(UUID.randomUUID()).isEmpty());
        assertTrue(repository.findByTargetId(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findLatestActiveMuteByTargetId__getsLatestActiveMuteCorrectlyAndReturnsNothingWhenThereIsNoActiveMute() {
        var randomTargetId = UUID.randomUUID();

        // Punishments are ordered from oldest to newest based off date issued.
        var notExpiredUnPardonedMute = MutePunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(3))
            .duration(null)
            .build();
        var notExpiredPardonedMute = MutePunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(2))
            .duration(Duration.ofDays(1000000))
            .pardoned(true)
            .build();
        var expiredUnPardonedMute = MutePunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(1))
            .duration(Duration.ZERO)
            .build();

        var registeredNotExpiredUnPardonedMute = repository.create(notExpiredUnPardonedMute);
        repository.create(notExpiredPardonedMute);
        repository.create(expiredUnPardonedMute);

        assertEquals(registeredNotExpiredUnPardonedMute, repository.findLatestActiveMuteByTargetId(randomTargetId).get());
        var pardonedRegisteredNotExpiredUnPardonedMute = registeredNotExpiredUnPardonedMute.toBuilder()
            .pardoned(true).build();
        repository.update(pardonedRegisteredNotExpiredUnPardonedMute);

        assertTrue(repository.findLatestActiveMuteByTargetId(randomTargetId).isEmpty());
    }

    @Test
    void findByTargetId__returnsCorrectResultsAndInCorrectOrder() {
        var targetId = UUID.randomUUID();
        var mute1 = MutePunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(3)).build();
        var mute2 = MutePunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(30)).build();
        var mute3 = MutePunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(2)).build();
        var mute4 = MutePunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(1)).build();
        repository.create(mute1);
        repository.create(mute2);
        repository.create(mute3);
        repository.create(mute4);
        List<MutePunishment> mutes = repository.findByTargetId(targetId);
        assertEquals(4, mutes.size());
        assertEquals(mute4, mutes.get(0).toBuilder().id(null).build());
        assertEquals(mute3, mutes.get(1).toBuilder().id(null).build());
        assertEquals(mute1, mutes.get(2).toBuilder().id(null).build());
        assertEquals(mute2, mutes.get(3).toBuilder().id(null).build());
    }
}
