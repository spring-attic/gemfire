/*
 * Copyright (c) 2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License") ;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.test.gemfire.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.stream.app.test.gemfire.support.FileSystemUtils;
import org.springframework.cloud.stream.app.test.gemfire.support.ThreadUtils;


/**
 * @author Christian Tzolov
 */
public class GeodeServerLauncherHelper {

	public static ProcessWrapper startGeode(String serverName, String gemfireServerXmlFileName) throws IOException {

		File serverWorkingDirectory = new File(FileSystemUtils.WORKING_DIRECTORY, serverName.toLowerCase());

		serverWorkingDirectory.isDirectory();
		serverWorkingDirectory.mkdirs();
		//assertTrue(serverWorkingDirectory.isDirectory() || serverWorkingDirectory.mkdirs());

		List<String> arguments = new ArrayList<>();

		arguments.add("-Dgemfire.name=" + serverName);
		arguments.add(gemfireServerXmlFileName);

		ProcessWrapper serverProcess = ProcessExecutor.launch(serverWorkingDirectory, ServerProcess.class,
				arguments.toArray(new String[arguments.size()]));

		waitForServerStart(TimeUnit.SECONDS.toMillis(20), serverProcess.getWorkingDirectory());

		return serverProcess;
	}

	private static void waitForServerStart(final long milliseconds, File workingDirectory) {
		ThreadUtils.timedWait(milliseconds, TimeUnit.MILLISECONDS.toMillis(500), new ThreadUtils.WaitCondition() {

			private File serverPidControlFile =
					new File(workingDirectory, ServerProcess.getServerProcessControlFilename());

			@Override
			public boolean waiting() {
				return !serverPidControlFile.isFile();
			}
		});
	}

	public static void tearDown(ProcessWrapper serverProcess) {
		serverProcess.shutdown();
		org.springframework.util.FileSystemUtils.deleteRecursively(serverProcess.getWorkingDirectory());
	}

}
