package net.boomerangplatform.service;

import java.util.List;

import net.boomerangplatform.model.*;

public interface ExampleService {

	List<Model1> getModel1(String token);
	Model2 getModel2(String value1, String value2);
	List<DatabaseModel> databaseInteraction(String value1, String value2, String value3);

}

