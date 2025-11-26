package me.maxallgaier.playermanagement.punishment.ban;

import me.maxallgaier.playermanagement.punishment.ban.postgres.PostgresBanPunishmentRepository;
import me.maxallgaier.playermanagement.service.PostgresDatabaseHelper;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresBanPunishmentRepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    static PostgresBanPunishmentRepository repository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        var connHelper = new PostgresDatabaseHelper(postgres.getHost(), postgres.getFirstMappedPort(),
            postgres.getDatabaseName(), postgres.getUsername(), postgres.getPassword());
        repository = new PostgresBanPunishmentRepository(connHelper);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void findById_findByTargetId_findLatestActiveBanByTargetId_create_update__failOnInvalidArgument() {
        assertThrows(Exception.class, () -> repository.findById(null));
        assertThrows(Exception.class, () -> repository.findByTargetId(null));
        assertThrows(Exception.class, () -> repository.findLatestActiveBanByTargetId(null));
        assertThrows(Exception.class, () -> repository.create(null));
        assertThrows(Exception.class, () -> repository.update(null));
    }

    @Test
    void create_findById_update__workForAllValidValuesWithEdgeCaseValues() {
        var minValidInfoBan = BanPunishment.builder().targetId(UUID.randomUUID())
            .issuedDateTime(OffsetDateTime.now().withYear(1971)).build(); // Epoch time starts 1970, Jan 1.
        var minValidInfoBanUpdated = BanPunishment.builder().targetId(UUID.randomUUID())
            .issuedDateTime(OffsetDateTime.now().plusYears(100_000)).build();
        var maxValidInfoBan = BanPunishment.builder().targetId(UUID.randomUUID()).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("reason 123").issuedDateTime(OffsetDateTime.now())
            .duration(Duration.ofDays(365 * 100_000))
            .pardoned(true).pardonerId(UUID.randomUUID()).pardonReason("reason 321").build();
        var maxValidInfoBanUpdated = BanPunishment.builder().targetId(UUID.randomUUID()).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("new reason 123").issuedDateTime(OffsetDateTime.now().plusSeconds(10))
            .pardoned(true).pardonerId(UUID.randomUUID()).pardonReason("new reason 321").build();
        var mapOfBanToUpdatedBan =
            Map.of(minValidInfoBan, minValidInfoBanUpdated, maxValidInfoBan, maxValidInfoBanUpdated);
        for (var entrySet : mapOfBanToUpdatedBan.entrySet()) {
            var ban = entrySet.getKey();
            var updatedBan = entrySet.getValue();

            var registeredBan = assertDoesNotThrow(() -> repository.create(ban));
            assertNotNull(registeredBan);
            assertNotNull(registeredBan.id());
            assertEquals(registeredBan.toBuilder().id(null).build(), ban);

            var banOptional = repository.findById(registeredBan.id());
            assertTrue(banOptional.isPresent());
            assertEquals(registeredBan, banOptional.get());

            var updatedBanWithId = updatedBan.toBuilder().id(registeredBan.id()).build();
            assertDoesNotThrow(() -> repository.update(updatedBanWithId));
            assertEquals(updatedBanWithId, repository.findById(registeredBan.id()).get());
            assertNotEquals(registeredBan, repository.findById(registeredBan.id()).get());
        }
    }

    @Test
    void create__failsWhenCreatingAlreadyRegisteredBan() {
        // Has a pre-defined id to act as a registered punishment.
        var fakeId = UUID.randomUUID();
        var fakeRegisteredBan = BanPunishment.builder().id(fakeId).targetId(UUID.randomUUID())
            .issuerId(UUID.randomUUID()).reason("reason").issuedDateTime(OffsetDateTime.now())
            .duration(Duration.ofHours(9999)).build();
        assertThrows(Exception.class, () -> repository.create(fakeRegisteredBan));
        assertTrue(repository.findById(fakeId).isEmpty());
    }

    @Test
    void update__failsWhenProvidedBanPunishmentIsNotRegistered() {
        var unregisteredBan = BanPunishment.builder()
            .targetId(UUID.randomUUID()).issuedDateTime(OffsetDateTime.now()).build();
        assertThrows(Exception.class, () -> repository.update(unregisteredBan));
        var fakeRegisteredBan = unregisteredBan.toBuilder().id(UUID.randomUUID()).build();
        assertThrows(Exception.class, () -> repository.update(fakeRegisteredBan));
    }

    @Test
    void findById_findLatestActiveBanByTargetId_findByTargetId__haveEmptyResultsForNonExistingIds() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        assertTrue(repository.findLatestActiveBanByTargetId(UUID.randomUUID()).isEmpty());
        assertTrue(repository.findByTargetId(UUID.randomUUID()).isEmpty());
    }

    @Test
    void findLatestActiveBanByTargetId__getsLatestActiveBanCorrectlyAndReturnsNothingWhenThereIsNoActiveBan() {
        var randomTargetId = UUID.randomUUID();

        // Punishments are ordered from oldest to newest based off date issued.
        var notExpiredUnPardonedBan = BanPunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(3))
            .duration(null)
            .build();
        var notExpiredPardonedBan = BanPunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(2))
            .duration(Duration.ofDays(1000000))
            .pardoned(true)
            .build();
        var expiredUnPardonedBan = BanPunishment.builder()
            .targetId(randomTargetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(1))
            .duration(Duration.ZERO)
            .build();

        var registeredNotExpiredUnPardonedBan = repository.create(notExpiredUnPardonedBan);
        repository.create(notExpiredPardonedBan);
        repository.create(expiredUnPardonedBan);

        assertEquals(registeredNotExpiredUnPardonedBan, repository.findLatestActiveBanByTargetId(randomTargetId).get());
        var pardonedRegisteredNotExpiredUnPardonedBan = registeredNotExpiredUnPardonedBan.toBuilder()
            .pardoned(true).build();
        repository.update(pardonedRegisteredNotExpiredUnPardonedBan);

        assertTrue(repository.findLatestActiveBanByTargetId(randomTargetId).isEmpty());
    }

    @Test
    void findByTargetId__returnsCorrectResultsAndInCorrectOrder() {
        var targetId = UUID.randomUUID();
        var ban1 = BanPunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(3)).build();
        var ban2 = BanPunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(30)).build();
        var ban3 = BanPunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(2)).build();
        var ban4 = BanPunishment.builder().targetId(targetId)
            .issuedDateTime(OffsetDateTime.now().minusSeconds(1)).build();
        repository.create(ban1);
        repository.create(ban2);
        repository.create(ban3);
        repository.create(ban4);
        List<BanPunishment> bans = repository.findByTargetId(targetId);
        assertEquals(4, bans.size());
        assertEquals(ban4, bans.get(0).toBuilder().id(null).build());
        assertEquals(ban3, bans.get(1).toBuilder().id(null).build());
        assertEquals(ban1, bans.get(2).toBuilder().id(null).build());
        assertEquals(ban2, bans.get(3).toBuilder().id(null).build());
    }
}
