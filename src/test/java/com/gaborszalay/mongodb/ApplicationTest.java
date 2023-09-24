package com.gaborszalay.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.flapdoodle.embed.mongo.commands.MongoImportArguments;
import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.ExecutedMongoImportProcess;
import de.flapdoodle.embed.mongo.transitions.MongoImport;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import de.flapdoodle.reverse.transitions.Start;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@AutoConfigureDataMongo
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

	private BooksController booksController;

	@Autowired
	private BooksService booksService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private TransitionWalker.ReachedState<RunningMongodProcess> mongoDProcess;

	private TransitionWalker.ReachedState<ExecutedMongoImportProcess> mongoImportProcess;

	@BeforeAll
	public void setUp() {
		String os = System.getProperty("os.name");
		String path = Resources.getResource("books.json").getPath();

		if (os != null && os.toLowerCase().contains("windows")) {
			path = path.substring(1);
		}

		MongoImportArguments arguments = MongoImportArguments.builder()
				.databaseName("bookstore")
				.collectionName("books")
				.importFile(path)
				.isJsonArray(true)
				.upsertDocuments(true)
				.build();

		Version.Main version = Version.Main.PRODUCTION;

		mongoDProcess = Mongod.builder()
				.net(Start.to(Net.class).initializedWith(Net.defaults().withPort(27019)))
				.build()
				.transitions(version)
				.walker()
				.initState(StateID.of(RunningMongodProcess.class));

		Transitions mongoImportTransitions = MongoImport.instance()
				.transitions(version)
				.replace(Start.to(MongoImportArguments.class).initializedWith(arguments))
				.addAll(Start.to(ServerAddress.class).initializedWith(mongoDProcess.current().getServerAddress()));

		mongoImportProcess = mongoImportTransitions.walker().initState(StateID.of(ExecutedMongoImportProcess.class));

		booksController = new BooksController(booksService);
	}

	@AfterAll
	public void tearDownAfterAll() {
		mongoImportProcess.close();
		mongoDProcess.close();
	}

	@AfterTestClass
	public void tearDownAfterTestClass() {
		mongoImportProcess.close();
		mongoDProcess.close();
	}

	@Test
	public void givenExpectedJson_whenGetBooksIsCalled_returnBooksEqualToExpected() throws IOException {
		List<Book> booksResponse = booksController.getBooks();

		String booksExpectedResponse = Files.readString(Paths.get("src/test/resources/books-expected-response.json"));
		Book[] books = objectMapper.readValue(booksExpectedResponse, Book[].class);

		assertThat(booksResponse).usingRecursiveComparison()
				.isEqualTo(Arrays.asList(books));
	}
}
