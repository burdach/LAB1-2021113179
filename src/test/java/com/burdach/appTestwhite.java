package com.burdach;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class appTestwhite {
    @Before
    public void setUp() {
        String filePath = "input.txt";
        App.graph = App.processText(filePath);
    }
    @Test
    public void testNoWordsInGraph() {
        String word1 = "cxc";
        String word2 = "bridge";
        // Capture output or use a similar approach to validate the output
        // This is a simplified example; use appropriate output capture techniques
        assertEquals("No word1 or word2 in the graph!", App.queryBridgeWords(App.graph,word1, word2));
    }

    @Test
    public void testNoWordsInGraph2() {
        String word1 = "get";
        String word2 = "cxc";
        // Capture output or use a similar approach to validate the output
        // This is a simplified example; use appropriate output capture techniques
        assertEquals("No word1 or word2 in the graph!", App.queryBridgeWords(App.graph,word1, word2));
    }

    @Test
    public void testNoBridgeWords() {
        String word1 = "version";
        String word2 = "for";
        // Capture output or use a similar approach to validate the output
        // This is a simplified example; use appropriate output capture techniques
        assertEquals("No bridge words from word1 to word2!", App.queryBridgeWords(App.graph,word1, word2));
    }

    @Test
    public void testBridgeWords() {
        String word1 = "with";
        String word2 = "bonus";
        // Capture output or use a similar approach to validate the output
        // This is a simplified example; use appropriate output capture techniques
        assertEquals("The bridge words from word1 to word2 are:a", App.queryBridgeWords(App.graph,word1, word2));
    }

    @Test
    public void testBridgeWords2() {
        String word1 = "get";
        String word2 = "normal";
        // Capture output or use a similar approach to validate the output
        // This is a simplified example; use appropriate output capture techniques
        assertEquals("The bridge words from word1 to word2 are:the, sunny", App.queryBridgeWords(App.graph,word1, word2));
    }
}


