package net.boomerangplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.model.DatabaseModel;
import net.boomerangplatform.model.Model1;
import net.boomerangplatform.model.Model2;
import net.boomerangplatform.service.ExampleService;

@RestController
public class Controller {

	@Autowired
	private ExampleService example;
	
	@RequestMapping("/example/data")
	public List<Model1> AccessModel1(
			@RequestParam(value="token", required=true ) String token){
		return example.getModel1(token);
	}
	
	@RequestMapping("/example/data/details")
	public Model2 AccessModel2(
			@RequestParam(value="value1", required=true ) String value1,
			@RequestParam(value="value2", required=true ) String value2){
		return example.getModel2(value1, value2);
	}
	
	@RequestMapping("/example/database")
	public List<DatabaseModel> AccessDatabaseInteraction(
			@RequestParam(value="value1", required=true ) String value1,
			@RequestParam(value="value2", required=true ) String value2,
			@RequestParam(value="value3", required=true ) String value3){
		return example.databaseInteraction(value1, value2, value3);
	}
}
