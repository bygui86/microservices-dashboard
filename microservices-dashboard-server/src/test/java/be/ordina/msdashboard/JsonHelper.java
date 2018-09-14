/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ordina.msdashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Helper class for JSON assertions
 *
 * @author Andreas Evers
 */
public class JsonHelper {

    public static String load(final String fileName)
            throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            final StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\n");
            return builder.toString().trim();
        }
    }

    public static String removeBlankNodes(String string) {
        return string.replaceAll("_:t\\d", "");
    }
}
