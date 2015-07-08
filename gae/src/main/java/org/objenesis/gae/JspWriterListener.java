/**
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package main.java.org.objenesis.gae;

import org.objenesis.tck.search.SearchWorkingInstantiatorListener;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * @author Henri Tremblay
 */
public class JspWriterListener implements SearchWorkingInstantiatorListener {

    private static final String PATTERN = "<tr><td>%s</td><td>%s</td></tr>";

    private JspWriter writer;

    public JspWriterListener(JspWriter out) {
        this.writer = out;
    }


    public void instantiatorSupported(Class<?> c) {
        System.out.println("allo");
        try {
            writer.println(String.format(PATTERN, c.getSimpleName(), "Working!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void instantiatorUnsupported(Class<?> c, Throwable t) {
        System.out.println("bonjour");
        try {
            writer.println(String.format(PATTERN, c.getSimpleName(), "KO - " + t));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
