package org.e4s.runjs;

import org.e4s.runjs.model.Device;
import org.e4s.runjs.model.MeterRead;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class RunJsApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(RunJsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RunJsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

		Device device = new Device("meter.001", "meter", "meter.001");

		MeterRead meterRead = new MeterRead();

		meterRead.setId("meter.001");
		meterRead.setVoltage(127.01f);

        File file = new File("/Users/e4s/Workspace/demo-app/dist/src/index.js");

        Source source = Source.newBuilder("js", file).mimeType("application/javascript+module").build();

        try (Context context = Context.newBuilder("js")
                .allowIO(true)
                .allowAllAccess(true)
//                .option("inspect", "4444") // debugger
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", "/Users/e4s/Workspace/demo-app/dist/src/")
                .build()) {
            context.getBindings("js")
                    .putMember("LOG", LoggerFactory.getLogger("ScriptService"));

            Value func = context.eval(source).getMember("meter_reads");

			Value result = func.execute(device, meterRead);

			LOG.info("result: {}", result.asBoolean());
        }


    }
}
