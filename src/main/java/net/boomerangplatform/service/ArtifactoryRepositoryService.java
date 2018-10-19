package net.boomerangplatform.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import net.boomerangplatform.model.VersionArtifact;

public interface ArtifactoryRepositoryService {

	byte[] getArtifactBytes(String ciTeamName, String ciComponentName, String version, String artifact);

	List<VersionArtifact> getArtifactList(String ciTeamName, String ciComponentName, String version);

	String getArtifactVersionCreatedDate(String ciTeamName, String ciComponentName, String version);

	void uploadFile(MultipartFile file, String fileName, String folderPath) throws Exception;
}
