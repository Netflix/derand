/**
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package netflixoss.derand;

import ai.djl.translate.TranslateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DerandTest {
    @Test public void testTokenize() throws TranslateException, IOException {
        assertEquals("hello <rnd> world", Derand.tokenize("hello 3y29842ysjhfs world"));
        assertEquals("", Derand.tokenize(""));
        assertEquals("", Derand.tokenize(null));
        assertEquals("<rnd>", Derand.tokenize("3y29842ysjhfs"));
    }


    @Test public void testClean() throws TranslateException, IOException {
        assertEquals("hello world", Derand.clean("hello 3y29842ysjhfs world"));
        assertEquals("", Derand.clean(""));
        assertEquals("", Derand.clean(null));
        assertEquals("", Derand.clean("3y29842ysjhfs"));
        assertEquals("hello hello", Derand.clean("y837sc42zsd hello sdyd8f7h34 hello 3y29842ysjhfs"));
    }
}
