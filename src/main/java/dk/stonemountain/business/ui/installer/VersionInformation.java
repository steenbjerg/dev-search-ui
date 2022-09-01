package dk.stonemountain.business.ui.installer;

import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VersionInformation {
	private BooleanProperty newerVersionAvailable = new SimpleBooleanProperty();
	private BooleanProperty mustBeUpdated = new SimpleBooleanProperty();
	private StringProperty newVersion = new SimpleStringProperty();
	private StringProperty newSha = new SimpleStringProperty();
	private StringProperty newReleaseNote = new SimpleStringProperty();
	private ObjectProperty<LocalDateTime> newReleaseTime = new SimpleObjectProperty<>();

	public BooleanProperty newerVersionAvailableProperty() {
		return newerVersionAvailable;
	}

	public Boolean getNewerVersionAvailable() {
		return newerVersionAvailable.get();
	}

	public void setNewerVersionAvailable(Boolean newerVersionAvailable) {
		this.newerVersionAvailable.set(newerVersionAvailable);
	}

	public BooleanProperty mustBeUpdatedProperty() {
		return mustBeUpdated;
	}

	public Boolean getMustBeUpdated() {
		return mustBeUpdated.get();
	}

	public void setMustBeUpdated(Boolean mustBeUpdated) {
		this.mustBeUpdated.set(mustBeUpdated);
	}

	public String getNewVersion() {
		return newVersion.get();
	}

	public StringProperty newVersionProperty() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion.set(newVersion);
	}

	public String getNewReleaseNote() {
		return newReleaseNote.get();
	}

	public StringProperty newReleaseNoteProperty() {
		return newReleaseNote;
	}

	public void setNewReleaseNote(String newReleaseNote) {
		this.newReleaseNote.set(newReleaseNote);
	}

	public String getNewSha() {
		return newSha.get();
	}

	public StringProperty newShaProperty() {
		return newSha;
	}

	public void setNewSha(String newSha) {
		this.newSha.set(newSha);
	}

	public LocalDateTime getNewReleaseTime() {
		return newReleaseTime.get();
	}

	public ObjectProperty<LocalDateTime> newReleaseTimeProperty() {
		return newReleaseTime;
	}

	public void setNewReleaseTime(LocalDateTime newReleaseTime) {
		this.newReleaseTime.set(newReleaseTime);
	}

	@Override
	public String toString() {
		return "VersionInformation [mustBeUpdated=" + mustBeUpdated + ", newReleaseNote=" + newReleaseNote
				+ ", newReleaseTime=" + newReleaseTime + ", newSha=" + newSha + ", newVersion=" + newVersion
				+ ", newerVersionAvailable=" + newerVersionAvailable + "]";
	}
}