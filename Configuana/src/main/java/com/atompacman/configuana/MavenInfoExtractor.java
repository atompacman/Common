package com.atompacman.configuana;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenInfoExtractor {

	public static void main(String[] args) throws FileNotFoundException, IOException, XmlPullParserException {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileInputStream("C:/Users/Utilisateur/Documents/eclipse_workspace/atomlibs/Configuana/pom.xml"));
		System.out.println(model.getArtifactId());
		System.out.println(model.getDescription());
		System.out.println(model.getGroupId());
		System.out.println(model.getId());
		System.out.println(model.getInceptionYear());
		System.out.println(model.getName());
		System.out.println(model.getPackaging());
		System.out.println(model.getVersion());
		System.out.println(model.getProperties().size());
		for (Dependency dep : model.getDependencies()) {
			System.out.println();
			System.out.println(dep.getArtifactId());
			System.out.println(dep.getGroupId());
			System.out.println(dep.getVersion());
			System.out.println(dep.getSystemPath());
		}
	}
}
