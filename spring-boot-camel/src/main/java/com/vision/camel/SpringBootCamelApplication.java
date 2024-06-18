package com.vision.camel;

import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootCamelApplication extends RouteBuilder {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCamelApplication.class, args);
	}

	@Override
	public void configure() throws Exception {
		// move data from one file another file
		System.out.println("started...");
		 //moveAllFile();
		 //moveSpecificFile("myFile");
		//moveSpecificFileWithBody("java");
		//fileProcess();
		multiFileProcessor();
		System.out.println("End...");

	}

	//Add few files in following source directory and uncomment to test 
	private void moveAllFile() {
		from("file:C:/tmp/camel/source?noop=true").to("file:C:/tmp/camel/destination");
	}
	
	//Add a file name as myFile in following source and uncomment this method to test.
	private void moveSpecificFile(String type) {
		from("file:C:/tmp/camel/source?noop=true").filter(header(Exchange.FILE_NAME).startsWith(type))
				.to("file:C:/tmp/camel/destination");
	}

	//Add some file in :/tmp/camel/source where file is having content as 'java' and uncomment to test
	private void moveSpecificFileWithBody(String content) {
		from("file:C:/tmp/camel/source?noop=true").filter(body().startsWith(content))
				.to("file:C:/tmp/camel/destination");
	}

	//Add a file in project TestFileSource and uncomment in configure method to test (it will create record.csv)
	private void fileProcess() {
		from("file:TestFileSource?noop=true").process(p -> {
			String body = p.getIn().getBody(String.class);
			StringBuilder sb = new StringBuilder();
			Arrays.stream(body.split(" ")).forEach(s -> {
				sb.append(s + ",");
			});

			p.getIn().setBody(sb);
		}).to("file:TestFileDestination?fileName=records.csv");
	}
	
	//Add a file (say paymentMethod.txt) with following contents (PoC menttioned 3 contents) to generate 3 files
	private void multiFileProcessor() {
		from("file:TestFileSource?noop=true")
		.unmarshal().csv().split(body().tokenize(",")).choice()
				.when(body().contains("Closed")).to("file:TestFileDestination?fileName=close.csv")
				.when(body().contains("Pending")).to("file:TestFileDestination?fileName=Pending.csv")
				.when(body().contains("Interest")).to("file:TestFileDestination?fileName=Interest.csv");

	}
}
