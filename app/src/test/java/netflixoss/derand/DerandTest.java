/*
 * This Java source file was generated by the Gradle 'init' task.
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
