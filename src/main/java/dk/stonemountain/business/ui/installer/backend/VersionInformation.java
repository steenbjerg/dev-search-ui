package dk.stonemountain.business.ui.installer.backend;

import java.time.ZonedDateTime;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public record VersionInformation(Boolean mustBeUpdated, Boolean currentIsWorking, String recommendedVersion, String recommendedSha, String recommendedReleaseNote, ZonedDateTime recommendedReleaseTime) {

    @JsonbCreator
    public static VersionInformation create(
        @JsonbProperty("current-version-must-be-updated") Boolean mustBeUpdated, 
        @JsonbProperty("current-version-is-working") Boolean currentIsWorking, 
        @JsonbProperty("recommended-version") String recommendedVersion, 
        @JsonbProperty("recommended-sha") String recommendedSha, 
        @JsonbProperty("recommended-release-note") String recommendedReleaseNote, 
        @JsonbProperty("recommended-release-time") ZonedDateTime recommendedReleaseTime) {
            return new VersionInformation(mustBeUpdated, currentIsWorking, recommendedVersion, recommendedSha, recommendedReleaseNote, recommendedReleaseTime);
        }
}
