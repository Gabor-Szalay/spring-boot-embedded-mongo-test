package com.gaborszalay.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackages = "com.gaborszalay.mongodb")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

	@Value("${mongodb.database-name}")
	private String databaseName;

	@Value("${mongodb.connection-string}")
	private String connectionString;

	@Value("${mongodb.username}")
	private String username;

	@Value("${mongodb.password}")
	private String password;

	@Override
	protected String getDatabaseName() {
		return databaseName;
	}

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(this.connectionString);
		MongoClientSettings mongoClientSettings = !"test".equals(username) ? MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.credential(MongoCredential.createCredential(username, databaseName, password.toCharArray()))
			.build() : MongoClientSettings.builder().applyConnectionString(connectionString).build();

		return MongoClients.create(mongoClientSettings);
	}

	@Override
	public Collection getMappingBasePackages() {
		return Collections.singleton("com.gaborszalay.mongodb");
	}
}
