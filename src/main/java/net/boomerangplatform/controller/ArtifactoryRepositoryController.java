package net.boomerangplatform.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.boomerangplatform.model.VersionArtifact;
import net.boomerangplatform.service.ArtifactoryRepositoryService;

@RequestMapping("/repository/artifactory")
@RestController
public class ArtifactoryRepositoryController {

	@Autowired
	private ArtifactoryRepositoryService repositoryService;

	@RequestMapping("/artifact")
	public byte[] getArtifactBytes(HttpServletResponse response,
			@RequestParam(value = "ciTeamName", required = true) String ciTeamName,
			@RequestParam(value = "ciComponentName", required = true) String ciComponentName,
			@RequestParam(value = "version", required = true) String version,
			@RequestParam(value = "artifact", required = true) String artifact) {
		return repositoryService.getArtifactBytes(ciTeamName, ciComponentName, version, artifact);
	}

	@RequestMapping("/artifact/list")
	public List<VersionArtifact> getArtifactList(HttpServletResponse response,
			@RequestParam(value = "ciTeamName", required = true) String ciTeamName,
			@RequestParam(value = "ciComponentName", required = true) String ciComponentName,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getArtifactList(ciTeamName, ciComponentName, version);
	}

	@RequestMapping("/artifact/version/created")
	public String getArtifactVersionCreatedDate(HttpServletResponse response,
			@RequestParam(value = "ciTeamName", required = true) String ciTeamName,
			@RequestParam(value = "ciComponentName", required = true) String ciComponentName,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getArtifactVersionCreatedDate(ciTeamName, ciComponentName, version);
	}

	@RequestMapping("")
	public void uploadFile(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "folderPath", required = true) String folderPath) throws Exception {
		repositoryService.uploadFile(file, fileName, folderPath);
	}
}
